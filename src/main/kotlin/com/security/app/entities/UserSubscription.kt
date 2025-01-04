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
@Table(name = "user_subscriptions")
class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var userSubscriptionId: UUID

    @Column(nullable = false)
    var subscriptionId: String = ""

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    var user: User? = null

    @Column(nullable = false)
    var expiryDate: LocalDateTime = LocalDateTime.now()

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    companion object {
        fun fromUser(user: User): UserSubscription {
            val userSubscription = UserSubscription()
            userSubscription.user = user
            return userSubscription
        }
    }
}