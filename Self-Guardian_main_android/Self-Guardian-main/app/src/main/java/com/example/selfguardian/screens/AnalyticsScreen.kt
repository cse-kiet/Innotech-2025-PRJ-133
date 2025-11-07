package com.example.selfguardian.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

// 🎨 Color palette
//val gradientTop = Color(0xFFB3E5FC)
//val gradientBottom = Color(0xFFE1BEE7)
//val primaryPurple = Color(0xFF7E57C2)
//val textColor = Color(0xFF3E3E3E)

@Composable
fun AnalyticsScreen() {
    var totalItems by remember { mutableStateOf(128) }
    var itemsExpired by remember { mutableStateOf(15) }
    var wastageReduced by remember { mutableStateOf(88) }

    var producePercent by remember { mutableStateOf(40f) }
    var dairyPercent by remember { mutableStateOf(30f) }
    var meatPercent by remember { mutableStateOf(20f) }
    var pantryPercent by remember { mutableStateOf(10f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            totalItems = Random.nextInt(100, 200)
            itemsExpired = Random.nextInt(5, 25)
            wastageReduced = Random.nextInt(70, 95)
            val randoms = listOf(
                Random.nextInt(10, 50),
                Random.nextInt(10, 40),
                Random.nextInt(10, 30),
                Random.nextInt(5, 20)
            )
            val sum = randoms.sum()
            producePercent = (randoms[0] * 100f / sum)
            dairyPercent = (randoms[1] * 100f / sum)
            meatPercent = (randoms[2] * 100f / sum)
            pantryPercent = (randoms[3] * 100f / sum)
        }
    }

    var selectedTab by remember { mutableStateOf("This Month") }
    val tabs = listOf("This Month", "Last 3 Months", "All Time")

    Scaffold(containerColor = Color.Transparent) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientTop, gradientBottom)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    "Analytics Dashboard",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryPurple
                )
                Spacer(modifier = Modifier.height(16.dp))

                SummaryCard("Total Items Tracked", "$totalItems")
                SummaryCard("Items Expired", "$itemsExpired")
                SummaryCard("Wastage Reduced", "$wastageReduced%", valueColor = primaryPurple)

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    tabs.forEach { tab ->
                        FilterChip(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            label = { Text(tab) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryPurple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                CategoryBreakdown(
                    producePercent = producePercent,
                    dairyPercent = dairyPercent,
                    meatPercent = meatPercent,
                    pantryPercent = pantryPercent
                )

                Spacer(modifier = Modifier.height(20.dp))

                UpcomingExpiriesChart()

                Spacer(modifier = Modifier.height(20.dp))

                ConsumptionTrendsChart()
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, valueColor: Color = textColor) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 14.sp, color = textColor.copy(alpha = 0.7f))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun CategoryBreakdown(producePercent: Float, dairyPercent: Float, meatPercent: Float, pantryPercent: Float) {
    val animatedProduce = animateFloatAsState(targetValue = producePercent)
    val animatedDairy = animateFloatAsState(targetValue = dairyPercent)
    val animatedMeat = animateFloatAsState(targetValue = meatPercent)
    val animatedPantry = animateFloatAsState(targetValue = pantryPercent)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Category Breakdown",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = primaryPurple,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Canvas(modifier = Modifier.size(130.dp)) {
                val stroke = 28f
                var startAngle = -90f
                val segments = listOf(
                    Color(0xFF2196F3) to animatedProduce.value,
                    Color(0xFF9C27B0) to animatedDairy.value,
                    Color(0xFFE91E63) to animatedMeat.value,
                    Color(0xFF4CAF50) to animatedPantry.value
                )
                for ((color, percent) in segments) {
                    val sweep = 360f * (percent / 100f)
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    startAngle += sweep
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CategoryRows("Produce", "${producePercent.roundToInt()}%", Color(0xFF2196F3))
                CategoryRows("Dairy", "${dairyPercent.roundToInt()}%", Color(0xFF9C27B0))
                CategoryRows("Meat", "${meatPercent.roundToInt()}%", Color(0xFFE91E63))
                CategoryRows("Pantry", "${pantryPercent.roundToInt()}%", Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
fun CategoryRows(name: String, percent: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text("$name ($percent)", fontSize = 13.sp, color = textColor)
    }
}

@Composable
fun UpcomingExpiriesChart() {
    val months = listOf("Jun", "Jul", "Aug", "Sep")
    val values = listOf(30, 60, 45, 25)
    val maxValue = values.maxOrNull() ?: 0

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Upcoming Expiries", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = primaryPurple)
            Spacer(modifier = Modifier.height(16.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 8.dp)
            ) {
                val barWidth = size.width / (values.size * 2)
                values.forEachIndexed { index, value ->
                    val barHeight = (value / maxValue.toFloat()) * size.height
                    drawRoundRect(
                        color = primaryPurple,
                        topLeft = Offset(
                            x = index * barWidth * 2 + barWidth / 2,
                            y = size.height - barHeight
                        ),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                months.forEach { Text(it, fontSize = 13.sp, color = textColor) }
            }
        }
    }
}

@Composable
fun ConsumptionTrendsChart() {
    val usedData = listOf(30, 45, 35, 60, 55, 70, 60, 75, 80, 65, 70, 90)
    val expiredData = listOf(20, 30, 25, 45, 35, 55, 50, 60, 70, 55, 50, 65)
    val months = listOf("Jan", "Feb", "Mar", "Apr")

    val maxValue = maxOf(usedData.maxOrNull() ?: 0, expiredData.maxOrNull() ?: 0)

    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        progress = 0f
        while (progress < 1f) {
            progress += 0.02f
            delay(30)
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Consumption Trends", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = primaryPurple)
            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(horizontal = 12.dp)
            ) {
                val stepX = size.width / (usedData.size - 1)
                val scaleY = size.height / maxValue.toFloat()

                val usedPath = Path().apply {
                    moveTo(0f, size.height - usedData[0] * scaleY)
                    for (i in 1 until (usedData.size * progress).toInt()) {
                        lineTo(i * stepX, size.height - usedData[i] * scaleY)
                    }
                }
                drawPath(
                    path = usedPath,
                    color = Color(0xFF4CAF50),
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )

                val expiredPath = Path().apply {
                    moveTo(0f, size.height - expiredData[0] * scaleY)
                    for (i in 1 until (expiredData.size * progress).toInt()) {
                        lineTo(i * stepX, size.height - expiredData[i] * scaleY)
                    }
                }

                val dottedEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                drawPath(
                    path = expiredPath,
                    color = Color(0xFFE53935),
                    style = Stroke(width = 3.5f, pathEffect = dottedEffect, cap = StrokeCap.Round)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LegendDot(color = Color(0xFF4CAF50), text = "Used")
                Spacer(modifier = Modifier.width(20.dp))
                LegendDot(color = Color(0xFFE53935), text = "Expired")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                months.forEach { Text(it, fontSize = 13.sp, color = textColor) }
            }
        }
    }
}

@Composable
fun LegendDot(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontSize = 13.sp, color = textColor)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAnalyticsScreen() {
    AnalyticsScreen()
}
