package com.security.app.controllers

import com.security.app.model.ListMessage
import com.security.app.responses.UserResponse
import com.security.app.services.UserDataService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userDataService: UserDataService,
) {
    @GetMapping("/data")
    fun getUserListData(
        @RequestParam userIds: List<String>
    ): ResponseEntity<ListMessage<UserResponse>> {
        val userResponse = userDataService.getUserData(userIds)
        return ResponseEntity.ok(ListMessage.Success("User data fetched successfully", userResponse))
    }
}