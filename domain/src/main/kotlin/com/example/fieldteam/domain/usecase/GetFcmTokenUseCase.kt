package com.example.fieldteam.domain.usecase

import com.example.fieldteam.domain.repository.FcmTokenRepository
import org.koin.core.annotation.Factory

@Factory
class GetFcmTokenUseCase(
    private val repository: FcmTokenRepository,
) {
    suspend operator fun invoke() = repository.getToken()
}
