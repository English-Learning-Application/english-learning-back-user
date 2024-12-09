package com.security.app.dao

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)