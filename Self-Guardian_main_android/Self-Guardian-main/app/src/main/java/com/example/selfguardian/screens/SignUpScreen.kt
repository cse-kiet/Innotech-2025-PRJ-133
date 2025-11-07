package com.example.selfguardian.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfguardian.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun SignUpScreen(
    context: Activity,
    onSignUpSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var signUpError by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    // Google Sign-In setup
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount? = task.getResult(Exception::class.java)
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { firebaseTask ->
                    if (firebaseTask.isSuccessful) {
                        signUpError = ""
                        onSignUpSuccess()
                    } else {
                        signUpError = firebaseTask.exception?.localizedMessage ?: "Google Sign-In failed"
                    }
                }
            }
        } catch (e: Exception) {
            signUpError = e.localizedMessage ?: "Google Sign-In failed"
        }
    }

    val gradientTop = Color(0xFFB3E5FC)
    val gradientBottom = Color(0xFFE1BEE7)
    val primaryPurple = Color(0xFF7E57C2)
    val textColor = Color(0xFF3E3E3E)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(gradientTop, gradientBottom))),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .width(420.dp)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.innotech_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 8.dp)
                )

                Text(
                    text = "Create Your Account",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryPurple
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email Field
                Text(
                    text = "Email",
                    color = textColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 4.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter your email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryPurple,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = primaryPurple
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password Field
                Text(
                    text = "Password",
                    color = textColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 4.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Enter your password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = "Toggle password visibility", tint = primaryPurple)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryPurple,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = primaryPurple
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password Field
                Text(
                    text = "Confirm Password",
                    color = textColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 4.dp)
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm your password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(icon, contentDescription = "Toggle confirm password visibility", tint = primaryPurple)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryPurple,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = primaryPurple
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Email/Password Sign Up Button
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            signUpError = "Please fill all fields"
                        } else if (password != confirmPassword) {
                            signUpError = "Passwords do not match"
                        } else {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        signUpError = ""
                                        onSignUpSuccess()
                                    } else {
                                        signUpError = task.exception?.localizedMessage ?: "Sign Up failed"
                                    }
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryPurple)
                ) {
                    Text("Sign Up", color = Color.White, fontSize = 16.sp)
                }

                if (signUpError.isNotEmpty()) {
                    Text(
                        text = signUpError,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("or", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(14.dp))

                // Google Sign-In Button
                OutlinedButton(
                    onClick = { launcher.launch(googleSignInClient.signInIntent) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_img),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continue with Google", color = textColor)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.Center) {
                    Text("Already have an account? ", color = Color.Gray)
                    Text(
                        text = "Log In",
                        color = primaryPurple,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }
        }
    }
}
