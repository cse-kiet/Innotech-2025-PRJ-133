package com.example.selfguardian.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// 🎨 Color theme (same as other screens)
//val gradientTop = Color(0xFFB3E5FC)
//val gradientBottom = Color(0xFFE1BEE7)
//val primaryPurple = Color(0xFF7E57C2)
//val accentGreen = Color(0xFF4CAF50)
//val textColor = Color(0xFF3E3E3E)

data class Items(
    val name: String,
    val category: String,
    val expiryDate: LocalDate
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(
    items: List<Items>,
) {
    var selectedCategory by remember { mutableStateOf("All") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Expiring Soon",
                        fontWeight = FontWeight.Bold,
                        color = primaryPurple
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = gradientTop
                ),
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        val today = LocalDate.now()

        val filtered = items.filter {
            selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
        }

        val sorted = filtered.sortedBy { it.expiryDate }

        val expiringSoon = sorted.filter {
            ChronoUnit.DAYS.between(today, it.expiryDate) >= 0
        }

        // 🌈 Gradient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(gradientTop, gradientBottom)
                    )
                )
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // 🟣 Category Filter Buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("All", "Food", "Medicine", "Other").forEach { cat ->
                        val selected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selected)
                                        primaryPurple.copy(alpha = 0.15f)
                                    else
                                        Color.White.copy(alpha = 0.5f)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cat,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) primaryPurple else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🧾 No Items Message
                if (expiringSoon.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(primaryPurple.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Done",
                                tint = primaryPurple,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "All clear!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                        Text(
                            "Nothing is expiring soon.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {

                    // 📋 Expiring Items List
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(expiringSoon) { item ->
                            val daysLeft = ChronoUnit.DAYS.between(today, item.expiryDate).toInt()
                            val (label, labelColor, bgColor) = when {
                                daysLeft == 0 -> Triple("Expires Today", Color.White, Color(0xFFFF6F61))
                                daysLeft <= 3 -> Triple("$daysLeft days left", Color.White, Color(0xFFFFC107))
                                daysLeft <= 10 -> Triple("$daysLeft days left", Color.White, Color(0xFF81C784))
                                else -> Triple("$daysLeft days left", Color.White, primaryPurple)
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            item.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 17.sp,
                                            color = textColor
                                        )
                                        Text(
                                            "Expires on ${item.expiryDate}",
                                            color = Color.Gray,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(bgColor, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            color = labelColor,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AlertScreenDemo() {
    val sampleItems = listOf(
        Items("Yogurt", "Food", LocalDate.now()),
        Items("Milk", "Food", LocalDate.now().plusDays(3)),
        Items("Aspirin", "Medicine", LocalDate.now().plusDays(10)),
        Items("Sunscreen", "Other", LocalDate.now().plusDays(17))
    )

    AlertScreen(items = sampleItems)
}
