package com.example.snapcash.client

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthUiClient(
    private val context: Context
) {
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("623432809530-5rfk2opms5kf8q924se4qggb7s7s3aa9.apps.googleusercontent.com") // Ganti dengan Client ID kamu
        .requestEmail()
        .build()

    private val googleClient = GoogleSignIn.getClient(context, gso)

    fun getSignInIntent(): Intent = googleClient.signInIntent

    fun getIdTokenFromIntent(data: Intent?, onTokenReady: (String?) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val googleIdToken = account.idToken

            if (googleIdToken != null) {
                val credential = GoogleAuthProvider.getCredential(googleIdToken, null)

                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener {
                        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                            ?.addOnSuccessListener { result ->
                                val firebaseIdToken = result.token
                                onTokenReady(firebaseIdToken) // Kirim ke backend
                            }
                            ?.addOnFailureListener {
                                Log.e("GoogleLogin", "Gagal mendapatkan Firebase ID Token", it)
                                onTokenReady(null)
                            }
                    }
                    .addOnFailureListener {
                        Log.e("GoogleLogin", "Login Firebase gagal", it)
                        onTokenReady(null)
                    }
            } else {
                Log.e("GoogleLogin", "Google ID Token null")
                onTokenReady(null)
            }

        } catch (e: ApiException) {
            Log.e("GoogleLogin", "Gagal mengambil ID Token dari Google: ${e.statusCode}", e)
            onTokenReady(null)
        }
    }

    fun signOut(onComplete: () -> Unit) {
        googleClient.signOut().addOnCompleteListener {
            FirebaseAuth.getInstance().signOut()
            onComplete()
        }
    }

}
