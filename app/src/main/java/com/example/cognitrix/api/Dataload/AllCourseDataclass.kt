package com.example.cognitrix.api.Dataload

class AllCourseDataclass {
    data class CourseResponse(
        val success: Boolean,
        val courses: List<Course>
    )

    data class Course(
        val _id: String,
        val videos: List<String>,
        val title: String,
        val description: String? = null,
        val creator: Creator,
        val status: String,
        val numEnrolledStudents: Int
    )

    data class Creator(
        val _id: String,
        val ongoingCourses: List<String>,
        val watchedVideos: List<String>,
        val notes: List<String>,
        val coins: Int,
        val fullName: String,
        val email: String,
        val phoneNumber: String,
        val verified: Boolean,
        val myCourses: List<String>,
        val role: String? = null
    )

}