package com.example.cognitrix.api.login

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val success: Boolean, val token: String?, val role: String, val verified: Boolean)

data class StudentInfoResponse(
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val discordId: String,
    val coins: Int,
    val rank: Int,
    val badge: String
)
