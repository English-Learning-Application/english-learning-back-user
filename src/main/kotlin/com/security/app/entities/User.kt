package com.security.app.entities

import jakarta.persistence.*
import lombok.*
import org.intellij.lang.annotations.Pattern
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

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
    var userId: String = ""

    @Column(unique = true)
    var email: String = ""

    @Column(unique = true)
    var username: String = ""

    @Column
    var password: String = ""

    @Column
    var mediaId: String = ""

    @Column(nullable = true, unique = true)
    var googleId: String = ""

    @Column(nullable = true, unique = true)
    var facebookId: String = ""

    @Column(nullable = true, unique = true)
    @Pattern("^(?:\\+84|0)(?:3\\d{8}|5\\d{8}|7\\d{8}|8\\d{8}|9\\d{8})\$\n")
    var phoneNumber: String = ""

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

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userProfile: UserProfile? = null

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userAchievements: List<UserAchievement> = mutableListOf()
}