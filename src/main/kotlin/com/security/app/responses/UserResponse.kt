package com.security.app.responses

import com.security.app.entities.*
import com.security.app.model.MediaModel
import com.security.app.model.RegistrationStatus
import java.util.*

class UserResponse {
    lateinit var userId: UUID

    var email: String = ""

    var username: String = ""

    var media: MediaModel? = null

    var googleId: String? = null

    var facebookId: String? = null

    var phoneNumber: String? = null

    var userRefreshTokens: List<UserRefreshToken> = mutableListOf()

    var userSubscriptions: List<UserSubscription> = mutableListOf()

    var userProfile: UserProfile? = null

    var userAchievements: List<UserAchievement> = mutableListOf()

    var registrationStatus: RegistrationStatus = RegistrationStatus.NOT_CONFIRMED

    companion object {
        fun fromUser(user: User) : UserResponse {
            return UserResponse().apply {
                userId = user.userId
                email = user.email
                username = user.username
                googleId = user.googleId
                facebookId = user.facebookId
                phoneNumber = user.phoneNumber
                userRefreshTokens = user.userRefreshTokens
                userSubscriptions = user.userSubscriptions
                userProfile = user.userProfile
                userAchievements = user.userAchievements
                registrationStatus = user.registrationStatus
            }
        }
    }
}