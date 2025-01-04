package com.security.app.requests

data class RegistrationCompletionRequest(
    val nativeLanguage: String,
    val learningLanguage: String,
    val learningTypes: List<String>
)