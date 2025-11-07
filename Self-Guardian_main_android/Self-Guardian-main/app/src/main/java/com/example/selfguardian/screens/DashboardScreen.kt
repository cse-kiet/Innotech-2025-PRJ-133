package com.example.selfguardian.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.selfguardian.R
import kotlin.math.roundToInt

data class ExpiringItem(
    val name: String,
    val category: String,
    val date: String,
    val daysLeft: Int,
    val icon: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {

    var expiringItems by remember {
        mutableStateOf(
            listOf(
                ExpiringItem("Milk", "Food", "29 Oct 2024", 2, R.drawable.ic_launcher_foreground),
                ExpiringItem("Painkillers", "Medicine", "01 Nov 2024", 5, R.drawable.ic_launcher_foreground),
                ExpiringItem("Eggs", "Food", "03 Nov 2024", 7, R.drawable.ic_launcher_foreground),
                ExpiringItem("Soap", "Others", "15 Nov 2024", 10, R.drawable.ic_launcher_foreground)
            )
        )
    }

    val totalItems = expiringItems.size
    val expiringSoon = expiringItems.count { it.daysLeft <= 3 }

    val foodCount = expiringItems.count { it.category == "Food" }
    val medicineCount = expiringItems.count { it.category == "Medicine" }
    val othersCount = expiringItems.count { it.category == "Others" }

    val foodPercent = if (totalItems > 0) (foodCount * 100f / totalItems) else 0f
    val medicinePercent = if (totalItems > 0) (medicineCount * 100f / totalItems) else 0f
    val othersPercent = if (totalItems > 0) (othersCount * 100f / totalItems) else 0f

    Scaffold(
        containerColor = Color(0xFFF6F7FB),
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp, end = 16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("chat") },
                    containerColor = Color(0xFF7E57C2),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Face, contentDescription = "Chatbot", tint = Color.White)
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp)
        ) {
            item {
                // 🔹 Top Row (Removed background)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.innotech_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(70.dp))
                            .size(50.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "Shelf Guardian",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryPurple
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.Black,
                        modifier = Modifier.clickable { }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("Hi Divya👋", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryPurple)
                        Text("Here's what's happening today", color = Color.Gray)
                    }
                    IconButton(onClick = { /* Settings click */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard(
                        title = "Items expiring soon",
                        value = "$expiringSoon items",
                        subtitle = "this week",
                        bgColor = Color(0xFFFFF3E0)
                    )
                    SummaryCard(
                        title = "Total items tracked",
                        value = "$totalItems total",
                        subtitle = "items",
                        bgColor = Color(0xFFE3F2FD)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                CategoryBreakdown(foodPercent, medicinePercent, othersPercent, totalItems)
                Spacer(modifier = Modifier.height(20.dp))
                Text("Upcoming Expiries", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(expiringItems.sortedBy { it.daysLeft }) { item ->
                ExpiryCard(item){
                    navController.navigate(
                        "itemDetail/${item.name}/${item.category}/${item.date}/${item.daysLeft}"
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            item { Spacer(modifier = Modifier.height(90.dp)) }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, subtitle: String, bgColor: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 13.sp, color = Color.Gray)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CategoryBreakdown(foodPercent: Float, medicinePercent: Float, othersPercent: Float, totalItems: Int) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Category Breakdown",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.align(alignment = Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Canvas(modifier = Modifier.size(100.dp)) {
                    val stroke = 25f
                    var startAngle = -90f
                    val totalAngle = 360f
                    val segments = listOf(
                        Color(0xFF2E8B75) to foodPercent,
                        Color(0xFF7E57C2) to medicinePercent,
                        Color(0xFFFFC107) to othersPercent
                    )

                    for ((color, percent) in segments) {
                        val sweep = totalAngle * (percent / 100)
                        drawArc(color = color, startAngle = startAngle, sweepAngle = sweep, useCenter = false, style = Stroke(stroke))
                        startAngle += sweep
                    }
                }

                Spacer(modifier = Modifier.width(50.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Total Items: $totalItems", fontWeight = FontWeight.Medium)
                    CategoryRow("Food", "${foodPercent.roundToInt()}%", Color(0xFF2E8B75))
                    CategoryRow("Medicine", "${medicinePercent.roundToInt()}%", Color(0xFF7E57C2))
                    CategoryRow("Others", "${othersPercent.roundToInt()}%", Color(0xFFFFC107))
                }
            }
        }
    }
}

@Composable
fun CategoryRow(name: String, percent: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("$name ($percent)", fontSize = 13.sp, color = Color.Gray)
    }
}

@Composable
fun ExpiryCard(item: ExpiringItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = item.name,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Medium)
                Text(item.date, fontSize = 12.sp, color = Color.Gray)
            }
            Box(
                modifier = Modifier
                    .background(Color(0xFFFFE8E0), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("${item.daysLeft} days left", color = Color(0xFFD84315), fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    val navController = rememberNavController()
    DashboardScreen(navController)
}