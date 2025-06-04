package com.example.campingapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campingapp.api.ApiService
import com.example.campingapp.api.RegistrationRequest
import kotlinx.coroutines.launch

class RegistrationViewModel(private val apiService: ApiService) : ViewModel() {

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    private fun validate(): Boolean {
        if (firstName.isBlank()) {
            errorMessage = "Ім'я не може бути порожнім"
            return false
        }
        if (firstName.any { it.isDigit() }) {
            errorMessage = "Ім'я не може містити цифри"
            return false
        }
        if (lastName.isBlank()) {
            errorMessage = "Прізвище не може бути порожнім"
            return false
        }
        if (lastName.any { it.isDigit() }) {
            errorMessage = "Прізвище не може містити цифри"
            return false
        }
        if (email.isBlank()) {
            errorMessage = "Email не може бути порожнім"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Невірний формат email"
            return false
        }
        if (phoneNumber.isBlank()) {
            errorMessage = "Номер телефону не може бути порожнім"
            return false
        }
        val phonePattern = Regex("^\\+?\\d{10,15}$")
        if (!phonePattern.matches(phoneNumber)) {
            errorMessage = "Невірний формат номера телефону"
            return false
        }
        if (password.isBlank()) {
            errorMessage = "Пароль не може бути порожнім"
            return false
        }
        return true
    }

    fun onRegister(onSuccess: () -> Unit) {
        if (!validate()) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val request = RegistrationRequest(
                first_name = firstName,
                last_name = lastName,
                email = email,
                phone_number = phoneNumber,
                password = password,
                role = "user"
            )

            try {
                val response = apiService.registerUser(request)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()

                    errorMessage = when {
                        response.code() == 409 || (errorBody?.contains(
                            "email already exists", ignoreCase = true
                        ) == true) -> "Користувач з таким емейлом уже існує"
                        response.code() == 400 -> "Некоректні дані для реєстрації"
                        else -> "Помилка при реєстрації: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Помилка при реєстрації: ${e.message}"
            }
            isLoading = false
        }
    }
}