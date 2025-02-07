package com.security.app.requests

data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)