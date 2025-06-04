package com.example.campingapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.material3.Text
import com.example.campingapp.api.ApiService
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.campingapp.viewmodel.RegistrationViewModel
import com.example.campingapp.viewmodel.RegistrationViewModelFactory
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import com.example.campingapp.R
import com.example.campingapp.components.MainButton


@Composable
fun RegistrationScreen(
    navController: NavController,
    apiService: ApiService
) {
    val factory = remember { RegistrationViewModelFactory(apiService) }
    val viewModel: RegistrationViewModel = viewModel(factory = factory)
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Реєстрація",
            style = myStyle,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = viewModel.firstName,
            onValueChange = { viewModel.firstName = it },
            label = { Text("Ім'я", color = Color(0xFF2A7F62)) },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.lastName,
            onValueChange = { viewModel.lastName = it },
            label = { Text("Прізвище", color = Color(0xFF2A7F62)) },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email", color = Color(0xFF2A7F62)) },
            singleLine = true,
            colors = fieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.phoneNumber,
            onValueChange = { viewModel.phoneNumber = it },
            label = { Text("Номер телефону", color = Color(0xFF2A7F62)) },
            singleLine = true,
            colors = fieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Пароль", color = Color(0xFF2A7F62)) },
            singleLine = true,
            colors = fieldColors,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image =
                    if (passwordVisible) com.example.campingapp.R.drawable.invisible else R.drawable.visible
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

        viewModel.errorMessage?.let { errorMsg ->
            Text(
                text = errorMsg,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        MainButton(
            onClick = {
                viewModel.onRegister {
                    navController.navigate("auth") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            },
            enabled = !viewModel.isLoading
        ) {
            Text(
                if (viewModel.isLoading) "Зачекайте..." else "Зареєструватися",
                style = TextStyle(fontWeight = FontWeight.Bold, color = Color.White)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("auth") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.White,
                contentColor = Color.White,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.White,
            )
        ) {
            Text(
                "Вже є акаунт? Увійти",
                color = Color(0xFF2A7F62)
            )
        }
    }
}
