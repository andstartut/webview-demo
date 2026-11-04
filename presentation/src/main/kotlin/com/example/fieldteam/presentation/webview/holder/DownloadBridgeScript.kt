package com.example.fieldteam.presentation.webview.holder

internal object DownloadBridgeScript {
    val SCRIPT = """
        (function() {
          if (window.__androidDownloadBridgeInstalled) return;
          window.__androidDownloadBridgeInstalled = true;

          const getBridge = () => window.ExternalApplication;

          const hasStreamingApi = () => {
            const bridge = getBridge();
            return !!(bridge && bridge.onDownloadBegin
            && bridge.onDownloadChunk
            && bridge.onDownloadFinish);
          };
          const hasLegacyApi = () => {
            const bridge = getBridge();
            return !!(bridge && bridge.onDownloadFile);
          };
          if (!hasStreamingApi() && !hasLegacyApi()) return;

          const CHUNK_SIZE = 192 * 1024;

          const fileNameFromUrl = (url) => {
            try {
              const parsed = new URL(url, window.location.href);
              const last = parsed.pathname.split('/').filter(Boolean).pop();
              return last || '';
            } catch (_) {
              return '';
            }
          };

          const ensureFileName = (name, fallbackUrl, mimeType) => {
            let resolved = (name || '').trim();
            if (!resolved && fallbackUrl) resolved = fileNameFromUrl(fallbackUrl);
            if (!resolved) resolved = 'download';
            if (!/\.[A-Za-z0-9]{1,6}$/.test(resolved)) {
              if (mimeType === 'application/pdf') resolved += '.pdf';
              else if (mimeType && mimeType.startsWith('image/')) {
                resolved += '.' + mimeType.split('/')[1];
              }
            }
            return resolved;
          };

          const bytesToBase64 = (bytes) => {
            let binary = '';
            const sub = 0x8000;
            for (let i = 0; i < bytes.length; i += sub) {
              binary += String.fromCharCode.apply(
                null,
                bytes.subarray(i, Math.min(i + sub, bytes.length))
              );
            }
            return btoa(binary);
          };

          const newSessionId = () =>
            'd_' + Date.now() + '_' + Math.random().toString(36).slice(2);

          const streamBlob = async (blob, fileName) => {
            const bridge = getBridge();
            if (!bridge || !hasStreamingApi()) {
              throw new Error('Streaming bridge unavailable');
            }

            const mime = blob.type || '';
            const resolvedName = ensureFileName(fileName, null, mime);
            const sessionId = newSessionId();

            const started = bridge.onDownloadBegin(
              sessionId,
              resolvedName,
              mime || null
            );
            if (started !== true && started !== 'true') {
              console.error('[AndroidDownloadBridge] onDownloadBegin rejected');
              return;
            }

            try {
              const reader = blob.stream().getReader();
              let buffered = new Uint8Array(0);

              const flushChunk = (chunk) => {
                const base64 = bytesToBase64(chunk);
                const ok = bridge.onDownloadChunk(sessionId, base64);
                if (ok !== true && ok !== 'true') {
                  throw new Error('onDownloadChunk rejected');
                }
              };

              while (true) {
                const { value, done } = await reader.read();
                if (done) break;
                if (!value || value.length === 0) continue;

                if (buffered.length === 0) {
                  buffered = value;
                } else {
                  const merged = new Uint8Array(buffered.length + value.length);
                  merged.set(buffered, 0);
                  merged.set(value, buffered.length);
                  buffered = merged;
                }

                while (buffered.length >= CHUNK_SIZE) {
                  flushChunk(buffered.subarray(0, CHUNK_SIZE));
                  buffered = buffered.subarray(CHUNK_SIZE);
                  await Promise.resolve();
                }
              }

              if (buffered.length > 0) flushChunk(buffered);
              bridge.onDownloadFinish(sessionId);
            } catch (err) {
              console.error('[AndroidDownloadBridge] streaming failed', err);
              try { bridge.onDownloadAbort(sessionId); } catch (_) {}
            }
          };

          const legacyBlob = (blob, fileName) => new Promise((resolve, reject) => {
            const bridge = getBridge();
            if (!bridge || !hasLegacyApi()) {
              reject(new Error('Legacy bridge unavailable'));
              return;
            }

            const reader = new FileReader();
            reader.onload = () => {
              const result = reader.result || '';
              const commaIndex = typeof result === 'string' ? result.indexOf(',') : -1;
              const base64 = commaIndex >= 0 ? result.substring(commaIndex + 1) : result;
              try {
                bridge.onDownloadFile(
                  base64,
                  ensureFileName(fileName, null, blob.type),
                  blob.type || null
                );
                resolve();
              } catch (e) {
                reject(e);
              }
            };
            reader.onerror = () => reject(reader.error);
            reader.readAsDataURL(blob);
          });

          const dispatchBlob = (blob, fileName) => hasStreamingApi()
            ? streamBlob(blob, fileName)
            : legacyBlob(blob, fileName);

          const handleUrl = async (url, fileName) => {
            if (!url) return false;
            try {
              const response = await fetch(url, { credentials: 'include' });
              if (!response.ok) return false;
              const blob = await response.blob();
              await dispatchBlob(blob, fileName);
              return true;
            } catch (err) {
              console.error('[AndroidDownloadBridge] fetch failed for', url, err);
              return false;
            }
          };

          const notifyStart = () => {
            const bridge = getBridge();
            if (!bridge) return;
            try { bridge.onDownloadStart && bridge.onDownloadStart(); } catch (_) {}
          };

          const interceptAnchor = (anchor, event) => {
            if (!anchor || anchor.dataset.androidDownloadBypass === '1') return;
            const hasDownload = anchor.hasAttribute('download');
            const href = anchor.getAttribute('href') || '';
            if (!hasDownload && !/^blob:|^data:/.test(href)) return;
            if (!href) return;

            event.preventDefault();
            event.stopPropagation();
            notifyStart();
            const fileName = anchor.getAttribute('download') || '';
            handleUrl(href, fileName);
          };

          document.addEventListener('click', (event) => {
            const anchor = event.target && event.target.closest
              ? event.target.closest('a[href]')
              : null;
            if (anchor) interceptAnchor(anchor, event);
          }, true);

          window.__androidDownloadBlob = (blob, fileName, mimeType) => {
            notifyStart();
            const wrapped = blob instanceof Blob
              ? blob
              : new Blob([blob], { type: mimeType || 'application/octet-stream' });
            return dispatchBlob(wrapped, fileName);
          };

          window.__androidDownloadUrl = (url, fileName) => {
            notifyStart();
            return handleUrl(url, fileName);
          };
        })();
    """.trimIndent()
}
