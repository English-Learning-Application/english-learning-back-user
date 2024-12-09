package com.security.app.entities

import jakarta.persistence.*
import lombok.*
import java.util.*


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String = ""

    @Version
    var version: Int = 0

    @Column
    var name : String = ""

    @Column(unique = true)
    var email: String = ""

    @Column
    var password: String = ""

    @Column
    var birthDate: String = ""

    @Column
    var role: Role = Role.USER
}

enum class Role {
    USER
}