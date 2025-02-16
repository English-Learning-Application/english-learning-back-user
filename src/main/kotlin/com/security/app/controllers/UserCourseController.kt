package com.security.app.controllers

import com.security.app.entities.UserBookmarkedCourse
import com.security.app.model.Message
import com.security.app.services.UserBookmarkedCourseService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController("/api/v1/course")
class UserCourseController(
    private val userBookmarkedCourseService: UserBookmarkedCourseService
) {
    @PostMapping("/bookmarks")
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

    @DeleteMapping("/bookmarks")
    fun removeBookmarkCourse(
        @RequestBody userBookmarkedCourse: UserBookmarkedCourse,
    ): ResponseEntity<Message<Boolean>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name
        try {
            val isRemoved = userBookmarkedCourseService.removeBookmarkCourse(
                userId = userId,
                courseId = userBookmarkedCourse.courseId,
                courseType = userBookmarkedCourse.courseType
            )
            
            return if (isRemoved) {
                ResponseEntity.ok(Message.Success("Course removed from bookmarks successfully", true))
            } else {
                ResponseEntity.badRequest().body(Message.BadRequest("Failed to remove course from bookmarks"))
            }
        } catch (e: Exception) {
            return ResponseEntity.badRequest()
                .body(Message.BadRequest("An error occurred while removing course from bookmarks"))
        }
    }

    @GetMapping("/bookmarks/{courseId}")
    fun isCourseBookmarked(
        @PathVariable("courseId") courseId: String,
    ): ResponseEntity<Message<Boolean>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name
        try {
            val isBookmarked = userBookmarkedCourseService.isCourseBookmarked(userId, courseId)
            return ResponseEntity.ok(Message.Success("Course bookmarked status fetched successfully", isBookmarked))
        } catch (e: Exception) {
            return ResponseEntity.badRequest()
                .body(Message.BadRequest("An error occurred while fetching bookmarked status"))
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