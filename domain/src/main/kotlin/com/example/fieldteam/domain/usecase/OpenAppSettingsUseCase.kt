package com.example.fieldteam.domain.usecase

import com.example.fieldteam.domain.repository.PermissionRepository
import org.koin.core.annotation.Factory

@Factory
class OpenAppSettingsUseCase(
    private val repository: PermissionRepository
) {
    suspend operator fun invoke() {
        repository.openAppSettings()
    }
}
