package com.security.app.services

import com.security.app.entities.UserBookmarkedCourse
import com.security.app.repositories.UserBookmarkedCourseRepository
import com.security.app.repositories.UserRepository
import com.security.app.utils.toUUID
import org.springframework.stereotype.Service

@Service
class UserBookmarkedCourseService(
    private val userBookmarkedCourseRepository: UserBookmarkedCourseRepository,
    private val userRepository: UserRepository
) {
    fun bookmarkCourse(userId: String, courseId: String, courseType: String): UserBookmarkedCourse? {
        val userData = userRepository.findByUserId(userId.toUUID()) ?: return null

        val existedBookmark = userBookmarkedCourseRepository.findByUser_UserIdAndCourseIdAndCourseType(
            userData.userId,
            courseId,
            courseType
        )
        if (existedBookmark != null) return existedBookmark

        val userBookmarkedCourse = UserBookmarkedCourse().let {
            it.user = userData
            it.courseId = courseId
            it.courseType = courseType
            it
        }
        return userBookmarkedCourseRepository.save(userBookmarkedCourse)
    }

    fun getUserBookmarkedCourses(userId: String): List<UserBookmarkedCourse> {
        return userBookmarkedCourseRepository.findAllByUser_UserId(userId.toUUID())
    }
}