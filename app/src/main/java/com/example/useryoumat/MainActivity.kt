package com.example.useryoumat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.useryoumat.presentation.screens.BookAppointmentScreen
import com.example.useryoumat.presentation.screens.LoginScreen
import com.example.useryoumat.presentation.viewModel.AppointmentViewModel
import com.example.useryoumat.presentation.viewModel.AuthViewModel
import com.example.useryoumat.ui.theme.UserYouMatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UserYouMatTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    val authViewModel: AuthViewModel = viewModel()
                    val appointmentViewModel: AppointmentViewModel = viewModel()

                    val currentUser by authViewModel.user.collectAsState()
                    val isChecking by authViewModel.isChecking.collectAsState()

                    //  Auto-check session once
                    LaunchedEffect(Unit) {
                        authViewModel.checkLoginSession()
                    }

                    when {
                        isChecking -> {
                            //Show loader
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        currentUser == null -> {
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = {
                                    // no-op
                                }
                            )
                        }

                        else -> {
                            BookAppointmentScreen(
                                user = currentUser!!,
                                viewModel = appointmentViewModel,
                                onBooked = {
                                    // Optional: show snackbar or toast

                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
