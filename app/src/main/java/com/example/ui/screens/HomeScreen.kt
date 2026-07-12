package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.database.ChatEntity
import com.example.data.database.UserEntity
import com.example.ui.components.UserAvatar
import com.example.ui.theme.PrimarySky
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.Trans
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import android.content.Context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: ChatViewModel) {
    val settings by viewModel.settings.collectAsState()
    val isFa = settings.language == "fa"
    val layoutDir = if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr

    var selectedTab by remember { mutableStateOf(0) }
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Dialog flags
    var showAddContactDialog by remember { mutableStateOf(false) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showRealChatDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
        Scaffold(
            topBar = {
                val isDark = settings.theme == "dark"
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (selectedTab == 0) "MHMT Messenger" else Trans.get(settings.language, if (selectedTab == 1) "contacts" else "settings"),
                            style = if (selectedTab == 0) MaterialTheme.typography.titleLarge.copy(
                                color = if (isDark) Color(0xFFD1E1FF) else Color.White,
                                fontSize = 24.sp,
                                letterSpacing = (-0.5).sp
                            ) else MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = if (isDark) Color(0xFF0A0C10) else PrimarySky,
                        titleContentColor = Color.White
                    ),
                    actions = {
                        if (selectedTab == 1) {
                            // Group Creation and Add Contact triggers
                            IconButton(onClick = { showCreateGroupDialog = true }) {
                                Icon(Icons.Default.GroupAdd, contentDescription = "Create Group", tint = Color.White)
                            }
                            IconButton(onClick = { showAddContactDialog = true }) {
                                Icon(Icons.Default.PersonAdd, contentDescription = "Add Contact", tint = Color.White)
                            }
                        }
                    }
                )
            },
            bottomBar = {
                val isDarkTheme = settings.theme == "dark"
                val navSelectedIconColor = if (isDarkTheme) Color(0xFFD1E1FF) else PrimarySky
                val navSelectedTextColor = if (isDarkTheme) Color(0xFFD1E1FF) else PrimarySky
                val navUnselectedIconColor = if (isDarkTheme) Color.White.copy(alpha = 0.4f) else Color(0xFF64748B)
                val navUnselectedTextColor = if (isDarkTheme) Color.White.copy(alpha = 0.4f) else Color(0xFF64748B)
                val navIndicatorColor = if (isDarkTheme) Color(0xFFD1E1FF).copy(alpha = 0.15f) else Color(0xFFD1E1FF).copy(alpha = 0.4f)

                NavigationBar(
                    containerColor = if (isDarkTheme) Color(0xFF111418).copy(alpha = 0.92f) else Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 1.dp,
                            color = if (isDarkTheme) Color.White.copy(alpha = 0.08f) else Color.LightGray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Box {
                                Icon(Icons.Default.ChatBubble, contentDescription = "Chats")
                            }
                        },
                        label = { Text(Trans.get(settings.language, "chats")) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = navSelectedIconColor,
                            selectedTextColor = navSelectedTextColor,
                            unselectedIconColor = navUnselectedIconColor,
                            unselectedTextColor = navUnselectedTextColor,
                            indicatorColor = navIndicatorColor
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.People, contentDescription = "Contacts") },
                        label = { Text(Trans.get(settings.language, "contacts")) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = navSelectedIconColor,
                            selectedTextColor = navSelectedTextColor,
                            unselectedIconColor = navUnselectedIconColor,
                            unselectedTextColor = navUnselectedTextColor,
                            indicatorColor = navIndicatorColor
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text(Trans.get(settings.language, "settings")) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = navSelectedIconColor,
                            selectedTextColor = navSelectedTextColor,
                            unselectedIconColor = navUnselectedIconColor,
                            unselectedTextColor = navUnselectedTextColor,
                            indicatorColor = navIndicatorColor
                        )
                    )
                }
            },
            floatingActionButton = {
                if (selectedTab == 0) {
                    FloatingActionButton(
                        onClick = { showRealChatDialog = true },
                        containerColor = Color(0xFFD1E1FF),
                        contentColor = Color(0xFF0A0C10)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Start real chat")
                    }
                }
            },
            containerColor = if (settings.theme == "dark") Slate900 else Color(0xFFF8FAFC)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> ChatsTabScreen(viewModel)
                    1 -> ContactsTabScreen(viewModel)
                    2 -> SettingsTabScreen(viewModel)
                }
            }
        }

        // --- Dialogs ---

        if (showAddContactDialog) {
            AddContactDialog(
                language = settings.language,
                onDismiss = { showAddContactDialog = false },
                onAdd = { id, name, phone, bio ->
                    viewModel.addContact(id, name, phone, bio)
                    showAddContactDialog = false
                }
            )
        }

        if (showCreateGroupDialog) {
            CreateGroupDialog(
                viewModel = viewModel,
                language = settings.language,
                onDismiss = { showCreateGroupDialog = false }
            )
        }

        if (showRealChatDialog) {
            StartRealChatDialog(
                viewModel = viewModel,
                onDismiss = { showRealChatDialog = false }
            )
        }
    }
}

