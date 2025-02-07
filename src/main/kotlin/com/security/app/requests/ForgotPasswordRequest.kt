package com.security.app.requests

data class ForgotPasswordRequest(
    val email: String?,
    val phoneNumber: String?
)