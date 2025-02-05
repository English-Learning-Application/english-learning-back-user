package com.security.app.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.security.app.model.RegistrationStatus
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import lombok.*
import org.intellij.lang.annotations.Pattern
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@EntityListeners(AuditingEntityListener::class)
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var userId: UUID

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email is required")
    var email: String = ""

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    var username: String = ""

    @Column
    @JsonIgnore
    var password: String = ""

    @Column
    var mediaId: String = ""

    @Column(nullable = true, unique = true)
    var googleId: String? = null

    @Column(nullable = true, unique = true)
    var facebookId: String? = null

    @Column(nullable = true, unique = true)
    @Pattern("^(?:\\+84|0)(?:3\\d{8}|5\\d{8}|7\\d{8}|8\\d{8}|9\\d{8})\$\n")
    var phoneNumber: String? = null

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    var isEmailVerified: Boolean = false

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    var isPhoneNumberVerified: Boolean = false

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userRefreshTokens: List<UserRefreshToken> = mutableListOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userSubscriptions: List<UserSubscription> = mutableListOf()

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL])
    var userProfile: UserProfile? = null

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userAchievements: List<UserAchievement> = mutableListOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userTodos: List<UserTodo> = mutableListOf()

    @Column(nullable = false)
    var registrationStatus: RegistrationStatus = RegistrationStatus.NOT_CONFIRMED
}