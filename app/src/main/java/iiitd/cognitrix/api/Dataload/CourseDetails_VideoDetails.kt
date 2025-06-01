package iiitd.cognitrix.api.Dataload

import com.google.gson.annotations.SerializedName

data class CourseDetailsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("_id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("creator") val creator: CreatorVideo,
    @SerializedName("videos") val videos: Map<String, List<Video>>
)

data class CreatorVideo(
    @SerializedName("_id") val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("__t") val type: String
)

data class Video(
    @SerializedName("_id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("lectureNumber") val lectureNumber: Int,
    @SerializedName("videoNumber") val videoNumber: Int,
    @SerializedName("watched") var watched: Boolean
)

data class VideoDetailsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("video") val video: VideoDetail
)

data class VideoDetail(
    @SerializedName("_id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("url") val url: String,
    @SerializedName("nextVideo") val nextVideo: NextVideo?
)

data class NextVideo(
    @SerializedName("_id") val id: String,
    @SerializedName("course") val courseId: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)
data class RecommendationsResponse(
    val success: Boolean,
    val relatedVideos: List<RecommendationVideo>
)
data class RecommendationVideo(
    val _id: String,
    val title: String,
    val url: String,
    val course: String,
    var watched: Boolean
)
