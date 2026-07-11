package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.api.GeminiClient
import com.example.data.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val messageDao = database.messageDao()
    private val chatDao = database.chatDao()
    private val settingDao = database.settingDao()

    val allChats: Flow<List<ChatEntity>> = chatDao.getAllChats()
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForChat(chatId)
    }

    fun getUserById(id: String): Flow<UserEntity?> {
        return userDao.getUserById(id)
    }

    fun getSettings(userId: String): Flow<SettingEntity?> {
        return settingDao.getSettings(userId)
    }

    suspend fun saveSettings(settings: SettingEntity) = withContext(Dispatchers.IO) {
        settingDao.insertSettings(settings)
    }

    suspend fun saveMessage(message: MessageEntity) = withContext(Dispatchers.IO) {
        messageDao.insertMessage(message)
        // Also update the chat thread info
        val chat = chatDao.getChatById(message.chatId)
        if (chat != null) {
            val contentPreview = when (message.type) {
                "IMAGE" -> "📷 [تصویر] / [Image]"
                "VOICE" -> "🎙️ [صدا] / [Voice]"
                "CALL" -> "📞 [تماس] / [Call]"
                else -> message.content
            }
            chatDao.insertChat(
                chat.copy(
                    lastMessage = contentPreview,
                    lastMessageTime = message.timestamp
                )
            )
        }
    }

    suspend fun createChat(chat: ChatEntity) = withContext(Dispatchers.IO) {
        chatDao.insertChat(chat)
    }

    suspend fun insertUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    suspend fun deleteChat(chatId: String) = withContext(Dispatchers.IO) {
        val chat = chatDao.getChatById(chatId)
        if (chat != null) {
            chatDao.deleteChat(chat)
            messageDao.clearChatMessages(chatId)
        }
    }

    suspend fun markChatAsRead(chatId: String) = withContext(Dispatchers.IO) {
        chatDao.resetUnreadCount(chatId)
        messageDao.markIncomingMessagesAsRead(chatId)
    }

    suspend fun markOutgoingMessagesAsRead(chatId: String) = withContext(Dispatchers.IO) {
        messageDao.markOutgoingMessagesAsRead(chatId)
    }

    suspend fun updateChatWallpaper(chatId: String, wallpaperValue: String) = withContext(Dispatchers.IO) {
        chatDao.updateChatWallpaper(chatId, wallpaperValue)
    }

    suspend fun getChatById(chatId: String): ChatEntity? = withContext(Dispatchers.IO) {
        chatDao.getChatById(chatId)
    }

    suspend fun updateChatPinned(chatId: String, isPinned: Boolean) = withContext(Dispatchers.IO) {
        chatDao.updateChatPinned(chatId, isPinned)
    }

    suspend fun updatePinnedMessageId(chatId: String, messageId: Int?) = withContext(Dispatchers.IO) {
        chatDao.updatePinnedMessageId(chatId, messageId)
    }

    suspend fun updateGroupMembers(chatId: String, memberIds: String) = withContext(Dispatchers.IO) {
        chatDao.updateGroupMembers(chatId, memberIds)
    }

    suspend fun updateMessageContent(messageId: Int, newContent: String) = withContext(Dispatchers.IO) {
        messageDao.updateMessageContent(messageId, newContent)
    }

    suspend fun deleteMessage(message: MessageEntity) = withContext(Dispatchers.IO) {
        messageDao.deleteMessage(message)
    }

    // --- Database Seeding ---
    suspend fun seedDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        // Log to verify
        Log.d("ChatRepository", "Checking database seeding...")
        val existingUsers = userDao.getAllUsers().firstOrNull() ?: emptyList()
        if (existingUsers.isEmpty()) {
            Log.d("ChatRepository", "Seeding default data...")

            // Seed Settings
            val defaultSettings = SettingEntity(
                userId = "me",
                language = "fa", // default to Persian
                theme = "dark",  // default to dark mode
                isLastSeenEnabled = true,
                wallpaperType = "preloaded",
                wallpaperValue = "bg_classic"
            )
            settingDao.insertSettings(defaultSettings)

            // Seed Users (Contacts)
            val users = listOf(
                UserEntity(
                    id = "mehdi_dev",
                    name = "مهدی (برنامه‌نویس)",
                    avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&h=200&q=80",
                    phone = "09121234567",
                    isOnline = true,
                    lastSeen = System.currentTimeMillis(),
                    bio = "عاشق کد زدن و اندروید! 💻📱"
                ),
                UserEntity(
                    id = "sara_designer",
                    name = "سارا (طراح رابط کاربری)",
                    avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&h=200&q=80",
                    phone = "09127654321",
                    isOnline = true,
                    lastSeen = System.currentTimeMillis() - 300000,
                    bio = "زیبایی در سادگی است. ✨🎨 UI/UX Designer"
                ),
                UserEntity(
                    id = "mhmt_ai",
                    name = "پشتیبان هوشمند MHMT AI",
                    avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=200&h=200&q=80",
                    phone = "02188888888",
                    isOnline = true,
                    lastSeen = System.currentTimeMillis(),
                    bio = "دستیار هوش مصنوعی شما در پیام‌رسان MHMT 🤖💡"
                ),
                UserEntity(
                    id = "hamid_sport",
                    name = "حمید (ورزشکار)",
                    avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&h=200&q=80",
                    phone = "09125555555",
                    isOnline = false,
                    lastSeen = System.currentTimeMillis() - 7200000,
                    bio = "بدن سالم، ذهن سالم. 🏃‍♂️💪"
                )
            )
            userDao.insertUsers(users)

            // Seed Initial Chats
            val chats = listOf(
                ChatEntity(
                    id = "mehdi_dev",
                    name = "مهدی (برنامه‌نویس)",
                    isGroup = false,
                    avatarUrl = users[0].avatarUrl,
                    lastMessage = "سلام! برنامه‌نویسی اپ چطور پیش میره؟",
                    lastMessageTime = System.currentTimeMillis() - 120000,
                    unreadCount = 1
                ),
                ChatEntity(
                    id = "sara_designer",
                    name = "سارا (طراح رابط کاربری)",
                    isGroup = false,
                    avatarUrl = users[1].avatarUrl,
                    lastMessage = "پالت رنگی جدید رو فرستادم، نظرت رو بگو.",
                    lastMessageTime = System.currentTimeMillis() - 600000,
                    unreadCount = 0
                ),
                ChatEntity(
                    id = "mhmt_ai",
                    name = "پشتیبان هوشمند MHMT AI",
                    isGroup = false,
                    avatarUrl = users[2].avatarUrl,
                    lastMessage = "خوش آمدید! چطور می‌توانم کمکتان کنم؟",
                    lastMessageTime = System.currentTimeMillis() - 3600000,
                    unreadCount = 0
                ),
                ChatEntity(
                    id = "mhmt_dev_group",
                    name = "گروه توسعه MHMT",
                    isGroup = true,
                    avatarUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&w=200&h=200&q=80",
                    lastMessage = "مهدی: بریم برای دپلوی نسخه جدید 🚀",
                    lastMessageTime = System.currentTimeMillis() - 10000,
                    unreadCount = 2
                )
            )
            for (c in chats) {
                chatDao.insertChat(c)
            }

            // Seed initial messages
            val initialMessages = listOf(
                MessageEntity(
                    chatId = "mehdi_dev",
                    senderId = "mehdi_dev",
                    content = "سلام! پیام‌رسان MHMT رو نصب کردی؟",
                    timestamp = System.currentTimeMillis() - 300000,
                    type = "TEXT"
                ),
                MessageEntity(
                    chatId = "mehdi_dev",
                    senderId = "me",
                    content = "سلام آره، واقعا رابط کاربری شیشه‌ای و جذابی داره!",
                    timestamp = System.currentTimeMillis() - 200000,
                    type = "TEXT"
                ),
                MessageEntity(
                    chatId = "mehdi_dev",
                    senderId = "mehdi_dev",
                    content = "سلام! برنامه‌نویسی اپ چطور پیش میره؟",
                    timestamp = System.currentTimeMillis() - 120000,
                    type = "TEXT"
                ),

                MessageEntity(
                    chatId = "sara_designer",
                    senderId = "sara_designer",
                    content = "سلام، طراحی کادرهای شیشه‌ای (Liquid Glass) رو تموم کردم.",
                    timestamp = System.currentTimeMillis() - 1200000,
                    type = "TEXT"
                ),
                MessageEntity(
                    chatId = "sara_designer",
                    senderId = "sara_designer",
                    content = "پالت رنگی جدید رو فرستادم، نظرت رو بگو.",
                    timestamp = System.currentTimeMillis() - 600000,
                    type = "TEXT"
                ),

                MessageEntity(
                    chatId = "mhmt_ai",
                    senderId = "mhmt_ai",
                    content = "سلام! من دستیار هوش مصنوعی شما هستم. هر سوالی داری ازم بپرس تا بهت جواب بدم. من همیشه آنلاینم!",
                    timestamp = System.currentTimeMillis() - 3600000,
                    type = "TEXT"
                ),

                MessageEntity(
                    chatId = "mhmt_dev_group",
                    senderId = "sara_designer",
                    content = "بک‌گراندهای شیشه‌ای خیلی قشنگ شدن 😍",
                    timestamp = System.currentTimeMillis() - 50000,
                    type = "TEXT"
                ),
                MessageEntity(
                    chatId = "mhmt_dev_group",
                    senderId = "mehdi_dev",
                    content = "بریم برای دپلوی نسخه جدید 🚀",
                    timestamp = System.currentTimeMillis() - 10000,
                    type = "TEXT"
                )
            )
            for (m in initialMessages) {
                messageDao.insertMessage(m)
            }
        }
    }

    // --- AI Reply Generator (Gemini Integration) ---
    suspend fun getAIChatBotReply(
        chatId: String,
        userMessage: String,
        currentLanguage: String,
        onTyping: (Boolean) -> Unit
    ): MessageEntity? = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSuspended(chatId) ?: return@withContext null
        val isGroup = chatDao.getChatById(chatId)?.isGroup ?: false

        val systemInstruction = when {
            chatId == "mehdi_dev" -> {
                if (currentLanguage == "fa") {
                    "تو مهدی هستی، یک توسعه‌دهنده اندروید خلاق، باانرژی و صمیمی. با زبان فارسی محاوره‌ای و دوستانه چت کن. از اصطلاحات برنامه‌نویسی و ایموجی‌ها استفاده کن. پاسخ‌هایت کوتاه و زیر ۲ الی ۳ جمله باشد."
                } else {
                    "You are Mehdi, a creative, friendly Android developer. Chat in a casual, friendly geek style. Use programming slang and emojis. Keep answers brief (under 2-3 sentences)."
                }
            }
            chatId == "sara_designer" -> {
                if (currentLanguage == "fa") {
                    "تو سارا هستی، یک طراح رابط کاربری (UI/UX) خوش‌سلیقه و دقیق. در پاسخ‌هایت به زیبایی، رنگ‌ها، سادگی طراحی و المان‌های بصری شیشه‌ای اشاره کن. صمیمی و خلاصه زیر ۲ الی ۳ جمله بنویس."
                } else {
                    "You are Sara, a tasteful and precise UI/UX designer. Mention design elegance, colors, simplicity, and glassmorphism. Keep it friendly and concise under 2-3 sentences."
                }
            }
            chatId == "hamid_sport" -> {
                if (currentLanguage == "fa") {
                    "تو حمید هستی، یک مربی بدنسازی و ورزشکار باانگیزه و پرانرژی. به کاربر انگیزه بده، ورزش کردن، دویدن و تغذیه سالم را پیشنهاد کن. لحنت بسیار مثبت و تشویق‌کننده باشد. زیر ۳ جمله بنویس."
                } else {
                    "You are Hamid, a highly motivated and high-energy fitness coach. Encourage the user to exercise, run, and eat healthy. Use an active, encouraging tone. Keep it under 3 sentences."
                }
            }
            chatId == "mhmt_ai" -> {
                if (currentLanguage == "fa") {
                    "تو پشتیبان هوشمند پیام‌رسان MHMT AI هستی. بسیار دانا، راهنما و پاسخگو. درباره قابلیت‌های پیام‌رسان مانند تم شیشه‌ای، تغییر پس‌زمینه، ضبط صدا، تماس شبیه‌سازی‌شده و چت گروهی توضیح بده. پاسخ کوتاه و مفید بده."
                } else {
                    "You are the MHMT AI Smart Assistant. Very helpful and responsive. You can explain app features like liquid glass design, wallpaper changing, voice messages, call simulation, and group chats. Keep answers helpful and short."
                }
            }
            isGroup -> {
                // For simulated group response, choose a random member to reply!
                val members = listOf("mehdi_dev", "sara_designer", "hamid_sport")
                val selectedMember = members.random()
                val memberUser = userDao.getUserByIdSuspended(selectedMember) ?: return@withContext null
                val prefix = if (currentLanguage == "fa") "عضو گروه ${memberUser.name}: " else "${memberUser.name}: "
                
                // Get AI response
                onTyping(true)
                val responseText = generateBotText(selectedMember, userMessage, currentLanguage)
                onTyping(false)

                return@withContext MessageEntity(
                    chatId = chatId,
                    senderId = selectedMember,
                    content = "$prefix$responseText",
                    timestamp = System.currentTimeMillis(),
                    type = "TEXT"
                )
            }
            else -> {
                // Custom added user
                if (currentLanguage == "fa") {
                    "تو ${user.name} هستی، یک دوست صمیمی و مهربان. پاسخ دوستانه و خلاصه به پیام کاربر بده. زیر ۲ جمله."
                } else {
                    "You are ${user.name}, a kind and close friend. Give a friendly, brief response to the user's message. Under 2 sentences."
                }
            }
        }

        onTyping(true)
        val responseText = generateBotText(chatId, userMessage, systemInstruction)
        onTyping(false)

        return@withContext MessageEntity(
            chatId = chatId,
            senderId = chatId,
            content = responseText,
            timestamp = System.currentTimeMillis(),
            type = "TEXT"
        )
    }

    private suspend fun generateBotText(contactId: String, currentMessage: String, systemInstruction: String): String {
        // Fetch last 6 messages of history for context
        val messagesFlow = messageDao.getMessagesForChat(contactId)
        val messages = messagesFlow.firstOrNull() ?: emptyList()
        val history = messages.takeLast(6).map {
            val role = if (it.senderId == "me") "user" else "model"
            Pair(role, it.content)
        }

        return GeminiClient.getReply(systemInstruction, currentMessage, history)
    }
}
