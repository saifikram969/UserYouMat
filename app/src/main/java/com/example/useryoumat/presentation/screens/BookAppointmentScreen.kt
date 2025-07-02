package com.example.useryoumat.presentation.screens

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.useryoumat.data.model.AppointmentModel
import com.example.useryoumat.data.model.UserModel
import com.example.useryoumat.presentation.viewModel.AppointmentViewModel
import java.util.*

@Composable
fun BookAppointmentScreen(
    user: UserModel,
    viewModel: AppointmentViewModel,
    onBooked: () -> Unit
) {
    var issue by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }

    val bookingSuccess by viewModel.bookingSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(bookingSuccess) {
        if (bookingSuccess) onBooked()
    }

    fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDate = "$dayOfMonth/${month + 1}/$year"
                onDateSelected(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Book Your Appointment",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )

        TextField(
            value = issue,
            onValueChange = { issue = it },
            label = { Text("Issue Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showDatePicker(context) { date ->
                    selectedDate = date
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(if (selectedDate.isEmpty()) "Select Date" else selectedDate)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val appointment = AppointmentModel(
                    userId = user.uid,
                    userEmail = user.email,
                    counselorId = "counselor1",
                    issue = issue,
                    dateTime = selectedDate
                )
                viewModel.bookAppointment(appointment)
                // ✅ Show Toast here
                Toast.makeText(context, "Booking appointment...", Toast.LENGTH_SHORT).show()

            },
            enabled = selectedDate.isNotEmpty() && issue.isNotBlank(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Book Appointment")
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
