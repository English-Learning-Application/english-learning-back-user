package com.security.app.repositories

import com.security.app.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository

interface UserProfileRepository : JpaRepository<UserProfile, String> {
}