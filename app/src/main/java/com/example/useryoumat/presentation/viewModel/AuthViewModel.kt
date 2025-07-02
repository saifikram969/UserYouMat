package com.example.useryoumat.presentation.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.useryoumat.data.firebase.FirebaseAuthRepository
import com.example.useryoumat.data.model.UserModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val authRepo = FirebaseAuthRepository(app.applicationContext)

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isChecking = MutableStateFlow(true)  //  New: to control loading
    val isChecking: StateFlow<Boolean> = _isChecking

    init {
        fetchFcmToken()
    }

    fun loginWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            authRepo.signInWithCredential(
                credential,
                onSuccess = {
                    _user.value = it
                    _isChecking.value = false
                },
                onFailure = {
                    _error.value = it
                    _isChecking.value = false
                }
            )
        }
    }

    fun getCurrentUser() {
        authRepo.getCurrentUser { fetchedUser ->
            _user.value = fetchedUser
            _isChecking.value = false
        }
    }

    fun logout() {
        authRepo.signOut()
        _user.value = null
        _isChecking.value = false
    }

    fun setError(message: String) {
        _error.value = message
    }

    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d("FCMToken", "User FCM token: $token")
        }.addOnFailureListener {
            Log.e("FCMToken", "Failed to fetch FCM token", it)
        }
    }

    // Auto-login function with loading state
    fun checkLoginSession() {
        _isChecking.value = true
        authRepo.getCurrentUser { fetchedUser ->
            _user.value = fetchedUser
            _isChecking.value = false
        }
    }
}
