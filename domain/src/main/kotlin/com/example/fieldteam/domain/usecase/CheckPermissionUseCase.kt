package com.example.fieldteam.domain.usecase

import com.example.fieldteam.domain.model.Permission
import com.example.fieldteam.domain.repository.PermissionRepository
import org.koin.core.annotation.Factory

@Factory
class CheckPermissionUseCase(
    private val repository: PermissionRepository
) {
    suspend operator fun invoke(permission: Permission): Boolean {
        return repository.hasPermission(permission)
    }
}
