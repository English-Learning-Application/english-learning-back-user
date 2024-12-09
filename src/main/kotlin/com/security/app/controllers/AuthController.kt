package com.security.app.controllers

import com.security.app.dao.LoginResponse
import com.security.app.dtos.LoginDTO
import com.security.app.dtos.RegisterDTO
import com.security.app.model.Message
import com.security.app.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody request : RegisterDTO) : ResponseEntity<LoginResponse> {
        val loginResponse = userService.registerUser(
            request
        )

        return ResponseEntity.ok(loginResponse)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginDTO) : ResponseEntity<Any> {
        val loginResponse = userService.loginUser(
            request.email,
            request.password
        )

        return if (loginResponse != null) {
            ResponseEntity.ok(loginResponse)
        } else {
            ResponseEntity.badRequest().body(Message("Invalid credentials"))
        }
    }
}