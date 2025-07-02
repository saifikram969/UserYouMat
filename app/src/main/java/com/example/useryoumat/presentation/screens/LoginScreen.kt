package com.example.useryoumat.presentation.screens

import android.app.Activity
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.useryoumat.data.firebase.GoogleAuthUiClient
import com.example.useryoumat.presentation.viewModel.AuthViewModel
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    val googleClient = remember { GoogleAuthUiClient(context) }

    val user by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()

    // Google Sign-In launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val credential = googleClient.getSignInCredential(result.data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                viewModel.loginWithCredential(firebaseCredential)
            } else {
                viewModel.setError("Google ID Token was null")
            }
        } else {
            viewModel.setError("Google Sign-In cancelled")
        }
    }

    // If user is already logged in
    LaunchedEffect(user) {
        if (user != null) {
            onLoginSuccess()
        }
    }

    // UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            user?.let {
                Image(
                    painter = rememberAsyncImagePainter(it.profilePicUrl),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
                Text(text = "Hello, ${it.name}")
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { viewModel.logout() }) {
                    Text("Logout")
                }
            } ?: run {
                Button(onClick = {
                    googleClient.getSignInIntent { intentSender: IntentSender ->
                        val request = IntentSenderRequest.Builder(intentSender).build()
                        launcher.launch(request)
                    }
                }) {
                    Text("Sign in with Google")
                }

                error?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
