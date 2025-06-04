package com.example.campingapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.campingapp.AuthSteps
import com.example.campingapp.api.RetrofitInstance
import com.example.campingapp.api.User
import com.example.campingapp.keystore.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthAuthorizationViewModel(application: Application) : AndroidViewModel(application) {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _step = MutableStateFlow(AuthSteps.LOGIN)
    val step: StateFlow<AuthSteps> = _step

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError
    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage: StateFlow<String?> = _authErrorMessage

    private fun validateEmail(email: String): String? {
        return if (email.isBlank()) {
            "Email не може бути порожнім"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Невірний формат email"
        } else {
            null
        }
    }

    private fun validatePassword(password: String): String? {
        return if (password.isBlank()) {
            "Пароль не може бути порожнім"
        } else {
            null
        }
    }

    fun onEmailFieldValueChanged(newValue: String) {
        _email.value = newValue
    }

    fun onPasswordFieldValueChanged(newValue: String) {
        _password.value = newValue
    }

    fun onLoginButtonClick() {
        _authErrorMessage.value = null
        val emailError = validateEmail(email.value)
        val passwordError = validatePassword(password.value)

        _emailError.value = emailError
        _passwordError.value = passwordError

        if (emailError == null && passwordError == null) {
            login(email.value, password.value)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val apiService = RetrofitInstance.getApiService(getApplication())
                val loginResponse = apiService.login(User(email, password))

                val token = loginResponse.token
                if (!token.isNullOrEmpty()) {
                    TokenManager.saveToken(getApplication<Application>().applicationContext, token)

                    val savedToken =
                        TokenManager.getToken(getApplication<Application>().applicationContext)
                    Log.d("TOKEN_CHECK", "Токен після збереження: $savedToken")
                    _authState.value = AuthState.Authenticated(token)
                    TokenManager.saveEmail(getApplication<Application>().applicationContext, email)

                    _authErrorMessage.value = null
                } else {
                    _authState.value = AuthState.Error("Отримано порожній токен")
                    Log.e("LOGIN_DEBUG", "Порожній токен у відповіді на вхід")
                    _authErrorMessage.value = "Отримано порожній токен, спробуйте пізніше"
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()

                _authErrorMessage.value = when {
                    errorBody?.contains("User not found") == true -> "Користувача не існує"
                    errorBody?.contains("Incorrect email or password") == true -> "Невірний email або пароль"
                    else -> "Сталася помилка: ${e.message()}"
                }
                _authState.value = AuthState.Error(_authErrorMessage.value ?: "Unknown error")
                Log.e("LOGIN_DEBUG", "HTTP exception: ${e.message()} / Body: $errorBody")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
                _authErrorMessage.value = e.message ?: "Помилка мережі або сервера"
                Log.e("LOGIN_DEBUG", "Не вдалося ввійти: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
}