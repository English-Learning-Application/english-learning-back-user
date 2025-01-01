package com.security.app.requests

import jakarta.validation.constraints.NotBlank

class RegisterRequest {
    @NotBlank(message = "Username is required")
    val username: String = ""
    @NotBlank(message = "Email is required")
    val email: String = ""
    @NotBlank(message = "Password is required")
    val password: String = ""
}