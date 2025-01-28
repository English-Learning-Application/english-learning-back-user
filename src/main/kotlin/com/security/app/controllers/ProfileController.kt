package com.security.app.controllers

import com.security.app.model.Message
import com.security.app.requests.RegistrationCompletionRequest
import com.security.app.requests.UpdateFcmTokenRequest
import com.security.app.requests.VerifyEmailOtpRequest
import com.security.app.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/profile")
class ProfileController(private val userService: UserService) {

    @PostMapping("/update-fcm-token")
    fun updateFcmToken(@RequestBody request: UpdateFcmTokenRequest): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        userService.updateFcmToken(UUID.fromString(userId), request.fcmToken)
            ?: return ResponseEntity.badRequest().body(Message.BadRequest<String>("Failed to update FCM token"))

        return ResponseEntity.ok(Message.Success("FCM token updated") {})
    }

    @PostMapping("/send-email-verification")
    fun sendEmailVerification(): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        userService.sendEmailVerification(UUID.fromString(userId))
            ?: return ResponseEntity.badRequest().body(Message.BadRequest<String>("Failed to send email verification"))

        return ResponseEntity.ok(Message.Success("Email verification sent") {})
    }

    @PostMapping("/verify-email")
    fun verifyEmail(@RequestBody request: VerifyEmailOtpRequest): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        userService.verifyEmail(userId, request.otpCode)
            ?: return ResponseEntity.badRequest().body(Message.BadRequest<String>("Failed to verify email"))

        return ResponseEntity.ok(Message.Success("Email verified") {})
    }

    @GetMapping("/me")
    fun getProfile(): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        val user = userService.getUserInfo(UUID.fromString(userId))

        return if (user != null) {
            ResponseEntity.ok(Message.Success("User found", user))
        } else {
            ResponseEntity.badRequest().body(Message.BadRequest<String>("User not found"))
        }
    }

    @PostMapping("/registration/completion")
    fun completeRegistration(@RequestBody request: RegistrationCompletionRequest): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        val user = userService.registrationCompletion(request, UUID.fromString(userId))

        return if (user != null) {
            ResponseEntity.ok(Message.Success("User registration completed", user))
        } else {
            ResponseEntity.badRequest().body(Message.BadRequest<String>("User not found"))
        }
    }
}