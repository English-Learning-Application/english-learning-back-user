package com.security.app.requests

data class SwapSubscriptionPlanRequest(
    val currentSubscriptionId: String,
    val subscriptionId: String
)