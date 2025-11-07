package com.example.selfguardian.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfguardian.R

data class ExpiringItems(
    val name: String,
    val category: String,
    val date: String,
    val daysLeft: Int,
    @DrawableRes val icon: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(item: ExpiringItems, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${item.name} Details",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryPurple)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Edit logic */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = primaryPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.8f))
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(gradientTop, gradientBottom)
                    )
                )
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = item.icon),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1.1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.8f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 📦 Item Name
            Text(
                text = item.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = primaryPurple
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🏷️ Category Tag
            Box(
                modifier = Modifier
                    .background(primaryPurple.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(item.category, color = primaryPurple, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ⚠️ Urgency Section
            val urgencyColor = when {
                item.daysLeft <= 3 -> Color(0xFFFFEBEE)
                item.daysLeft <= 7 -> Color(0xFFFFF8E1)
                else -> Color(0xFFE8F5E9)
            }
            val urgencyTextColor = when {
                item.daysLeft <= 3 -> Color(0xFFD32F2F)
                item.daysLeft <= 7 -> Color(0xFFFFA000)
                else -> Color(0xFF388E3C)
            }
            val urgencyText = when {
                item.daysLeft <= 3 -> "High Urgency"
                item.daysLeft <= 7 -> "Medium Urgency"
                else -> "Low Urgency"
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = urgencyColor.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        urgencyText,
                        color = urgencyTextColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Expires on: ${item.date}",
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                    Text(
                        "Expires in ${item.daysLeft} days",
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 🗑️ Delete Button
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCDD2)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Item", color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailScreenPreview() {
    val demoItem = ExpiringItems(
        name = "Greek Yogurt",
        category = "Dairy",
        date = "November 12, 2025",
        daysLeft = 3,
        icon = R.drawable.ic_launcher_background
    )
    ItemDetailScreen(
        item = demoItem,
        onBack = {}
    )
}
