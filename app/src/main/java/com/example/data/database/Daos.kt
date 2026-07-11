package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserByIdSuspended(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET content = :newContent WHERE id = :messageId")
    suspend fun updateMessageContent(messageId: Int, newContent: String)

    @Delete
    suspend fun deleteMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun clearChatMessages(chatId: String)

    @Query("UPDATE messages SET isRead = 1 WHERE chatId = :chatId AND senderId != 'me'")
    suspend fun markIncomingMessagesAsRead(chatId: String)

    @Query("UPDATE messages SET isRead = 1 WHERE chatId = :chatId AND senderId = 'me'")
    suspend fun markOutgoingMessagesAsRead(chatId: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY isPinned DESC, lastMessageTime DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :chatId")
    suspend fun getChatById(chatId: String): ChatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Query("UPDATE chats SET lastMessage = :lastMessage, lastMessageTime = :timestamp WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: String, lastMessage: String, timestamp: Long)

    @Query("UPDATE chats SET unreadCount = unreadCount + 1 WHERE id = :chatId")
    suspend fun incrementUnreadCount(chatId: String)

    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun resetUnreadCount(chatId: String)

    @Query("UPDATE chats SET wallpaperValue = :wallpaperValue WHERE id = :chatId")
    suspend fun updateChatWallpaper(chatId: String, wallpaperValue: String)

    @Query("UPDATE chats SET isPinned = :isPinned WHERE id = :chatId")
    suspend fun updateChatPinned(chatId: String, isPinned: Boolean)

    @Query("UPDATE chats SET pinnedMessageId = :messageId WHERE id = :chatId")
    suspend fun updatePinnedMessageId(chatId: String, messageId: Int?)

    @Query("UPDATE chats SET memberIds = :memberIds WHERE id = :chatId")
    suspend fun updateGroupMembers(chatId: String, memberIds: String)

    @Delete
    suspend fun deleteChat(chat: ChatEntity)
}

@Dao
interface SettingDao {
    @Query("SELECT * FROM settings WHERE userId = :userId")
    fun getSettings(userId: String): Flow<SettingEntity?>

    @Query("SELECT * FROM settings WHERE userId = :userId")
    suspend fun getSettingsSuspended(userId: String): SettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingEntity)
}
