package com.security.app.services

import com.security.app.entities.User
import com.security.app.entities.UserRefreshToken
import com.security.app.repositories.UserRefreshTokenRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserRefreshTokenService(
    private val userRefreshTokenRepository: UserRefreshTokenRepository,
) {
    @Transactional
    fun saveRefreshToken(refreshTokenStr: String, user: User, deviceId: String): UserRefreshToken {

        val userRefreshToken = UserRefreshToken().let {
            it.refreshToken = refreshTokenStr
            it.user = user
            it.deviceId = deviceId
            it
        }

        return userRefreshTokenRepository.save(userRefreshToken)
    }

    @Transactional
    fun saveUserRefreshToken(userRefreshToken: UserRefreshToken): UserRefreshToken {
        return userRefreshTokenRepository.save(userRefreshToken)
    }

    @Transactional
    fun removeRefreshToken(userRefreshToken: UserRefreshToken) {
        userRefreshTokenRepository.delete(userRefreshToken)
    }

    fun getRefreshTokenByUserIdAndDeviceId(userId: UUID, deviceId: String): List<UserRefreshToken> {
        return userRefreshTokenRepository.findUserRefreshTokenByUserIdAndDeviceId(userId, deviceId)
    }
}