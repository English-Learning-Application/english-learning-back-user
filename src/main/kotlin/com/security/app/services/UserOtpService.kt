package com.security.app.services

import com.security.app.entities.UserOTP
import com.security.app.repositories.UserOTPRepository
import com.security.app.repositories.UserRepository
import com.security.app.utils.isAfterNow
import com.security.app.utils.toUUID
import org.springframework.stereotype.Service

@Service
class UserOtpService(
    private val userOTPRepository: UserOTPRepository,
    private val userRepository: UserRepository
) {
    fun createNewOtp(userId: String): UserOTP? {
        val user = userRepository.findById(userId.toUUID())

        if (user.isPresent) {
            val userOTP = UserOTP().let {
                it.user = user.get()
                /// Generate a random 4 digit OTP from 0000 to 9999
                it.otpValue = "%04d".format((0..9999).random())
                it.expiryDate = it.expiryDate.plusMinutes(5)
                it.isUsed = false
                it
            }
            return userOTPRepository.save(userOTP)
        } else {
            return null
        }
    }

    fun verifyOtp(userId: String, otp: String): Boolean {
        val user = userRepository.findById(userId.toUUID())
        if (user.isPresent) {
            val userOTPs = userOTPRepository.findAllByUserUserIdOrderByCreatedAtDesc(userId.toUUID())
            userOTPs.forEach {
                if (it.otpValue == otp && !it.isUsed && it.expiryDate.isAfterNow()) {
                    it.isUsed = true
                    userOTPRepository.save(it)
                    return true
                }
            }
        }
        return false
    }
}