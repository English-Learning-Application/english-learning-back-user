package com.security.app.services

import com.security.app.model.ListMessage.Success
import com.security.app.model.Message
import com.security.app.requests.SendNotificationMessage
import com.security.app.requests.SendNotificationRequest
import com.security.app.requests.UpdateUserNotificationCredentialRequest
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class NotificationService(
    private val webClient: WebClient
) {
    private val NOTIFICATION_SERVICE_URL = System.getenv("NOTIFICATION_SERVICE_URL")

    fun sendResetPasswordConfirmationEmail(userId: String, email: String, otp: String): Any? {
        return webClient.post()
            .uri("$NOTIFICATION_SERVICE_URL/send")
            .bodyValue(
                SendNotificationRequest(
                    notificationType = "reset_password_email",
                    channels = listOf("mail"),
                    receiverId = userId,
                    message = SendNotificationMessage(
                        otpCode = otp
                    )
                )
            )
            .retrieve()
            .bodyToMono(Success::class.java)
            .subscribe()
    }

    fun sendResetPasswordConfirmationSms(userId: String, phoneNumber: String, otp: String): Any? {
        return webClient.post()
            .uri("$NOTIFICATION_SERVICE_URL/send")
            .bodyValue(
                SendNotificationRequest(
                    notificationType = "reset_password_sms",
                    channels = listOf("sms"),
                    receiverId = userId,
                    message = SendNotificationMessage(
                        otpCode = otp
                    )
                )
            )
            .retrieve()
            .bodyToMono(Success::class.java)
            .subscribe()
    }

    fun updateUserNotificationCredential(request: UpdateUserNotificationCredentialRequest): Any? {
        return webClient.post()
            .uri("$NOTIFICATION_SERVICE_URL/credentials")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Message.Success::class.java)
            .subscribe()
    }

    fun sendEmailVerificationOtp(
        userId: String,
        otp: String,
    ): Any? {
        return webClient.post()
            .uri("$NOTIFICATION_SERVICE_URL/send")
            .bodyValue(
                SendNotificationRequest(
                    notificationType = "otp_confirm_email",
                    channels = listOf("mail"),
                    receiverId = userId,
                    message = SendNotificationMessage(
                        otpCode = otp
                    )
                )
            )
            .retrieve()
            .bodyToMono(Success::class.java)
            .subscribe()
    }

    fun sendPhoneNumberVerificationOtp(userId: String, otp: String): Any? {
        return webClient.post()
            .uri("$NOTIFICATION_SERVICE_URL/send")
            .bodyValue(
                SendNotificationRequest(
                    notificationType = "otp_confirm_sms",
                    channels = listOf("sms"),
                    receiverId = userId,
                    message = SendNotificationMessage(
                        otpCode = otp
                    )
                )
            )
            .retrieve()
            .bodyToMono(Success::class.java)
            .subscribe()
    }

    fun deleteFcmTokens(userId: String, fcmTokens: List<String>): Any? {
        return webClient.post()
            .uri("$NOTIFICATION_SERVICE_URL/credentials/delete-fcm")
            .bodyValue(
                mapOf(
                    "userId" to userId,
                    "fcmTokens" to fcmTokens
                )
            )
            .retrieve()
            .bodyToMono(Message.Success::class.java)
            .subscribe()
    }
}