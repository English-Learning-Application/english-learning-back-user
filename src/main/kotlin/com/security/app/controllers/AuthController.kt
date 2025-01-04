package com.security.app.controllers

import com.security.app.model.Message
import com.security.app.requests.FacebookSignInRequest
import com.security.app.requests.GoogleSignInRequest
import com.security.app.requests.LoginRequest
import com.security.app.requests.RegisterRequest
import com.security.app.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody request : RegisterRequest, @RequestHeader("X-Device-Id") deviceId: String) : ResponseEntity<Any> {

        try{
            val loginResponse = userService.registerUser(
                request, deviceId
            )

            return ResponseEntity.ok(Message.Success("User registered successfully", loginResponse))
        }
        catch(e: Exception){
            return ResponseEntity.badRequest().body(Message.BadRequest<String>(e.message.toString()))
        }
    }

    @PostMapping("/google")
    fun googleRegistration(@RequestBody request: GoogleSignInRequest, @RequestHeader("X-Device-Id") deviceId: String) : ResponseEntity<Any> {
        try {
            val resp = userService.googleSignInUser(request.idToken, deviceId)
            return ResponseEntity.ok(Message.Success("User registered successfully", resp))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest<String>(e.message.toString()))
        }
    }

    @PostMapping("/facebook")
    fun facebookRegistration(@RequestBody request: FacebookSignInRequest, @RequestHeader("X-Device-Id") deviceId: String) : ResponseEntity<Any> {
        try {
            val resp = userService.facebookSignInUser(request.accessToken, deviceId)
            return ResponseEntity.ok(Message.Success("User registered successfully", resp))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest<String>(e.message.toString()))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, @RequestHeader("X-Device-Id") deviceId: String) : ResponseEntity<Any> {
        println("Login request received")
        val loginResponse = userService.loginUser(
            request.email,
            request.password,
            deviceId
        )

        return if (loginResponse != null) {
            ResponseEntity.ok(Message.Success("User logged in successfully", loginResponse))
        } else {
            ResponseEntity.badRequest().body(Message.BadRequest<String>("Invalid credentials"))
        }
    }
}