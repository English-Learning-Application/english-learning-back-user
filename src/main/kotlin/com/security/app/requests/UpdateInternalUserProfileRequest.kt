package com.security.app.requests

import com.security.app.model.SubscriptionModel

data class UpdateInternalUserProfileRequest(
    val type: String,
    val userId: String,
    val subscription: SubscriptionModel?
)