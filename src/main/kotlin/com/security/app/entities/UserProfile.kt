package com.security.app.entities

import com.security.app.model.Language
import com.security.app.model.LearningType
import jakarta.persistence.*
import lombok.*
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
@Table(name = "user_profiles")
class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var userProfileId: String = ""

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    var user: User? = null

    @Column(nullable = false)
    var nativeLanguage: Language = Language.VIETNAMESE

    @Column(nullable = false)
    var learningLanguage: Language = Language.ENGLISH

    @Column(nullable = true)
    var learningTypeOne: LearningType? = null

    @Column(nullable = true)
    var learningTypeTwo: LearningType? = null

    @Column(nullable = true)
    var learningTypeThree: LearningType? = null

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    companion object {
        fun fromUser(user: User): UserProfile {
            val userProfile = UserProfile()
            userProfile.user = user
            return userProfile
        }
    }
}