package com.security.app.requests

data class DeleteFcmTokenRequest(
    val fcmTokens: List<String>?
)