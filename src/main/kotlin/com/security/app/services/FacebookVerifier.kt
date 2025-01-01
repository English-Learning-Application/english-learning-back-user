package com.security.app.services

import com.security.app.model.FacebookUserData
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Service
class FacebookVerifier(
    private val webClient: WebClient.Builder
) {
    private val facebookDebugUrl = "https://graph.facebook.com/debug_token"
    private val appId = System.getenv()["FACEBOOK_APP_ID"]
    private val appSecret = System.getenv()["FACEBOOK_APP_SECRET"]

    fun verifyAccessToken(accessToken: String): Mono<Boolean> {
        val appAccessToken = "$appId|$appSecret"

        val uri = UriComponentsBuilder.fromHttpUrl(facebookDebugUrl)
            .queryParam("input_token", accessToken)
            .queryParam("access_token", appAccessToken)
            .build()
            .toUriString()

        return webClient.build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono(Map::class.java)
            .mapNotNull { response ->
                val data = response["data"] as Map<*, *>?
                val isValid = data?.get("is_valid") as Boolean? ?: false
                val tokenAppId = data?.get("app_id") as String?
                isValid && tokenAppId == appId
            }
            .onErrorReturn(false)
    }

    fun fetchUserProfile(accessToken: String): Mono<FacebookUserData> {
        return webClient.build()
            .get()
            .uri { uriBuilder ->
                uriBuilder
                    .scheme("https")
                    .host("graph.facebook.com")
                    .path("/me")
                    .queryParam("fields", "id,name,email,picture")
                    .queryParam("access_token", accessToken)
                    .build()
            }
            .retrieve()
            .bodyToMono(FacebookUserData::class.java)
            .onErrorResume { error ->
                Mono.error(IllegalArgumentException("Failed to fetch user data: ${error.message}"))
            }
    }
}
