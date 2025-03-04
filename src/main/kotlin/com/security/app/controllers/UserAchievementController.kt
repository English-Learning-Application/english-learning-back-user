package com.security.app.controllers

import com.security.app.entities.UserAchievement
import com.security.app.model.ListMessage
import com.security.app.services.UserAchievementService
import com.security.app.utils.toUUID
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user-achievement")
class UserAchievementController(
    private val userAchievementService: UserAchievementService
) {
    @GetMapping("/all")
    fun getAllUserAchievements(): ResponseEntity<ListMessage<UserAchievement>> {
        val userId = SecurityContextHolder.getContext().authentication.name
        try {
            val userAchievements = userAchievementService.getUserAchievements(userId.toUUID())
            return ResponseEntity.ok(ListMessage.Success("Success", userAchievements))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(ListMessage.BadRequest("Error"))
        }
    }
}