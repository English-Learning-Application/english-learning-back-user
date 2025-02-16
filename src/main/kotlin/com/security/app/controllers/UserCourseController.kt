package com.security.app.controllers

import com.security.app.entities.UserBookmarkedCourse
import com.security.app.model.Message
import com.security.app.services.UserBookmarkedCourseService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/api/v1/courses")
class UserCourseController(
    private val userBookmarkedCourseService: UserBookmarkedCourseService
) {
    @PostMapping("/bookmark")
    fun bookmarkCourse(
        @RequestBody userBookmarkedCourse: UserBookmarkedCourse,
    ): ResponseEntity<Message<UserBookmarkedCourse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name
        try {
            val bookmarkedCourse = userBookmarkedCourseService.bookmarkCourse(
                userId = userId,
                courseId = userBookmarkedCourse.courseId,
                courseType = userBookmarkedCourse.courseType
            ) ?: return ResponseEntity.badRequest().body(Message.BadRequest("Failed to bookmark course"))
            return ResponseEntity.ok(Message.Success("Course bookmarked successfully", bookmarkedCourse))
        } catch (e: Exception) {
            return ResponseEntity.badRequest()
                .body(Message.BadRequest("An error occurred while bookmarking course"))
        }
    }

    @GetMapping("/bookmarks")
    fun getUserBookmarkedCourses(): ResponseEntity<Message<List<UserBookmarkedCourse>>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name
        try {
            val bookmarkedCourses = userBookmarkedCourseService.getUserBookmarkedCourses(userId)
            return ResponseEntity.ok(Message.Success("User bookmarked courses fetched successfully", bookmarkedCourses))
        } catch (e: Exception) {
            return ResponseEntity.badRequest()
                .body(Message.BadRequest("An error occurred while fetching bookmarked courses"))
        }
    }
}