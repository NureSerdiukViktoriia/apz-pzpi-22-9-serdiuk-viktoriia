package com.example.campingapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.auth0.android.jwt.JWT
import com.example.campingapp.api.Location
import com.example.campingapp.api.Booking
import com.example.campingapp.api.Review
import com.example.campingapp.api.ReviewRequest
import com.example.campingapp.components.ReviewDisplay
import com.example.campingapp.components.ReviewItem
import com.example.campingapp.keystore.TokenManager
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.campingapp.services.createBooking
import com.example.campingapp.services.createReview
import com.example.campingapp.services.deleteReview
import com.example.campingapp.services.fetchLocation
import com.example.campingapp.services.getReviewsByLocationId
import com.example.campingapp.services.updateReview
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.util.Date
import androidx.compose.material.OutlinedTextField

@Composable
fun LocationDetailsScreen(locationId: Int) {
    val context = LocalContext.current
    var showReviewDialog by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf<Location?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val token = TokenManager.getToken(context)
    val decodedJWT = token?.let { JWT(it) }
    val userId = decodedJWT?.getClaim("id")?.asString()
    var editingReview by remember { mutableStateOf<Review?>(null) }
    var bookingSuccess by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var startDate by remember { mutableStateOf(TextFieldValue("")) }
    var endDate by remember { mutableStateOf(TextFieldValue("")) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var feedbackMessage by remember { mutableStateOf(false) }
    var feedbackMessageUpdate by remember { mutableStateOf(false) }

    LaunchedEffect(locationId, token) {
        if (token == null || userId == null) {
            errorMessage = "Користувач не авторизований"
            isLoading = false
            return@LaunchedEffect
        }
        try {
            location = fetchLocation(context, locationId)
            reviews = getReviewsByLocationId(context, locationId)
        } catch (e: Exception) {
            errorMessage = e.localizedMessage
        } finally {
            isLoading = false
        }
    }

    val daysBetween: Long? = try {
        val start = LocalDate.parse(startDate.text, dateFormatter)
        val end = LocalDate.parse(endDate.text, dateFormatter)
        ChronoUnit.DAYS.between(start, end).takeIf { it > 0 }
    } catch (e: Exception) {
        null
    }
    val fieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color(0xFF2A7F62),
        unfocusedBorderColor = Color(0xFF2A7F62),
        focusedLabelColor = Color(0xFF2A7F62),
        cursorColor = Color(0xFF2A7F62),
        textColor = Color(0xFF2A7F62)
    )
    val totalPrice = daysBetween?.let { it.toFloat() * (location?.price ?: 0f) } ?: 0f
    val scrollState = rememberScrollState()

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        errorMessage != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Помилка: $errorMessage", color = MaterialTheme.colorScheme.error)
            }
        }

        location != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = location!!.name,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                val imageUrl = "http://192.168.0.103:5000${location!!.image}"
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = location!!.name,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Тип: ")
                        }
                        append(location!!.type)
                    })
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Опис: ")
                        }
                        append(location!!.description)
                    })
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Ціна: ")
                        }
                        append("${location!!.price} грн")
                    })
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Місткість: ")
                        }
                        append("${location!!.max_capacity} людини")
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (location!!.availability) "Доступне до бронювання" else "Заброньовано",
                        color = Color(0xFFFF0000)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Дата початку (yyyy-mm-dd)", color = Color(0xFF2E7D32)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Дата кінця (yyyy-mm-dd)", color = Color(0xFF2E7D32)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (location!!.availability) {
                    Text(
                        "Загальна ціна: $totalPrice грн",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                if (token == null || userId == null) {
                                    Toast.makeText(
                                        context,
                                        "Користувач не авторизований",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }
                                if (daysBetween == null) {
                                    Toast.makeText(
                                        context,
                                        "Невірний формат дат або кінцева дата раніше початкової",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@launch
                                }
                                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val bookingRequest = Booking(
                                    location_id = location!!.id,
                                    user_id = userId.toInt(),
                                    start_date = formatter.parse(startDate.text),
                                    end_date = formatter.parse(endDate.text),
                                    total_price = totalPrice.toFloat(),
                                    payment_status = "pending",
                                    payment_date = Date()
                                )
                                createBooking(context, token, bookingRequest)
                                bookingSuccess = true
                            } catch (e: Exception) {
                                Toast.makeText(context, "Помилка: ${e.message}", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    },
                    enabled = location!!.availability && daysBetween != null && daysBetween > 0,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3160B2),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFB0B0B0),
                        disabledContentColor = Color.White
                    )

                ) {
                    Text(
                        "Забронювати",
                        color = Color.White
                    )
                }
                if (bookingSuccess) {
                    Text(
                        text = "Локація успішно заброньована!",
                        color = Color(0xFF2A7F62),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Відгуки:", style = MaterialTheme.typography.headlineSmall)
                if (feedbackMessage) {
                    Text(
                        text = "Відгук додано!",
                        color = Color(0xFF2A7F62),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (feedbackMessageUpdate) {
                    Text(
                        text = "Відгук оновлено!",
                        color = Color(0xFF2A7F62),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                reviews.forEach { review ->
                    ReviewItem(
                        review = review,
                        currentUserId = userId ?: "",
                        onSubmit = { updatedReview ->
                            val reviewRequest = ReviewRequest(
                                user_id = updatedReview.user_id,
                                location_id = updatedReview.location_id,
                                rating = updatedReview.rating,
                                message = updatedReview.message,
                                date = updatedReview.date
                            )
                            coroutineScope.launch {
                                updateReview(context, token!!, editingReview!!.id, reviewRequest)
                            }
                        },
                        onEdit = { review ->
                            editingReview = review
                        },

                        onDelete = { reviewId ->
                            coroutineScope.launch {
                                try {
                                    if (token == null) {
                                        Toast.makeText(
                                            context,
                                            "Користувач не авторизований",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@launch
                                    }
                                    deleteReview(context, token, reviewId)
                                    reviews = getReviewsByLocationId(context, locationId)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Помилка видалення: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    )
                }
                if (editingReview != null) {
                    ReviewDisplay(
                        onDismiss = { editingReview = null },
                        onSubmit = { reviewRequest ->
                            coroutineScope.launch {
                                try {
                                    if (token == null) {
                                        Toast.makeText(
                                            context,
                                            "Користувач не авторизований",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@launch
                                    }
                                    updateReview(context, token, editingReview!!.id, reviewRequest)
                                    reviews = getReviewsByLocationId(context, locationId)
                                    editingReview = null
                                    feedbackMessageUpdate = true
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Помилка оновлення: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        userId = userId!!,
                        locationId = locationId,
                        initialMessage = editingReview!!.message,
                        initialRating = editingReview!!.rating
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showReviewDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A7F62),
                        contentColor = Color.White
                    )
                ) {
                    Text("Додати відгук")
                }

                if (showReviewDialog) {
                    ReviewDisplay(
                        onDismiss = { showReviewDialog = false },
                        onSubmit = { reviewRequest ->
                            coroutineScope.launch {
                                try {
                                    if (token == null) {
                                        Toast.makeText(
                                            context,
                                            "Користувач не авторизований",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@launch
                                    }
                                    createReview(context, token, reviewRequest)
                                    reviews = getReviewsByLocationId(context, locationId)
                                    feedbackMessage = true
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Помилка: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        userId = userId!!,
                        locationId = locationId
                    )
                }
            }
        }
    }
}
