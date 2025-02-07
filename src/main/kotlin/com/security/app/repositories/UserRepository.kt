package com.security.app.repositories

import com.security.app.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUserId(userId: UUID): User?
    fun findByEmail(email: String): User?
    fun findByPhoneNumber(phoneNumber: String): User?
}