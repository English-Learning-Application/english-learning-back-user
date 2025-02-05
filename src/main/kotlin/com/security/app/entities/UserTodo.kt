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
class UserTodo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var userTodoId: UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @JsonIgnore
    var user: User? = null

    @Column(nullable = false)
    var todoTitle: String = ""

    @Column(nullable = false)
    var todoDescription: String = ""

    @Column(nullable = false)
    var completed: Boolean = false

    @Column(nullable = false)
    var todoType: String = ""

    @Column(nullable = false)
    var todoPriority: String = ""

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
}