package com.example.cognitrix.api.Api_data

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

data class Note(
    val _id: String,
    val title: String,
    val content: String,
    val video: String,
    val createdBy: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)

data class NotesResponse(
    val success: Boolean,
    val notes: List<Note>
)
data class AddNoteRequest(
    val title: String,
    val content: String
)
