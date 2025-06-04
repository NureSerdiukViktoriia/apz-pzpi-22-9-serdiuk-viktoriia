package com.example.campingapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.TextFieldDefaults
import com.example.campingapp.api.ReviewRequest
import java.util.Date

@Composable
fun ReviewDisplay(
    onDismiss: () -> Unit,
    onSubmit: (ReviewRequest) -> Unit,
    userId: String,
    locationId: Int,
    initialMessage: String = "",
    initialRating: Int = 0
) {
    var message by remember { mutableStateOf(initialMessage) }
    var rating by remember { mutableStateOf(initialRating) }
    var messageError by remember { mutableStateOf<String?>(null) }
    var ratingError by remember { mutableStateOf<String?>(null) }

    val fieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color(0xFF2A7F62),
        unfocusedBorderColor = Color(0xFF2A7F62),
        focusedLabelColor = Color(0xFF2A7F62),
        unfocusedLabelColor = Color(0xFF2A7F62),
        cursorColor = Color(0xFF2A7F62),
        textColor = Color(0xFF2A7F62)
    )

    AlertDialog(
        onDismissRequest = onDismiss, title = {
            Text(
                text = if (initialMessage.isEmpty()) "Додати відгук" else "Редагувати відгук",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color.Black
            )
        }, text = {
            Column {
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                        if (!it.isBlank()) messageError = null
                    },
                    label = { Text("Повідомлення", color = Color(0xFF2A7F62)) },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 5,
                    shape = RoundedCornerShape(5.dp),
                    isError = false
                )
                if (messageError != null) {
                    Text(
                        text = messageError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Оцінка:", color = Color.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (i in 1..5) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    rating = i
                                    ratingError = null
                                }, contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFF2A7F62)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
                if (ratingError != null) {
                    Text(
                        text = ratingError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }, confirmButton = {
            TextButton(
                onClick = {
                    var hasError = false

                    if (message.isBlank()) {
                        messageError = "Повідомлення не може бути порожнім"
                        hasError = true
                    }
                    if (rating !in 1..5) {
                        ratingError = "Оцінка не може бути порожня"
                        hasError = true
                    }

                    if (hasError) return@TextButton

                    val reviewRequest = ReviewRequest(
                        user_id = userId.toInt(),
                        location_id = locationId,
                        message = message,
                        rating = rating,
                        date = Date()
                    )
                    onSubmit(reviewRequest)
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A7F62), contentColor = Color.White
                ), shape = RoundedCornerShape(5.dp)
            ) {
                Text("Зберегти")
            }
        }, dismissButton = {
            TextButton(
                onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red, contentColor = Color.White
                ), shape = RoundedCornerShape(5.dp)
            ) {
                Text("Скасувати")
            }
        }, containerColor = Color.White
    )
}
