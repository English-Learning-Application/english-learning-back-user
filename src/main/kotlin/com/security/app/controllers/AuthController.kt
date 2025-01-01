package com.security.app.controllers

import com.security.app.model.Message
import com.security.app.requests.FacebookSignInRequest
import com.security.app.requests.GoogleSignInRequest
import com.security.app.requests.LoginRequest
import com.security.app.requests.RegisterRequest
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
    fun register(@RequestBody request : RegisterRequest) : ResponseEntity<Any> {
        try{
            val loginResponse = userService.registerUser(
                request
            )

            return ResponseEntity.ok(Message.Success("User registered successfully", loginResponse))
        }
        catch(e: Exception){
            return ResponseEntity.badRequest().body(Message.BadRequest<String>(e.message.toString()))
        }
    }

    @PostMapping("/google")
    fun googleRegistration(@RequestBody request: GoogleSignInRequest) : ResponseEntity<Any> {
        try {
            val resp = userService.googleSignInUser(request.idToken)
            return ResponseEntity.ok(Message.Success("User registered successfully", resp))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest<String>(e.message.toString()))
        }
    }

    @PostMapping("/facebook")
    fun facebookRegistration(@RequestBody request: FacebookSignInRequest) : ResponseEntity<Any> {
        try {
            val resp = userService.facebookSignInUser(request.accessToken)
            return ResponseEntity.ok(Message.Success("User registered successfully", resp))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest<String>(e.message.toString()))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest) : ResponseEntity<Any> {
        val loginResponse = userService.loginUser(
            request.email,
            request.password
        )

        return if (loginResponse != null) {
            ResponseEntity.ok(Message.Success("User logged in successfully", loginResponse))
        } else {
            ResponseEntity.badRequest().body(Message.BadRequest<String>("Invalid credentials"))
        }
    }
}