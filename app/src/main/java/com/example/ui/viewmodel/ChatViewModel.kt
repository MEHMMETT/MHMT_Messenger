package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthResult
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// --- Translation Map Helper ---
object Trans {
    private val map = mapOf(
        "fa" to mapOf(
            "app_title" to "پیام‌رسان MHMT",
            "chats" to "گفتگوها",
            "contacts" to "مخاطبین",
            "settings" to "تنظیمات",
            "search" to "جستجو...",
            "online" to "آنلاین",
            "offline" to "آفلاین",
            "last_seen" to "آخرین بازدید",
            "typing" to "در حال نوشتن...",
            "login" to "ورود به MHMT Messenger",
            "email_or_phone" to "آیدی، جیمیل یا شماره تلفن",
            "password" to "رمز عبور",
            "enter" to "ورود به حساب کاربری",
            "enter_with_google" to "ورود سریع با جیمیل",
            "no_account" to "هنوز ثبت نام نکرده‌اید؟ ساخت حساب جدید",
            "language" to "زبان برنامه",
            "theme" to "تم برنامه",
            "dark" to "تم تاریک",
            "light" to "تم روشن",
            "privacy_last_seen" to "نمایش وضعیت آنلاین / لست سین",
            "wallpaper" to "پس‌زمینه چت شیشه‌ای",
            "wallpaper_classic" to "سرمه‌ای کلاسیک",
            "wallpaper_neon" to "آسمان نئونی",
            "wallpaper_sunset" to "غروب صورتی نئون",
            "wallpaper_forest" to "جنگل جادویی",
            "wallpaper_custom" to "آپلود عکس سفارشی از گالری",
            "profile" to "پروفایل کاربری",
            "bio" to "درباره من / بیو",
            "edit_profile" to "ویرایش اطلاعات پروفایل",
            "new_group" to "ایجاد گروه چت جدید",
            "group_name" to "نام گروه جدید",
            "create" to "ایجاد گروه",
            "select_members" to "انتخاب اعضای گروه",
            "voice_call" to "تماس صوتی شبیه‌سازی‌شده",
            "calling" to "در حال تماس...",
            "connected" to "متصل شد",
            "call_ended" to "تماس پایان یافت",
            "send_message" to "پیام خود را بنویسید...",
            "record_voice" to "وویس",
            "recording" to "در حال ضبط صدا...",
            "media_image" to "گالری",
            "no_chats" to "هنوز چتی وجود ندارد. از مخاطبین یکی شروع کنید!",
            "save" to "ذخیره تغییرات",
            "cancel" to "لغو",
            "last_seen_hidden" to "اخیراً دیده شده",
            "status_toggle_on" to "روشن (سایرین می‌توانند لست سین شما را ببینند)",
            "status_toggle_off" to "خاموش (لست سین شما مخفی می‌شود)",
            "logout" to "خروج از حساب",
            "name" to "نام نمایش داده شده",
            "phone" to "شماره تماس",
            "unsupported_audio" to "برای ضبط وویس دکمه میکروفون را نگه دارید.",
            "duration" to "مدت زمان",
            "members" to "عضو",
            "all" to "همه",
            "personal" to "شخصی",
            "groups" to "گروه‌ها",
            "shared_media" to "رسانه‌های مشترک",
            "set_wallpaper" to "تغییر پس‌زمینه گفتگو"
        ),
        "en" to mapOf(
            "app_title" to "MHMT Messenger",
            "chats" to "Chats",
            "contacts" to "Contacts",
            "settings" to "Settings",
            "search" to "Search...",
            "online" to "Online",
            "offline" to "Offline",
            "last_seen" to "Last seen",
            "typing" to "typing...",
            "login" to "Log in to MHMT Messenger",
            "email_or_phone" to "ID, Gmail, or Phone Number",
            "password" to "Password",
            "enter" to "Log In",
            "enter_with_google" to "Quick Sign-in with Gmail",
            "no_account" to "Don't have an account? Sign Up",
            "language" to "App Language",
            "theme" to "App Theme",
            "dark" to "Dark Mode",
            "light" to "Light Mode",
            "privacy_last_seen" to "Show Online / Last Seen",
            "wallpaper" to "Glass Chat Wallpaper",
            "wallpaper_classic" to "Classic Slate Blue",
            "wallpaper_neon" to "Neon Sky Glow",
            "wallpaper_sunset" to "Sunset Pink Neon",
            "wallpaper_forest" to "Magic Forest Dark",
            "wallpaper_custom" to "Upload Custom Wallpaper",
            "profile" to "My Profile",
            "bio" to "About Me / Bio",
            "edit_profile" to "Edit Profile Details",
            "new_group" to "Create New Chat Group",
            "group_name" to "New Group Name",
            "create" to "Create Group",
            "select_members" to "Select Group Members",
            "voice_call" to "Simulated Voice Call",
            "calling" to "Calling...",
            "connected" to "Connected",
            "call_ended" to "Call Ended",
            "send_message" to "Type a message...",
            "record_voice" to "Voice",
            "recording" to "Recording voice message...",
            "media_image" to "Gallery",
            "no_chats" to "No chats yet. Pick a contact and start typing!",
            "save" to "Save Changes",
            "cancel" to "Cancel",
            "last_seen_hidden" to "Last seen recently",
            "status_toggle_on" to "On (Others can see your last seen)",
            "status_toggle_off" to "Off (Your last seen is hidden)",
            "logout" to "Logout",
            "name" to "Display Name",
            "phone" to "Phone Number",
            "unsupported_audio" to "Hold microphone button to record audio.",
            "duration" to "Duration",
            "members" to "members",
            "all" to "All",
            "personal" to "Personal",
            "groups" to "Groups",
            "shared_media" to "Shared Media",
            "set_wallpaper" to "Change Chat Wallpaper"
        )
    )

