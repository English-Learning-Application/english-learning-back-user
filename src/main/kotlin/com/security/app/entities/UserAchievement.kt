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
@Table(name = "user_achievements")
class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var userAchievementId: UUID

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    var user: User? = null

    @Column(nullable = false)
    var achievementId: String = ""

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    companion object {
        fun fromUser(user: User): UserAchievement {
            val userAchievement = UserAchievement()
            userAchievement.user = user
            return userAchievement
        }
    }
}