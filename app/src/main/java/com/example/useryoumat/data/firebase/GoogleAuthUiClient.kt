package com.example.useryoumat.data.firebase

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.useryoumat.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential

class GoogleAuthUiClient(private val context: Context) {

    private val oneTapClient: SignInClient = Identity.getSignInClient(context)

    private val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(context.getString(R.string.web_client_id)) //  Replace from Firebase Console
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    // Returns IntentSender to launch One Tap UI
    fun getSignInIntent(callback: (IntentSender) -> Unit) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                callback(result.pendingIntent.intentSender)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    // Extract Google credential from result Intent
    fun getSignInCredential(data: Intent?): SignInCredential {
        return oneTapClient.getSignInCredentialFromIntent(data)
    }

}
