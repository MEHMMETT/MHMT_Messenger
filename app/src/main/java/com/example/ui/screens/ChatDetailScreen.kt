package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.data.database.MessageEntity
import com.example.ui.components.UserAvatar
import com.example.ui.components.EmojiPickerPanel
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.ui.theme.PrimarySky
import com.example.ui.theme.PrimarySkyDark
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.Trans
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(viewModel: ChatViewModel) {
    val activeChatId by viewModel.activeChatId.collectAsState()
    val messages by viewModel.activeMessages.collectAsState(initial = emptyList())
    val contacts by viewModel.contacts.collectAsState(initial = emptyList())
    val chatsList by viewModel.chats.collectAsState(initial = emptyList())
    val settings by viewModel.settings.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val emojiFontFamily by viewModel.emojiFontFamily.collectAsState()

    val chatId = activeChatId ?: return
    val isFa = settings.language == "fa"
    val layoutDir = if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Find contact info or group info
    val chatInfo = chatsList.find { it.id == chatId }
    val contact = contacts.find { it.id == chatId }

    var textInput by remember { mutableStateOf("") }
    var showEmojiPicker by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Recording simulation states
    var isRecordingMode by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableStateOf(0) }
    
    var showWallpaperSelector by remember { mutableStateOf(false) }
    var showProfileDetail by remember { mutableStateOf(false) }
    var showAttachmentMenu by remember { mutableStateOf(false) }

    // Message actions & Viewers
    var editingMessage by remember { mutableStateOf<MessageEntity?>(null) }
    var activeFullScreenImageUrl by remember { mutableStateOf<String?>(null) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var showAddGroupMemberDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Back button interception to go back screen-by-screen
    BackHandler(enabled = true) {
        if (showProfileDetail) {
            showProfileDetail = false
        } else if (showEmojiPicker) {
            showEmojiPicker = false
        } else if (activeFullScreenImageUrl != null) {
            activeFullScreenImageUrl = null
        } else if (editingMessage != null) {
            editingMessage = null
            textInput = ""
        } else {
            viewModel.selectChat(null)
        }
    }

    // Launchers
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = saveUriToInternalStorage(context, uri, "img_${System.currentTimeMillis()}.jpg")
            if (savedPath != null) {
                viewModel.sendMessage(
                    content = if (settings.language == "fa") "📷 تصویر" else "📷 Photo",
                    type = "IMAGE",
                    mediaUrl = savedPath
                )
            }
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileName(context, uri) ?: "audio_${System.currentTimeMillis()}.mp3"
            val savedPath = saveUriToInternalStorage(context, uri, fileName)
            if (savedPath != null) {
                viewModel.sendMessage(
                    content = fileName,
                    type = "AUDIO",
                    mediaUrl = savedPath
                )
            }
        }
    }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { contactUri: Uri? ->
        if (contactUri != null) {
            val (name, phone) = queryContactDetails(context, contactUri)
            if (name != null) {
                viewModel.sendMessage(
                    content = if (settings.language == "fa") "👤 مخاطب: $name (${phone ?: "نامشخص"})" else "👤 Contact: $name (${phone ?: "Unknown"})",
                    type = "CONTACT"
                )
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
            try {
                val location = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude
                    val mapLink = "https://www.google.com/maps/search/?api=1&query=$lat,$lng"
                    viewModel.sendMessage(
                        content = if (settings.language == "fa") "📍 موقعیت زنده شما: $lat, $lng" else "📍 Your live location: $lat, $lng",
                        type = "LOCATION",
                        mediaUrl = mapLink
                    )
                } else {
                    Toast.makeText(context, if (settings.language == "fa") "موقعیت یافت نشد. لطفا GPS را روشن کنید." else "Location not found. Please enable GPS.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                // ignore
            }
        } else {
            Toast.makeText(context, if (settings.language == "fa") "دسترسی به موقعیت داده نشد." else "Location permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher for selecting a custom chat background from gallery
    val chatWallpaperLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = saveUriToInternalStorage(context, uri, "wallpaper_$chatId.jpg")
            if (savedPath != null) {
                viewModel.updateChatWallpaper(chatId, savedPath)
                Toast.makeText(context, if (settings.language == "fa") "پس‌زمینه گفتگو تغییر یافت!" else "Wallpaper updated!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Automatically scroll to bottom on new message
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // --- 1. Wallpaper Background Loader ---
            val activeWallpaper = chatInfo?.wallpaperValue ?: settings.wallpaperValue
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (activeWallpaper != null && (activeWallpaper.startsWith("/") || activeWallpaper.startsWith("content://") || activeWallpaper.startsWith("http"))) {
                    Image(
                        painter = rememberAsyncImagePainter(model = activeWallpaper),
                        contentDescription = "Chat Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Semi-transparent overlay to keep chat bubbles readable in both modes
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (settings.theme == "dark") Color.Black.copy(alpha = 0.5f)
                                else Color.White.copy(alpha = 0.35f)
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ChatWallpaper.getBrush(activeWallpaper ?: "bg_classic"))
                    )
                }
            }

            // --- Main Content Column ---
            Scaffold(
                modifier = Modifier.imePadding(),
                containerColor = Color.Transparent,
                topBar = {
                    // Glassmorphic Top Bar
                    val isDarkTheme = settings.theme == "dark"
                    val topBarBg = if (isDarkTheme) Color.Black.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.85f)
                    val topBarBorder = if (isDarkTheme) Color.White.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.4f)
                    val topBarText = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    val topBarSubText = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color(0xFF64748B)

                    Surface(
                        color = topBarBg,
                        border = BorderStroke(1.dp, topBarBorder),
                        shadowElevation = if (isDarkTheme) 0.dp else 2.dp
                    ) {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        showProfileDetail = true
                                    }
                                ) {
                                    UserAvatar(
                                        avatarUrl = chatInfo?.avatarUrl,
                                        name = chatInfo?.name ?: "",
                                        size = 40.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.border(1.dp, if (isDarkTheme) Color.White.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = chatInfo?.name ?: "",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = topBarText
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        // Online or Last Seen status
                                        val subtitleText = when {
                                            isTyping -> Trans.get(settings.language, "typing")
                                            chatInfo?.isGroup == true -> "4 ${Trans.get(settings.language, "members")}"
                                            contact?.isOnline == true -> Trans.get(settings.language, "online")
                                            else -> Trans.get(settings.language, "last_seen_hidden")
                                        }
                                        Text(
                                            text = subtitleText,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isTyping) Color(0xFFD1E1FF) else if (contact?.isOnline == true) Color(0xFF4ADE80) else topBarSubText,
                                                fontWeight = if (isTyping) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                    }
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = { viewModel.selectChat(null) }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = topBarText)
                                }
                            },
                            actions = {
                                IconButton(onClick = { showWallpaperSelector = true }) {
                                    Icon(Icons.Default.Palette, contentDescription = "Change Chat Theme", tint = topBarText)
                                }
                                if (chatInfo?.isGroup != true) {
                                    IconButton(onClick = { viewModel.startCall(chatId) }) {
                                        Icon(Icons.Default.Phone, contentDescription = "Simulated Call", tint = topBarText)
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                },
                bottomBar = {
                    // Message Input Area (Glassmorphic)
                    val isDarkTheme = settings.theme == "dark"
                    Surface(
                        color = if (isDarkTheme) Color.Black.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.95f),
                        border = BorderStroke(1.dp, if (isDarkTheme) Color.White.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (isRecordingMode) {
                                // Recording indicator panel
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .background(if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color.Red)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${Trans.get(settings.language, "recording")} ${viewModel.formatDuration(recordingDuration)}",
                                            color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                            fontSize = 14.sp
                                        )
                                    }
                                    Row {
                                        IconButton(onClick = { isRecordingMode = false }) {
                                            Icon(Icons.Default.Cancel, contentDescription = "Cancel", tint = Color.Red)
                                        }
                                        IconButton(
                                            onClick = {
                                                if (recordingDuration > 0) {
                                                    viewModel.sendMessage(
                                                        content = "🎙️ وویس صوتی (${viewModel.formatDuration(recordingDuration)})",
                                                        type = "VOICE",
                                                        duration = recordingDuration
                                                    )
                                                }
                                                isRecordingMode = false
                                            }
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Send Voice", tint = Color(0xFF22C55E))
                                        }
                                    }
                                }
                            } else {
                                // Normal Input Area (wrapped in Column to support Edit Banner)
                                Column {
                                    if (editingMessage != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.LightGray.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Editing",
                                                    tint = if (isDarkTheme) Color(0xFF93C5FD) else Color(0xFF1E3A8A),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = if (settings.language == "fa") "در حال ویرایش پیام..." else "Editing message...",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = if (isDarkTheme) Color(0xFF93C5FD) else Color(0xFF1E3A8A),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    editingMessage = null
                                                    textInput = ""
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Cancel Edit",
                                                    tint = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Gray,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Attachment Plus button
                                        IconButton(
                                            onClick = {
                                                showAttachmentMenu = true
                                            }
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Attachments", tint = if (isDarkTheme) Color.White else Color(0xFF1E3A8A))
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        // Main Text Field with Mixed RTL/LTR alignment fix
                                        OutlinedTextField(
                                            value = textInput,
                                            onValueChange = { textInput = it },
                                            placeholder = { Text(Trans.get(settings.language, "send_message"), color = if (isDarkTheme) Color.White.copy(alpha = 0.5f) else Color.Gray) },
                                            maxLines = 4,
                                            visualTransformation = remember(emojiFontFamily) { EmojiVisualTransformation(emojiFontFamily) },
                                            leadingIcon = {
                                                IconButton(
                                                    onClick = {
                                                        showEmojiPicker = !showEmojiPicker
                                                        if (showEmojiPicker) {
                                                            keyboardController?.hide()
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = if (showEmojiPicker) Icons.Default.Keyboard else Icons.Default.SentimentSatisfied,
                                                        contentDescription = "Toggle Emojis",
                                                        tint = if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color(0xFF1E3A8A)
                                                    )
                                                }
                                            },
                                            textStyle = TextStyle(
                                                textDirection = TextDirection.Content,
                                                fontSize = 16.sp,
                                                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                            ),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                                unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                                focusedBorderColor = if (isDarkTheme) Color.White.copy(alpha = 0.5f) else Color(0xFF1E3A8A),
                                                unfocusedBorderColor = if (isDarkTheme) Color.White.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.6f),
                                                focusedContainerColor = if (isDarkTheme) Color.White.copy(alpha = 0.08f) else Color.White,
                                                unfocusedContainerColor = if (isDarkTheme) Color.White.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.8f)
                                            ),
                                            shape = RoundedCornerShape(24.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .onFocusChanged { focusState ->
                                                    if (focusState.isFocused) {
                                                        showEmojiPicker = false
                                                    }
                                                }
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Send/Mic Button
                                        if (textInput.isNotBlank()) {
                                            FloatingActionButton(
                                                onClick = {
                                                    if (editingMessage != null) {
                                                        viewModel.editMessage(editingMessage!!.id, textInput)
                                                        editingMessage = null
                                                    } else {
                                                        viewModel.sendMessage(textInput)
                                                    }
                                                    textInput = ""
                                                },
                                                containerColor = if (isDarkTheme) Color(0xFFD1E1FF) else Color(0xFF1E3A8A),
                                                contentColor = Color.White,
                                                shape = CircleShape,
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Icon(Icons.Default.Send, contentDescription = "Send")
                                            }
                                        } else {
                                            // Simulated mic recorder trigger
                                            FloatingActionButton(
                                                onClick = {
                                                    isRecordingMode = true
                                                    recordingDuration = 0
                                                    coroutineScope.launch {
                                                        while (isRecordingMode) {
                                                            delay(1000)
                                                            recordingDuration++
                                                        }
                                                    }
                                                },
                                                containerColor = if (isDarkTheme) Color.White.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.2f),
                                                contentColor = if (isDarkTheme) Color.White else Color(0xFF1E3A8A),
                                                shape = CircleShape,
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Icon(Icons.Default.Mic, contentDescription = "Record Voice")
                                            }
                                        }
                                    }
                                }
                            }

                            if (showEmojiPicker) {
                                EmojiPickerPanel(
                                    onEmojiSelected = { emoji ->
                                        textInput += emoji
                                    },
                                    onBackspace = {
                                        if (textInput.isNotEmpty()) {
                                            try {
                                                val lastCodePointIndex = textInput.offsetByCodePoints(textInput.length, -1)
                                                textInput = textInput.substring(0, lastCodePointIndex)
                                            } catch (e: Exception) {
                                                textInput = textInput.dropLast(1)
                                            }
                                        }
                                    },
                                    onClose = {
                                        showEmojiPicker = false
                                    },
                                    isDarkTheme = isDarkTheme,
                                    language = settings.language,
                                    emojiFontFamily = emojiFontFamily,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                },
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Pinned Message Top Banner
                    val pinnedMessageId = chatInfo?.pinnedMessageId
                    if (pinnedMessageId != null) {
                        val pinnedMessage = messages.find { it.id == pinnedMessageId }
                        if (pinnedMessage != null) {
                            Surface(
                                color = if (settings.theme == "dark") Color(0xFF1E293B) else Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, if (settings.theme == "dark") Color.White.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val index = messages.indexOfFirst { it.id == pinnedMessageId }
                                        if (index >= 0) {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(index)
                                            }
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = "Pinned Message",
                                        tint = if (settings.theme == "dark") Color(0xFF93C5FD) else Color(0xFF1E3A8A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isFa) "پیام سنجاق شده" else "Pinned Message",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (settings.theme == "dark") Color(0xFF93C5FD) else Color(0xFF1E3A8A)
                                            )
                                        )
                                        Text(
                                            text = pinnedMessage.content,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = if (settings.theme == "dark") Color.White else Color(0xFF0F172A)
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.toggleMessagePinned(chatId, null) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Unpin",
                                            tint = if (settings.theme == "dark") Color.White.copy(alpha = 0.6f) else Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Messages List View
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(messages) { message ->
                            MessageBubble(
                                message = message,
                                settings = settings,
                                viewModel = viewModel,
                                onEditMessage = { msg ->
                                    editingMessage = msg
                                    textInput = msg.content
                                },
                                onFullScreenImage = { url ->
                                    activeFullScreenImageUrl = url
                                }
                            )
                        }

                        // Simulated typing indicator at the end
                        if (isTyping) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    GlassCard(
                                        alpha = 0.15f,
                                        cornerRadius = 16.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = Trans.get(settings.language, "typing"),
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showWallpaperSelector) {
                val isDarkTheme = settings.theme == "dark"
                AlertDialog(
                    onDismissRequest = { showWallpaperSelector = false },
                    title = {
                        Text(
                            text = Trans.get(settings.language, "set_wallpaper"),
                            style = MaterialTheme.typography.titleMedium.copy(color = if (isDarkTheme) Color.White else Color(0xFF0F172A))
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Real Phone Gallery Picker option
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDarkTheme) Color(0xFFD1E1FF).copy(alpha = 0.15f) else Color(0xFFD1E1FF).copy(alpha = 0.4f))
                                    .clickable {
                                        chatWallpaperLauncher.launch("image/*")
                                        showWallpaperSelector = false
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Gallery",
                                        tint = if (isDarkTheme) Color(0xFFD1E1FF) else Color(0xFF1E3A8A)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = if (settings.language == "fa") "انتخاب از گالری گوشی" else "Choose from Phone Gallery",
                                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }

                            val wallpapers = listOf(
                                Pair("bg_classic", Trans.get(settings.language, "wallpaper_classic")),
                                Pair("bg_neon", Trans.get(settings.language, "wallpaper_neon")),
                                Pair("bg_sunset", Trans.get(settings.language, "wallpaper_sunset")),
                                Pair("bg_forest", Trans.get(settings.language, "wallpaper_forest"))
                            )
                            wallpapers.forEach { (wpId, wpName) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.LightGray.copy(alpha = 0.2f))
                                        .clickable {
                                            viewModel.updateChatWallpaper(chatId, wpId)
                                            showWallpaperSelector = false
                                            Toast.makeText(context, if (settings.language == "fa") "پس‌زمینه گفتگو تغییر یافت!" else "Wallpaper updated!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = wpName, color = if (isDarkTheme) Color.White else Color(0xFF0F172A), style = MaterialTheme.typography.bodyMedium)
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(ChatWallpaper.getBrush(wpId))
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showWallpaperSelector = false }) {
                            Text(text = Trans.get(settings.language, "cancel"), color = if (isDarkTheme) Color(0xFFD1E1FF) else Color(0xFF1E3A8A))
                        }
                    },
                    containerColor = if (isDarkTheme) Color(0xFF161920) else Color.White,
                    shape = RoundedCornerShape(24.dp)
                )
            }

            AnimatedVisibility(
                visible = showProfileDetail,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                // Glass-styled Profile Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0A0C10),
                                    Color(0xFF161920)
                                )
                            )
                        )
                ) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        text = Trans.get(settings.language, "profile"),
                                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { showProfileDetail = false }) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Color.Transparent
                                )
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Large Avatar
                            UserAvatar(
                                avatarUrl = chatInfo?.avatarUrl,
                                name = chatInfo?.name ?: "",
                                size = 120.dp,
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.border(2.dp, Color(0xFFD1E1FF), RoundedCornerShape(24.dp))
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // User or Group details Card
                            if (chatInfo?.isGroup == true) {
                                val parsedMemberIds = if (chatInfo?.memberIds?.isNotBlank() == true) chatInfo.memberIds.split(",") else emptyList()
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.White.copy(alpha = 0.03f),
                                    shape = RoundedCornerShape(24.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = if (isFa) "اطلاعات گروه" else "Group Details",
                                            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFD1E1FF), fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "${Trans.get(settings.language, "members")}: ${parsedMemberIds.size}",
                                            color = Color.White.copy(alpha = 0.7f),
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.08f))

                                        // Members List header
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isFa) "اعضای گروه" else "Group Members",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.4f))
                                            )
                                            TextButton(
                                                onClick = { showAddGroupMemberDialog = true },
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = "Add Member", tint = Color(0xFFD1E1FF), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (isFa) "افزودن عضو" else "Add Member",
                                                    color = Color(0xFFD1E1FF),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Render actual members
                                        val groupMembers = contacts.filter { parsedMemberIds.contains(it.id) }
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painter = rememberAsyncImagePainter("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80"),
                                                contentDescription = null,
                                                modifier = Modifier.size(32.dp).clip(CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = if (isFa) "شما (مدیر)" else "You (Owner)",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }

                                        groupMembers.forEach { memb ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                UserAvatar(
                                                    avatarUrl = memb.avatarUrl,
                                                    name = memb.name,
                                                    size = 32.dp,
                                                    shape = CircleShape
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = memb.name,
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.White.copy(alpha = 0.03f),
                                    shape = RoundedCornerShape(24.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        // Name
                                        Text(
                                            text = chatInfo?.name ?: "",
                                            style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFFD1E1FF), fontWeight = FontWeight.Bold)
                                        )
                                        
                                        // Status
                                        val isOnline = contact?.isOnline ?: false
                                        Text(
                                            text = if (isOnline) Trans.get(settings.language, "online") else Trans.get(settings.language, "last_seen_hidden"),
                                            color = if (isOnline) Color(0xFF4ADE80) else Color.White.copy(alpha = 0.5f),
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                        
                                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.08f))
                                        
                                        // Phone Number
                                        Text(
                                            text = Trans.get(settings.language, "phone"),
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.4f))
                                        )
                                        Text(
                                            text = contact?.phone ?: "نامشخص / Unknown",
                                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                        
                                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.08f))
                                        
                                        // Bio
                                        Text(
                                            text = Trans.get(settings.language, "bio"),
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.4f))
                                        )
                                        Text(
                                            text = contact?.bio ?: "در دسترس / Available",
                                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // Shared Media Headers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = Trans.get(settings.language, "shared_media"),
                                    style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFD1E1FF), fontWeight = FontWeight.Bold)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Shared media list
                            val sharedImages = messages.filter { it.type == "IMAGE" && it.mediaUrl != null }
                            
                            if (sharedImages.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (settings.language == "fa") "هنوز هیچ رسانه‌ای ارسال نشده است." else "No media shared yet.",
                                        color = Color.White.copy(alpha = 0.4f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    val rows = sharedImages.chunked(3)
                                    items(rows) { rowItems ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            rowItems.forEach { imgMsg ->
                                                AsyncImage(
                                                    model = imgMsg.mediaUrl,
                                                    contentDescription = "Shared Image",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .aspectRatio(1f)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                                )
                                            }
                                            repeat(3 - rowItems.size) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showAttachmentMenu) {
                ModalBottomSheet(
                    onDismissRequest = { showAttachmentMenu = false },
                    containerColor = if (settings.theme == "dark") Color(0xFF181C26) else Color.White,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = if (settings.theme == "dark") Color.White.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.4f)) }
                ) {
                    AttachmentSheetContent(
                        settings = settings,
                        onDismiss = { showAttachmentMenu = false },
                        onSendPhoto = {
                            galleryPickerLauncher.launch("image/*")
                        },
                        onSendMusic = {
                            audioPickerLauncher.launch("audio/*")
                        },
                        onSendLocation = {
                            showLocationPicker = true
                        },
                        onSendContact = {
                            contactPickerLauncher.launch(null)
                        }
                    )
                }
            }

            // Location Picker Dialog
            if (showLocationPicker) {
                AlertDialog(
                    onDismissRequest = { showLocationPicker = false },
                    title = {
                        Text(
                            text = if (isFa) "ارسال موقعیت مکانی" else "Send Location",
                            style = MaterialTheme.typography.titleMedium.copy(color = if (settings.theme == "dark") Color.White else Color(0xFF0F172A))
                        )
                    },
                    text = {
                        Text(
                            text = if (isFa) "آیا می‌خواهید موقعیت زنده خود را ارسال کنید؟ این کار نیاز به دسترسی به GPS دارد." else "Do you want to send your live GPS location?",
                            color = if (settings.theme == "dark") Color.LightGray else Color.Gray
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showLocationPicker = false
                                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text(text = if (isFa) "تایید" else "Send Location", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLocationPicker = false }) {
                            Text(text = Trans.get(settings.language, "cancel"), color = if (settings.theme == "dark") Color(0xFF93C5FD) else Color(0xFF1E3A8A))
                        }
                    },
                    containerColor = if (settings.theme == "dark") Color(0xFF161920) else Color.White,
                    shape = RoundedCornerShape(24.dp)
                )
            }

            // Add Group Member Dialog
            if (showAddGroupMemberDialog) {
                val parsedIds = if (chatInfo?.memberIds?.isNotBlank() == true) chatInfo.memberIds.split(",") else emptyList()
                val nonMembers = contacts.filter { parsedIds.contains(it.id).not() }
                AlertDialog(
                    onDismissRequest = { showAddGroupMemberDialog = false },
                    title = {
                        Text(
                            text = if (isFa) "افزودن عضو جدید" else "Add New Member",
                            style = MaterialTheme.typography.titleMedium.copy(color = if (settings.theme == "dark") Color.White else Color(0xFF0F172A))
                        )
                    },
                    text = {
                        if (nonMembers.isEmpty()) {
                            Text(
                                text = if (isFa) "تمام مخاطبین در گروه هستند." else "All contacts are already in this group.",
                                color = if (settings.theme == "dark") Color.LightGray else Color.Gray
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(nonMembers) { c ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                viewModel.addGroupMember(chatId, c.id)
                                                showAddGroupMemberDialog = false
                                                Toast.makeText(context, if (isFa) "${c.name} به گروه اضافه شد." else "${c.name} added to the group.", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        UserAvatar(
                                            avatarUrl = c.avatarUrl,
                                            name = c.name,
                                            size = 36.dp,
                                            shape = CircleShape
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = c.name,
                                            color = if (settings.theme == "dark") Color.White else Color(0xFF0F172A),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showAddGroupMemberDialog = false }) {
                            Text(text = Trans.get(settings.language, "cancel"), color = if (settings.theme == "dark") Color(0xFF93C5FD) else Color(0xFF1E3A8A))
                        }
                    },
                    containerColor = if (settings.theme == "dark") Color(0xFF161920) else Color.White,
                    shape = RoundedCornerShape(24.dp)
                )
            }

            // Fullscreen Image Viewer Modal
            if (activeFullScreenImageUrl != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .clickable { activeFullScreenImageUrl = null },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = activeFullScreenImageUrl,
                        contentDescription = "Fullscreen image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { activeFullScreenImageUrl = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 40.dp, end = 20.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: MessageEntity,
    settings: com.example.data.database.SettingEntity,
    viewModel: ChatViewModel,
    onEditMessage: (MessageEntity) -> Unit,
    onFullScreenImage: (String) -> Unit
) {
    val isMe = message.senderId == "me"
    val isDark = settings.theme == "dark"
    val emojiFontFamily by viewModel.emojiFontFamily.collectAsState()

    val chatsList by viewModel.chats.collectAsState(initial = emptyList())
    val chatInfo = chatsList.find { it.id == message.chatId }
    val isPinned = chatInfo?.pinnedMessageId == message.id

    val playingMessageId by viewModel.playingMessageId.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()

    val isThisPlaying = playingMessageId == message.id
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }

    // Dynamic light/dark styling for bubble backgrounds and texts
    val bubbleColor = if (isMe) {
        if (isDark) Color(0xFFD1E1FF).copy(alpha = 0.15f) else Color(0xFFD1E1FF)
    } else {
        if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFE2E8F0)
    }

    val bubbleBorderColor = if (isMe) {
        if (isDark) Color(0xFFD1E1FF).copy(alpha = 0.3f) else Color(0xFF93C5FD).copy(alpha = 0.5f)
    } else {
        if (isDark) Color.White.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.3f)
    }

    val textColor = if (isDark) Color.White else Color(0xFF0F172A)
    val timeColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF0F172A).copy(alpha = 0.6f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            Box {
                // Glass Bubble
                Surface(
                    color = bubbleColor,
                    border = BorderStroke(1.dp, bubbleBorderColor),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 16.dp
                    ),
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .combinedClickable(
                            onClick = {
                                if (message.type == "IMAGE" && message.mediaUrl != null) {
                                    onFullScreenImage(message.mediaUrl)
                                }
                            },
                            onLongClick = {
                                if (message.senderId != "system") {
                                    showMenu = true
                                }
                            }
                        )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // If Pinned Indicator inside the Bubble itself
                        if (isPinned) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "Pinned",
                                    tint = if (isDark) Color(0xFF93C5FD) else Color(0xFF1E3A8A),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (settings.language == "fa") "سنجاق شده" else "Pinned",
                                    fontSize = 10.sp,
                                    color = if (isDark) Color(0xFF93C5FD) else Color(0xFF1E3A8A),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Content rendering based on type
                        when (message.type) {
                            "IMAGE" -> {
                                AsyncImage(
                                    model = message.mediaUrl,
                                    contentDescription = "Sent Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                            "AUDIO", "VOICE" -> {
                                val audioUrl = if (message.mediaUrl.isNullOrBlank()) {
                                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                                } else {
                                    message.mediaUrl
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.playAudio(message.id, audioUrl) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(textColor.copy(alpha = 0.15f), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = if (isThisPlaying && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play/Pause Audio",
                                            tint = textColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        if (isThisPlaying) {
                                            LinearProgressIndicator(
                                                progress = { playbackProgress },
                                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                                color = if (isMe) Color(0xFF2563EB) else Color(0xFF10B981),
                                                trackColor = textColor.copy(alpha = 0.1f)
                                            )
                                        } else {
                                            Row(
                                                modifier = Modifier.width(130.dp).height(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                repeat(12) {
                                                    Box(
                                                        modifier = Modifier
                                                            .width(3.dp)
                                                            .height((6..16).random().dp)
                                                            .background(textColor.copy(alpha = 0.5f))
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (message.type == "VOICE") {
                                                "${if (settings.language == "fa") "صدای ضبط شده" else "Voice Note"} (${viewModel.formatDuration(message.duration)})"
                                            } else {
                                                message.content
                                            },
                                            fontSize = 11.sp,
                                            color = textColor.copy(alpha = 0.7f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            "LOCATION" -> {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = message.content,
                                            color = textColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (!message.mediaUrl.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Button(
                                            onClick = {
                                                try {
                                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(message.mediaUrl))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // fallback
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFEFF6FF)),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text(
                                                text = if (settings.language == "fa") "مشاهده روی نقشه" else "View on Map",
                                                fontSize = 11.sp,
                                                color = if (isDark) Color.White else Color(0xFF1E3A8A)
                                            )
                                        }
                                    }
                                }
                            }
                            "CALL" -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PhoneCallback, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = message.content,
                                        color = textColor,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            else -> {
                                // Standard text with mixed dynamic alignment and spacing fix
                                val annotatedText = remember(message.content, emojiFontFamily) {
                                    buildEmojiAnnotatedString(message.content, emojiFontFamily)
                                }
                                Text(
                                    text = annotatedText,
                                    color = textColor,
                                    fontSize = 15.sp,
                                    style = LocalTextStyle.current.copy(textDirection = TextDirection.Content)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Time & Read receipts
                        Row(
                            modifier = Modifier.align(Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = viewModel.formatTime(message.timestamp, settings.language),
                                color = timeColor,
                                fontSize = 10.sp
                            )
                            if (isMe) {
                                if (message.isRead) {
                                    Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Done,
                                            contentDescription = "Read",
                                            tint = Color(0xFF4ADE80),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Done,
                                            contentDescription = "Read",
                                            tint = Color(0xFF4ADE80),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Sent",
                                        tint = textColor.copy(alpha = 0.4f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Dropdown Options Menu for Pinned, Edit, Delete
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(if (isDark) Color(0xFF1E293B) else Color.White)
                ) {
                    // Pin option
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isPinned) {
                                    if (settings.language == "fa") "برداشتن سنجاق" else "Unpin Message"
                                } else {
                                    if (settings.language == "fa") "سنجاق کردن پیام" else "Pin Message"
                                },
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.PushPin, contentDescription = null, tint = if (isDark) Color.LightGray else Color.Gray)
                        },
                        onClick = {
                            showMenu = false
                            viewModel.toggleMessagePinned(message.chatId, if (isPinned) null else message.id)
                        }
                    )

                    // Edit option (if Me and Text)
                    if (isMe && message.type == "TEXT") {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (settings.language == "fa") "ویرایش پیام" else "Edit Message",
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = if (isDark) Color.LightGray else Color.Gray)
                            },
                            onClick = {
                                showMenu = false
                                onEditMessage(message)
                            }
                        )
                    }

                    // Delete option
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (settings.language == "fa") "حذف پیام" else "Delete Message",
                                color = Color.Red
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        },
                        onClick = {
                            showMenu = false
                            viewModel.deleteMessage(message)
                        }
                    )
                }
            }
        }
    }
}

private fun saveUriToInternalStorage(context: android.content.Context, uri: android.net.Uri, fileName: String): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = java.io.File(context.filesDir, fileName)
        val outputStream = java.io.FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getFileName(context: android.content.Context, uri: android.net.Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val index = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        result = c.getString(index)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}

private fun queryContactDetails(context: android.content.Context, contactUri: android.net.Uri): Pair<String?, String?> {
    var name: String? = null
    var phone: String? = null
    try {
        val cursor = context.contentResolver.query(contactUri, null, null, null, null)
        cursor?.use { c ->
            if (c.moveToFirst()) {
                val nameIndex = c.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME)
                if (nameIndex >= 0) name = c.getString(nameIndex)
                
                val hasPhoneIndex = c.getColumnIndex(android.provider.ContactsContract.Contacts.HAS_PHONE_NUMBER)
                val hasPhone = if (hasPhoneIndex >= 0) c.getString(hasPhoneIndex) else "0"
                if (hasPhone == "1") {
                    val idIndex = c.getColumnIndex(android.provider.ContactsContract.Contacts._ID)
                    val id = if (idIndex >= 0) c.getString(idIndex) else null
                    if (id != null) {
                        val phoneCursor = context.contentResolver.query(
                            android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        phoneCursor?.use { pc ->
                            if (pc.moveToFirst()) {
                                val numberIndex = pc.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
                                if (numberIndex >= 0) phone = pc.getString(numberIndex)
                            }
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return Pair(name, phone)
}

@Composable
fun AttachmentSheetContent(
    settings: com.example.data.database.SettingEntity,
    onDismiss: () -> Unit,
    onSendPhoto: () -> Unit,
    onSendMusic: () -> Unit,
    onSendLocation: () -> Unit,
    onSendContact: () -> Unit
) {
    val isFa = settings.language == "fa"
    val isDark = settings.theme == "dark"
    val textColor = if (isDark) Color.White else Color(0xFF0F172A)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = if (isFa) "ارسال به گفتگو" else "Send to Chat",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = textColor),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery
            AttachmentItem(
                title = if (isFa) "گالری" else "Gallery",
                icon = Icons.Default.Image,
                color = Color(0xFF3B82F6),
                textColor = textColor,
                onClick = {
                    onSendPhoto()
                    onDismiss()
                }
            )

            // Music
            AttachmentItem(
                title = if (isFa) "موسیقی" else "Music",
                icon = Icons.Default.MusicNote,
                color = Color(0xFFF59E0B),
                textColor = textColor,
                onClick = {
                    onSendMusic()
                    onDismiss()
                }
            )

            // Location
            AttachmentItem(
                title = if (isFa) "مکان" else "Location",
                icon = Icons.Default.LocationOn,
                color = Color(0xFF10B981),
                textColor = textColor,
                onClick = {
                    onSendLocation()
                    onDismiss()
                }
            )

            // Contact
            AttachmentItem(
                title = if (isFa) "مخاطب" else "Contact",
                icon = Icons.Default.Person,
                color = Color(0xFF06B6D4),
                textColor = textColor,
                onClick = {
                    onSendContact()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
fun AttachmentItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

// Emoji spacing fix helpers
private fun isEmoji(codePoint: Int): Boolean {
    return (codePoint in 0x1F300..0x1F5FF) || // Misc Symbols and Pictographs
           (codePoint in 0x1F600..0x1F64F) || // Emoticons
           (codePoint in 0x1F680..0x1F6FF) || // Transport & Map
           (codePoint in 0x2600..0x27BF) ||   // Misc Symbols & Dingbats
           (codePoint in 0x1F900..0x1F9FF) || // Supplemental Symbols
           (codePoint in 0x1FA70..0x1FAFF) || // Symbols and Pictographs Extended-A
           (codePoint in 0x1F1E6..0x1F1FF) || // Flags
           (codePoint in 0x1F3FB..0x1F3FF) || // Skin tones
           (codePoint in 0xE0020..0xE007F) || // Tags
           (codePoint == 0x200D) ||           // Zero Width Joiner
           (codePoint == 0x20E3) ||           // Keycap Enclosing Keycap
           (codePoint == 0xFE0F)              // Variation Selector 16
}

fun buildEmojiAnnotatedString(
    text: String,
    emojiFontFamily: FontFamily?
): AnnotatedString {
    if (emojiFontFamily == null) return AnnotatedString(text)
    
    return buildAnnotatedString {
        append(text)
        var i = 0
        val length = text.length
        while (i < length) {
            val codePoint = text.codePointAt(i)
            val charCount = Character.charCount(codePoint)
            if (isEmoji(codePoint)) {
                val start = i
                var end = i + charCount
                while (end < length) {
                    val nextCodePoint = text.codePointAt(end)
                    val nextCharCount = Character.charCount(nextCodePoint)
                    if (isEmoji(nextCodePoint) || nextCodePoint == 0x200D || nextCodePoint == 0xFE0F) {
                        end += nextCharCount
                    } else {
                        break
                    }
                }
                addStyle(
                    style = SpanStyle(fontFamily = emojiFontFamily),
                    start = start,
                    end = end
                )
                i = end
            } else {
                i += charCount
            }
        }
    }
}

class EmojiVisualTransformation(private val emojiFontFamily: FontFamily?) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (emojiFontFamily == null) return TransformedText(text, OffsetMapping.Identity)
        val transformed = buildEmojiAnnotatedString(text.text, emojiFontFamily)
        return TransformedText(transformed, OffsetMapping.Identity)
    }
}

