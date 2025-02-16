package com.security.app.controllers

import com.security.app.model.Message
import com.security.app.requests.*
import com.security.app.services.UserService
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val userService: UserService) {
    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestBody request: ForgotPasswordRequest): ResponseEntity<Any> {
        try {
            userService.forgotPassword(request.email, request.phoneNumber)
            return ResponseEntity.ok(Message.Success("Password reset link sent successfully", {}))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest<String>("Invalid email or phone number"))
        }
    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody request: ResetPasswordRequest): ResponseEntity<Any> {
        try {
            userService.resetPassword(request.email, request.phoneNumber, request.otp, request.newPassword)
            return ResponseEntity.ok(Message.Success("Password reset successfully", {}))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest<String>("Invalid email or phone number"))
        }
    }

    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterRequest,
        @RequestHeader("X-Device-Id") deviceId: String
    ): ResponseEntity<Any> {

        try {
            val loginResponse = userService.registerUser(
                request, deviceId
            )

            return ResponseEntity.ok(Message.Success("User registered successfully", loginResponse))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest<String>("Invalid email or phone number"))
        }
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
        @RequestHeader("X-Device-Id") deviceId: String,
        @RequestBody request: RefreshTokenRequest
    ): ResponseEntity<Any> {
        try {
            val resp = userService.refreshToken(request.refreshToken, deviceId)
            return if (resp != null) {
                ResponseEntity.ok(Message.Success("Token refreshed successfully", resp))
            } else {
                ResponseEntity.status(HttpStatusCode.valueOf(401))
                    .body(Message.BadRequest<String>("Invalid refresh token"))
            }
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest<String>("Invalid refresh token"))
        }
    }

    @PostMapping("/google")
    fun googleRegistration(
        @RequestBody request: GoogleSignInRequest,
        @RequestHeader("X-Device-Id") deviceId: String
    ): ResponseEntity<Any> {
        try {
            val resp = userService.googleSignInUser(request.idToken, deviceId)
            return ResponseEntity.ok(Message.Success("User registered successfully", resp))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest<String>("Invalid email or phone number"))
        }
    }

    @PostMapping("/facebook")
    fun facebookRegistration(
        @RequestBody request: FacebookSignInRequest,
        @RequestHeader("X-Device-Id") deviceId: String
    ): ResponseEntity<Any> {
        try {
            val resp = userService.facebookSignInUser(request.accessToken, deviceId)
            return ResponseEntity.ok(Message.Success("User registered successfully", resp))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest<String>("Invalid email or phone number"))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, @RequestHeader("X-Device-Id") deviceId: String): ResponseEntity<Any> {
        val loginResponse = userService.loginUser(
            request.email,
            request.password,
            deviceId
        )

        return if (loginResponse != null) {
            ResponseEntity.ok(Message.Success("User logged in successfully", loginResponse))
        } else {
            ResponseEntity.badRequest().body(Message.BadRequest<String>("Invalid credentials"))
        }
    }
}