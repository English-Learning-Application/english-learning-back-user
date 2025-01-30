package com.security.app.requests

data class UpdateUserProfileRequest(
    val nativeLanguage: String,
    val learningLanguage: String,
    val learningTypes: List<String>,
    val username: String
)