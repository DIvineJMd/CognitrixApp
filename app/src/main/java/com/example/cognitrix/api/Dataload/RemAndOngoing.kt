package com.example.cognitrix.api.Dataload

import com.google.gson.annotations.SerializedName

data class Course(
    val _id: String,
    val title: String,
    val progress: Double?,
    val numWatchedVideos: Int?,
    val watchedVideos: List<WatchedVideo>?,
    val numVideosInCourse: Int,
    val creator: Creator,
    val numEnrolledStudents: Int,
)

data class Creator(
    val _id: String,
    val fullName: String,
    @SerializedName("__t") val type: String // Maps JSON "__t" to "type"
)
data class EnrollCourseResponse(
    val success: Boolean,
    val message: String
)

data class WatchedVideo(
    val id: String,
    val course: String,
    val description: String,
    val duration: String,
    val lectureNumber: Int,
    val relatedVideos: List<RelatedVideo>,
    val tags: List<String>,
    val title: String,
    val url: String,
    val videoNumber: Int,
    val watchedUsers: List<String>,
    val topics: List<String>
)

data class RelatedVideo(
    val video: String,
    val weight: Double
)

data class  CourseResponse(
    val success: Boolean,
    val courses: List<Course>
)
