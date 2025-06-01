package iiitd.cognitrix.api.Dataload

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
// leader
data class LeaderResponse(
    val success: Boolean,
    val students: List<LeaderData>
)
data class LeaderData(
    val _id: String,
    val ongoingCourses: List<String>,
    val watchedVideos: List<String>,
    val notes: List<String>,
    val coins: Int,
    val rank: Int,
    val badge: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val discordId: String?,
    val verified: Boolean,
    val __t: String,
    val __v: Int
)
