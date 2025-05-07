package com.example.snapcash.data
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

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
