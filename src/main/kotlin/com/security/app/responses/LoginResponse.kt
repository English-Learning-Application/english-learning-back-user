package com.security.app.responses

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)