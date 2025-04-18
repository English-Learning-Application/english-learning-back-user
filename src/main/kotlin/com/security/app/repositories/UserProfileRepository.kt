package com.security.app.repositories

import com.security.app.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserProfileRepository : JpaRepository<UserProfile, UUID> {
}