package com.security.app.services

import com.security.app.entities.UserTodo
import com.security.app.repositories.UserTodoRepository
import com.security.app.utils.toUUID
import org.springframework.stereotype.Service

@Service
class UserTodoService(
    private val userTodoRepository: UserTodoRepository
) {
    fun getAllTodosByUser(
        userId: String
    ): List<UserTodo> {
        return userTodoRepository.findAllByUser_UserId(userId.toUUID())
    }

    fun getTodosByType(
        userId: String,
        todoType: String
    ): List<UserTodo> {
        return userTodoRepository.findAllByUser_UserIdAndTodoType(
            userId.toUUID(),
            todoType
        )
    }

    fun getTodosByCompletionStatus(
        userId: String,
        isCompleted: Boolean
    ): List<UserTodo> {
        return userTodoRepository.findAllByCompletedAndUser_UserId(
            isCompleted,
            userId.toUUID()
        )
    }

    fun createTodo(
        userId: String,
        title: String,
        description: String,
        type: String,
        priority: String
    ): UserTodo {
        val userTodo = UserTodo().let {
            it.todoType = type
            it.todoTitle = title
            it.todoDescription = description
            it.todoPriority = priority
            it.completed = false
            it
        }
        return userTodoRepository.save(userTodo)
    }

    fun deleteTodo(
        todoId: String
    ) {
        userTodoRepository.deleteById(todoId.toUUID())
    }

    fun updateTodo(
        todoId: String,
        title: String,
        description: String,
        type: String,
        priority: String,
        isCompleted: Boolean
    ): UserTodo {
        val userTodo = userTodoRepository.findById(todoId.toUUID()).get()
        userTodo.todoTitle = title
        userTodo.todoDescription = description
        userTodo.todoType = type
        userTodo.todoPriority = priority
        userTodo.completed = isCompleted
        return userTodoRepository.save(userTodo)
    }
}