package com.security.app.services

import com.security.app.dao.LoginResponse
import com.security.app.dtos.RegisterDTO
import com.security.app.entities.User
import com.security.app.repositories.UserRepository
import com.security.app.utils.JwtTokenUtils
import io.jsonwebtoken.Jwts
import jakarta.transaction.Transactional
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository,
                  private val userDetailsService: UserDetailsService,
                  private val authManager: AuthenticationManager,
                    private val jwtTokenUtils: JwtTokenUtils
    ) {
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    fun saveUser(user : User) : User {
        return userRepository.save(user)
    }

    @Transactional
    fun registerUser(registerDTO: RegisterDTO) : LoginResponse? {
        val newUser = User().apply {
            email = registerDTO.email
            password = passwordEncoder.encode(registerDTO.password)
        }

        val user = saveUser(newUser)

        println(user)

        val accessToken = createAccessToken(newUser.email)
        val refreshToken = createRefreshToken(newUser.email)

        return LoginResponse(accessToken, refreshToken)
    }

    fun getUserInfo(email: String) : User? {
        return userRepository.findByEmail(email)
    }


    fun loginUser(email: String, password: String) : LoginResponse? {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(email, password)
        )

        val user = userDetailsService.loadUserByUsername(email)

        val accessToken = createAccessToken(user.username)
        val refreshToken = createRefreshToken(user.username)

        return LoginResponse(accessToken, refreshToken)
    }

    private fun createAccessToken(email: String) : String {
        // Access token expires in 1 hour, duration in milliseconds
        return jwtTokenUtils.generateToken(email, 3600000)
    }

    private fun createRefreshToken(email: String) : String {
        // Refresh token expires in 1 week, duration in milliseconds
        return jwtTokenUtils.generateToken(email, 604800000)
    }
}