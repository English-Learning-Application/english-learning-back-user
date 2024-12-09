package com.security.app.controllers

import com.security.app.dao.UserResponse
import com.security.app.entities.User
import com.security.app.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/profile")
class ProfileController(
    private val userService: UserService
) {
    @GetMapping("/me")
    fun getProfile(auth: UsernamePasswordAuthenticationToken): ResponseEntity<UserResponse> {
        val user = userService.getUserInfo(auth.name) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(UserResponse.fromUser(user))
    }
}