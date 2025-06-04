package com.example.campingapp.api

data class Location(
    val id: Int,
    val name: String,
    val type: String,
    val price: Float,
    val description: String,
    val max_capacity: Int,
    val availability: Boolean,
    val image: String
)