// ==========================================
// CHATS TAB
// ==========================================
@Composable
fun ChatsTabScreen(viewModel: ChatViewModel) {
    val chatsList by viewModel.chats.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val settings by viewModel.settings.collectAsState()
    
    var activeCategory by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("all") }

    val isDark = settings.theme == "dark"
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text(Trans.get(settings.language, "search"), color = if (isDark) Color.LightGray else Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = if (isDark) Color.White else Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (isDark) Color.White else Color(0xFF0F172A),
                unfocusedTextColor = if (isDark) Color.White else Color(0xFF0F172A),
                focusedBorderColor = if (isDark) Color(0xFFD1E1FF) else PrimarySky,
                unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.6f)
            )
        )

        // Telegram-style Category Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
                .border(
                    width = 0.5.dp,
                    color = if (settings.theme == "dark") Color.White.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    if (settings.theme == "dark") Color(0xFF111418).copy(alpha = 0.6f) else Color.White, 
                    RoundedCornerShape(16.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val categories = listOf("all", "personal", "groups")
            categories.forEach { cat ->
                val selected = activeCategory == cat
                val text = Trans.get(settings.language, cat)
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selected) {
                                if (settings.theme == "dark") Color(0xFFD1E1FF).copy(alpha = 0.15f) else Color(0xFFD1E1FF).copy(alpha = 0.5f)
                            } else Color.Transparent
                        )
                        .clickable { activeCategory = cat }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) {
                                if (settings.theme == "dark") Color(0xFFD1E1FF) else Color(0xFF0F172A)
                            } else {
                                if (settings.theme == "dark") Color.White.copy(alpha = 0.4f) else Color.Gray
                            }
                        )
                    )
                }
            }
        }

        val filteredChats = androidx.compose.runtime.remember(chatsList, activeCategory) {
            val base = when (activeCategory) {
                "personal" -> chatsList.filter { !it.isGroup }
                "groups" -> chatsList.filter { it.isGroup }
                else -> chatsList
            }
            base.sortedWith(compareByDescending<ChatEntity> { it.isPinned }.thenByDescending { it.lastMessageTime })
        }

        if (filteredChats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillGrid()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Trans.get(settings.language, "no_chats"),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredChats) { chat ->
                    ChatListItem(chat = chat, viewModel = viewModel, language = settings.language)
                }
            }
        }
    }
}

private fun Modifier.fillGrid(): Modifier = this.fillMaxSize()

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ChatListItem(chat: ChatEntity, viewModel: ChatViewModel, language: String) {
    val isUnread = chat.unreadCount > 0
    val isDark = viewModel.settings.value.theme == "dark"
    var showMenu by remember { mutableStateOf(false) }

    val rowModifier = if (isUnread) {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFD1E1FF).copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .combinedClickable(
                onClick = { viewModel.selectChat(chat.id) },
                onLongClick = { showMenu = true }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { viewModel.selectChat(chat.id) },
                onLongClick = { showMenu = true }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    }

    Box {
        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with rounded-2xl custom corners
            Box(modifier = Modifier.size(56.dp)) {
                UserAvatar(
                    avatarUrl = chat.avatarUrl,
                    name = chat.name,
                    size = 56.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                // Online status indicator (glowing dot)
                if (!chat.isGroup) {
                    // Determine online status
                    val contactsList by viewModel.contacts.collectAsState(initial = emptyList())
                    val contact = contactsList.find { it.id == chat.id }
                    if (contact?.isOnline == true) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4ADE80))
                                .border(2.dp, if (isDark) Color(0xFF0A0C10) else Color.White, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Name and Message preview
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (chat.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = if (isDark) Color(0xFF93C5FD) else PrimarySky,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 6.dp)
                        )
                    }
                    Text(
                        text = chat.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Slate900
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chat.lastMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isUnread) (if (isDark) Color(0xFFD1E1FF) else PrimarySky) else Color.Gray,
                        fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Time and Unread count
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = viewModel.formatTime(chat.lastMessageTime, language),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isUnread) (if (isDark) Color(0xFFD1E1FF) else PrimarySky) else Color.Gray,
                        fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (chat.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFD1E1FF), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unreadCount.toString(),
                            color = Color(0xFF0A0C10),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(if (isDark) Color(0xFF1E293B) else Color.White)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = if (chat.isPinned) {
                            if (language == "fa") "برداشتن سنجاق" else "Unpin Chat"
                        } else {
                            if (language == "fa") "سنجاق کردن گفتگو" else "Pin Chat"
                        },
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = if (isDark) Color.LightGray else Color.Gray
                    )
                },
                onClick = {
                    showMenu = false
                    viewModel.toggleChatPinned(chat.id, !chat.isPinned)
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = if (language == "fa") "حذف گفتگو" else "Delete Chat",
                        color = Color.Red
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.Red
                    )
                },
                onClick = {
                    showMenu = false
                    viewModel.deleteChat(chat.id)
                }
            )
        }
    }
}

