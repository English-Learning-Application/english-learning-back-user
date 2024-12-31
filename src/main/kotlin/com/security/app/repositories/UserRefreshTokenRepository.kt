package com.security.app.repositories

import com.security.app.entities.UserRefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface UserRefreshTokenRepository : JpaRepository<UserRefreshToken, String> {
}