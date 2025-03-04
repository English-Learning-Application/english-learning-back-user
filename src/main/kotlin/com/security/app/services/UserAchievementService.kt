package com.security.app.services

import com.security.app.entities.UserAchievement
import com.security.app.repositories.UserAchievementRepository
import com.security.app.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserAchievementService(
    private val userAchievementRepository: UserAchievementRepository,
    private val userRepository: UserRepository,
) {
    fun addUserAchievement(userId: UUID, achievementId: UUID) {
        val existingUserAchievement =
            userAchievementRepository.findAllByUserUserIdAndAchievementId(userId, achievementId.toString())
        if (existingUserAchievement != null) {
            return
        }
        val user = userRepository.findByUserId(userId) ?: return
        val userAchievement = UserAchievement().let {
            it.user = user
            it.achievementId = achievementId.toString()
            it
        }
        userAchievementRepository.save(userAchievement)
    }

    fun getUserAchievements(userId: UUID): List<UserAchievement> {
        return userAchievementRepository.findAllByUser_UserId(userId)
    }
}