package com.security.app.services

import com.nimbusds.jose.shaded.gson.Gson
import com.security.app.model.ListMessage
import com.security.app.model.MediaModel
import com.security.app.repositories.UserRepository
import com.security.app.responses.UserResponse
import com.security.app.utils.toUUID
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class UserDataService(
    private val userRepository: UserRepository,
    private val webClient: WebClient
) {
    private val MEDIA_SERVICE_URL = System.getenv("MEDIA_SERVICE_URL")
    private fun getUserMedia(mediaIds: List<UUID>): List<MediaModel> {
        webClient.get()
            .uri("${MEDIA_SERVICE_URL}/query?q=${mediaIds.joinToString(",")}")
            .retrieve()
            .bodyToMono(ListMessage.Success::class.java)
            .block()
            .let {
                val gson = Gson()
                val listJson = gson.toJson(it?.results)
                val mediaModelList = gson.fromJson(listJson, Array<MediaModel>::class.java).toList()
                return mediaModelList
            }
    }

    fun getUserData(userIds: List<String>): List<UserResponse> {
        val users = userRepository.findAllById(userIds.map { it.toUUID() })
        val userResponses = mutableListOf<UserResponse>()

        /// remove where empty
        val mediaIdList = users.map {
            it.mediaId
        }.filter {
            it.isNotEmpty()
        }.map {
            it.toUUID()
        }

        val mediaModelList =
            if (mediaIdList.isEmpty()) {
                emptyList()
            } else {
                getUserMedia(mediaIdList)
            }
        users.forEach { user ->
            val media = mediaModelList.find { it.mediaId == user.mediaId }
            val userResp = UserResponse.fromUser(user)
            userResp.media = media
            userResponses.add(userResp)
        }

        return userResponses
    }
}