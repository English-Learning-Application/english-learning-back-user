package com.security.app.requests

data class ResetPasswordRequest(
    val email: String?,
    val phoneNumber: String?,
    val otp: String,
    val newPassword: String,
)