package com.example.useryoumat.presentation.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.useryoumat.data.model.AppointmentModel
import com.example.useryoumat.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppointmentViewModel : ViewModel() {

    private val repo = AppointmentRepository()

    private val _bookingSuccess = MutableStateFlow(false)
    val bookingSuccess: StateFlow<Boolean> = _bookingSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun bookAppointment(appointment: AppointmentModel) {
        viewModelScope.launch {
            repo.bookAppointment(
                appointment,
                onSuccess = { _bookingSuccess.value = true },
                onFailure = { msg -> _error.value = msg }
            )
        }
    }
}
