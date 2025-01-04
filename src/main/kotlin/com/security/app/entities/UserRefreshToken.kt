package com.security.app.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.*
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
@Table(name = "user_refresh_tokens")
class UserRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var userRefreshTokenId: UUID

    @Column(nullable = false)
    var deviceId: String = ""

    @Column(nullable = false)
    var refreshToken: String = ""

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    var user: User? = null

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    companion object {
        fun fromUser(user: User): UserRefreshToken {
            val userRefreshToken = UserRefreshToken()
            userRefreshToken.user = user
            return userRefreshToken
        }
    }
}