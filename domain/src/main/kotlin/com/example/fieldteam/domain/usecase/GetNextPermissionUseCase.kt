package com.example.fieldteam.domain.usecase

import com.example.fieldteam.domain.model.Permission
import org.koin.core.annotation.Factory

@Factory
class GetNextPermissionUseCase {
    operator fun invoke(current: Permission): Permission? {
        return when (current) {
            Permission.LOCATION -> Permission.NOTIFICATIONS
            Permission.NOTIFICATIONS -> null
        }
    }
}
