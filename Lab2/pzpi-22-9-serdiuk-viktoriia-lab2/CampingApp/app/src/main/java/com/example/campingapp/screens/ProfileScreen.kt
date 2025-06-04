package com.example.campingapp.screens

import android.util.Log
import com.example.campingapp.services.fetchUser
import com.example.campingapp.services.updateUser
import com.example.campingapp.services.deleteUser
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.auth0.android.jwt.JWT
import com.example.campingapp.api.UserData
import com.example.campingapp.keystore.TokenManager
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val token = TokenManager.getToken(context)
    val coroutineScope = rememberCoroutineScope()
    Log.d("TOKEN_CHECK", "Token: $token")
    val greenColor = Color(0xFF2A7F62)

    val decodedJWT = token?.let { JWT(it) }
    val userId = decodedJWT?.getClaim("id")?.asString()

    var userData by remember { mutableStateOf(UserData()) }
    var editMode by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        if (userId == null) {
            navController.navigate("auth")
        } else {
            try {
                val user = fetchUser(context, userId)

                userData = user
            } catch (e: Exception) {
                TokenManager.clearToken(context)
                navController.navigate("auth")
            }
        }
    }
    fun validate(): Boolean {
        var valid = true
        if (userData.first_name.isBlank()) {
            firstNameError = "Ім'я не може бути порожнім"
            valid = false
        } else if (!userData.first_name.matches(Regex("^[А-Яа-яЇїІіЄєҐґA-Za-z]+$"))) {
            firstNameError = "Ім'я може містити лише літери"
            valid = false
        } else {
            firstNameError = null
        }

        if (userData.last_name.isBlank()) {
            lastNameError = "Прізвище не може бути порожнім"
            valid = false
        } else if (!userData.last_name.matches(Regex("^[А-Яа-яЇїІіЄєҐґA-Za-z]+$"))) {
            lastNameError = "Прізвище може містити лише літери"
            valid = false
        } else {
            lastNameError = null
        }
        if (userData.email.isBlank()) {
            emailError = "Email не може бути порожнім"
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userData.email).matches()) {
            emailError = "Невірний формат email"
            valid = false
        } else {
            emailError = null
        }

        val phonePattern = Regex("^\\+?\\d{10,15}$")
        if (userData.phone_number.isBlank()) {
            phoneError = "Номер телефону не може бути порожнім"
            valid = false
        } else if (!phonePattern.matches(userData.phone_number)) {
            phoneError = "Невірний формат телефону"
            valid = false
        } else {
            phoneError = null
        }

        return valid
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Профіль",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(25.dp))
        val textWidth = 290.dp
        if (!editMode) {
            Box(modifier = Modifier.width(textWidth)) {
                Text(
                    "Ім'я: ${userData.first_name}",
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.width(textWidth)) {
                Text(
                    "Прізвище: ${userData.last_name}",
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.width(textWidth)) {
                Text(
                    "Email: ${userData.email}",
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.width(textWidth)) {
                Text(
                    "Номер телефону: ${userData.phone_number}",
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { editMode = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF2A7F62),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text("Редагувати")
            }
        } else {
            if (error.isNotEmpty()) {
                Text(error, color = Color.Red)
            }

            OutlinedTextField(
                value = userData.first_name,
                onValueChange = { userData = userData.copy(first_name = it) },
                label = { Text("Ім’я") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = greenColor,
                    unfocusedBorderColor = greenColor,
                    cursorColor = greenColor,
                    focusedLabelColor = greenColor,
                    unfocusedLabelColor = greenColor
                )
            )
            if (firstNameError != null) {
                Text(text = firstNameError!!, color = Color.Red, fontSize = 12.sp)
            }
            OutlinedTextField(
                value = userData.last_name,
                onValueChange = { userData = userData.copy(last_name = it) },
                label = { Text("Прізвище") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = greenColor,
                    unfocusedBorderColor = greenColor,
                    cursorColor = greenColor,
                    focusedLabelColor = greenColor,
                    unfocusedLabelColor = greenColor
                )
            )
            if (lastNameError != null) {
                Text(text = lastNameError!!, color = Color.Red, fontSize = 12.sp)
            }
            OutlinedTextField(
                value = userData.email,
                onValueChange = { userData = userData.copy(email = it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = greenColor,
                    unfocusedBorderColor = greenColor,
                    cursorColor = greenColor,
                    focusedLabelColor = greenColor,
                    unfocusedLabelColor = greenColor
                )
            )
            if (emailError != null) {
                Text(text = emailError!!, color = Color.Red, fontSize = 12.sp)
            }
            OutlinedTextField(
                value = userData.phone_number,
                onValueChange = { userData = userData.copy(phone_number = it) },
                label = { Text("Номер телефону") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = greenColor,
                    unfocusedBorderColor = greenColor,
                    cursorColor = greenColor,
                    focusedLabelColor = greenColor,
                    unfocusedLabelColor = greenColor
                )
            )
            if (phoneError != null) {
                Text(text = phoneError!!, color = Color.Red, fontSize = 12.sp)
            }
            Row {
                Button(
                    onClick = {
                        if (validate()) {
                            coroutineScope.launch {
                                try {
                                    updateUser(context, userId!!, userData)
                                    editMode = false
                                    error = ""
                                } catch (e: Exception) {
                                    error = e.message ?: "Помилка оновлення користувача"
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF2A7F62),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Зберегти")
                }

                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { editMode = false },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Скасувати")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                TokenManager.clearToken(context)
                navController.navigate("register") {
                    popUpTo("profile") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF3160B2),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text("Вийти з акаунту")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        deleteUser(context, userId!!)
                        TokenManager.clearToken(context)
                        navController.navigate("auth") {
                            popUpTo("profile") { inclusive = true }
                        }
                    } catch (e: Exception) {
                        error = e.message ?: "Помилка видалення користувача"
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Red,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Видалити акаунт", color = Color.White)
        }
    }
}