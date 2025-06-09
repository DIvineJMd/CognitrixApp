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
import iiitd.cognitrix.api.Api_data.SignupRequest

sealed class Resource<out T> {
    object Idle : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<Resource<String>>(Resource.Idle)
    val loginState: StateFlow<Resource<String>> = _loginState.asStateFlow()

    private val _signupState = MutableStateFlow<Resource<String>>(Resource.Idle)
    val signupState: StateFlow<Resource<String>> = _signupState.asStateFlow()

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

    fun refreshStudentInfo(context: Context) {
        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)
        if (token != null) {
            authToken = token
            fetchStudentInfo(context)
        }
    }

    fun logout() {
        _loginState.value = Resource.Idle
        authToken = null
    }

    fun resetLoginState() {
        _loginState.value = Resource.Idle
    }

    fun signup(
        fullName: String,
        email: String,
        password: String,
        phoneNumber: String,
        discordId: String,
        role: String
    ) {
        _signupState.value = Resource.Loading
        viewModelScope.launch {
            try {
                val apiService = ApiClient.getInstance(null)
                val response = apiService.signup(
                    SignupRequest(fullName, email, password, phoneNumber, discordId, role)
                ).awaitResponse()

                if (response.isSuccessful) {
                    val signupResponse = response.body()
                    if (signupResponse != null && signupResponse.success) {
                        _signupState.value = Resource.Success("User created successfully")
                    } else {
                        _signupState.value = Resource.Error("Sign up failed: Invalid data")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("Signup", "Error response: $errorBody")

                    // Parse error message from response
                    val errorMessage = errorBody?.let { body ->
                        try {
                            val jsonObject = org.json.JSONObject(body)
                            val errorObject = jsonObject.optJSONObject("error")
                            val message = errorObject?.optString("message") ?: "Sign up failed"

                            // If message is "400", show the status code instead
                            if (message == "400" || message == "401" || message == "403" || message == "404" || message == "500") {
                                "Error: ${response.code()}"

                            } else {
                                message
                            }
                        } catch (e: Exception) {
                            "Sign up failed: ${e.message}"
                        }
                    } ?: "Sign up failed"

                    _signupState.value = Resource.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("Signup", "Exception: ${e.message}")
                _signupState.value = Resource.Error("Exception: ${e.message}")
            }
        }
    }
}
