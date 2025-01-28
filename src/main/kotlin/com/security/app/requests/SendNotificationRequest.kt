package com.security.app.requests

data class SendNotificationRequest(
    val notificationType: String,
    val channels: List<String>,
    val receiverId: String,
    val message: SendNotificationMessage
)

data class SendNotificationMessage(
    val otpCode: String,
)