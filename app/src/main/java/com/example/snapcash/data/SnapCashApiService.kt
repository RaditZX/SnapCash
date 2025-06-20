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
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse

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
        @Query("nominalMax") max: Int,
        @Query("search") searcQuery: String
    ): DefaultResponse

    @GET("pemasukanUser")
    suspend fun getPemasukanUser(
        @Header("Authorization") token: String,
        @Query("kategori") kategori: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("nominalMin") min: Int,
        @Query("nominalMax") max: Int,
        @Query("search") searcQuery: String
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

    @DELETE("pemasukan/delete/{id}")
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

    @POST("signout")
    suspend fun signOut(): DefaultResponse

    @GET("kategoriUser")
    suspend fun getAllCategories(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null,
        @Query("isPengeluaran") isPengeluaran: Boolean? = null
    ): DefaultResponse

    @GET("kategoriUser/{id}")
    suspend fun getCategoryById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): generateTextFromInvoiceResponse

    @POST("kategoriUser/add")
    suspend fun addCategory(
        @Header("Authorization") token: String,
        @Body data: JsonObject
    ): generateTextFromInvoiceResponse

    @PUT("kategoriUser/update/{id}")
    suspend fun updateCategory(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body data: JsonObject
    ): generateTextFromInvoiceResponse

    @DELETE("kategoriUser/delete/{id}")
    suspend fun deleteCategory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): generateTextFromInvoiceResponse

    @POST("resetPassword")
    suspend fun resetPassword(
        @Body request: resetRequest
    ): DefaultResponse2
}
