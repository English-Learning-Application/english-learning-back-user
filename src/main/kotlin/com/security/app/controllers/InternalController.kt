package com.security.app.controllers

import com.security.app.model.Message
import com.security.app.requests.UpdateInternalUserProfileRequest
import com.security.app.requests.UpdateUserAchievementsRequest
import com.security.app.services.UserAchievementService
import com.security.app.services.UserService
import com.security.app.utils.toUUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/internal")
class InternalController(
    private val userService: UserService,
    private val userAchievementService: UserAchievementService
) {
    @PostMapping("/update-user-profile")
    fun updateUserProfile(@RequestBody request: UpdateInternalUserProfileRequest): ResponseEntity<Message<Any?>> {
        val response = userService.updateUserInternalProfile(request) ?: return ResponseEntity.badRequest()
            .body(Message.BadRequest("Failed to update user profile"))

        return ResponseEntity.ok(Message.Success("User profile updated successfully", response))
    }

    @PostMapping("/update-user-achievements")
    fun updateUserProgress(
        @RequestBody request: UpdateUserAchievementsRequest
    ): ResponseEntity<Message<Any?>> {
        try {
            userAchievementService.addUserAchievement(request.userId.toUUID(), request.achievementId.toUUID())
            return ResponseEntity.ok(Message.Success("User achievements updated successfully", {}))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest("Failed to update user achievements"))
        }
    }
}