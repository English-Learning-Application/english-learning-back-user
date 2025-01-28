package com.security.app.requests

data class VerifyEmailOtpRequest(
    val otpCode: String,
)