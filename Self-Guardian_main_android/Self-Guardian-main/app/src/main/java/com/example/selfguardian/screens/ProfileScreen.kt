package com.example.selfguardian.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

// 🎨 Custom color palette
val gradientTop = Color(0xFFB3E5FC)
val gradientBottom = Color(0xFFE1BEE7)
val primaryPurple = Color(0xFF7E57C2)
val textColor = Color(0xFF3E3E3E)

@Composable
fun ProfileScreen() {
    var username by remember { mutableStateOf("Rishika Nigam") }
    var email by remember { mutableStateOf("rishika243@gmail.com") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isEditingUsername by remember { mutableStateOf(false) }
    var isEditingEmail by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val gradient = Brush.verticalGradient(colors = listOf(gradientTop, gradientBottom))

    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Profile Picture
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, primaryPurple, CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(selectedImageUri)
                                    .build()
                            ),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Icon",
                            tint = primaryPurple,
                            modifier = Modifier.size(70.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-8).dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(primaryPurple)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Edit Photo",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(username, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textColor)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Personal Information",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = primaryPurple,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                EditableInfoCard(
                    label = "Username",
                    value = username,
                    isEditing = isEditingUsername,
                    onEditClick = { isEditingUsername = !isEditingUsername },
                    onValueChange = { username = it }
                )

                EditableInfoCard(
                    label = "Email",
                    value = email,
                    isEditing = isEditingEmail,
                    onEditClick = { isEditingEmail = !isEditingEmail },
                    onValueChange = { email = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Account Management",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = primaryPurple,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(10.dp))

                ActionCard(icon = Icons.Default.ExitToApp, text = "Logout", textColor = textColor)
                ActionCard(icon = Icons.Default.Delete, text = "Delete Account", textColor = Color.Red)

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        isEditingUsername = false
                        isEditingEmail = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Save Changes", fontSize = 16.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun EditableInfoCard(
    label: String,
    value: String,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onValueChange: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 13.sp, color = Color.Gray)
                if (isEditing) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryPurple,
                            cursorColor = primaryPurple
                        )
                    )
                } else {
                    Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = textColor)
                }
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = primaryPurple)
            }
        }
    }
}

@Composable
fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    textColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = text, tint = textColor)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text, fontWeight = FontWeight.Medium, color = textColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen()
}
