package com.example.snapcash.data
import com.google.gson.JsonObject

data class SignInRequest(
    val email: String,
    val password: String
)

data class SignUpResponse(
    val data: UserCredentialData,  // Adjust according to your API response
    val message: String,
    val isSucces : Boolean
)

data class SignInResponse(
    val data: UserCredentialData,
    val message: String,
    val isSucces : Boolean,
)


data class UserCredentialData(
    val userCredential: UserCredential
)

data class token(
    val idToken : String
)

data class UserCredential(
    val user: User,
    val _tokenResponse : token
)

data class generateTextFromInvoiceResponse(
    val message: String,
    val isSucces: Boolean,
    val data: JsonObject
)

data class DefaultResponse(
    val message: String,
    val isSucces: Boolean,
    val data: Array<JsonObject>
)

data class User(
    val uid: String,
    val email: String,
    val emailVerified: Boolean,
    val isAnonymous: Boolean,
    val providerData: List<ProviderData>,
    val stsTokenManager: StsTokenManager
)

data class ProviderData(
    val providerId: String,
    val uid: String,
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoURL: String?
)

data class StsTokenManager(
    val refreshToken: String,
    val accessToken: String,
    val expirationTime: Long
)

data class Transaction(
    val title: String,
    val category: String,
    val amount: Int,
    val date: String,
    val id: String,
    val isPengeluaran: Boolean
)

data class FilterModel(
    val min: Int = 0,
    val max: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val kategori: String = ""
)

data class userResponse(
    val data: userData,
    val message: String,
    val isSucces: Boolean
)

data class userData(
    val email: String,
    val username: String?,
    val currencyChoice: String?,
    val foto: String?,
    val no_hp: String?
)

data class currencyResponse(
    val data: currencyData,
    val message: String,
    val isSucces: Boolean
)

data class currencyData(
    val currency_code: String,
    val locale: String,
    val currency_symbol: String
)

data class DashboardResponse(
    val message: String,
    val isSucces: Boolean,
    val data: DashboardData
)

data class DashboardData(
    val total: Int,
    val totalTahunSebelumnya: Int,
    val perubahanPersentase: Int,
    val perubahanTotal: Int,
    val TotalByKategori: Map<String, Int>,
    val TotalByRange: Map<String, Int>
)