// ==========================================
// CONTACTS TAB
// ==========================================
@Composable
fun ContactsTabScreen(viewModel: ChatViewModel) {
    val contactsList by viewModel.contacts.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val isDark = settings.theme == "dark"
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text(Trans.get(settings.language, "search"), color = if (isDark) Color.LightGray else Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = if (isDark) Color.White else Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (isDark) Color.White else Color(0xFF0F172A),
                unfocusedTextColor = if (isDark) Color.White else Color(0xFF0F172A),
                focusedBorderColor = if (isDark) Color(0xFFD1E1FF) else PrimarySky,
                unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.6f)
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(contactsList) { contact ->
                ContactListItem(contact = contact, viewModel = viewModel, language = settings.language)
            }
        }
    }
}

@Composable
fun ContactListItem(contact: UserEntity, viewModel: ChatViewModel, language: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.selectChat(contact.id) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with rounded-2xl custom corners
        Box(modifier = Modifier.size(52.dp)) {
            UserAvatar(
                avatarUrl = contact.avatarUrl,
                name = contact.name,
                size = 52.dp,
                shape = RoundedCornerShape(16.dp)
            )
            // Online status indicator
            if (contact.isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4ADE80))
                        .border(2.dp, if (viewModel.settings.value.theme == "dark") Color(0xFF0A0C10) else Color.White, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Name and Bio
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (viewModel.settings.value.theme == "dark") Color.White else Slate900
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = contact.bio ?: Trans.get(language, if (contact.isOnline) "online" else "offline"),
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Call & Chat buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.startCall(contact.id) }) {
                Icon(Icons.Default.Phone, contentDescription = "Voice Call", tint = PrimarySky)
            }
            IconButton(onClick = { viewModel.selectChat(contact.id) }) {
                Icon(Icons.Default.Chat, contentDescription = "Open Chat", tint = PrimarySky)
            }
        }
    }
}

