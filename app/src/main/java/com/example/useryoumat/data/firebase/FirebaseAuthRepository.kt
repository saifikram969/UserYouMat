package com.example.useryoumat.data.firebase

import android.content.Context
import android.util.Log
import com.example.useryoumat.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.auth.AuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseAuthRepository(
    private val context: Context
) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    //  Updated: getCurrentUser now reads FCM token from Firestore
    fun getCurrentUser(onReady: (UserModel?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    val model = UserModel(
                        uid = user.uid,
                        name = user.displayName ?: "",
                        email = user.email ?: "",
                        profilePicUrl = user.photoUrl?.toString() ?: "",
                        fcmToken = doc.getString("fcmToken") ?: ""
                    )
                    onReady(model)
                }
                .addOnFailureListener {
                    Log.e("USER_FETCH", "Error fetching Firestore user: ${it.localizedMessage}")
                    onReady(null)
                }
        } else {
            onReady(null)
        }
    }

    fun signInWithCredential(
        credential: AuthCredential,
        onSuccess: (UserModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                val firebaseUser = it.user

                if (firebaseUser != null) {
                    //  Get FCM token
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                        Log.d("FCM_TOKEN", "FCM Token retrieved: $token")

                        val user = UserModel(
                            uid = firebaseUser.uid,
                            name = firebaseUser.displayName ?: "",
                            email = firebaseUser.email ?: "",
                            profilePicUrl = firebaseUser.photoUrl?.toString() ?: "",
                            fcmToken = token // ✅ Save FCM token
                        )

                        //  Save user with token to Firestore
                        db.collection("users").document(user.uid).set(user)
                            .addOnSuccessListener {
                                onSuccess(user)
                            }
                            .addOnFailureListener { e ->
                                onFailure("Error saving user: ${e.localizedMessage}")
                            }

                    }.addOnFailureListener { fcmError ->
                        onFailure("Failed to get FCM token: ${fcmError.localizedMessage}")
                    }
                } else {
                    onFailure("User is null")
                }
            }
            .addOnFailureListener { ex ->
                onFailure(ex.message ?: "Login failed")
            }
    }

    fun signOut() {
        auth.signOut()
    }
}
