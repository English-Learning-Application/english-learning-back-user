package com.security.app.model

data class SubscriptionModel(
    var subscriptionId: String,
    var subscriptionName: String,
    var subscriptionDescription: String,
    var subscriptionPrice: Double,
    var subscriptionDuration: Int,
    var subscriptionDurationLength: String,
    var isEnabled: Boolean,
)
