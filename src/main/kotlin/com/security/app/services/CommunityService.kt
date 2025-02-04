package com.security.app.services

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class CommunityService(
    private val webClient: WebClient
) {
    private final val COMMUNITY_SERVICE_URL = System.getenv("COMMUNITY_SERVICE_URL")

    fun updateMessageUser(
        tokenString: String,
        username: String,
        imageUrl: String,
        email: String,
        phoneNumber: String
    ) {
        webClient.post()
            .uri("$COMMUNITY_SERVICE_URL/update")
            .headers { headers ->
                headers.set("Authorization", tokenString)
            }
            .bodyValue(
                mapOf(
                    "username" to username,
                    "imageUrl" to imageUrl,
                    "email" to email,
                    "phoneNumber" to phoneNumber
                )
            )
            .retrieve()
            .bodyToMono(Void::class.java)
            .block()
    }
}