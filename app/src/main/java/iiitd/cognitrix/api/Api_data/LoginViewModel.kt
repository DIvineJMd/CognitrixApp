package iiitd.cognitrix.api.Api_data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import android.util.Log
import android.content.Context
import iiitd.cognitrix.api.Api_data.ApiClient
import iiitd.cognitrix.api.Api_data.LoginRequest
import iiitd.cognitrix.api.Api_data.StudentInfoResponse

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<Resource<String>>(Resource.Loading)
    val loginState: StateFlow<Resource<String>> = _loginState.asStateFlow()

    private var authToken: String? = null

    fun login(email: String, password: String, context: Context) {
        _loginState.value = Resource.Loading
        viewModelScope.launch {
            try {
                val apiService = ApiClient.getInstance(null)
                val response = apiService.login(LoginRequest(email, password)).awaitResponse()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.success) {
                        authToken = loginResponse.token
                        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
                        sharedPref.edit().apply {
                            putString("auth_token", loginResponse.token)
                            putString("role", loginResponse.role)
                            apply()
                        }
                        _loginState.value = Resource.Success("Login Successful")
                        if (loginResponse.role == "Student") {
                            fetchStudentInfo(context)
                        }
                    } else {
                        _loginState.value = Resource.Error("Login failed: Invalid credentials")
                    }
                } else {
                    _loginState.value = Resource.Error("Login failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _loginState.value = Resource.Error("Exception: ${e.message}")
                Log.e("Login", "Exception: $e")
            }
        }
    }

    private fun fetchStudentInfo(context: Context) {
        viewModelScope.launch {
            try {
                val apiService = ApiClient.getInstance(authToken) // Pass token for authorized request
                val response = apiService.getStudentInfo().awaitResponse()

                if (response.isSuccessful) {
                    val studentInfo = response.body()
                    if (studentInfo != null) {
                        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
                        sharedPref.edit().apply {
                            putString("fullName", studentInfo.fullName)
                            putString("email", studentInfo.email)
                            putString("phoneNumber", studentInfo.phoneNumber)
                            putString("discordId", studentInfo.discordId)
                            putInt("coins", studentInfo.coins)
                            putInt("rank", studentInfo.rank)
                            putString("badge", studentInfo.badge)
                            apply()
                        }
                        Log.d("StudentInfo", "Student information stored successfully.")
                    }
                } else {
                    Log.e("StudentInfo", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("StudentInfo", "Exception: $e")
            }
        }
    }
    fun getStudentInfo(context: Context): StudentInfoResponse? {
        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        return if (sharedPref.contains("fullName")) {
            sharedPref.getString("fullName", null)?.let {
                StudentInfoResponse(
                    fullName = it,
                    email = sharedPref.getString("email", null)!!,
                    phoneNumber = sharedPref.getString("phoneNumber", null)!!,
                    discordId = sharedPref.getString("discordId", null)!!,
                    coins = sharedPref.getInt("coins", 0),
                    rank = sharedPref.getInt("rank", 0),
                    badge = sharedPref.getString("badge", null)!!
                )
            }
        } else {
            null // Return null if no student info is stored
        }
    }

}
