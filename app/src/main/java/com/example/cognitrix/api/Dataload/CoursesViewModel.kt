package com.example.cognitrix.api.Dataload

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognitrix.api.Api_data.AddNoteRequest
import com.example.cognitrix.api.Api_data.ApiClient
import com.example.cognitrix.api.Api_data.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseViewModel : ViewModel() {
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    private val _noteError = MutableLiveData<String>()
    val noteError: LiveData<String> = _noteError

    private val _noteAddSuccess = MutableLiveData<Boolean>()
    val noteAddSuccess: LiveData<Boolean> = _noteAddSuccess


    private val _ongoingCourses = MutableLiveData<List<Course>>()
    val ongoingCourses: LiveData<List<Course>> = _ongoingCourses

    private val _remainingCourses = MutableLiveData<List<Course>>()
    val remainingCourses: LiveData<List<Course>> = _remainingCourses

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _Allcourses = MutableLiveData<List<AllCourseDataclass.Course>>()
    val courses: LiveData<List<AllCourseDataclass.Course>> = _Allcourses

    private val _Cerror = MutableLiveData<String>()
    private val _leaderboard = MutableLiveData<List<LeaderData>>()
    val leaderboard: LiveData<List<LeaderData>> = _leaderboard

    private val _leaderboardError = MutableLiveData<String>()
    val leaderboardError: LiveData<String> = _leaderboardError
    fun fetchLeaderboard(context: Context) {
        _isLoading.value = true

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

     fun getAuthToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        return sharedPref.getString("auth_token", null)
    }
    private val _courseDetails = MutableLiveData<Resource<CourseDetailsResponse?>>()
    val courseDetails: LiveData<Resource<CourseDetailsResponse?>> = _courseDetails
    private val _videoDetails = MutableLiveData<Resource<VideoDetail>>()
    val videoDetails: LiveData<Resource<VideoDetail>> = _videoDetails

    fun markWatched(context: Context, videoId: String,onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                println("videoId: $videoId")
                withContext(Dispatchers.IO) {
                    val auth = getAuthToken(context)
                    if (auth != null) {
                        val response = ApiClient.getInstance(auth).watchedVideo(videoId)

                        if (response.isSuccessful) {
                            onSuccess.invoke()
                            Log.d("TAG", "Video marked as watched: $videoId")
                        } else {
                            Log.e("TAG", "Failed to mark as watched. Code: ${response.code()}, Message: ${response.message()}")
                        }
                    } else {
                        Log.e("TAG", "Auth token is null")
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error marking video as watched", e)
            }
        }
    }
    fun fetchNotes(context: Context, videoId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val authToken = getAuthToken(context)
                if (authToken != null) {
                    val response = ApiClient.getInstance(authToken).getNotes(videoId)
                    if (response.isSuccessful && response.body()?.success == true) {
                        _notes.postValue(response.body()?.notes ?: emptyList())
                    } else {
                        _noteError.postValue("Failed to fetch notes: ${response.message()}")
                    }
                } else {
                    _noteError.postValue("Auth token missing")
                }
            } catch (e: Exception) {
                _noteError.postValue("Error fetching notes: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun addNote(context: Context, videoId: String, title: String, content: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val authToken = getAuthToken(context)
                if (authToken != null) {
                    val request = AddNoteRequest(title, content)
                    val response = ApiClient.getInstance(authToken).addNote(videoId, request)
                    if (response.isSuccessful) {
                        _noteAddSuccess.postValue(true)
                        fetchNotes(context, videoId)
                        delay(2000)
                        _noteAddSuccess.postValue(false)
                    } else {
                        _noteError.postValue("Failed to add note: ${response.message()}")
                    }
                } else {
                    _noteError.postValue("Auth token missing")
                }
            } catch (e: Exception) {
                _noteError.postValue("Error adding note: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun unmarkWatched(context: Context, videoId: String,onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                println("videoId: $videoId")
                withContext(Dispatchers.IO) {
                    val auth = getAuthToken(context)
                    if (auth != null) {
                        val response = ApiClient.getInstance(auth).unwatchedVideo(videoId)
                        if (response.isSuccessful) {
                            onSuccess.invoke()
                            Log.d("TAG", "Video marked as unwatched: $videoId ${response.body()}")
                        } else {
                            Log.e("TAG", "Failed to mark as unwatched. Code: ${response.code()}, Message: ${response.message()}")
                        }
                    } else {
                        Log.e("TAG", "Auth token is null")
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error marking video as unwatched", e)
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
                        Log.d("Fetchingdat","Reco: ${Resource.Success(response.body()!!.video)} ")

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
        Log.d("Fetchingdat","courseID: $courseId")
        _courseDetails.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val authToken = getAuthToken(context)
                if (authToken != null) {
                    val response = ApiClient.getInstance(authToken).getCourseDetails(courseId)
                    if (response.isSuccessful && response.body()?.success == true) {
                        _courseDetails.postValue(Resource.Success(response.body()))
                        fetchVideoDetails(context, response.body()!!.videos.values.first()[0].id)
//                        println("======>"+response.body()!!.videos)
                    } else {
                        _courseDetails.postValue(Resource.Error("Error fetching course details: ${response.message()}"))
                    }
                } else {
                    _courseDetails.postValue(Resource.Error("Auth token missing"))
                }
            } catch (e: Exception) {
                _courseDetails.postValue(Resource.Error("Exception: ${e.localizedMessage}"))
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
    fun reloadRecommendation(){
        currentOffset = 0
        hasMoreItems = true
        _relatedVideos.value = emptyList()
    }
    fun loadRecommendations(videoId: String, context: Context,reload:Boolean) {
        if (_isLoading.value == true || !hasMoreItems) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                if(reload){
                    currentOffset = 0
                    hasMoreItems = true
                    _relatedVideos.value = emptyList()

                    Log.d("Fetchingdat","${_relatedVideos.value}")
                }
                val authToken = getAuthToken(context)
                val response = ApiClient.getInstance(authToken)
                    .getRecommendations(videoId, pageSize, currentOffset)

                if (response.isSuccessful) {
                    response.body()?.let {

                        if (it.success) {
                            val newVideos = it.relatedVideos

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
