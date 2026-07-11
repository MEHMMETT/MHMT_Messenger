package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // email, username, or phone
    val name: String,
    val avatarUrl: String? = null,
    val phone: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L,
    val bio: String? = null
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatId: String, // Chat thread ID (can be direct contact ID or group ID)
    val senderId: String, // Sender user ID
    val content: String,
    val timestamp: Long,
    val type: String, // "TEXT", "IMAGE", "VOICE", "CALL"
    val mediaUrl: String? = null, // URI string for images or voice notes
    val duration: Int = 0, // Duration in seconds for voice notes or calls
    val isRead: Boolean = false
)

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String, // Contact user ID or Group ID
    val name: String,
    val isGroup: Boolean = false,
    val avatarUrl: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val unreadCount: Int = 0,
    val wallpaperValue: String? = null, // Per-chat wallpaper value (e.g. bg_neon, bg_sunset, bg_forest)
    val isPinned: Boolean = false,
    val pinnedMessageId: Int? = null,
    val memberIds: String = "" // Comma-separated list of member IDs for groups
)

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val userId: String, // Usually current user's ID
    val language: String = "fa", // "fa" or "en"
    val theme: String = "dark", // "dark" or "light"
    val isLastSeenEnabled: Boolean = true,
    val wallpaperType: String = "preloaded", // "preloaded" or "custom"
    val wallpaperValue: String = "bg_classic", // wallpaper ID or image path
    val isPhoneVisible: Boolean = true
)
