package iiitd.cognitrix.api.Api_data

import iiitd.cognitrix.api.Dataload.AllCourseDataclass
import iiitd.cognitrix.api.Dataload.CourseDetailsResponse
import iiitd.cognitrix.api.Dataload.CourseResponse
import iiitd.cognitrix.api.Dataload.EnrollCourseResponse
import iiitd.cognitrix.api.Dataload.LeaderResponse
import iiitd.cognitrix.api.Dataload.RecommendationsResponse
import iiitd.cognitrix.api.Dataload.VideoDetailsResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/auth/signup")
    fun signup(@Body request: SignupRequest): Call<LoginResponse>

    @GET("api/student")
    fun getStudentInfo(): Call<StudentInfoResponse>

    @GET("api/course/ongoing")
    suspend fun getOngoingCourses(): CourseResponse

    @GET("api/course/remaining")
    suspend fun getRemainingCourses(): CourseResponse

    @GET("api/course")
    suspend fun getAllCourses(): Response<AllCourseDataclass.CourseResponse>

    @PATCH("api/course/enroll/{courseId}")
    suspend fun enrollCourse(@Path("courseId") courseId: String): Response<EnrollCourseResponse>

    @GET("api/course/{courseID}")
    suspend fun getCourseDetails(@Path("courseID") courseID: String): Response<CourseDetailsResponse>

    @GET("api/video/{videoId}")
    suspend fun getVideoDetails(@Path("videoId") videoId: String): Response<VideoDetailsResponse>

    @GET("api/video/{videoId}/recommendations")
    suspend fun getRecommendations(
        @Path("videoId") videoId: String,
        @Query("items") pageSize: Int,
        @Query("offset") offset: Int
    ): Response<RecommendationsResponse>

    @GET("api/leaderboard")
    suspend fun getLeaderboard(): Response<LeaderResponse>

    @PATCH("api/video/watch/{videoId}")
    suspend fun watchedVideo(@Path("videoId") videoId: String): Response<Void>

    @PATCH("api/video/unwatch/{videoId}")
    suspend fun unwatchedVideo(@Path("videoId") videoId: String): Response<Void>

    @GET("api/note/{videoId}")
    suspend fun getNotes(@Path("videoId") videoId: String): Response<NotesResponse>

    @POST("api/note/{videoId}")
    suspend fun addNote(
        @Path("videoId") videoId: String,
        @Body note: AddNoteRequest
    ): Response<Note>

    @PATCH("api/note/{noteId}")
    suspend fun editNote(
        @Path("noteId") noteId: String,
        @Body note: AddNoteRequest
    ): Response<Note>

    @PATCH("api/note/share/{noteId}")
    suspend fun changeNoteStatus(
        @Path("noteId") noteId: String,
        @Body request: ChangeNoteStatusRequest
    ): Response<Note>

    @DELETE("api/note/{noteId}")
    suspend fun deleteNote(@Path("noteId") noteId: String): Response<Void>

    @GET("api/rating/{videoId}")
    suspend fun getRatings(@Path("videoId") videoId: String): Response<RatingResponse>

    @POST("api/rating/{videoId}")
    suspend fun rateVideo(
        @Path("videoId") videoId: String,
        @Body request: RateVideoRequest
    ): Response<RateVideoResponse>

    @DELETE("api/rating/{videoId}")
    suspend fun deleteRating(@Path("videoId") videoId: String): Response<Void>
}

object ApiClient {
    private const val BASE_URL = "https://szuumq8b3e.execute-api.ap-south-1.amazonaws.com/prod/"

    fun getInstance(authToken: String? = null): ApiService {
        val client = OkHttpClient.Builder()
            .apply {
                if (!authToken.isNullOrEmpty()) {
                    addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $authToken")
                            .build()
                        chain.proceed(request)
                    }
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
