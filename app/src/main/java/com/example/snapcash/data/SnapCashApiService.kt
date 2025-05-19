package com.example.snapcash.data

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
    suspend fun getPengeluaranUser(
        @Header("Authorization") token: String,
        @Query("kategori") kategori: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("nominalMin") min: Int,
        @Query("nominalMax") max: Int
    ): DefaultResponse

    @GET("pemasukanUser")
    suspend fun getPemasukanUser(
        @Header("Authorization") token: String,
        @Query("kategori") kategori: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("nominalMin") min: Int,
        @Query("nominalMax") max: Int
    ): DefaultResponse

    @GET("getDashboardAnalytics")
    suspend fun getDashboardAnalytics(
        @Header("Authorization") token: String,
        @Query("jenis") jenis: String = "Pemasukan",
        @Query("filter") filter: String = "tahun",
        @Query("tahun") tahun: Int,
        @Query("bulan") bulan: Int? = null,
        @Query("hari") hari: Int? = null
    ): DashboardResponse

    @GET("pemasukanUser/{id}")
    suspend fun getPemasukanUserById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): generateTextFromInvoiceResponse

    @GET("pengeluaranUser/{id}")
    suspend fun getPengeluaranUserById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): generateTextFromInvoiceResponse

    @PUT("/pengeluaranUser/update/{id}")
    suspend fun updatePengeluaranById(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body data: JsonObject
    ): generateTextFromInvoiceResponse

    @PUT("/pemasukanUser/update/{id}")
    suspend fun updatePemasukanById(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body data: JsonObject
    ): generateTextFromInvoiceResponse

    @POST("pengeluaranUser")
    suspend fun addPengeluaran(
        @Header("Authorization") token: String,
        @Body data: JsonObject
    ): generateTextFromInvoiceResponse

    @POST("pemasukanUser")
    suspend fun addPemasukan(
        @Header("Authorization") token: String,
        @Body data: JsonObject
    ): generateTextFromInvoiceResponse

    @DELETE("pemasukanUser/delete/{id}")
    suspend fun deletePemasukanById(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): generateTextFromInvoiceResponse

    @DELETE("pengeluaranUser/delete/{id}")
    suspend fun deletePengeluaranById(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): generateTextFromInvoiceResponse

    @GET("currency")
    suspend fun getCurrency(
        @Header("Authorization") token: String,
    ) : DefaultResponse

    @GET("getUser")
    suspend fun getUserData(
        @Header("Authorization") token: String,
    ) : userResponse

    @GET("currency/{id}")
    suspend fun getCurrencyData(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ) : currencyResponse

    @Multipart
    @PUT("updateProfile")
    suspend fun updateUserData(
        @Header("Authorization") token: String,
        @Part("username") username: RequestBody?,
        @Part("currencyChoice") currencyChoice: RequestBody?,
        @Part("no_hp") noHp: RequestBody?,
        @Part photo: MultipartBody.Part?
    ): userResponse

}
