package com.example.tbssmarthub.data.model

/**
 * User data class representing a user in the system
 * Follows the class diagram structure with required fields
 */
data class User(
    val userId: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val isEmailVerified: Boolean = false,
    val role: UserRole = UserRole.STUDENT,
    val language: String = "en"
)

/**
 * Enum class representing the possible user roles in the system
 * As specified in the class diagram
 */
enum class UserRole {
    ADMIN,
    PROFESSOR,
    STUDENT
}

/**
 * Student data class extending the base user information
 * Contains student-specific fields as per class diagram
 */
data class Student(
    val studentId: String,
    val userId: String,
    val name: String,
    val cumulativeGPA: Float = 0.0f,
    val enrolledCourses: List<String> = emptyList()
)

/**
 * Professor data class extending the base user information
 * Contains professor-specific fields as per class diagram
 */
data class Professor(
    val professorId: String,
    val userId: String,
    val name: String,
    val department: String
)

/**
 * Administrator data class extending the base user information
 * Contains admin-specific fields as per class diagram
 */
data class Administrator(
    val adminId: String,
    val userId: String,
    val name: String
)

/**
 * Course data class representing a course in the system
 * As specified in the class diagram
 */
data class Course(
    val courseId: String,
    val name: String,
    val schedule: Map<String, String>, // Day -> Time slots
    val professorId: String
)

/**
 * Feedback data class for anonymous feedback functionality
 * As specified in the class diagram
 */
data class Feedback(
    val feedbackId: String,
    val userId: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Notification data class for system notifications
 * As specified in the class diagram
 */
data class Notification(
    val notificationId: String,
    val content: String,
    val sentBy: String,
    val sentToRole: UserRole,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Event data class for calendar events and upcoming events
 * Extended from the class diagram to support the calendar feature
 */
data class Event(
    val eventId: String,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val location: String = "",
    val organizer: String = ""
)
