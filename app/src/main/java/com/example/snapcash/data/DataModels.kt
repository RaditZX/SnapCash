package com.example.snapcash.data

import com.google.gson.annotations.SerializedName

data class SignInRequest(
    val email: String,
    val password: String
)

data class SignUpResponse(
    val data: SignInRequest,  // Adjust according to your API response
    val message: String
)

data class SignInResponse(
    val data: UserCredentialData,
    val message: String
)

data class UserCredentialData(
    val userCredential: UserCredential
)

data class UserCredential(
    val user: User
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
