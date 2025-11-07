package com.example.selfguardian.screens

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.selfguardian.R
import java.util.*

// 🎨 Color palette (same as onboarding)
//val gradientTop = Color(0xFFB3E5FC)
//val gradientBottom = Color(0xFFE1BEE7)
//val primaryPurple = Color(0xFF7E57C2)
//val textColor = Color(0xFF3E3E3E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onSave: (Item) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var itemName by remember { mutableStateOf(TextFieldValue("")) }
    var category by remember { mutableStateOf("Food") }
    var expiryDate by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            expiryDate = String.format("%02d/%02d/%d", day, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(gradientTop, gradientBottom)
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🟪 Upload Options Row
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .padding(vertical = 8.dp)
            ) {
                TabButtonWithIcon("Upload", Icons.Default.Upload, selectedTab == 0) {
                    selectedTab = 0
                    imagePickerLauncher.launch("image/*")
                }
                TabButtonWithIcon("Image", Icons.Default.Image, selectedTab == 1) { selectedTab = 1 }
                TabButtonWithIcon("Scan", Icons.Default.QrCodeScanner, selectedTab == 2) { selectedTab = 2 }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🖼 Image Preview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Placeholder",
                                tint = Color.Gray,
                                modifier = Modifier.size(80.dp)
                            )
                            Text("No image selected", color = textColor.copy(alpha = 0.6f), fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 📝 Item Name
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name", color = primaryPurple) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryPurple,
                    unfocusedBorderColor = primaryPurple.copy(alpha = 0.4f),
                    cursorColor = primaryPurple
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🧾 Category & Date Picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }
                val categories = listOf("Food", "Medicine", "Other")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        label = { Text("Category", color = primaryPurple) },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryPurple,
                            unfocusedBorderColor = primaryPurple.copy(alpha = 0.4f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    category = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = {},
                    label = { Text("Expiry Date", color = primaryPurple, fontSize = 14.sp) },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick Date", tint = primaryPurple)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryPurple,
                        unfocusedBorderColor = primaryPurple.copy(alpha = 0.4f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 💜 Save Button
            Button(
                onClick = {
                    if (itemName.text.isNotEmpty() && expiryDate.isNotEmpty()) {
                        onSave(
                            Item(
                                name = itemName.text,
                                category = category,
                                expiryDate = expiryDate,
                                imageUri = imageUri
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple)
            ) {
                Text("Save Item", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// 🧩 Data Model
data class Item(
    val name: String,
    val category: String,
    val expiryDate: String,
    val imageUri: Uri?
)

// 🟣 Tab Button Component
@Composable
fun TabButtonWithIcon(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected) primaryPurple.copy(alpha = 0.1f) else Color.Transparent
    val textColor = if (selected) primaryPurple else textColor

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(bgColor)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Icon(icon, contentDescription = text, tint = textColor, modifier = Modifier.size(24.dp))
        Text(text, color = textColor, fontSize = 13.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun AddItemScreenPreview() {
    AddItemScreen()
}
