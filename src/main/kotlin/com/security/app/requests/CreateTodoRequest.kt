package com.security.app.requests

data class CreateTodoRequest(
    val title: String,
    val description: String,
    val type: String,
    val priority: String,
    val isCompleted: Boolean?
)