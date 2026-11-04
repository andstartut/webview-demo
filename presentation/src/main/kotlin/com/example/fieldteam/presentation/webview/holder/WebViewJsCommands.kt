package com.example.fieldteam.presentation.webview.holder

import kotlinx.serialization.json.JsonPrimitive

internal object WebViewJsCommands {

    private const val COMMAND_BRIDGE_NAME = "__androidNativeCommandBridge"
    private const val COMMAND_RETRY_DELAY_MS = 250
    private const val COMMAND_TTL_MS = 15_000

    private val COMMAND_BRIDGE_SCRIPT = """
        (function() {
          var existingBridge = window.$COMMAND_BRIDGE_NAME;
          if (existingBridge) {
            existingBridge.flush();
            return;
          }

          var queue = Object.create(null);
          var retryTimerId = null;
          var flushScheduled = false;

          var nextQueueKey = function(target) {
            return target + ':' + Date.now() + ':' + Math.random().toString(36).slice(2);
          };

          var cancelRetry = function() {
            if (retryTimerId === null) return;
            clearTimeout(retryTimerId);
            retryTimerId = null;
          };

          var scheduleRetry = function() {
            if (retryTimerId !== null) return;
            retryTimerId = setTimeout(function() {
              retryTimerId = null;
              flush();
            }, $COMMAND_RETRY_DELAY_MS);
          };

          var flush = function() {
            flushScheduled = false;
            var now = Date.now();
            var hasPendingTargets = false;

            Object.keys(queue).forEach(function(key) {
              var entry = queue[key];
              if (!entry) return;

              if ((now - entry.enqueuedAt) > $COMMAND_TTL_MS) {
                delete queue[key];
                console.warn('[AndroidNativeCommandBridge] Dropped stale command for', entry.target);
                return;
              }

              var target = window[entry.target];
              if (typeof target !== 'function') {
                hasPendingTargets = true;
                return;
              }

              try {
                target.apply(window, entry.args);
                delete queue[key];
              } catch (error) {
                delete queue[key];
                console.error('[AndroidNativeCommandBridge] Command failed for', entry.target, error);
              }
            });

            if (hasPendingTargets) {
              scheduleRetry();
            } else {
              cancelRetry();
            }
          };

          var scheduleFlush = function() {
            if (flushScheduled) return;
            flushScheduled = true;
            setTimeout(flush, 0);
          };

          var wrapHistoryMethod = function(methodName) {
            var historyObject = window.history;
            if (!historyObject) return;

            var originalMethod = historyObject[methodName];
            if (typeof originalMethod !== 'function' || originalMethod.__androidWrapped === true) return;

            var wrappedMethod = function() {
              var result = originalMethod.apply(this, arguments);
              scheduleFlush();
              return result;
            };
            wrappedMethod.__androidWrapped = true;
            historyObject[methodName] = wrappedMethod;
          };

          document.addEventListener('readystatechange', scheduleFlush, true);
          window.addEventListener('load', scheduleFlush, true);
          window.addEventListener('pageshow', scheduleFlush, true);
          window.addEventListener('popstate', scheduleFlush, true);
          window.addEventListener('hashchange', scheduleFlush, true);
          wrapHistoryMethod('pushState');
          wrapHistoryMethod('replaceState');

          window.$COMMAND_BRIDGE_NAME = {
            invoke: function(targetName, args, dedupeKey) {
              var target = window[targetName];
              if (typeof target === 'function') {
                try {
                  target.apply(window, args);
                  return true;
                } catch (error) {
                  console.error('[AndroidNativeCommandBridge] Immediate command failed for', targetName, error);
                  return false;
                }
              }

              queue[dedupeKey || nextQueueKey(targetName)] = {
                target: targetName,
                args: args,
                enqueuedAt: Date.now(),
              };
              scheduleRetry();
              return false;
            },
            flush: flush,
          };

          flush();
        })();
    """.trimIndent()

    private fun jsQuoted(value: String): String = JsonPrimitive(value).toString()

    private fun invokeGlobalFunction(
        functionName: String,
        argsExpression: String,
        dedupeKey: String? = null,
    ): String = buildString {
        append(COMMAND_BRIDGE_SCRIPT)
        append('\n')
        append("window.$COMMAND_BRIDGE_NAME.invoke(")
        append(jsQuoted(functionName))
        append(", ")
        append(argsExpression)
        append(", ")
        append(dedupeKey?.let(::jsQuoted) ?: "null")
        append(");")
    }

    fun ensureCommandBridge(): String = COMMAND_BRIDGE_SCRIPT

    fun updateDeviceToken(token: String): String =
        invokeGlobalFunction(
            functionName = "UpdateDeviceToken",
            argsExpression = "[${jsQuoted(token)}]",
            dedupeKey = "device-token"
        )

    fun updateUserLocation(geoPointJson: String): String =
        invokeGlobalFunction(
            functionName = "UpdateUserLocation",
            argsExpression = "[$geoPointJson]",
            dedupeKey = "user-location"
        )

    fun applyDatePickerValue(requestId: String, value: String): String {
        val escapedRequestId = jsQuoted(requestId)
        val escapedValue = jsQuoted(value)
        return invokeGlobalFunction(
            functionName = "__androidApplyDatePickerValue",
            argsExpression = "[$escapedRequestId, $escapedValue]",
            dedupeKey = "date-picker-apply:$requestId"
        )
    }

    fun cancelDatePicker(requestId: String): String {
        val escapedRequestId = jsQuoted(requestId)
        return invokeGlobalFunction(
            functionName = "__androidCancelDatePicker",
            argsExpression = "[$escapedRequestId]",
            dedupeKey = "date-picker-cancel:$requestId"
        )
    }

    fun applyTimePickerValue(requestId: String, value: String): String {
        val escapedRequestId = jsQuoted(requestId)
        val escapedValue = jsQuoted(value)
        return invokeGlobalFunction(
            functionName = "__androidApplyTimePickerValue",
            argsExpression = "[$escapedRequestId, $escapedValue]",
            dedupeKey = "time-picker-apply:$requestId"
        )
    }

    fun cancelTimePicker(requestId: String): String {
        val escapedRequestId = jsQuoted(requestId)
        return invokeGlobalFunction(
            functionName = "__androidCancelTimePicker",
            argsExpression = "[$escapedRequestId]",
            dedupeKey = "time-picker-cancel:$requestId"
        )
    }

    fun downloadUrl(url: String, fileName: String): String = buildString {
        append(DownloadBridgeScript.SCRIPT)
        append('\n')
        append("window.__androidDownloadUrl && window.__androidDownloadUrl(")
        append(jsQuoted(url))
        append(", ")
        append(jsQuoted(fileName))
        append(");")
    }
}
