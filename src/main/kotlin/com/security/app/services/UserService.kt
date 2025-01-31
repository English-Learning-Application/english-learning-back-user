package com.security.app.services

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.gson.Gson
import com.security.app.entities.User
import com.security.app.entities.UserProfile
import com.security.app.entities.UserSubscription
import com.security.app.model.*
import com.security.app.repositories.UserRepository
import com.security.app.repositories.UserSubscriptionRepository
import com.security.app.requests.RegisterRequest
import com.security.app.requests.RegistrationCompletionRequest
import com.security.app.requests.UpdateInternalUserProfileRequest
import com.security.app.requests.UpdateUserNotificationCredentialRequest
import com.security.app.responses.LoginResponse
import com.security.app.responses.UserResponse
import com.security.app.utils.JwtTokenUtils
import com.security.app.utils.toUUID
import jakarta.transaction.Transactional
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userDetailsService: UserDetailsService,
    private val authManager: AuthenticationManager,
    private val jwtTokenUtils: JwtTokenUtils,
    private val googleIdTokenVerifier: GoogleIdTokenVerifier,
    private val passwordEncoder: PasswordEncoder,
    private val facebookVerifier: FacebookVerifier,
    private val userRefreshTokenService: UserRefreshTokenService,
    private val notificationService: NotificationService,
    private val userOtpService: UserOtpService,
    private val userSubscriptionRepository: UserSubscriptionRepository,
    private val webClient: WebClient,
) {

    private val MEDIA_SERVICE_URL = System.getenv("MEDIA_SERVICE_URL")

    private fun saveUser(user: User): User {
        return userRepository.save(user)
    }

    @Transactional
    fun refreshToken(refreshToken: String, deviceId: String): LoginResponse? {
        val userId = jwtTokenUtils.getUserId(refreshToken)
        val user = userId?.toUUID()?.let { userRepository.findByUserId(it) } ?: return null

        val userRefreshToken =
            userRefreshTokenService.getRefreshTokenByUserIdAndDeviceId(user.userId, deviceId) ?: return null

        if (userRefreshToken.refreshToken == refreshToken && !isTokenStillValid(refreshToken)) {
            userRefreshTokenService.removeRefreshToken(userRefreshToken)
            return null
        }

        val newAccessToken = createAccessToken(user.userId)

        return LoginResponse(newAccessToken, userRefreshToken.refreshToken)
    }

    @Transactional
    fun googleSignInUser(idToken: String, deviceId: String): LoginResponse? {
        val idTokenParsed = googleIdTokenVerifier.verify(idToken)
        if (idTokenParsed != null) {
            val payload = idTokenParsed.payload
            val email = payload.email

            val userExists = userRepository.findByEmail(email)

            if (userExists?.googleId != null) {
                val accessToken = createAccessToken(userExists.userId)

                val alreadyExistRefreshToken =
                    userRefreshTokenService.getRefreshTokenByUserIdAndDeviceId(userExists.userId, deviceId)

                if (alreadyExistRefreshToken != null) {
                    val isStillValid = jwtTokenUtils.isTokenStillValid(alreadyExistRefreshToken.refreshToken)
                    if (isStillValid) {
                        return LoginResponse(accessToken, alreadyExistRefreshToken.refreshToken)
                    } else {
                        userRefreshTokenService.removeRefreshToken(alreadyExistRefreshToken)
                    }
                }

                val refreshToken = createRefreshToken(userExists.userId)
                userRefreshTokenService.saveRefreshToken(refreshToken, userExists, deviceId)

                return LoginResponse(accessToken, refreshToken)
            }

            val user = User().let {
                it.email = email
                it.username = email
                it.googleId = payload.subject
                it.userProfile = UserProfile().let { userProfile ->
                    userProfile.user = it
                    userProfile
                }
                it
            }

            val savedUser = saveUser(user)

            val accessToken = createAccessToken(savedUser.userId)
            val refreshToken = createRefreshToken(savedUser.userId)
            userRefreshTokenService.saveRefreshToken(refreshToken, savedUser, deviceId)

            return LoginResponse(accessToken, refreshToken)
        } else {
            return null
        }
    }

    @Transactional
    fun facebookSignInUser(accessToken: String, deviceId: String): LoginResponse? {
        val isValid = facebookVerifier.verifyAccessToken(accessToken).block() ?: false
        if (!isValid) {
            return null
        }

        val facebookUserData = facebookVerifier.fetchUserProfile(accessToken).block() ?: return null
        val email = facebookUserData.email ?: return null

        val existingUser = userRepository.findByEmail(email)
        if (existingUser?.facebookId != null) {
            val aToken = createAccessToken(existingUser.userId)

            val alreadyExistRefreshToken =
                userRefreshTokenService.getRefreshTokenByUserIdAndDeviceId(existingUser.userId, deviceId)

            if (alreadyExistRefreshToken != null) {
                val isStillValid = jwtTokenUtils.isTokenStillValid(alreadyExistRefreshToken.refreshToken)
                if (isStillValid) {
                    return LoginResponse(accessToken, alreadyExistRefreshToken.refreshToken)
                } else {
                    userRefreshTokenService.removeRefreshToken(alreadyExistRefreshToken)
                }
            }

            val refreshToken = createRefreshToken(existingUser.userId)
            userRefreshTokenService.saveRefreshToken(refreshToken, existingUser, deviceId)
            return LoginResponse(aToken, refreshToken)
        }

        val newUser = User().let {
            it.email = email
            it.username = facebookUserData.name ?: email
            it.facebookId = facebookUserData.id
            it.userProfile = UserProfile().let { userProfile ->
                userProfile.user = it
                userProfile
            }
            it
        }
        val savedUser = saveUser(newUser)

        val aToken = createAccessToken(savedUser.userId)
        val refreshToken = createRefreshToken(savedUser.userId)
        userRefreshTokenService.saveRefreshToken(refreshToken, savedUser, deviceId)

        return LoginResponse(aToken, refreshToken)
    }

    @Transactional
    fun registerUser(registerDTO: RegisterRequest, deviceId: String): LoginResponse? {
        val user = User().let {
            it.email = registerDTO.email
            it.username = registerDTO.username
            it.password = passwordEncoder.encode(registerDTO.password)
            it.userProfile = UserProfile().let { userProfile ->
                userProfile.user = it
                userProfile
            }
            it
        }

        val savedUser = saveUser(user)

        val accessToken = createAccessToken(savedUser.userId)
        val refreshToken = createRefreshToken(savedUser.userId)
        userRefreshTokenService.saveRefreshToken(refreshToken, savedUser, deviceId)

        val newUserRefreshToken = userRefreshTokenService.saveRefreshToken(refreshToken, savedUser, deviceId)

        savedUser.userRefreshTokens = mutableListOf(newUserRefreshToken)

        userRepository.save(savedUser)

        return LoginResponse(accessToken, refreshToken)
    }

    fun getUserInfo(userId: UUID): UserResponse? {
        val user = userRepository.findByUserId(userId) ?: return null
        val userResponse = UserResponse.fromUser(user)

        if (user.mediaId.isNotEmpty()) {
            println("Media ID: ${user.mediaId}")
            val mediaModel = getUserMedia(UUID.fromString(user.mediaId))

            userResponse.media = mediaModel ?: return null
        }

        notificationService.updateUserNotificationCredential(
            UpdateUserNotificationCredentialRequest(
                user.userId.toString(),
                user.username,
                user.email,
                user.phoneNumber,
                null
            )
        ) ?: return null

        return userResponse
    }


    @Transactional
    fun loginUser(email: String, password: String, deviceId: String): LoginResponse? {
        val userExist = userRepository.findByEmail(email) ?: return null
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(userExist.userId, password)
        )

        val user = userDetailsService.loadUserByUsername(userExist.userId.toString())

        val userData = userRepository.findByUserId(UUID.fromString(user.username)) ?: return null

        val accessToken = createAccessToken(userData.userId)
        val alreadyExistRefreshToken =
            userRefreshTokenService.getRefreshTokenByUserIdAndDeviceId(userData.userId, deviceId)

        if (alreadyExistRefreshToken != null) {
            val isStillValid = jwtTokenUtils.isTokenStillValid(alreadyExistRefreshToken.refreshToken)
            if (isStillValid) {
                return LoginResponse(accessToken, alreadyExistRefreshToken.refreshToken)
            } else {
                userRefreshTokenService.removeRefreshToken(alreadyExistRefreshToken)
            }
        }

        val refreshToken = createRefreshToken(userData.userId)
        val newUserRefreshToken = userRefreshTokenService.saveRefreshToken(refreshToken, userData, deviceId)

        if (userData.userRefreshTokens.isEmpty()) {
            userData.userRefreshTokens = mutableListOf(newUserRefreshToken)
        } else {
            userData.userRefreshTokens = userData.userRefreshTokens.plus(newUserRefreshToken)
        }

        userRepository.save(userData)

        return LoginResponse(accessToken, refreshToken)
    }

    private fun createAccessToken(userId: UUID): String {
        // Access token expires in 1 hour, duration in milliseconds
        return jwtTokenUtils.generateToken(userId, 3600000)
    }

    private fun createRefreshToken(userId: UUID): String {
        // Refresh token expires in 1 week, duration in milliseconds
        return jwtTokenUtils.generateToken(userId, 604800000)
    }

    private fun isTokenStillValid(token: String): Boolean {
        return jwtTokenUtils.isTokenStillValid(token)
    }

    private fun getUserMedia(mediaId: UUID): MediaModel? {
        val media = webClient.get()
            .uri("${MEDIA_SERVICE_URL}/${mediaId}")
            .retrieve()
            .bodyToMono(Message.Success::class.java)
            .block()

        println("Media: $media")

        return Gson().fromJson(Gson().toJson(media?.data), MediaModel::class.java)
    }

    private fun uploadUserMedia(media: MultipartFile, mediaType: String): MediaModel? {
        val multipartData = MultipartBodyBuilder()
        multipartData.part("file", InputStreamResource(media.inputStream))
            .filename(media.originalFilename ?: "user_avatar_${UUID.randomUUID()}")
        multipartData.part("mediaType", mediaType)
        val mediaItem = webClient.post()
            .uri("${MEDIA_SERVICE_URL}/upload")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(multipartData.build())
            .retrieve()
            .bodyToMono(Message.Success::class.java)
            .block()

        return Gson().fromJson(Gson().toJson(mediaItem?.data), MediaModel::class.java)
    }

    private fun updateUserMedia(mediaId: String, media: MultipartFile, mediaType: String): MediaModel? {
        val multipartData = MultipartBodyBuilder()
        multipartData.part("file", InputStreamResource(media.inputStream))
            .filename(media.originalFilename ?: "user_avatar_${UUID.randomUUID()}")
        multipartData.part("mediaType", mediaType)
        val mediaItem = webClient.put()
            .uri("${MEDIA_SERVICE_URL}/${mediaId}")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(multipartData.build())
            .retrieve()
            .bodyToMono(Message.Success::class.java)
            .block()

        return Gson().fromJson(Gson().toJson(mediaItem?.data), MediaModel::class.java)
    }


    fun registrationCompletion(request: RegistrationCompletionRequest, userId: UUID): UserResponse? {
        val user = userRepository.findByUserId(userId) ?: return null

        var resultUser: UserResponse? = null

        user.userProfile?.let {
            it.nativeLanguage = Language.fromString(request.nativeLanguage)
            it.learningLanguage = Language.fromString(request.learningLanguage)
            val learningTypeRequest = request.learningTypes
            if (learningTypeRequest.getOrNull(0) != null) {
                it.learningTypeOne = LearningType.fromString(learningTypeRequest[0])
            }
            if (learningTypeRequest.getOrNull(1) != null) {
                it.learningTypeTwo = LearningType.fromString(learningTypeRequest[1])
            }
            if (learningTypeRequest.getOrNull(2) != null) {
                it.learningTypeThree = LearningType.fromString(learningTypeRequest[2])
            }

            user.registrationStatus = RegistrationStatus.CONFIRMED

            val savedUser = userRepository.save(user)

            resultUser = UserResponse.fromUser(savedUser)
            if (savedUser.mediaId.isNotEmpty()) {
                val mediaModel = getUserMedia(UUID.fromString(savedUser.mediaId))

                resultUser?.media = mediaModel
            }
        }

        return resultUser
    }

    fun updateFcmToken(userId: UUID, fcmToken: String): Any? {
        val user = userRepository.findByUserId(userId) ?: return null

        notificationService.updateUserNotificationCredential(
            UpdateUserNotificationCredentialRequest(
                user.userId.toString(),
                user.username,
                user.email,
                user.phoneNumber,
                fcmToken
            )
        ) ?: return null

        return true
    }

    fun updateAvatar(userId: UUID, avatar: MultipartFile, mediaType: String): UserResponse? {
        val user = userRepository.findByUserId(userId) ?: return null

        var mediaModel: MediaModel? = null
        mediaModel = if (user.mediaId.isEmpty()) {
            uploadUserMedia(avatar, mediaType)
        } else {
            updateUserMedia(user.mediaId, avatar, mediaType)
        }

        if (mediaModel == null) return null

        user.mediaId = mediaModel.mediaId
        val savedUser = userRepository.save(user)

        val userResponse = UserResponse.fromUser(savedUser)

        userResponse.media = mediaModel

        return userResponse
    }


    fun sendEmailVerification(userId: UUID): Any? {
        val otpResp = userOtpService.createNewOtp(userId.toString()) ?: return null

        notificationService.sendEmailVerificationOtp(userId.toString(), otpResp.otpValue) ?: return null

        return true
    }

    fun sendPhoneNumberVerification(userId: UUID, phoneNumber: String): Any? {
        val user = userRepository.findByUserId(userId) ?: return null

        notificationService.updateUserNotificationCredential(
            UpdateUserNotificationCredentialRequest(
                user.userId.toString(),
                user.username,
                user.email,
                phoneNumber,
                null
            )
        ) ?: return null

        val otpResp = userOtpService.createNewOtp(userId.toString()) ?: return null

        notificationService.sendPhoneNumberVerificationOtp(userId.toString(), otpResp.otpValue) ?: return null

        user.phoneNumber = phoneNumber
        return userRepository.save(user)
    }

    fun verifyEmail(userId: String, otp: String): Any? {
        val otpResp = userOtpService.verifyOtp(userId, otp)

        if (!otpResp) return null

        val user = userRepository.findByUserId(userId.toUUID()) ?: return null

        user.isEmailVerified = true

        userRepository.save(user)

        return true
    }

    fun verifyPhoneNumber(userId: String, otp: String): Any? {
        val otpResp = userOtpService.verifyOtp(userId, otp)

        if (!otpResp) return null

        val user = userRepository.findByUserId(userId.toUUID()) ?: return null

        user.isPhoneNumberVerified = true

        userRepository.save(user)

        return true
    }

    fun updateUserProfile(
        userId: UUID,
        nativeLanguage: String,
        learningLanguage: String,
        learningTypes: List<String>,
        userName: String
    ): UserResponse? {
        val user = userRepository.findByUserId(userId) ?: return null
        val userProfile = user.userProfile ?: return null

        user.username = userName
        userProfile.nativeLanguage = Language.fromString(nativeLanguage)
        userProfile.learningLanguage = Language.fromString(learningLanguage)

        if (learningTypes.getOrNull(0) != null) {
            userProfile.learningTypeOne = LearningType.fromString(learningTypes[0])
        }
        if (learningTypes.getOrNull(1) != null) {
            userProfile.learningTypeTwo = LearningType.fromString(learningTypes[1])
        }
        if (learningTypes.getOrNull(2) != null) {
            userProfile.learningTypeThree = LearningType.fromString(learningTypes[2])
        }

        val savedUser = userRepository.save(user)

        val userResponse = UserResponse.fromUser(savedUser)

        return userResponse
    }

    fun updateUserInternalProfile(request: UpdateInternalUserProfileRequest): Any? {
        val requestType = UpdateUserProfileType.fromString(request.type)

        val user = userRepository.findByUserId(request.userId.toUUID()) ?: return null

        when (requestType) {
            UpdateUserProfileType.SUBSCRIPTION -> {
                request.subscription ?: return null
                val expiredTime = LocalDateTime.now()

                val durationLength = DurationLength.fromServerValue(request.subscription.subscriptionDurationLength)
                when (durationLength) {
                    DurationLength.DAY -> {
                        expiredTime.plusDays(request.subscription.subscriptionDuration.toLong())
                    }

                    DurationLength.WEEK -> {
                        expiredTime.plusWeeks(request.subscription.subscriptionDuration.toLong())
                    }

                    DurationLength.MONTH -> {
                        expiredTime.plusMonths(request.subscription.subscriptionDuration.toLong())
                    }

                    DurationLength.YEAR -> {
                        expiredTime.plusYears(request.subscription.subscriptionDuration.toLong())
                    }

                }

                val userSubscription = UserSubscription().let {
                    it.user = user
                    it.subscriptionId = request.subscription.subscriptionId
                    it.expiryDate = expiredTime
                    it
                }

                val savedUserSubscription = userSubscriptionRepository.save(userSubscription)
                return savedUserSubscription
            }

            UpdateUserProfileType.ACHIEVEMENT -> {
                return true
            }
        }
    }
}