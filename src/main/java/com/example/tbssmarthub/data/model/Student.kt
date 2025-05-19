package com.example.tbssmarthub.data.model

/**
 * Student data class extending the base user information
 * Contains student-specific fields as per class diagram and CSV data
 */
data class Student(
    val studentId: String,
    val userId: String,
    val name: String,
    val level: String,
    val cumulativeGPA: Float = 0.0f,
    val enrolledCourses: List<String> = emptyList()
)
