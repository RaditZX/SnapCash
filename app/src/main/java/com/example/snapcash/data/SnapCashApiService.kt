package com.example.snapcash.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SnapCashApiService {
    @POST("signup")
    suspend fun signUp(@Body request: SignInRequest): SignUpResponse

    @POST("signin")
    suspend fun signIn(@Body request: SignInRequest): SignInResponse

}
