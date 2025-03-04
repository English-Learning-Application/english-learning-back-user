package com.security.app.repositories

import com.security.app.entities.UserAchievement
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserAchievementRepository : JpaRepository<UserAchievement, UUID> {
    fun findAllByUserUserIdAndAchievementId(userUserId: UUID, achievementId: String): UserAchievement?
    fun findAllByUser_UserId(userUserId: UUID): List<UserAchievement>
}