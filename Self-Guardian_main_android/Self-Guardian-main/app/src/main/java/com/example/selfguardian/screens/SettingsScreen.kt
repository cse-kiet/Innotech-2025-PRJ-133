package com.example.selfguardian.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var notifyBeforeExpiry by remember { mutableStateOf(true) }
    var dailySummary by remember { mutableStateOf(false) }

    // 🎨 Theme colors
    val gradientTop = Color(0xFFB3E5FC)
    val gradientBottom = Color(0xFFE1BEE7)
    val primaryPurple = Color(0xFF7E57C2)
    val textColor = Color(0xFF3E3E3E)

    Scaffold(containerColor = Color.Transparent) { paddingValues ->
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
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {
                Text(
                    "Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryPurple
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("NOTIFICATIONS", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                SettingItem(
                    title = "Notify 3 days before expiry",
                    icon = Icons.Default.Notifications,
                    primaryPurple = primaryPurple,
                    textColor = textColor,
                    trailing = {
                        Checkbox(
                            checked = notifyBeforeExpiry,
                            onCheckedChange = { notifyBeforeExpiry = it },
                            colors = CheckboxDefaults.colors(checkedColor = primaryPurple)
                        )
                    }
                )

                SettingItem(
                    title = "Daily summary alert",
                    icon = Icons.Default.Alarm,
                    primaryPurple = primaryPurple,
                    textColor = textColor,
                    trailing = {
                        Checkbox(
                            checked = dailySummary,
                            onCheckedChange = { dailySummary = it },
                            colors = CheckboxDefaults.colors(checkedColor = primaryPurple)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text("APP", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                SettingItem(
                    title = "Version",
                    icon = Icons.Default.Info,
                    trailingText = "1.0.0",
                    primaryPurple = primaryPurple,
                    textColor = textColor
                )

                SettingItem(
                    title = "About",
                    icon = Icons.Default.Info,
                    primaryPurple = primaryPurple,
                    textColor = textColor
                ) {}

                SettingItem(
                    title = "Contact Support",
                    icon = Icons.Default.Email,
                    primaryPurple = primaryPurple,
                    textColor = textColor
                ) {}
            }
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trailingText: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    primaryPurple: Color,
    textColor: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = primaryPurple)
                Spacer(modifier = Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.Medium, color = textColor)
            }
            when {
                trailing != null -> trailing()
                trailingText != null -> Text(trailingText, color = Color.Gray)
                else -> Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}
