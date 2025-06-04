package com.example.campingapp.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("users/login")
    suspend fun login(@Body user: User): LoginResponse

    @POST("users")
    suspend fun registerUser(@Body request: RegistrationRequest): Response<Unit>

    @GET("locations")
    suspend fun getAllLocations(): List<Location>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): UserData

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body userData: UserData): Response<Unit>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>

    @GET("locations/{id}")
    suspend fun getLocation(@Path("id") locationId: Int): Location

    @POST("bookings")
    suspend fun createBookings(
        @Header("Authorization") token: String,
        @Body bookingRequest: Booking
    ): Response<BookingResponse>

    @POST("reviews")
    suspend fun createReview(
        @Header("Authorization") token: String,
        @Body reviewRequest: ReviewRequest
    ): Response<Review>

    @GET("reviews/location/{locationId}")
    suspend fun getReviewsByLocationId(
        @Path("locationId") locationId: Int
    ): Response<List<Review>>

    @PUT("reviews/{id}")
    suspend fun updateReview(
        @Header("Authorization") authHeader: String,
        @Path("id") reviewId: Int,
        @Body reviewRequest: ReviewRequest
    ): Response<Review>

    @DELETE("reviews/{id}")
    suspend fun deleteReview(
        @Header("Authorization") authHeader: String,
        @Path("id") reviewId: Int
    ): Response<Unit>
}