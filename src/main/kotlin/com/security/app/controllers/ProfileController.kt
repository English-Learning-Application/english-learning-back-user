package com.security.app.controllers

import com.security.app.model.Message
import com.security.app.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/profile")
class ProfileController(private val userService: UserService) {

    @GetMapping("/me")
    fun getProfile() : ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication.name

        val user = userService.getUserInfo(email)

        return if(user != null) {
            ResponseEntity.ok(Message.Success("User found", user))
        } else {
            ResponseEntity.badRequest().body(Message.BadRequest<String>("User not found"))
        }
    }
}