package com.example.selfguardian.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HelpScreen() {

    // 🎨 Theme Colors
    val gradientTop = Color(0xFFB3E5FC)
    val gradientBottom = Color(0xFFE1BEE7)
    val primaryPurple = Color(0xFF7E57C2)
    val textColor = Color(0xFF3E3E3E)

    val scrollState = rememberScrollState()

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
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {

                Text(
                    text = "Help & Support",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryPurple
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Frequently Asked Questions",
                    fontWeight = FontWeight.SemiBold,
                    color = primaryPurple,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                FAQItem(
                    question = "How to reset my password?",
                    answer = "Go to Profile → tap on 'Reset Password'.",
                    primaryPurple = primaryPurple,
                    textColor = textColor
                )
                FAQItem(
                    question = "How can I edit my profile?",
                    answer = "Open Profile screen and tap the edit icon.",
                    primaryPurple = primaryPurple,
                    textColor = textColor
                )
                FAQItem(
                    question = "How can I delete my account?",
                    answer = "Go to Profile → 'Delete Account' section.",
                    primaryPurple = primaryPurple,
                    textColor = textColor
                )
                FAQItem(
                    question = "How do I contact support?",
                    answer = "Use the Contact Support buttons below.",
                    primaryPurple = primaryPurple,
                    textColor = textColor
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Contact Support",
                    fontWeight = FontWeight.SemiBold,
                    color = primaryPurple,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                SupportCard(
                    icon = Icons.Default.Email,
                    title = "Email Us",
                    description = "support@selfguardian.com",
                    primaryPurple = primaryPurple,
                    textColor = textColor
                )

                SupportCard(
                    icon = Icons.Default.Phone,
                    title = "Call Support",
                    description = "+91-9876543210",
                    primaryPurple = primaryPurple,
                    textColor = textColor
                )

                SupportCard(
                    icon = Icons.Default.Help,
                    title = "Chat with Support Bot",
                    description = "Get instant help through chat.",
                    primaryPurple = primaryPurple,
                    textColor = textColor
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "App Information",
                    fontWeight = FontWeight.SemiBold,
                    color = primaryPurple,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow("App Version", "1.0.0", textColor)
                InfoRow("Last Updated", "October 2025", textColor)
                InfoRow("Developed by", "Self Guardian Team", textColor)
            }
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String, primaryPurple: Color, textColor: Color) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                question,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = primaryPurple
            )
            if (expanded) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(answer, fontSize = 14.sp, color = textColor)
            }
        }
    }
}

@Composable
fun SupportCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    primaryPurple: Color,
    textColor: Color
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = primaryPurple)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium, color = textColor)
                Text(description, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium, color = textColor)
    }
}
