package com.security.app.repositories

import com.security.app.entities.UserSubscription
import org.springframework.data.jpa.repository.JpaRepository

interface UserSubscriptionRepository: JpaRepository<UserSubscription, String> {
}