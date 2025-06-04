package com.example.campingapp.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.campingapp.api.Review

@Composable
fun ReviewItem(
    review: Review,
    currentUserId: String?,
    onSubmit: (Review) -> Unit,
    onEdit: (Review) -> Unit,
    onDelete: (Int) -> Unit
) {
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD6D5D8)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Оцінка: ${review.rating}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = review.message, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (currentUserId == review.user_id.toString()) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val editInteractionSource = remember { MutableInteractionSource() }
                    TextButton(
                        onClick = { onEdit(review) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Black,
                            containerColor = Color(0xFFD6D5D8)
                        ),
                        interactionSource = editInteractionSource
                    ) {
                        Text("Редагувати")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    val deleteInteractionSource = remember { MutableInteractionSource() }
                    TextButton(
                        onClick = { onDelete(review.id) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Red,
                            containerColor = Color(0xFFD6D5D8)
                        ),
                        interactionSource = deleteInteractionSource
                    ) {
                        Text("Видалити")
                    }
                }
            }
        }
    }
}