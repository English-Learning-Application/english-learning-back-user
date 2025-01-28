package com.security.app.requests

data class UpdateUserNotificationCredentialRequest(
    val userId: String,
    val username: String? = null,
    val userEmailAddress: String? = null,
    val userPhoneNumber: String? = null,
    val userFcmToken: String? = null
)