package com.example.campingapp.api

data class RegistrationRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone_number: String,
    val password: String,
    val role: String
)
