package com.security.app.controllers

import com.security.app.model.Message
import com.security.app.requests.*
import com.security.app.services.UserService
import com.security.app.utils.toUUID
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/v1/profile")
class ProfileController(private val userService: UserService) {

    @PutMapping("/swap-subscription-plan")
    fun swapSubscriptionPlan(@RequestBody request: SwapSubscriptionPlanRequest): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        val userSubscription =
            userService.swapUserSubscriptionPlan(userId.toUUID(), request.currentSubscriptionId, request.subscriptionId)
                ?: return ResponseEntity.badRequest()
                    .body(Message.BadRequest<String>("Failed to swap subscription plan"))

        return ResponseEntity.ok(Message.Success("Subscription plan swapped", userSubscription))
    }

    @PostMapping("/update-fcm-token")
    fun updateFcmToken(@RequestBody request: UpdateFcmTokenRequest): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        userService.updateFcmToken(UUID.fromString(userId), request.fcmToken)
            ?: return ResponseEntity.badRequest().body(Message.BadRequest<String>("Failed to update FCM token"))

        return ResponseEntity.ok(Message.Success("FCM token updated") {})
    }

    @PostMapping("/delete-fcm-token")
    fun deleteFcmToken(@RequestBody request: DeleteFcmTokenRequest): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        userService.deleteFcmToken(UUID.fromString(userId), request.fcmTokens ?: emptyList())
            ?: return ResponseEntity.badRequest().body(Message.BadRequest<String>("Failed to delete FCM token"))

        return ResponseEntity.ok(Message.Success("FCM token deleted") {})
    }

    @PostMapping("/send-phone-verification")
    fun verifyPhoneNumberRequest(@RequestBody body: VerifyPhoneNumberRequest): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        userService.sendPhoneNumberVerification(UUID.fromString(userId), body.phoneNumber)
            ?: return ResponseEntity.badRequest().body(Message.BadRequest<String>("Failed to send phone verification"))

        return ResponseEntity.ok(Message.Success("Phone verification sent") {})
    }

    @PostMapping("/verify-phone")
    fun verifyPhoneNumber(@RequestBody request: VerifyEmailOtpRequest): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        userService.verifyPhoneNumber(userId, request.otpCode)
            ?: return ResponseEntity.badRequest().body(Message.BadRequest<String>("Failed to verify phone number"))

        return ResponseEntity.ok(Message.Success("Phone number verified") {})
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

    @PutMapping("/update-avatar")
    fun updateAvatar(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("mediaType") mediaTypeStr: String,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        val user = userService.updateAvatar(UUID.fromString(userId), file, mediaTypeStr, authHeader)
            ?: return ResponseEntity.badRequest().body(Message.BadRequest<String>("Failed to update avatar"))

        return ResponseEntity.ok(Message.Success("Avatar updated", user))
    }

    @GetMapping("/me")
    fun getProfile(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        val user = userService.getUserInfo(UUID.fromString(userId), authHeader)

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

    @PutMapping("/update-profile")
    fun updateProfile(@RequestBody request: UpdateUserProfileRequest): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        val user = userService.updateUserProfile(
            userId.toUUID(),
            request.nativeLanguage,
            request.learningLanguage,
            request.learningTypes,
            request.username
        )

        return if (user != null) {
            ResponseEntity.ok(Message.Success("User profile updated", user))
        } else {
            ResponseEntity.badRequest().body(Message.BadRequest<String>("User not found"))
        }
    }

    @PutMapping("/update-password")
    fun updatePassword(@RequestBody request: UpdatePasswordRequest): ResponseEntity<Message<Boolean>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        val resp = userService.updatePassword(userId.toUUID(), request.oldPassword, request.newPassword)

        return if (resp != null) {
            ResponseEntity.ok(Message.Success("Password updated", resp))
        } else {
            ResponseEntity.badRequest().body(Message.BadRequest("Failed to update password"))
        }
    }
}