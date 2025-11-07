package com.example.selfguardian.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfguardian.R
import kotlinx.coroutines.launch


data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboard1,
            title = "Effortless Upload",
            description = "Snap a photo of your item, and we'll do the rest!"
        ),
        OnboardingPage(
            imageRes = R.drawable.onboard2,
            title = "Smart Detection",
            description = "Our AI instantly identifies expiry dates, no manual entry needed."
        ),
        OnboardingPage(
            imageRes = R.drawable.onboard3,
            title = "Timely Alerts & Chat",
            description = "Get reminders before items expire and ask our AI about your inventory."
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }) {
                        Text("Back", color = primaryPurple, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Spacer(modifier = Modifier.width(64.dp))
                }

                TextButton(onClick = { onGetStartedClick() }) {
                    Text("Skip", color = primaryPurple, fontWeight = FontWeight.Medium)
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    repeat(pages.size) { index ->
                        val color =
                            if (pagerState.currentPage == index) primaryPurple
                            else primaryPurple.copy(alpha = 0.3f)
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                // 🔘 Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.lastIndex) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onGetStartedClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.lastIndex)
                            "Get Started"
                        else
                            "Next",
                        fontSize = 17.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientTop, gradientBottom)
                    )
                )
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                OnboardingPageContent(pages[page])
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = page.title,
            modifier = Modifier
                .size(230.dp)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = page.title,
            fontSize = 24.sp,
            color = primaryPurple,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}
