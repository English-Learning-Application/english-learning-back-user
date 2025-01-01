package com.security.app.model

sealed class Message<T>(val message: String, val statusCode: Int, val data: T? = null) {
    class Success<T>(message: String, data: T) : Message<T>(message, 200, data)
    class Error<T>(message: String, statusCode: Int) : Message<T>(message, statusCode)
    class NotFound<T>(message: String) : Message<T>(message, 404)
    class BadRequest<T>(message: String) : Message<T>(message, 400)
    class Unauthorized<T>(message: String) : Message<T>(message, 401)
}