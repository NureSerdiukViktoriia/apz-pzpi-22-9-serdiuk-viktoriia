package com.example.campingapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.campingapp.api.Location
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.campingapp.api.RetrofitInstance
import com.example.campingapp.R

@Composable
fun HomeScreen(navController: NavController, context: Context) {
    val apiService = remember { RetrofitInstance.getApiService(context) }
    var locations by remember { mutableStateOf<List<Location>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            locations = apiService.getAllLocations()
        } catch (e: Exception) {
            errorMessage = "Помилка: ${e.message}"

        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Всі локації:",
                        color = Color(0xFF2A7F62),
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Профіль",
                            tint = Color(0xFF2A7F62),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = "logo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Кемпінги",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color(0xFF2A7F62)
                                )
                                Text(
                                    text = "Місце, де кожен відпочинок стає незабутнім",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }

                        items(locations) { location ->
                            LocationItem(location = location, onDetailsClick = {
                                navController.navigate("locationDetails/${location.id}")
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationItem(location: Location, onDetailsClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD6D5D8))
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            val imageUrl = "http://192.168.0.103:5000${location.image}"
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = location.name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = location.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ціна: ${location.price} грн",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onDetailsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A7F62),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(bottom = 10.dp, start = 10.dp, end = 10.dp, top = 10.dp)
            ) {
                Text(
                    text = "Детальніше",
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}