    fun get(lang: String, key: String): String {
        return map[lang]?.get(key) ?: key
    }
}

// --- Calling State Structure ---
data class CallState(
    val contact: UserEntity,
    val status: String, // "DIALING", "CONNECTED", "DISCONNECTED"
    val duration: Int = 0
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChatRepository(application)
    private val authRepository = AuthRepository()
    private val supabaseChatRepository = com.example.data.repository.SupabaseChatRepository()
    private val sharedPrefs = application.getSharedPreferences("mhmt_prefs", Context.MODE_PRIVATE)

    // --- iOS Emoji States ---
    private val _useIosEmojis = MutableStateFlow(sharedPrefs.getBoolean("use_ios_emojis", false))
    val useIosEmojis: StateFlow<Boolean> = _useIosEmojis.asStateFlow()

    private val _emojiFontFamily = MutableStateFlow<FontFamily?>(null)
    val emojiFontFamily: StateFlow<FontFamily?> = _emojiFontFamily.asStateFlow()

    private val _emojiDownloadProgress = MutableStateFlow<Float?>(null)
    val emojiDownloadProgress: StateFlow<Float?> = _emojiDownloadProgress.asStateFlow()

    // --- Audio Player States ---
    private val _playingMessageId = MutableStateFlow<Int?>(null)
    val playingMessageId: StateFlow<Int?> = _playingMessageId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private var mediaPlayer: android.media.MediaPlayer? = null
    private var progressJob: Job? = null

    // --- Authentication States ---
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _myProfile = MutableStateFlow(
        UserEntity(
            id = "me",
            name = "پویا منوری",
            avatarUrl = null,
            phone = "09120001122",
            isOnline = true,
            bio = "توسعه دهنده موبایل | استفاده از MHMT شگفت انگیزه! 🚀"
        )
    )
    val myProfile: StateFlow<UserEntity> = _myProfile.asStateFlow()

    // --- Search & Selection Query ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- Settings State (Synchronized with Room) ---
    private val _settings = MutableStateFlow(
        SettingEntity(
            userId = "me",
            language = "fa",
            theme = "dark",
            isLastSeenEnabled = true,
            wallpaperType = "preloaded",
            wallpaperValue = "bg_classic"
        )
    )
    val settings: StateFlow<SettingEntity> = _settings.asStateFlow()

    // --- Active Chat & Loading ---
    private val _activeChatId = MutableStateFlow<String?>(null)
    val activeChatId: StateFlow<String?> = _activeChatId.asStateFlow()

    // --- Chat partners typing state ---
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    // --- Simulated Calling States ---
    private val _activeCall = MutableStateFlow<CallState?>(null)
    val activeCall: StateFlow<CallState?> = _activeCall.asStateFlow()
    private var callTimerJob: Job? = null

    // --- Group Creation States ---
    val selectedGroupMembers = mutableStateListOf<String>()

    // --- Active List Flows ---
    val chats: Flow<List<ChatEntity>> = combine(
        repository.allChats,
        _searchQuery
    ) { allChats, query ->
        if (query.isBlank()) {
            allChats
        } else {
            allChats.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.lastMessage.contains(query, ignoreCase = true)
            }
        }
    }

    val contacts: Flow<List<UserEntity>> = combine(
        repository.allUsers,
        _searchQuery
    ) { allUsers, query ->
        // Exclude the current user "me"
        val filteredMe = allUsers.filter { it.id != "me" }
        if (query.isBlank()) {
            filteredMe
        } else {
            filteredMe.filter {
                it.name.contains(query, ignoreCase = true) ||
                (it.bio?.contains(query, ignoreCase = true) ?: false)
            }
        }
    }

    val activeMessages: Flow<List<MessageEntity>> = _activeChatId.flatMapLatest { chatId ->
        if (chatId == null) flowOf(emptyList())
        else repository.getMessagesForChat(chatId)
    }

    init {
        // Load custom emoji font if enabled and file exists
        if (sharedPrefs.getBoolean("use_ios_emojis", false)) {
            val file = File(application.filesDir, "apple_emoji.ttf")
            if (file.exists() && file.length() > 100000) {
                try {
                    _emojiFontFamily.value = FontFamily(android.graphics.Typeface.createFromFile(file))
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Error loading saved emoji font", e)
                }
            }
        }

        viewModelScope.launch {
            // Seed database with mock contacts & history on cold-start
            repository.seedDatabaseIfEmpty()

            // Collect the persistent settings
            repository.getSettings("me").collect { savedSettings ->
                if (savedSettings != null) {
                    _settings.value = savedSettings
                }
            }
        }

        // Check if there is already a real Supabase session (e.g. app was reopened)
        viewModelScope.launch {
            if (authRepository.isLoggedIn) {
                applyRealProfile()
                _isLoggedIn.value = true
            }
        }
    }

    // --- Core Operations ---

    private suspend fun applyRealProfile() {
        val profile = authRepository.getMyProfile() ?: return
        // Note: we intentionally keep the local "me" id used throughout the
        // rest of the (still simulated) chat data layer, and only copy over
        // the real display fields here. The "me" id will be replaced once
        // the chat/message layer is migrated to Supabase in the next stage.
        _myProfile.value = _myProfile.value.copy(
            name = profile.name,
            avatarUrl = profile.avatarUrl ?: _myProfile.value.avatarUrl,
            phone = profile.phone ?: _myProfile.value.phone,
            bio = profile.bio ?: _myProfile.value.bio
        )
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) return
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            when (val result = authRepository.signIn(email.trim(), password)) {
                is AuthResult.Success -> {
                    applyRealProfile()
                    _isLoggedIn.value = true
                }
                is AuthResult.Error -> {
                    _authError.value = result.message
                }
            }
            _authLoading.value = false
        }
    }

    fun signUp(email: String, password: String, displayName: String, username: String) {
        if (email.isBlank() || password.isBlank() || displayName.isBlank() || username.isBlank()) return
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            when (val result = authRepository.signUp(email.trim(), password, displayName.trim(), username.trim())) {
                is AuthResult.Success -> {
                    applyRealProfile()
                    _isLoggedIn.value = true
                }
                is AuthResult.Error -> {
                    _authError.value = result.message
                }
            }
            _authLoading.value = false
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            _isLoggedIn.value = false
        }
    }

    // --- Real (Supabase-backed) Chat State ---
    private val _activeRealChatId = MutableStateFlow<String?>(null)
    val activeRealChatId: StateFlow<String?> = _activeRealChatId.asStateFlow()

    private val _realChatPeerName = MutableStateFlow("")
    val realChatPeerName: StateFlow<String> = _realChatPeerName.asStateFlow()

    private val _realChatPeerAvatar = MutableStateFlow<String?>(null)
    val realChatPeerAvatar: StateFlow<String?> = _realChatPeerAvatar.asStateFlow()

    private val _realMessages = MutableStateFlow<List<com.example.data.repository.RealMessageRow>>(emptyList())
    val realMessages: StateFlow<List<com.example.data.repository.RealMessageRow>> = _realMessages.asStateFlow()

    private val _realChatLoading = MutableStateFlow(false)
    val realChatLoading: StateFlow<Boolean> = _realChatLoading.asStateFlow()

    private val _realChatError = MutableStateFlow<String?>(null)
    val realChatError: StateFlow<String?> = _realChatError.asStateFlow()

    val myUserId: String? get() = authRepository.currentUserId

    private var realtimeJob: Job? = null

    fun openRealChatByUsername(username: String) {
        if (username.isBlank()) return
        viewModelScope.launch {
            _realChatLoading.value = true
            _realChatError.value = null
            try {
                val peer = supabaseChatRepository.findUserByUsername(username.trim())
                if (peer == null) {
                    _realChatError.value = "کاربری با این آیدی پیدا نشد"
                    _realChatLoading.value = false
                    return@launch
                }
                val chatId = supabaseChatRepository.getOrCreateDirectChat(peer.id)
                _realChatPeerName.value = peer.name
                _realChatPeerAvatar.value = peer.avatarUrl
                _activeRealChatId.value = chatId
                loadRealMessages(chatId)
                listenForNewMessages(chatId)
            } catch (e: Exception) {
                _realChatError.value = e.message ?: "خطا در برقراری چت"
            } finally {
                _realChatLoading.value = false
            }
        }
    }

    private fun loadRealMessages(chatId: String) {
        viewModelScope.launch {
            try {
                _realMessages.value = supabaseChatRepository.getMessages(chatId)
            } catch (e: Exception) {
                // keep whatever we already had; non-fatal
            }
        }
    }

    private fun listenForNewMessages(chatId: String) {
        realtimeJob?.cancel()
        realtimeJob = viewModelScope.launch {
            try {
                supabaseChatRepository.subscribeChannel(chatId)
                supabaseChatRepository.observeNewMessages(chatId).collect { newMessage ->
                    if (_realMessages.value.none { it.id == newMessage.id }) {
                        _realMessages.value = _realMessages.value + newMessage
                    }
                }
            } catch (e: Exception) {
                // Realtime is a nice-to-have; the chat still works via manual refresh
            }
        }
    }

    fun sendRealMessage(text: String) {
        val chatId = _activeRealChatId.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            try {
                supabaseChatRepository.sendMessage(chatId, text)
                // Safety-net refresh in case the Realtime event is delayed/missed.
                loadRealMessages(chatId)
            } catch (e: Exception) {
                _realChatError.value = e.message ?: "ارسال پیام ناموفق بود"
            }
        }
    }

    fun closeRealChat() {
        realtimeJob?.cancel()
        realtimeJob = null
        _activeRealChatId.value = null
        _realMessages.value = emptyList()
        _realChatError.value = null
    }

    fun clearRealChatError() {
        _realChatError.value = null
    }

    fun selectChat(chatId: String?) {
        _activeChatId.value = chatId
        if (chatId != null) {
            viewModelScope.launch {
                repository.markChatAsRead(chatId)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSettings(
        language: String,
        theme: String,
        isLastSeen: Boolean,
        wallpaperType: String,
        wallpaperValue: String,
        isPhoneVisible: Boolean = _settings.value.isPhoneVisible
    ) {
        viewModelScope.launch {
            val updated = SettingEntity(
                userId = "me",
                language = language,
                theme = theme,
                isLastSeenEnabled = isLastSeen,
                wallpaperType = wallpaperType,
                wallpaperValue = wallpaperValue,
                isPhoneVisible = isPhoneVisible
            )
            _settings.value = updated
            repository.saveSettings(updated)
        }
    }

    fun toggleIosEmojis(enabled: Boolean) {
        _useIosEmojis.value = enabled
        sharedPrefs.edit().putBoolean("use_ios_emojis", enabled).apply()
        
        val file = File(getApplication<Application>().filesDir, "apple_emoji.ttf")
        if (enabled) {
            if (file.exists() && file.length() > 100000) {
                try {
                    _emojiFontFamily.value = FontFamily(android.graphics.Typeface.createFromFile(file))
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Error loading emoji font", e)
                    _emojiFontFamily.value = null
                }
            } else {
                downloadEmojiFont(file)
            }
        } else {
            _emojiFontFamily.value = null
        }
    }

    private fun downloadEmojiFont(file: File) {
        _emojiDownloadProgress.value = 0f
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val urlsToTry = listOf(
                "https://cdn.jsdelivr.net/gh/BlueBubblesApp/bluebubbles-fonts@master/AppleColorEmoji.ttf",
                "https://raw.githubusercontent.com/BlueBubblesApp/bluebubbles-fonts/master/AppleColorEmoji.ttf",
                "https://github.com/BlueBubblesApp/bluebubbles-fonts/raw/master/AppleColorEmoji.ttf"
            )
            
            var success = false
            var lastException: Exception? = null
            
            for (urlString in urlsToTry) {
                try {
                    var currentUrl = urlString
                    var connection: java.net.HttpURLConnection? = null
                    var redirectCount = 0
                    val maxRedirects = 5
                    
                    while (redirectCount < maxRedirects) {
                        val url = java.net.URL(currentUrl)
                        connection = url.openConnection() as java.net.HttpURLConnection
                        connection.connectTimeout = 15000
                        connection.readTimeout = 15000
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                        connection.instanceFollowRedirects = false // manually handle to ensure cross-protocol & domain support
                        connection.connect()
                        
                        val status = connection.responseCode
                        if (status == java.net.HttpURLConnection.HTTP_MOVED_TEMP || 
                            status == java.net.HttpURLConnection.HTTP_MOVED_PERM || 
                            status == 303 || status == 307 || status == 308) {
                            val newUrl = connection.getHeaderField("Location")
                            if (newUrl != null) {
                                currentUrl = newUrl
                                redirectCount++
                                connection.disconnect()
                                continue
                            }
                        }
                        break
                    }
                    
                    if (connection == null) throw Exception("Could not open connection")
                    
                    if (connection.responseCode == java.net.HttpURLConnection.HTTP_OK) {
                        val fileLength = connection.contentLength
                        val inputStream = connection.inputStream
                        val outputStream = java.io.FileOutputStream(file)
                        
                        val data = ByteArray(4096)
                        var total: Long = 0
                        var count: Int
                        while (inputStream.read(data).also { count = it } != -1) {
                            total += count
                            if (fileLength > 0) {
                                _emojiDownloadProgress.value = total.toFloat() / fileLength
                            }
                            outputStream.write(data, 0, count)
                        }
                        outputStream.flush()
                        outputStream.close()
                        inputStream.close()
                        connection.disconnect()
                        
                        if (file.exists() && file.length() > 100000) {
                            success = true
                            launch(kotlinx.coroutines.Dispatchers.Main) {
                                _emojiFontFamily.value = FontFamily(android.graphics.Typeface.createFromFile(file))
                            }
                            break // Success! Exit URL list loop.
                        } else {
                            throw Exception("File download incomplete or corrupt")
                        }
                    } else {
                        throw Exception("HTTP code ${connection.responseCode} for $currentUrl")
                    }
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Failed downloading from $urlString", e)
                    lastException = e
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }
            
            if (!success) {
                launch(kotlinx.coroutines.Dispatchers.Main) {
                    _emojiFontFamily.value = null
                    _useIosEmojis.value = false
                    sharedPrefs.edit().putBoolean("use_ios_emojis", false).apply()
                    android.widget.Toast.makeText(
                        getApplication(),
                        if (_settings.value.language == "fa") "دانلود فونت ایموجی با خطا مواجه شد! اتصال اینترنت خود را چک کنید." else "Failed to download emoji font! Please check your internet connection.",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
            _emojiDownloadProgress.value = null
        }
    }

    fun updateMyProfile(name: String, bio: String, phone: String, avatarUrl: String?) {
        viewModelScope.launch {
            val updated = _myProfile.value.copy(
                name = name,
                bio = bio,
                phone = phone,
                avatarUrl = avatarUrl ?: _myProfile.value.avatarUrl
            )
            _myProfile.value = updated
            repository.insertUser(updated)
        }
    }

    // --- Sending Messages & Gemini Loop ---

    fun sendMessage(content: String, type: String = "TEXT", mediaUrl: String? = null, duration: Int = 0) {
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val message = MessageEntity(
                chatId = chatId,
                senderId = "me",
                content = content,
                timestamp = timestamp,
                type = type,
                mediaUrl = mediaUrl,
                duration = duration
            )
            repository.saveMessage(message)

            // Simulate that the other person reads the message (double ticks) after 2 seconds
            launch {
                delay(2000)
                repository.markOutgoingMessagesAsRead(chatId)
            }

            // Trigger simulated AI responder if chatting with bot or a saved friend!
            if (type == "TEXT") {
                triggerAIResponse(chatId, content)
            }
        }
    }

    private fun triggerAIResponse(chatId: String, userMessage: String) {
        viewModelScope.launch {
            // Wait 1.5 seconds to feel natural
            delay(1000)
            val currentLang = _settings.value.language

            val response = repository.getAIChatBotReply(chatId, userMessage, currentLang) { typing ->
                _isTyping.value = typing
            }

            if (response != null) {
                // Check if user is still in the same chat
                val isViewing = _activeChatId.value == chatId
                val finalMsg = response.copy(
                    isRead = isViewing
                )
                repository.saveMessage(finalMsg)

                // If not viewing, increment unread count
                if (!isViewing) {
                    val db = AppDatabase.getDatabase(getApplication() as Application)
                    db.chatDao().incrementUnreadCount(chatId)
                }
            }
        }
    }

    // --- Contact Addition ---
    fun addContact(id: String, name: String, phone: String, bio: String) {
        viewModelScope.launch {
            val contact = UserEntity(
                id = id,
                name = name,
                phone = phone,
                bio = bio,
                isOnline = false,
                lastSeen = System.currentTimeMillis() - 86400000
            )
            repository.insertUser(contact)
            
            val chat = ChatEntity(
                id = id,
                name = name,
                isGroup = false,
                lastMessage = "مخاطب ذخیره شد. پیام دهید!",
                lastMessageTime = System.currentTimeMillis()
            )
            repository.createChat(chat)
        }
    }

    // --- Group Management ---

    fun toggleGroupMemberSelection(contactId: String) {
        if (selectedGroupMembers.contains(contactId)) {
            selectedGroupMembers.remove(contactId)
        } else {
            selectedGroupMembers.add(contactId)
        }
    }

    fun createGroup(name: String, avatarUrl: String? = null) {
        if (name.isBlank() || selectedGroupMembers.isEmpty()) return
        viewModelScope.launch {
            val groupId = "group_" + UUID.randomUUID().toString().take(6)
            val membersList = mutableListOf("me")
            membersList.addAll(selectedGroupMembers)
            val memberIdsString = membersList.joinToString(",")

            val groupChat = ChatEntity(
                id = groupId,
                name = name,
                isGroup = true,
                avatarUrl = avatarUrl ?: "https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&w=200&h=200&q=80",
                lastMessage = "گروه ساخته شد. به اعضا خوش آمد بگوئید!",
                lastMessageTime = System.currentTimeMillis(),
                memberIds = memberIdsString
            )
            repository.createChat(groupChat)

            // Save group creation log message
            val creationMsg = MessageEntity(
                chatId = groupId,
                senderId = "system",
                content = "گروه چت جدید به نام '$name' با حضور شما ایجاد گردید.",
                timestamp = System.currentTimeMillis(),
                type = "TEXT"
            )
            repository.saveMessage(creationMsg)

            // Clear selected members
            selectedGroupMembers.clear()
            selectChat(groupId)
        }
    }

    fun addGroupMember(chatId: String, contactId: String) {
        viewModelScope.launch {
            val chat = repository.getChatById(chatId)
            if (chat != null) {
                val currentMembers = chat.memberIds.split(",").filter { it.isNotBlank() }.toMutableList()
                if (!currentMembers.contains(contactId)) {
                    currentMembers.add(contactId)
                    val updatedMembers = currentMembers.joinToString(",")
                    repository.updateGroupMembers(chatId, updatedMembers)
                    
                    // Add system message
                    val db = AppDatabase.getDatabase(getApplication())
                    val user = db.userDao().getUserByIdSuspended(contactId)
                    val nameStr = user?.name ?: contactId
                    val sysMsg = MessageEntity(
                        chatId = chatId,
                        senderId = "system",
                        content = "مخاطب '$nameStr' به گروه اضافه شد.",
                        timestamp = System.currentTimeMillis(),
                        type = "TEXT"
                    )
                    repository.saveMessage(sysMsg)
                }
            }
        }
    }

    fun toggleChatPinned(chatId: String, isPinned: Boolean) {
        viewModelScope.launch {
            repository.updateChatPinned(chatId, isPinned)
        }
    }

    fun toggleMessagePinned(chatId: String, messageId: Int?) {
        viewModelScope.launch {
            repository.updatePinnedMessageId(chatId, messageId)
        }
    }

    fun editMessage(messageId: Int, newContent: String) {
        viewModelScope.launch {
            repository.updateMessageContent(messageId, newContent)
        }
    }

    fun deleteMessage(message: MessageEntity) {
        viewModelScope.launch {
            repository.deleteMessage(message)
            val chat = repository.getChatById(message.chatId)
            if (chat?.pinnedMessageId == message.id) {
                repository.updatePinnedMessageId(message.chatId, null)
            }
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            repository.deleteChat(chatId)
        }
    }

    // --- MediaPlayer Audio Methods ---
    fun playAudio(messageId: Int, audioUrl: String) {
        if (_playingMessageId.value == messageId) {
            if (_isPlaying.value) {
                mediaPlayer?.pause()
                _isPlaying.value = false
            } else {
                mediaPlayer?.start()
                _isPlaying.value = true
                startProgressTracker()
            }
            return
        }
        
        stopAudio()
        
        _playingMessageId.value = messageId
        _isPlaying.value = true
        _playbackProgress.value = 0f
        
        mediaPlayer = android.media.MediaPlayer().apply {
            try {
                setDataSource(audioUrl)
                prepare()
                start()
                startProgressTracker()
                setOnCompletionListener {
                    stopAudio()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stopAudio()
            }
        }
    }
    
    fun stopAudio() {
        progressJob?.cancel()
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaPlayer = null
        _isPlaying.value = false
        _playingMessageId.value = null
        _playbackProgress.value = 0f
    }
    
    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (_isPlaying.value && mediaPlayer != null) {
                try {
                    val current = mediaPlayer?.currentPosition ?: 0
                    val duration = mediaPlayer?.duration ?: 1
                    _playbackProgress.value = current.toFloat() / duration
                } catch (e: Exception) {
                    // ignore
                }
                delay(100)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }

    // --- Simulated Call Management ---

    fun startCall(contactId: String) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(getApplication() as Application)
            val user = db.userDao().getUserByIdSuspended(contactId) ?: return@launch
            
            _activeCall.value = CallState(contact = user, status = "DIALING")

            // Simulate call connection after 2.5 seconds
            delay(2500)
            if (_activeCall.value?.status == "DIALING") {
                _activeCall.value = _activeCall.value?.copy(status = "CONNECTED")
                startCallTimer()
            }
        }
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            var elapsed = 0
            while (_activeCall.value?.status == "CONNECTED") {
                delay(1000)
                elapsed++
                _activeCall.value = _activeCall.value?.copy(duration = elapsed)
            }
        }
    }

    fun endCall() {
        val active = _activeCall.value ?: return
        viewModelScope.launch {
            callTimerJob?.cancel()
            _activeCall.value = active.copy(status = "DISCONNECTED")
            delay(1000) // show call ended screen briefly
            
            // Save call history message
            val callMsg = MessageEntity(
                chatId = active.contact.id,
                senderId = if (active.duration > 0) "me" else active.contact.id,
                content = if (active.duration > 0) "📞 تماس صوتی پایان یافته (${formatDuration(active.duration)})" else "📞 تماس صوتی ناموفق",
                timestamp = System.currentTimeMillis(),
                type = "CALL",
                duration = active.duration
            )
            repository.saveMessage(callMsg)
            _activeCall.value = null
        }
    }

    // --- Per-Chat Wallpaper ---
    fun updateChatWallpaper(chatId: String, wallpaperValue: String) {
        viewModelScope.launch {
            repository.updateChatWallpaper(chatId, wallpaperValue)
        }
    }

    // --- Date/Time Utility Methods ---

    fun formatTime(timestamp: Long, language: String): String {
        val date = Date(timestamp)
        return if (language == "fa") {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val hour = calendar.get(Calendar.HOUR)
            val min = calendar.get(Calendar.MINUTE)
            val amPm = calendar.get(Calendar.AM_PM)
            
            val formattedHour = if (hour == 0) 12 else hour
            val minStr = String.format(Locale("fa", "IR"), "%02d", min)
            val hourStr = String.format(Locale("fa", "IR"), "%d", formattedHour)
            
            val amPmStr = if (amPm == Calendar.AM) "قبل از ظهر" else "بعد از ظهر"
            "$hourStr:$minStr $amPmStr"
        } else {
            val sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            sdf.format(date)
        }
    }

    fun formatDuration(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", m, s)
    }
}