// ==========================================
// SETTINGS TAB
// ==========================================
@Composable
fun SettingsTabScreen(viewModel: ChatViewModel) {
    val settings by viewModel.settings.collectAsState()
    val myProfile by viewModel.myProfile.collectAsState()
    val language = settings.language

    var isEditingProfile by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(myProfile.name) }
    var editBio by remember { mutableStateOf(myProfile.bio ?: "") }
    var editPhone by remember { mutableStateOf(myProfile.phone ?: "") }

    val context = LocalContext.current

    // Real Phone Gallery Picker launcher for the default application background
    val globalWallpaperLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = saveUriToInternalStorageForGlobal(context, uri, "global_wallpaper.jpg")
            if (savedPath != null) {
                viewModel.updateSettings(
                    settings.language,
                    settings.theme,
                    settings.isLastSeenEnabled,
                    "custom",
                    savedPath
                )
                Toast.makeText(context, if (settings.language == "fa") "پس‌زمینه پیش‌فرض برنامه تغییر یافت!" else "Default wallpaper updated!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var showAvatarSourceDialog by remember { mutableStateOf(false) }

    // System Gallery Picker launcher for the user profile picture
    val avatarGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = saveUriToInternalStorageForGlobal(context, uri, "profile_avatar.jpg")
            if (savedPath != null) {
                viewModel.updateMyProfile(myProfile.name, myProfile.bio ?: "", myProfile.phone ?: "", savedPath)
                Toast.makeText(context, if (settings.language == "fa") "عکس پروفایل شما تغییر یافت!" else "Profile picture updated!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Current user profile card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(90.dp)) {
                        UserAvatar(
                            avatarUrl = myProfile.avatarUrl,
                            name = myProfile.name,
                            size = 90.dp,
                            shape = CircleShape,
                            modifier = Modifier.border(2.dp, PrimarySky, CircleShape)
                        )
                        IconButton(
                            onClick = {
                                showAvatarSourceDialog = true
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.BottomEnd)
                                .background(PrimarySky, CircleShape)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Change Photo", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isEditingProfile) {
                        Text(
                            text = myProfile.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (settings.theme == "dark") Color.White else Slate900
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = myProfile.phone ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = myProfile.bio ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                editName = myProfile.name
                                editBio = myProfile.bio ?: ""
                                editPhone = myProfile.phone ?: ""
                                isEditingProfile = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimarySky)
                        ) {
                            Text(Trans.get(language, "edit_profile"))
                        }
                    } else {
                        // Editable fields
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text(Trans.get(language, "name")) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text(Trans.get(language, "phone")) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            label = { Text(Trans.get(language, "bio")) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateMyProfile(editName, editBio, editPhone, null)
                                    isEditingProfile = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimarySky),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(Trans.get(language, "save"))
                            }
                            OutlinedButton(
                                onClick = { isEditingProfile = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(Trans.get(language, "cancel"))
                            }
                        }
                    }
                }
            }
        }

        // App customization panel
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (settings.theme == "dark") Slate800 else Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title
                    Text(
                        text = Trans.get(language, "settings"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (settings.theme == "dark") Color.White else Slate900
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 1. Language Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = PrimarySky)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(Trans.get(language, "language"), color = if (settings.theme == "dark") Color.White else Slate900)
                        }
                        Row {
                            Button(
                                onClick = {
                                    viewModel.updateSettings("fa", settings.theme, settings.isLastSeenEnabled, settings.wallpaperType, settings.wallpaperValue)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (language == "fa") PrimarySky else Color.LightGray.copy(alpha = 0.3f),
                                    contentColor = if (language == "fa") Color.White else Color.Gray
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("فارسی", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Button(
                                onClick = {
                                    viewModel.updateSettings("en", settings.theme, settings.isLastSeenEnabled, settings.wallpaperType, settings.wallpaperValue)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (language == "en") PrimarySky else Color.LightGray.copy(alpha = 0.3f),
                                    contentColor = if (language == "en") Color.White else Color.Gray
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("English", fontSize = 12.sp)
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.2f))

                    // 2. Theme Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (settings.theme == "dark") Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = PrimarySky
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(Trans.get(language, "theme"), color = if (settings.theme == "dark") Color.White else Slate900)
                        }
                        Switch(
                            checked = settings.theme == "dark",
                            onCheckedChange = { checked ->
                                viewModel.updateSettings(
                                    settings.language,
                                    if (checked) "dark" else "light",
                                    settings.isLastSeenEnabled,
                                    settings.wallpaperType,
                                    settings.wallpaperValue
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimarySky)
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.2f))

                    // 3. Privacy Last Seen Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = PrimarySky)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(Trans.get(language, "privacy_last_seen"), color = if (settings.theme == "dark") Color.White else Slate900)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Trans.get(language, if (settings.isLastSeenEnabled) "status_toggle_on" else "status_toggle_off"),
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp),
                                modifier = Modifier.padding(start = 36.dp)
                            )
                        }
                        Switch(
                            checked = settings.isLastSeenEnabled,
                            onCheckedChange = { checked ->
                                viewModel.updateSettings(
                                    settings.language,
                                    settings.theme,
                                    checked,
                                    settings.wallpaperType,
                                    settings.wallpaperValue
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimarySky)
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.2f))

                    // 3b. Phone Number Privacy Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = PrimarySky)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (language == "fa") "نمایش شماره تلفن من" else "Show My Phone Number",
                                    color = if (settings.theme == "dark") Color.White else Slate900
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (settings.isPhoneVisible) {
                                    if (language == "fa") "شماره شما برای همه نمایش داده می‌شود" else "Your number is visible to everyone"
                                } else {
                                    if (language == "fa") "شماره شما مخفی است" else "Your number is hidden"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp),
                                modifier = Modifier.padding(start = 36.dp)
                            )
                        }
                        Switch(
                            checked = settings.isPhoneVisible,
                            onCheckedChange = { checked ->
                                viewModel.updateSettings(
                                    settings.language,
                                    settings.theme,
                                    settings.isLastSeenEnabled,
                                    settings.wallpaperType,
                                    settings.wallpaperValue,
                                    isPhoneVisible = checked
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimarySky)
                        )
                    }

                    // 4. iOS Emojis Toggle
                    val useIosEmojis by viewModel.useIosEmojis.collectAsState()
                    val emojiDownloadProgress by viewModel.emojiDownloadProgress.collectAsState()

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Face, contentDescription = null, tint = PrimarySky)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (language == "fa") "ایموجی‌های iOS (آیفون)" else "iOS Emojis (Apple Style)",
                                    color = if (settings.theme == "dark") Color.White else Slate900
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            if (emojiDownloadProgress != null) {
                                val pct = (emojiDownloadProgress!! * 100).toInt()
                                Text(
                                    text = if (language == "fa") "در حال دریافت فونت ایموجی: $pct%" else "Downloading emoji font: $pct%",
                                    style = MaterialTheme.typography.bodySmall.copy(color = PrimarySky, fontSize = 11.sp),
                                    modifier = Modifier.padding(start = 36.dp)
                                )
                                LinearProgressIndicator(
                                    progress = { emojiDownloadProgress!! },
                                    color = PrimarySky,
                                    trackColor = Color.LightGray.copy(alpha = 0.2f),
                                    modifier = Modifier
                                        .padding(start = 36.dp, top = 6.dp)
                                        .fillMaxWidth(0.8f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                )
                            } else {
                                val statusStr = if (useIosEmojis) {
                                    if (language == "fa") "فونت ایموجی iOS فعال و بارگذاری شده است." else "iOS emoji font is active and loaded."
                                } else {
                                    if (language == "fa") "بر روی دکمه مقابل ضربه بزنید تا فونت دریافت و فعال شود." else "Tap the switch to download and activate."
                                }
                                Text(
                                    text = statusStr,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp),
                                    modifier = Modifier.padding(start = 36.dp)
                                )
                            }
                        }
                        Switch(
                            checked = useIosEmojis,
                            onCheckedChange = { checked ->
                                viewModel.toggleIosEmojis(checked)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimarySky),
                            enabled = emojiDownloadProgress == null
                        )
                    }
                }
            }
        }

        // Wallpapers Selector Panel
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (settings.theme == "dark") Slate800 else Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = Trans.get(language, "wallpaper"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (settings.theme == "dark") Color.White else Slate900
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val wallList = listOf(
                        Triple("bg_classic", Trans.get(language, "wallpaper_classic"), Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B)))),
                        Triple("bg_neon", Trans.get(language, "wallpaper_neon"), Brush.verticalGradient(listOf(Color(0xFF020208), Color(0xFF1E1B4B)))),
                        Triple("bg_sunset", Trans.get(language, "wallpaper_sunset"), Brush.verticalGradient(listOf(Color(0xFF1A0202), Color(0xFF450A0A)))),
                        Triple("bg_forest", Trans.get(language, "wallpaper_forest"), Brush.verticalGradient(listOf(Color(0xFF011A13), Color(0xFF064E3B))))
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.height(150.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(wallList) { wall ->
                            val selected = settings.wallpaperValue == wall.first
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(wall.third)
                                    .border(
                                        width = if (selected) 3.dp else 1.dp,
                                        color = if (selected) PrimarySky else Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        viewModel.updateSettings(
                                            settings.language,
                                            settings.theme,
                                            settings.isLastSeenEnabled,
                                            "preloaded",
                                            wall.first
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = wall.second,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Gallery Custom Wallpaper loader
                    OutlinedButton(
                        onClick = {
                            globalWallpaperLauncher.launch("image/*")
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Trans.get(language, "wallpaper_custom"))
                    }
                }
            }
        }

        // Logout
        item {
            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = "Logout")
                Spacer(modifier = Modifier.width(8.dp))
                Text(Trans.get(language, "logout"), fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showAvatarSourceDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarSourceDialog = false },
            title = {
                Text(
                    text = if (language == "fa") "تغییر عکس پروفایل" else "Change Profile Photo",
                    style = MaterialTheme.typography.titleMedium.copy(color = if (settings.theme == "dark") Color.White else Slate900)
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (language == "fa") "یک روش را برای تغییر عکس پروفایل خود انتخاب کنید:" else "Choose a method to change your profile picture:",
                        color = if (settings.theme == "dark") Color.LightGray else Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Option 1: Gallery
                    Surface(
                        onClick = {
                            showAvatarSourceDialog = false
                            avatarGalleryLauncher.launch("image/*")
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (settings.theme == "dark") Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = PrimarySky)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = if (language == "fa") "انتخاب از گالری گوشی" else "Select from Gallery",
                                color = if (settings.theme == "dark") Color.White else Slate900,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    
                    // Option 2: Preloaded Avatars
                    Surface(
                        onClick = {
                            showAvatarSourceDialog = false
                            val preloadedAvatars = listOf(
                                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&h=200&q=80",
                                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&h=200&q=80",
                                "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=200&h=200&q=80",
                                "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=200&h=200&q=80"
                            )
                            val currentIdx = preloadedAvatars.indexOf(myProfile.avatarUrl)
                            val nextIdx = (currentIdx + 1) % preloadedAvatars.size
                            viewModel.updateMyProfile(myProfile.name, myProfile.bio ?: "", myProfile.phone ?: "", preloadedAvatars[nextIdx])
                            Toast.makeText(context, if (settings.language == "fa") "عکس پروفایل تغییر یافت!" else "Profile photo updated!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (settings.theme == "dark") Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Autorenew, contentDescription = null, tint = PrimarySky)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = if (language == "fa") "چرخش عکس‌های پیش‌فرض" else "Rotate Preloaded Avatars",
                                color = if (settings.theme == "dark") Color.White else Slate900,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarSourceDialog = false }) {
                    Text(text = Trans.get(language, "cancel"), color = if (settings.theme == "dark") Color(0xFF93C5FD) else Color(0xFF1E3A8A))
                }
            },
            containerColor = if (settings.theme == "dark") Color(0xFF161920) else Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

private fun saveUriToInternalStorageForGlobal(context: android.content.Context, uri: android.net.Uri, fileName: String): String? {
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

@Composable
fun StartRealChatDialog(
    viewModel: ChatViewModel,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val loading by viewModel.realChatLoading.collectAsState()
    val error by viewModel.realChatError.collectAsState()
    val activeRealChatId by viewModel.activeRealChatId.collectAsState()

    LaunchedEffect(activeRealChatId) {
        if (activeRealChatId != null) {
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("شروع چت واقعی") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "ایمیل کاربری که قبلاً تو اپ ثبت‌نام کرده رو وارد کن",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("ایمیل") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error ?: "", color = androidx.compose.ui.graphics.Color.Red, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.clearRealChatError()
                    viewModel.openRealChatByEmail(email)
                },
                enabled = email.isNotBlank() && !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("شروع چت")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(Trans.get(viewModel.settings.value.language, "cancel"))
            }
        }
    )
}

@Composable
fun AddContactDialog(
    language: String,
    onDismiss: () -> Unit,
    onAdd: (id: String, name: String, phone: String, bio: String) -> Unit
) {
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Trans.get(language, "new_group")) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(Trans.get(language, "name")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("ID / جیمیل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(Trans.get(language, "phone")) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text(Trans.get(language, "bio")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (id.isNotBlank() && name.isNotBlank()) {
                        onAdd(id, name, phone, bio)
                    }
                },
                enabled = id.isNotBlank() && name.isNotBlank()
            ) {
                Text(Trans.get(language, "save"))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(Trans.get(language, "cancel"))
            }
        }
    )
}

@Composable
fun CreateGroupDialog(
    viewModel: ChatViewModel,
    language: String,
    onDismiss: () -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    val contactsList by viewModel.contacts.collectAsState(initial = emptyList())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Trans.get(language, "new_group")) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text(Trans.get(language, "group_name")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = Trans.get(language, "select_members"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(contactsList) { contact ->
                        val isSelected = viewModel.selectedGroupMembers.contains(contact.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleGroupMemberSelection(contact.id) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { viewModel.toggleGroupMemberSelection(contact.id) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(contact.name, fontSize = 14.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (groupName.isNotBlank() && viewModel.selectedGroupMembers.isNotEmpty()) {
                        viewModel.createGroup(groupName)
                        onDismiss()
                    }
                },
                enabled = groupName.isNotBlank() && viewModel.selectedGroupMembers.isNotEmpty()
            ) {
                Text(Trans.get(language, "create"))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(Trans.get(language, "cancel"))
            }
        }
    )
}
