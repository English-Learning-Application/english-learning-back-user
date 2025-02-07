package com.security.app.requests

data class UpdateUserAchievementsRequest(
    val userId: String,
    val achievementId: String,
)