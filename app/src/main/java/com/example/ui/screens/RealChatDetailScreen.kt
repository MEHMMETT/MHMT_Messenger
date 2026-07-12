package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.components.UserAvatar
import com.example.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealChatDetailScreen(viewModel: ChatViewModel) {
    val peerName by viewModel.realChatPeerName.collectAsState()
    val peerAvatar by viewModel.realChatPeerAvatar.collectAsState()
    val messages by viewModel.realMessages.collectAsState()
    val error by viewModel.realChatError.collectAsState()
    val myId = viewModel.myUserId

    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.size - 1) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(avatarUrl = peerAvatar, name = peerName, size = 36.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(peerName, color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.closeRealChat() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0C10))
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(Color(0xFF0A0C10))) {
                if (error != null) {
                    Text(
                        text = error ?: "",
                        color = Color(0xFFFF8A8A),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("پیام...", color = Color.White.copy(alpha = 0.4f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFD1E1FF),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (input.isNotBlank()) {
                                viewModel.sendRealMessage(input)
                                input = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFFD1E1FF))
                    }
                }
            }
        },
        containerColor = Color(0xFF0A0C10)
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                val isMine = message.senderId == myId
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (isMine) Color(0xFFD1E1FF) else Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = message.content,
                            color = if (isMine) Color(0xFF0A0C10) else Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}
