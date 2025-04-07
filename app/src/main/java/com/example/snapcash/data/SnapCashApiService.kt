package com.example.snapcash.data

import android.media.Image
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SnapCashApiService {
    @POST("signup")
    suspend fun signUp(@Body request: SignInRequest): SignUpResponse

    @POST("signin")
    suspend fun signIn(@Body request: SignInRequest): SignInResponse

    @POST("registerWithGoogle")
    suspend fun registerWithGoogle(@Header("Authorization") token: String): SignUpResponse

    @POST("signinWithGoogle")
    suspend fun signWithGoogle(@Header("Authorization") token: String): SignInResponse

    @Multipart
    @POST("generateInvoiceData")
    suspend fun addPengeluaranOrPemasukanByGPT(
        @Header("Authorization") token: String?,
        @Part image: MultipartBody.Part
    ): generateTextFromInvoiceResponse

    @GET("pengeluaranUser")
    suspend fun getPengeluaranUser(@Header("Authorization") token: String) : DefaultResponse

    @GET("pemasukanUser")
    suspend fun getPemasukanUser(@Header("Authorization") token: String) : DefaultResponse


}
