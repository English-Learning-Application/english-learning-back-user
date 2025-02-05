package com.security.app.repositories

import com.security.app.entities.UserTodo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserTodoRepository : JpaRepository<UserTodo, UUID> {
    fun findAllByUser_UserId(userUserId: UUID): List<UserTodo>
    fun findAllByUser_UserIdAndTodoType(userUserId: UUID, todoType: String): List<UserTodo>
    fun findAllByCompletedAndUser_UserId(isCompleted: Boolean, userId: UUID): List<UserTodo>
}