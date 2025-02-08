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
@Table(name = "user_otps")
class UserOTP {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var userOTPId: UUID

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    var user: User? = null

    @Column(nullable = false)
    var otpValue: String = ""

    @Column(nullable = false)
    var expiryDate: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    var isUsed: Boolean = false

    // Type of OTP (e.g. forgot-password, email-verification)
    @Column(nullable = false, length = 50, columnDefinition = "varchar(50) default ''")
    var type: String = ""

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    companion object {
        fun fromUser(user: User): UserOTP {
            val userOTP = UserOTP()
            userOTP.user = user
            return userOTP
        }
    }
}