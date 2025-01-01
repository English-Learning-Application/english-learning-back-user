package com.security.app.services

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.security.app.entities.User
import com.security.app.repositories.UserRepository
import com.security.app.requests.RegisterRequest
import com.security.app.responses.LoginResponse
import com.security.app.utils.JwtTokenUtils
import jakarta.transaction.Transactional
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository,
                  private val userDetailsService: UserDetailsService,
                  private val authManager: AuthenticationManager,
                  private val jwtTokenUtils: JwtTokenUtils,
                  private val googleIdTokenVerifier: GoogleIdTokenVerifier,
       private val passwordEncoder: PasswordEncoder,
        private val facebookVerifier: FacebookVerifier
    ) {

    @Transactional
    fun saveUser(user : User) : User {
        return userRepository.save(user)
    }

    @Transactional
    fun googleSignInUser(idToken: String) : LoginResponse? {
        val idTokenParsed = googleIdTokenVerifier.verify(idToken)
        if(idTokenParsed != null) {
            val payload = idTokenParsed.payload
            val email = payload.email

            val userExists = userRepository.findByEmail(email)

            if(userExists?.googleId != null) {
                val accessToken = createAccessToken(email)
                val refreshToken = createRefreshToken(email)

                return LoginResponse(accessToken, refreshToken)
            }

            val user = User().let {
                it.email = email
                it.username = email
                it.googleId = payload.subject
                it
            }

            saveUser(user)

            val accessToken = createAccessToken(email)
            val refreshToken = createRefreshToken(email)

            return LoginResponse(accessToken, refreshToken)
        }
        else{
            return null
        }
    }

    @Transactional
    fun facebookSignInUser(accessToken: String) : LoginResponse? {
        val isValid = facebookVerifier.verifyAccessToken(accessToken).block() ?: false
        if (!isValid) {
            return null
        }

        val facebookUserData = facebookVerifier.fetchUserProfile(accessToken).block() ?: return null
        val email = facebookUserData.email ?: return null

        val existingUser = userRepository.findByEmail(email)
        if (existingUser?.facebookId != null) {
            val aToken = createAccessToken(email)
            val refreshToken = createRefreshToken(email)
            return LoginResponse(aToken, refreshToken)
        }

        val newUser = User().apply {
            this.email = email
            this.username = facebookUserData.name ?: email
            this.facebookId = facebookUserData.id
        }
        saveUser(newUser)

        val aToken = createAccessToken(email)
        val refreshToken = createRefreshToken(email)
        return LoginResponse(aToken, refreshToken)
    }

    @Transactional
    fun registerUser(registerDTO: RegisterRequest) : LoginResponse? {
        val user = User().let {
            it.email = registerDTO.email
            it.username = registerDTO.username
            it.password = passwordEncoder.encode(registerDTO.password)
            it
        }

        val savedUser = saveUser(user)

        val accessToken = createAccessToken(savedUser.email)
        val refreshToken = createRefreshToken(savedUser.email)

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