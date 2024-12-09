package com.security.app.services

import com.security.app.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

class JwtUserDetailService(
        private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)?: throw Exception("User not found")

        return org.springframework.security.core.userdetails.User
                .withUsername(user.email)
                .password(user.password)
                .roles(user.role.name)
                .build()
    }
}