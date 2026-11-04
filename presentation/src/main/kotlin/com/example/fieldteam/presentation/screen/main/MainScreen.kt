package com.example.fieldteam.presentation.screen.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import com.example.fieldteam.presentation.R
import com.example.fieldteam.presentation.composable.OnLifecycleEvent
import com.example.fieldteam.presentation.extension.startGoogleMapsNavigation
import com.example.fieldteam.presentation.screen.main.content.LoadingState
import com.example.fieldteam.presentation.screen.main.content.ReadyContent
import com.example.fieldteam.presentation.screen.main.content.WarningContent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
internal fun MainScreen(
    viewModel: MainViewModel,
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val clearText = stringResource(id = R.string.clear)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionRequestFinished()
            if (isGranted) {
                viewModel.onPermissionGranted()
            } else {
                viewModel.onPermissionDenied()
            }
        }
    )

    OnLifecycleEvent(
        event = Lifecycle.Event.ON_RESUME,
        action = { viewModel.onResumed() }
    )

    OnLifecycleEvent(
        event = Lifecycle.Event.ON_PAUSE,
        action = { viewModel.onPaused() }
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SideEffect.LaunchPermission -> {
                viewModel.onPermissionRequestStarted()
                permissionLauncher.launch(sideEffect.permission)
            }

            is SideEffect.LaunchGoogleMapsNavigation -> {
                context.startGoogleMapsNavigation(route = sideEffect.googleMapsRoute) { startFailure ->
                    if (startFailure) {
                        Toast.makeText(
                            context,
                            R.string.no_application_available,
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            }

            is SideEffect.ShowToast -> {
                Toast.makeText(
                    context,
                    sideEffect.messageResId,
                    Toast.LENGTH_LONG,
                ).show()
            }

            is SideEffect.ShowDatePicker -> {
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                val now = LocalDate.now()
                val initialDate = runCatching {
                    sideEffect.currentValue?.let { LocalDate.parse(it, formatter) }
                }.getOrNull() ?: now

                val dialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                        viewModel.onDatePicked(
                            requestId = sideEffect.requestId,
                            selectedValue = formatter.format(selectedDate),
                        )
                    },
                    initialDate.year,
                    initialDate.monthValue - 1,
                    initialDate.dayOfMonth,
                )

                sideEffect.minValue?.let { min ->
                    runCatching {
                        val minDate = LocalDate.parse(min, formatter)
                        dialog.datePicker.minDate = minDate.toEpochDay() * 24L * 60L * 60L * 1000L
                    }
                }

                sideEffect.maxValue?.let { max ->
                    runCatching {
                        val maxDate = LocalDate.parse(max, formatter)
                        dialog.datePicker.maxDate = maxDate.toEpochDay() * 24L * 60L * 60L * 1000L
                    }
                }

                dialog.setButton(
                    DatePickerDialog.BUTTON_NEUTRAL,
                    clearText,
                ) { _, _ ->
                    viewModel.onDatePicked(
                        requestId = sideEffect.requestId,
                        selectedValue = "",
                    )
                }

                dialog.setOnCancelListener {
                    viewModel.onDatePickerCancelled(sideEffect.requestId)
                }

                dialog.show()
            }

            is SideEffect.ShowTimePicker -> {
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val now = LocalTime.now()
                val initialTime = runCatching {
                    sideEffect.currentValue?.let { LocalTime.parse(it, formatter) }
                }.getOrNull() ?: now

                val dialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val selectedTime = LocalTime.of(hourOfDay, minute)
                        viewModel.onTimePicked(
                            requestId = sideEffect.requestId,
                            selectedValue = formatter.format(selectedTime),
                        )
                    },
                    initialTime.hour,
                    initialTime.minute,
                    DateFormat.is24HourFormat(context),
                )

                dialog.setOnCancelListener {
                    viewModel.onTimePickerCancelled(sideEffect.requestId)
                }

                dialog.show()
            }
        }
    }

    MainScreen(
        state = state,
        viewModel = viewModel,
    )
}

@Composable
internal fun MainScreen(
    state: State,
    viewModel: MainViewModel,
) {
    when (state) {
        is State.CheckingPermissions -> LoadingState()

        is State.Warning -> WarningContent(
            permissionState = state.permission,
            onConfirm = viewModel::onDialogConfirmClicked,
        )

        is State.Ready -> ReadyContent(
            webViewHolder = state.webViewHolder,
            isDownloading = state.isDownloading,
        )
    }
}
