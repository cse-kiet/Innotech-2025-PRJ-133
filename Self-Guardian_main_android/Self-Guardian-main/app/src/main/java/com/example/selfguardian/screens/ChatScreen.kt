package com.example.selfguardian.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val message: String,
    val isUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController? = null) {
    val gradientTop = Color(0xFFB3E5FC)
    val gradientBottom = Color(0xFFE1BEE7)
    val primaryPurple = Color(0xFF7E57C2)
    val textColor = Color(0xFF3E3E3E)

    var chatList by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputValue by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Chatbot", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryPurple
                )
            )
        },
        bottomBar = {
            ChatInputField(
                value = inputValue,
                onValueChange = { inputValue = it },
                onSend = {
                    if (inputValue.text.isNotBlank()) {
                        val userMessage = ChatMessage(inputValue.text, true)
                        chatList = chatList + userMessage
                        inputValue = TextFieldValue("")

                        // scroll after sending
                        coroutineScope.launch {
                            delay(100)
                            listState.animateScrollToItem(chatList.size)
                        }

                        // fake AI reply
                        coroutineScope.launch {
                            delay(600)
                            chatList = chatList + ChatMessage(
                                message = generateBotReply(userMessage.message),
                                isUser = false
                            )
                            delay(200)
                            listState.animateScrollToItem(chatList.size)
                        }
                    }
                },
                buttonColor = primaryPurple
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(gradientTop, gradientBottom)
                    )
                )
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                reverseLayout = false
            ) {
                items(chatList.size) { index ->
                    ChatBubble(message = chatList[index], primaryPurple, textColor)
                }
                item { Spacer(modifier = Modifier.height(70.dp)) }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, primaryPurple: Color, textColor: Color) {
    val bubbleColor = if (message.isUser) primaryPurple else Color.White
    val bubbleTextColor = if (message.isUser) Color.White else textColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = if (message.isUser) 16.dp else 0.dp,
                        topEnd = if (message.isUser) 0.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(bubbleColor)
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.message,
                color = bubbleTextColor,
                fontSize = 15.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun ChatInputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSend: () -> Unit,
    buttonColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 34.dp), // 🔼 extra bottom padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF2F2F2))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 15.sp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(buttonColor)
                .clickable { onSend() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}

fun generateBotReply(userMessage: String): String {
    return when {
        "hi" in userMessage.lowercase() -> "Hello! How can I assist you today?"
        "help" in userMessage.lowercase() -> "Sure! Please tell me what you need help with."
        "thank" in userMessage.lowercase() -> "You're most welcome 😊"
        "bye" in userMessage.lowercase() -> "Goodbye! Take care 👋"
        else -> "I'm still learning! Please rephrase your question 🤖"
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    val navController = rememberNavController()
    ChatScreen(navController)
}
