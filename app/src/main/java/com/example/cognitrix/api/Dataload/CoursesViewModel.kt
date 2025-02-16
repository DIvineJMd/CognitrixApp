package com.example.cognitrix.api.Dataload

import android.content.ContentValues.TAG
import android.content.Context
import android.nfc.Tag
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognitrix.api.login.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseViewModel : ViewModel() {

    private val _ongoingCourses = MutableLiveData<List<Course>>()
    val ongoingCourses: LiveData<List<Course>> = _ongoingCourses // use to get data

    private val _remainingCourses = MutableLiveData<List<Course>>()
    val remainingCourses: LiveData<List<Course>> = _remainingCourses // use to get data

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _Allcourses = MutableLiveData<List<AllCourseDataclass.Course>>()
    val courses: LiveData<List<AllCourseDataclass.Course>> = _Allcourses // use to get data

    private val _Cerror = MutableLiveData<String>()
    // MutableLiveData for Leaderboard data
    private val _leaderboard = MutableLiveData<List<LeaderData>>()
    val leaderboard: LiveData<List<LeaderData>> = _leaderboard

    // MutableLiveData for any errors related to leaderboard
    private val _leaderboardError = MutableLiveData<String>()
    val leaderboardError: LiveData<String> = _leaderboardError
    // Function to fetch leaderboard data
    fun fetchLeaderboard(context: Context) {
        _isLoading.value = true // Start loading

        viewModelScope.launch {
            try {
                val authToken = getAuthToken(context) // Get auth token
                if (authToken != null) {
                    // Make the API call to fetch the leaderboard
                    val response = ApiClient.getInstance(authToken).getLeaderboard()

                    if (response.isSuccessful) {
                        // If the response is successful, set the leaderboard data
                        _leaderboard.value =
                            response.body()?.students // Directly use the body of the response
                    } else {
                        // If the response is not successful, set the error message
                        _leaderboardError.value = "Failed to fetch leaderboard: ${response.message()}"
                    }
                } else {
                    // Handle case where auth token is missing
                    _leaderboardError.value = "Authorization token missing"
                }
            } catch (e: Exception) {
                // Handle any exceptions that occur during the API call
                _leaderboardError.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false // End loading
            }
        }
    }

    // Helper function to get the auth token
     fun getAuthToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        return sharedPref.getString("auth_token", null)
    }
    private val _courseDetails = MutableLiveData<Resource<CourseDetailsResponse?>>()
    val courseDetails: LiveData<Resource<CourseDetailsResponse?>> = _courseDetails
    private val _videoDetails = MutableLiveData<Resource<VideoDetail>>()
    val videoDetails: LiveData<Resource<VideoDetail>> = _videoDetails

    fun markWatched(context: Context, videoId: String) {
        viewModelScope.launch {
            try {
                println("videoId: $videoId")
                withContext(Dispatchers.IO) {
                    val auth = getAuthToken(context)
                    if (auth != null) {
                        val response = ApiClient.getInstance(auth).watchedVideo(videoId)

                        if (response.isSuccessful) {
                            Log.d(TAG, "Video marked as watched: $videoId")
                        } else {
                            Log.e(TAG, "Failed to mark as watched. Code: ${response.code()}, Message: ${response.message()}")
                        }
                    } else {
                        Log.e(TAG, "Auth token is null")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error marking video as watched", e)
            }
        }
    }

    fun fetchVideoDetails(context: Context, videoId: String) {
        _videoDetails.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val authToken = getAuthToken(context)
                if (authToken != null) {
                    val response = ApiClient.getInstance(authToken).getVideoDetails(videoId)
                    if (response.isSuccessful && response.body()?.success == true) {
                        _videoDetails.postValue(Resource.Success(response.body()!!.video))
                    } else {
                        _Cerror.value = "Failed to fetch video details: ${response.message()}"
                        _videoDetails.postValue(Resource.Error("Failed to fetch video details: ${response.message()}"))
                    }
                } else {
                    _Cerror.value = "Authorization token missing"
                    _videoDetails.postValue(Resource.Error("Authorization token missing"))
                }
            } catch (e: Exception) {
                _Cerror.value = "Error: ${e.localizedMessage}"
                _videoDetails.postValue(Resource.Error("Error: ${e.localizedMessage}"))

            } finally {
                _isLoading.value = false
            }
        }
    }
    // Function to fetch course details
    fun fetchCourseDetails(context: Context, courseId: String) {
        _courseDetails.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val authToken = getAuthToken(context)
                println("====> $authToken")
                if (authToken != null) {
                    val response = ApiClient.getInstance(authToken).getCourseDetails(courseId)
                    if (response.isSuccessful && response.body()?.success == true) {
                        _courseDetails.postValue(Resource.Success(response.body()))
                        fetchVideoDetails(context, response.body()!!.videos.values.first()[0].id)
//                        println("======>"+response.body()!!.videos)
                    } else {
                        _courseDetails.postValue(Resource.Error("Error fetching course details: ${response.message()}"))
                        println("Error fetching course details: ${response.message()}")
                    }
                } else {
                    _courseDetails.postValue(Resource.Error("Auth token missing"))
                }
            } catch (e: Exception) {
                _courseDetails.postValue(Resource.Error("Exception: ${e.localizedMessage}"))
                println("Exception: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Function to fetch ongoing courses
    fun fetchOngoingCourses(context: Context) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val authToken = getAuthToken(context)
                if (authToken != null) {
                    val response = ApiClient.getInstance(authToken).getOngoingCourses()
                    if (response.success) {
                        println(response.courses)
                        _ongoingCourses.value = response.courses
                    } else {
                    }
                } else {
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Function to fetch remaining courses
    fun fetchRemainingCourses(context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val authToken = getAuthToken(context)
                if (authToken != null) {
                    val response = ApiClient.getInstance(authToken).getRemainingCourses()
                    if (response.success) {
                        _remainingCourses.value = response.courses
                    } else {
                        // Handle error if the response indicates failure
                        // You could set an error message LiveData here
                    }
                } else {
                    // Handle missing auth token case
                }
            } catch (e: Exception) {
                // Handle error (consider logging or exposing an error LiveData for this)
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Get all Courses
    fun fetchAllCourse(context: Context){
        viewModelScope.launch {
            try {
                val authToken = getAuthToken(context)

                val response = ApiClient.getInstance(authToken).getAllCourses()
                if (response.isSuccessful && response.body()?.success == true) {
                    _Allcourses.postValue(response.body()?.courses)
                } else {
                    _Cerror.postValue("Error: ${response.message()}")
                    println("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _Cerror.postValue("Exception: ${e.localizedMessage}")

            }
        }
    }
     fun enrollInCourse( context: Context,courseId: String) {
         viewModelScope.launch {
             try {
                 val authToken = getAuthToken(context)
                 val response = ApiClient.getInstance(authToken).enrollCourse(courseId)
                 if (response.isSuccessful) {
                     val enrollResponse = response.body()
                     if (enrollResponse != null && enrollResponse.success) {
                     } else {
//                         println("Enrollment failed: ${response.errorBody()?.string()}")
                     }
                 } else {
//                     println("API Error: ${response.errorBody()?.string()}")
                 }
             } catch (e: Exception) {
                 println("Exception: ${e.message}")
             }
         }
    }
//    =========================================================================
private val _relatedVideos = MutableLiveData<List<RecommendationVideo>>(emptyList())
    val relatedVideos: LiveData<List<RecommendationVideo>> = _relatedVideos

//    private val _isLoadingRVideo = MutableLiveData(false)
//    val isLoadingRvideo: LiveData<Boolean> = _isLoading

    private var currentOffset = 0
    private val pageSize = 10
    private var hasMoreItems = true

    fun loadRecommendations(videoId: String, context: Context,reload:Boolean) {
        // Prevent multiple simultaneous loading or when no more items
        if (_isLoading.value == true || !hasMoreItems) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                if(reload){
                    currentOffset = 0
                    hasMoreItems = true
                    _relatedVideos.value = emptyList()
                }
                val authToken = getAuthToken(context)
                val response = ApiClient.getInstance(authToken)
                    .getRecommendations(videoId, pageSize, currentOffset)

                if (response.isSuccessful) {
                    response.body()?.let {

                        if (it.success) {
                            val newVideos = it.relatedVideos

                            // Update list
                            val currentList = _relatedVideos.value.orEmpty()
                            _relatedVideos.value = currentList + newVideos

                            // Update pagination
                            currentOffset += pageSize
                            hasMoreItems = newVideos.size == pageSize
                        } else {
                            hasMoreItems = false
                        }
                    }
                } else {
                    // Handle API error
                    hasMoreItems = false
                }
            } catch (e: Exception) {
                // Handle network or other exceptions
                hasMoreItems = false
                Log.e("RecommendationsLoad", "Error loading recommendations", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
