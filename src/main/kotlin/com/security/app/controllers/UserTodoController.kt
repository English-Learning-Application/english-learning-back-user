package com.security.app.controllers

import com.security.app.entities.UserTodo
import com.security.app.model.ListMessage
import com.security.app.model.Message
import com.security.app.requests.CreateTodoRequest
import com.security.app.services.UserTodoService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/todo")
class UserTodoController(
    private val userTodoService: UserTodoService
) {
    @GetMapping("/all")
    fun getAllTodos(): ResponseEntity<ListMessage<UserTodo>> {
        val auth = SecurityContextHolder.getContext().authentication
        val userId = auth.name

        try {
            val todos = userTodoService.getAllTodosByUser(userId)
            return ResponseEntity.ok(ListMessage.Success("Success", todos))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(ListMessage.BadRequest("Error"))
        }
    }

    @GetMapping("/type/{type}")
    fun getTodosByType(
        @PathVariable type: String
    ): ResponseEntity<ListMessage<UserTodo>> {
        val auth = SecurityContextHolder.getContext().authentication
        val userId = auth.name

        try {
            val todos = userTodoService.getTodosByType(userId, type)
            return ResponseEntity.ok(ListMessage.Success("Success", todos))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(ListMessage.BadRequest("Error"))
        }
    }

    @GetMapping("/completed/{status}")
    fun getTodosByCompletionStatus(
        @PathVariable status: Boolean
    ): ResponseEntity<ListMessage<UserTodo>> {
        val auth = SecurityContextHolder.getContext().authentication
        val userId = auth.name

        try {
            val todos = userTodoService.getTodosByCompletionStatus(userId, status)
            return ResponseEntity.ok(ListMessage.Success("Success", todos))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(ListMessage.BadRequest("Error"))
        }
    }

    @DeleteMapping("/delete/{todoId}")
    fun deleteTodo(
        @PathVariable todoId: String
    ): ResponseEntity<Message<Any>> {
        try {
            userTodoService.deleteTodo(todoId)
            return ResponseEntity.ok(Message.Success("Success", {}))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest("Error"))
        }
    }

    @PostMapping("/create")
    fun createTodo(
        @RequestBody request: CreateTodoRequest
    ): ResponseEntity<Message<UserTodo>> {
        val auth = SecurityContextHolder.getContext().authentication
        val userId = auth.name

        try {
            val todo =
                userTodoService.createTodo(userId, request.title, request.description, request.type, request.priority)
            return ResponseEntity.ok(Message.Success("Success", todo))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest("Error"))
        }
    }

    @PutMapping("/update/{todoId}")
    fun updateTodo(
        @PathVariable todoId: String,
        @RequestBody request: CreateTodoRequest
    ): ResponseEntity<Message<UserTodo>> {
        try {
            val todo = userTodoService.updateTodo(
                todoId,
                request.title,
                request.description,
                request.type,
                request.priority,
                request.isCompleted ?: false
            )
            return ResponseEntity.ok(Message.Success("Success", todo))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message.BadRequest("Error"))
        }
    }
}