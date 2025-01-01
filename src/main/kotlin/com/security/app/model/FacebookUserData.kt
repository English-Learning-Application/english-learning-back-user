package com.security.app.model

data class FacebookUserData(
    val id: String,
    val name: String?,
    val email: String?,
    val picture: Map<String, Any>?
)
