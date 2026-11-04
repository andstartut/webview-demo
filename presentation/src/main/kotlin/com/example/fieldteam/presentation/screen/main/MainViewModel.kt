@file:OptIn(OrbitExperimental::class, ExperimentalCoroutinesApi::class)

package com.example.fieldteam.presentation.screen.main

import androidx.lifecycle.ViewModel
import com.example.fieldteam.domain.usecase.CheckPermissionUseCase
import com.example.fieldteam.domain.usecase.GetFcmTokenUseCase
import com.example.fieldteam.domain.usecase.GetFilteredLocationUpdatesUseCase
import com.example.fieldteam.domain.usecase.GetNextPermissionUseCase
import com.example.fieldteam.domain.usecase.OpenAppSettingsUseCase
import com.example.fieldteam.presentation.R
import com.example.fieldteam.presentation.screen.main.mapper.toDomain
import com.example.fieldteam.presentation.screen.main.mapper.toPresentation
import com.example.fieldteam.presentation.webview.download.DownloadedFileOpener
import com.example.fieldteam.presentation.webview.event.handler.WebViewEventHandler
import com.example.fieldteam.presentation.webview.event.model.WebViewEvent
import com.example.fieldteam.presentation.webview.holder.WebViewHolder
import com.example.fieldteam.presentation.webview.logger.webViewDebugLog
import com.example.fieldteam.presentation.webview.navigation.handler.GoogleMapsNavigationHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container

@KoinViewModel
internal class MainViewModel(
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val getNextPermissionUseCase: GetNextPermissionUseCase,
    private val openAppSettingsUseCase: OpenAppSettingsUseCase,
    private val googleMapsNavigationHandler: GoogleMapsNavigationHandler,
    private val webViewHolder: WebViewHolder,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val getFilteredLocationUpdatesUseCase: GetFilteredLocationUpdatesUseCase,
    private val webViewEventHandler: WebViewEventHandler,
    private val downloadedFileOpener: DownloadedFileOpener,
) : ViewModel(), ContainerHost<State, SideEffect> {
    override val container = container<State, SideEffect>(State.CheckingPermissions())

    init {
        observeGoogleMapsNavigation()
        observeWebViewEvents()
        observeDownloadProgress()
        observeLocationUpdatesWhenReady()
    }

    private fun observeWebViewEvents() = intent {
        webViewEventHandler.events.collect { event ->
            runOn<State.Ready> {
                when (event) {
                    is WebViewEvent.PageFinished -> onWebViewReady()
                    is WebViewEvent.ExternalNavigationError -> onExternalNavigationError()
                    is WebViewEvent.RequestDatePicker -> onRequestDatePicker(event)
                    is WebViewEvent.RequestTimePicker -> onRequestTimePicker(event)
                }
            }
        }
    }

    private suspend fun onExternalNavigationError() = withContext(Dispatchers.Main) {
        val hasBack = webViewHolder.goBack()
        if (hasBack) {
            intent {
                postSideEffect(SideEffect.ShowToast(R.string.external_navigation_error))
            }
        }
    }

    private fun observeGoogleMapsNavigation() = intent {
        googleMapsNavigationHandler.data.collect { route ->
            runOn<State.Ready> {
                postSideEffect(
                    SideEffect.LaunchGoogleMapsNavigation(googleMapsRoute = route)
                )
            }
        }
    }

    fun onPermissionRequestStarted() = intent {
        runOn<State.CheckingPermissions> {
            reduce { state.copy(isPermissionRequestActive = true) }
        }
    }

    fun onPermissionRequestFinished() = intent {
        runOn<State.CheckingPermissions> {
            reduce { state.copy(isPermissionRequestActive = false) }
        }
    }

    private fun checkAndRequestPermission() = intent {
        runOn<State.CheckingPermissions> {
            if (state.isPermissionRequestActive) return@runOn

            val hasPermission = checkPermissionUseCase(state.permission.toDomain())
            if (hasPermission) {
                onPermissionGranted()
            } else {
                postSideEffect(SideEffect.LaunchPermission(state.permission.id))
            }
        }
    }

    fun onPermissionGranted() = intent {
        runOn<State.CheckingPermissions> {
            val nextPermission = getNextPermissionUseCase(current = state.permission.toDomain())

            if (nextPermission != null) {
                val nextPermissionState = nextPermission.toPresentation()
                reduce { state.copy(permission = nextPermissionState) }
                postSideEffect(SideEffect.LaunchPermission(nextPermissionState.id))
            } else {
                reduce { State.Ready(webViewHolder = webViewHolder) }
            }
        }
    }

    fun onPermissionDenied() = intent {
        runOn<State.CheckingPermissions> {
            reduce { State.Warning(permission = state.permission) }
        }
    }

    fun onDialogConfirmClicked() = intent {
        openAppSettingsUseCase()
    }

    fun onResumed() = intent {
        webViewHolder.onResume()
        checkAndRequestPermission()
    }

    fun onPaused() = intent {
        runOn<State.Warning> {
            reduce { State.CheckingPermissions(state.permission) }
        }
    }

    private suspend fun onWebViewReady() {
        webViewHolder.installSpaCommandBridge()
        webViewHolder.injectDateTimeInputBridge()
        webViewHolder.injectDownloadBridge()
        getFcmTokenUseCase()?.let {
            webViewHolder.updateDeviceToken(it)
        }
    }

    private fun onRequestDatePicker(event: WebViewEvent.RequestDatePicker) = intent {
        postSideEffect(
            SideEffect.ShowDatePicker(
                requestId = event.requestId,
                currentValue = event.currentValue,
                minValue = event.minValue,
                maxValue = event.maxValue,
            )
        )
    }

    private fun onRequestTimePicker(event: WebViewEvent.RequestTimePicker) = intent {
        postSideEffect(
            SideEffect.ShowTimePicker(
                requestId = event.requestId,
                currentValue = event.currentValue,
                minValue = event.minValue,
                maxValue = event.maxValue,
            )
        )
    }

    fun onDatePicked(requestId: String, selectedValue: String) = intent {
        webViewHolder.applyDatePickerValue(requestId, selectedValue)
    }

    fun onDatePickerCancelled(requestId: String) = intent {
        webViewHolder.cancelDatePicker(requestId)
    }

    fun onTimePicked(requestId: String, selectedValue: String) = intent {
        webViewHolder.applyTimePickerValue(requestId, selectedValue)
    }

    fun onTimePickerCancelled(requestId: String) = intent {
        webViewHolder.cancelTimePicker(requestId)
    }

    private fun observeDownloadProgress() = intent {
        downloadedFileOpener.isDownloading.collect { isDownloading ->
            webViewDebugLog("Download state update: isDownloading=$isDownloading")
            runOn<State.Ready> {
                reduce { state.copy(isDownloading = isDownloading) }
            }
        }
    }

    private fun observeLocationUpdatesWhenReady() = intent {
        getFilteredLocationUpdatesUseCase().collect { geoPoint ->
            runOn<State.Ready> {
                geoPoint?.let { webViewHolder.updateUserLocation(it) }
            }
        }
    }
}
