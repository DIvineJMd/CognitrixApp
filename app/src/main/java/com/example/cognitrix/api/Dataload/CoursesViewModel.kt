package com.example.cognitrix.api.Dataload

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognitrix.api.login.ApiClient
import com.example.cognitrix.api.login.ApiService
import kotlinx.coroutines.launch

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
    val cerror: LiveData<String> = _Cerror
    // Helper function to get the auth token
     fun getAuthToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        println("token --> "+sharedPref.getString("auth_token", null))
        return sharedPref.getString("auth_token", null)
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
                println("response" +response )
                if (response.isSuccessful && response.body()?.success == true) {
                    _Allcourses.postValue(response.body()?.courses)
                } else {
                    _Cerror.postValue("Error: ${response.message()}")
                    println("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _Cerror.postValue("Exception: ${e.localizedMessage}")
                print("adfadfadfa")
            }
        }
    }
    suspend fun enrollInCourse( context: Context,courseId: String) {
        try {
            val authToken = getAuthToken(context)
            val response = ApiClient.getInstance(authToken).enrollCourse(courseId)
            if (response.isSuccessful) {
                val enrollResponse = response.body()
                if (enrollResponse != null && enrollResponse.success) {
                    println("Enrollment Successful: ${enrollResponse.message}")
                } else {
                    println("Enrollment failed: ${response.errorBody()?.string()}")
                }
            } else {
                println("API Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }

}
