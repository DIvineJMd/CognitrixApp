package com.example.cognitrix.api.login

import com.example.cognitrix.api.Dataload.AllCourseDataclass
import com.example.cognitrix.api.Dataload.CourseResponse
import com.example.cognitrix.api.Dataload.EnrollCourseResponse
import okhttp3.OkHttpClient
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

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