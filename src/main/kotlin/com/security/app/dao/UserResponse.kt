package com.security.app.dao

import com.security.app.entities.User

class UserResponse {
    var id: String = ""
    var name: String = ""
    var email: String = ""
    var birthDate: String = ""

    companion object {
        fun fromUser(user: User): UserResponse {
            val userResponse = UserResponse()
            userResponse.id = user.id
            userResponse.name = user.name
            userResponse.email = user.email
            userResponse.birthDate = user.birthDate
            return userResponse
        }
    }
}