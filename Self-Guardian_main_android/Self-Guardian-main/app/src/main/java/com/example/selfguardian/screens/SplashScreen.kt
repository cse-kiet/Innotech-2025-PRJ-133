@file:OptIn(ExperimentalAnimationApi::class)
package com.example.selfguardian.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfguardian.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    val primaryPurple = Color(0xFF7E57C2)
    val gradientTop = Color(0xFFB3E5FC)
    val gradientBottom = Color(0xFFE1BEE7)
    val textColor = Color(0xFF3E3E3E)

    var showLogo by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(400)
        showLogo = true
        delay(700)
        showText = true
        delay(1800)
        onTimeout()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = gradientTop
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientTop, gradientBottom)
                    )
                )
        ) {

            Box(
                modifier = Modifier
                    .size(280.dp)
                    .offset(x = (-70).dp, y = (-90).dp)
                    .clip(CircleShape)
                    .background(primaryPurple.copy(alpha = 0.10f))
                    .blur(100.dp)
                    .align(Alignment.TopStart)
            )
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .offset(x = (70).dp, y = (100).dp)
                    .clip(CircleShape)
                    .background(primaryPurple.copy(alpha = 0.10f))
                    .blur(100.dp)
                    .align(Alignment.BottomEnd)
            )


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = showLogo,
                    enter = fadeIn(animationSpec = tween(700)) + scaleIn(initialScale = 0.8f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.innotech_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                AnimatedVisibility(
                    visible = showText,
                    enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { it / 4 })
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Self Guardian",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryPurple,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Never let your essentials expire again",
                            fontSize = 16.sp,
                            color = textColor,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}
