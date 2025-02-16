package com.security.app.repositories

import com.security.app.entities.UserBookmarkedCourse
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserBookmarkedCourseRepository : JpaRepository<UserBookmarkedCourse, UUID> {
    fun findAllByUser_UserId(userUserId: UUID): List<UserBookmarkedCourse>
    fun findByUser_UserIdAndCourseIdAndCourseType(
        userUserId: UUID,
        courseId: String,
        courseType: String
    ): UserBookmarkedCourse?
}