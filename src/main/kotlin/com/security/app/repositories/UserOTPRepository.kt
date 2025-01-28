package com.security.app.repositories

import com.security.app.entities.UserOTP
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserOTPRepository : JpaRepository<UserOTP, UUID> {
    fun findAllByUserUserIdOrderByCreatedAtDesc(userId: UUID): List<UserOTP>
}