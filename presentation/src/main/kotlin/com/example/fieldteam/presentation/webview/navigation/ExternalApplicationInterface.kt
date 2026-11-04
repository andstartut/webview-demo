package com.example.fieldteam.presentation.webview.navigation

import android.webkit.JavascriptInterface
import com.example.fieldteam.presentation.webview.download.DownloadedFileOpener
import com.example.fieldteam.presentation.webview.event.handler.WebViewEventHandler
import com.example.fieldteam.presentation.webview.event.model.WebViewEvent
import com.example.fieldteam.presentation.webview.logger.webViewDebugLog
import com.example.fieldteam.presentation.webview.navigation.handler.GoogleMapsNavigationHandler
import com.example.fieldteam.presentation.webview.navigation.model.GeoCoordinates
import com.example.fieldteam.presentation.webview.navigation.model.GoogleMapsRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Factory

@Factory
internal class ExternalApplicationInterface(
    private val spaGoogleMapsNavigationHandler: GoogleMapsNavigationHandler,
    private val webViewEventHandler: WebViewEventHandler,
    private val downloadedFileOpener: DownloadedFileOpener,
) : WebAppInterface {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @JavascriptInterface
    override fun onNavigateTo(stopsJson: String) {
        val parsedStops = parseStops(stopsJson)
        if (parsedStops.size < 2) {
            webViewDebugLog(
                "Google Maps navigation request ignored: invalid stops payload: $stopsJson"
            )
            return
        }

        val start = parsedStops.first()
        val end = parsedStops.last()
        val waypoints = if (parsedStops.size > 2) parsedStops.drop(1).dropLast(1) else emptyList()

        webViewDebugLog(
            "Google Maps navigation requested: from (${start.lat}, ${start.lng}) to (${end.lat}, ${end.lng}) " +
                    "via ${waypoints.size} waypoints"
        )

        val request = GoogleMapsRoute(start = start, end = end, waypoints = waypoints)
        coroutineScope.launch {
            spaGoogleMapsNavigationHandler.update(request)
        }
    }

    @JavascriptInterface
    override fun onRequestDatePicker(
        requestId: String,
        currentValue: String?,
        minValue: String?,
        maxValue: String?,
    ) {
        webViewEventHandler.emit(
            WebViewEvent.RequestDatePicker(
                requestId = requestId,
                currentValue = currentValue,
                minValue = minValue,
                maxValue = maxValue,
            )
        )
    }

    @JavascriptInterface
    override fun onRequestTimePicker(
        requestId: String,
        currentValue: String?,
        minValue: String?,
        maxValue: String?,
    ) {
        webViewEventHandler.emit(
            WebViewEvent.RequestTimePicker(
                requestId = requestId,
                currentValue = currentValue,
                minValue = minValue,
                maxValue = maxValue,
            )
        )
    }

    @JavascriptInterface
    override fun onDownloadFile(
        base64Content: String,
        fileName: String,
        mimeType: String?,
    ) {
        webViewDebugLog(
            "onDownloadFile requested: fileName=$fileName, mimeType=$mimeType, " +
                    "base64Length=${base64Content.length}"
        )
        coroutineScope.launch {
            downloadedFileOpener.openBase64(
                base64Content = base64Content,
                fileName = fileName,
                mimeType = mimeType,
            )
        }
    }

    @JavascriptInterface
    override fun onDownloadStart() {
        webViewDebugLog("onDownloadStart")
        downloadedFileOpener.notifyDownloadStarted()
    }

    @JavascriptInterface
    override fun onDownloadBegin(
        sessionId: String,
        fileName: String,
        mimeType: String?,
    ): Boolean {
        webViewDebugLog(
            "onDownloadBegin: session=$sessionId, file=$fileName, mime=$mimeType"
        )
        return downloadedFileOpener.beginSession(sessionId, fileName, mimeType)
    }

    @JavascriptInterface
    override fun onDownloadChunk(sessionId: String, base64Chunk: String): Boolean {
        return downloadedFileOpener.appendChunk(sessionId, base64Chunk)
    }

    @JavascriptInterface
    override fun onDownloadFinish(sessionId: String) {
        webViewDebugLog("onDownloadFinish: session=$sessionId")
        downloadedFileOpener.finishSession(sessionId)
    }

    @JavascriptInterface
    override fun onDownloadAbort(sessionId: String) {
        webViewDebugLog("onDownloadAbort: session=$sessionId")
        downloadedFileOpener.cancelSession(sessionId)
    }

    private fun parseStops(stopsJson: String): List<GeoCoordinates> {
        return runCatching {
            Json.parseToJsonElement(stopsJson).jsonArray.mapIndexedNotNull { index, element ->
                runCatching {
                    val json = element.jsonObject
                    val lat = json["lat"]?.jsonPrimitive?.doubleOrNull
                    val lng = json["lng"]?.jsonPrimitive?.doubleOrNull
                    if (lat != null && lng != null) {
                        GeoCoordinates(lat, lng)
                    } else {
                        null
                    }
                }.onFailure { error ->
                    webViewDebugLog("Failed to parse stop at index $index: ${error.message}")
                }.getOrNull()
            }
        }.onFailure { error ->
            webViewDebugLog("Failed to parse stops JSON: ${error.message}")
        }.getOrDefault(emptyList())
    }
}
