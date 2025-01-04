package com.security.app.repositories

import com.security.app.entities.UserRefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserRefreshTokenRepository : JpaRepository<UserRefreshToken, UUID> {
    @Query("SELECT * FROM user_refresh_tokens WHERE user_id = :userId AND device_id = :deviceId", nativeQuery = true)
    fun findUserRefreshTokenByUserIdAndDeviceId(userId: UUID, deviceId: String) : UserRefreshToken?
}