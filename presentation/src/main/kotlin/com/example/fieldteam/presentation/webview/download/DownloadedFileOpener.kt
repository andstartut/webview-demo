package com.example.fieldteam.presentation.webview.download

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Base64
import androidx.core.content.FileProvider
import com.example.fieldteam.presentation.webview.logger.webViewDebugErrorLog
import com.example.fieldteam.presentation.webview.logger.webViewDebugLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single
import java.io.BufferedOutputStream
import java.io.File
import java.io.OutputStream
import java.util.concurrent.ConcurrentHashMap

@Single
internal class DownloadedFileOpener(
    private val context: Context,
) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val sessions = ConcurrentHashMap<String, Session>()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    fun notifyDownloadStarted() {
        _isDownloading.value = true
    }

    fun beginSession(
        sessionId: String,
        fileName: String,
        mimeType: String?,
    ): Boolean {
        cancelSession(sessionId)
        return runCatching {
            val dir = File(context.cacheDir, DOWNLOADS_DIR).apply { mkdirs() }
            val safeName = sanitizeFileName(fileName)
            val file = File(dir, safeName)
            val stream = BufferedOutputStream(file.outputStream(), STREAM_BUFFER)
            sessions[sessionId] = Session(
                file = file,
                stream = stream,
                mimeType = resolveMimeType(mimeType, fileName),
            )
            _isDownloading.value = true
            webViewDebugLog(
                "Download session $sessionId started: file=${file.name}, mime=$mimeType"
            )
            true
        }.onFailure { error ->
            webViewDebugErrorLog("Failed to start download session $sessionId: ${error.message}")
        }.getOrDefault(false)
    }

    fun appendChunk(sessionId: String, base64Chunk: String): Boolean {
        val session = sessions[sessionId] ?: run {
            webViewDebugErrorLog("appendChunk: unknown session $sessionId")
            return false
        }
        return runCatching {
            val bytes = Base64.decode(base64Chunk, Base64.NO_WRAP)
            session.stream.write(bytes)
            true
        }.onFailure { error ->
            webViewDebugErrorLog("appendChunk failed for $sessionId: ${error.message}")
            cancelSession(sessionId)
        }.getOrDefault(false)
    }

    fun finishSession(sessionId: String) {
        val session = sessions.remove(sessionId) ?: run {
            webViewDebugErrorLog("finishSession: unknown session $sessionId")
            return
        }
        runCatching {
            session.stream.flush()
            session.stream.close()
        }.onFailure { error ->
            webViewDebugErrorLog("finishSession close failed: ${error.message}")
        }
        _isDownloading.value = false
        launchChooser(session.file, session.mimeType)
    }

    fun cancelSession(sessionId: String) {
        val session = sessions.remove(sessionId) ?: return
        runCatching { session.stream.close() }
        runCatching { session.file.delete() }
        if (sessions.isEmpty()) {
            _isDownloading.value = false
        }
    }

    fun openBase64(
        base64Content: String,
        fileName: String,
        mimeType: String?,
    ) {
        runCatching {
            val payload = base64Content.substringAfter(',', base64Content)
            val bytes = Base64.decode(payload, Base64.DEFAULT)
            val dir = File(context.cacheDir, DOWNLOADS_DIR).apply { mkdirs() }
            val file = File(dir, sanitizeFileName(fileName))
            file.writeBytes(bytes)
            _isDownloading.value = false
            launchChooser(file, resolveMimeType(mimeType, fileName))
        }.onFailure { error ->
            _isDownloading.value = false
            webViewDebugErrorLog("Failed to open downloaded file: ${error.message}")
        }
    }

    private fun sanitizeFileName(fileName: String): String {
        val trimmed = fileName.trim().ifEmpty { DEFAULT_FILE_NAME }
        return trimmed.replace(Regex("[\\\\/:*?\"<>|]"), "_")
    }

    private fun resolveMimeType(mimeType: String?, fileName: String): String {
        if (!mimeType.isNullOrBlank() && mimeType != "application/octet-stream") {
            return mimeType
        }
        return when (fileName.substringAfterLast('.', "").lowercase()) {
            "pdf" -> "application/pdf"
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "csv" -> "text/csv"
            "txt" -> "text/plain"
            else -> "*/*"
        }
    }

    private fun launchChooser(file: File, mimeType: String) {
        mainHandler.post {
            runCatching {
                val authority = "${context.packageName}.fileprovider"
                val uri = FileProvider.getUriForFile(context, authority, file)
                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooser = Intent.createChooser(viewIntent, null).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(chooser)
                webViewDebugLog("Launched chooser for ${file.name} (${file.length()} bytes)")
            }.onFailure { error ->
                webViewDebugErrorLog("Failed to launch chooser: ${error.message}")
            }
        }
    }

    private class Session(
        val file: File,
        val stream: OutputStream,
        val mimeType: String,
    )

    companion object {
        private const val DOWNLOADS_DIR = "downloads"
        private const val DEFAULT_FILE_NAME = "download"
        private const val STREAM_BUFFER = 64 * 1024
    }
}
