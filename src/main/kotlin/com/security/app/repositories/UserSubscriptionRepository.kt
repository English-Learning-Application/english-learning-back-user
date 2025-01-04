package com.security.app.repositories

import com.security.app.entities.UserSubscription
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserSubscriptionRepository: JpaRepository<UserSubscription, UUID> {
}