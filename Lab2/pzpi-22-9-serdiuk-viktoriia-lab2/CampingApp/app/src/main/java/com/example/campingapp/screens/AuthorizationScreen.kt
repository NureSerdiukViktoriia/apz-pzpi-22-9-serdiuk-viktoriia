package com.example.campingapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavController
import com.example.campingapp.AuthSteps
import com.example.campingapp.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campingapp.viewmodel.AuthAuthorizationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campingapp.components.MainButton
import com.example.campingapp.viewmodel.AuthState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.Text
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.*

@Composable
fun AuthorizationScreen(
    navController: NavController,
    viewModel: AuthAuthorizationViewModel = viewModel()
) {
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val authErrorMessage by viewModel.authErrorMessage.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val loading by viewModel.loading.collectAsState()
    val step by viewModel.step.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val authState by viewModel.authState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val myStyle = TextStyle(
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2A7F62)
    )
    val fieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color(0xFF2A7F62),
        unfocusedBorderColor = Color(0xFF2A7F62),
        focusedLabelColor = Color(0xFF2A7F62),
        cursorColor = Color(0xFF2A7F62),
        textColor = Color(0xFF2A7F62)
    )
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
                .clip(RoundedCornerShape(16.dp))
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Вхід",
                style = myStyle,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (step) {
                AuthSteps.LOGIN -> {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { viewModel.onEmailFieldValueChanged(it) },
                        label = { Text("Пошта", color = Color(0xFF2A7F62)) },
                        singleLine = true,
                        colors = fieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.onPasswordFieldValueChanged(it) },
                        label = { Text("Пароль", color = Color(0xFF2A7F62)) },
                        singleLine = true,
                        colors = fieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image =
                                if (passwordVisible) R.drawable.invisible else R.drawable.visible
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(id = image),
                                    contentDescription = if (passwordVisible) "Сховати пароль" else "Показати пароль"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MainButton(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.onLoginButtonClick()
                        }
                    ) {
                        Text(
                            text = "Вхід",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        )
                    }

                    emailError?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    passwordError?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    authErrorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                AuthSteps.FINAL -> {
                    navController.navigate("home")
                }

                else -> {
                    Text(
                        text = "Помилка",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (loading) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Завантаження")
            }
        }
    }
}