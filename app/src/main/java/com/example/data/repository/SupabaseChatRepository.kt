package com.example.data.repository

import com.example.data.api.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RealChatRow(
    val id: String,
    val name: String? = null,
    @SerialName("is_group") val isGroup: Boolean = false,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

@Serializable
data class NewChatRow(
    @SerialName("is_group") val isGroup: Boolean = false
)

@Serializable
data class RealChatMemberRow(
    @SerialName("chat_id") val chatId: String,
    @SerialName("user_id") val userId: String
)

@Serializable
data class NewChatMemberRow(
    @SerialName("chat_id") val chatId: String,
    @SerialName("user_id") val userId: String
)

@Serializable
data class RealMessageRow(
    val id: String = "",
    @SerialName("chat_id") val chatId: String,
    @SerialName("sender_id") val senderId: String,
    val content: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class NewMessageRow(
    @SerialName("chat_id") val chatId: String,
    @SerialName("sender_id") val senderId: String,
    val content: String
)

data class RealChatSummary(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserAvatar: String?
)

class SupabaseChatRepository {
    private val supabase = SupabaseClientProvider.client
    private val authRepository = AuthRepository()

    /**
     * Finds an existing 1:1 chat with [otherUserId], or creates a brand new
     * one (plus the two chat_members rows) if none exists yet.
     * Returns the chat id.
     */
    suspend fun getOrCreateDirectChat(otherUserId: String): String {
        val myId = authRepository.currentUserId
            ?: throw IllegalStateException("Not logged in")

        val myChatIds = supabase.postgrest["chat_members"]
            .select { filter { eq("user_id", myId) } }
            .decodeList<RealChatMemberRow>()
            .map { it.chatId }

        val theirChatIds = supabase.postgrest["chat_members"]
            .select { filter { eq("user_id", otherUserId) } }
            .decodeList<RealChatMemberRow>()
            .map { it.chatId }

        val shared = myChatIds.toSet().intersect(theirChatIds.toSet())

        if (shared.isNotEmpty()) {
            // Re-use the first shared chat (in this MVP every chat between
            // exactly these two members is treated as "the" direct chat).
            return shared.first()
        }

        val newChat = supabase.postgrest["chats"]
            .insert(NewChatRow(isGroup = false)) {
                select()
            }
            .decodeSingle<RealChatRow>()

        supabase.postgrest["chat_members"].insert(
            listOf(
                NewChatMemberRow(chatId = newChat.id, userId = myId),
                NewChatMemberRow(chatId = newChat.id, userId = otherUserId)
            )
        )

        return newChat.id
    }

    suspend fun findUserByEmail(email: String): ProfileRow? = authRepository.findUserByEmail(email)

    /**
     * Returns the list of direct chats for the current user, along with
     * the other participant's profile info.
     */
    suspend fun getMyChats(): List<RealChatSummary> {
        val myId = authRepository.currentUserId ?: return emptyList()

        val myMemberships = supabase.postgrest["chat_members"]
            .select { filter { eq("user_id", myId) } }
            .decodeList<RealChatMemberRow>()

        val summaries = mutableListOf<RealChatSummary>()
        for (membership in myMemberships) {
            val otherMember = supabase.postgrest["chat_members"]
                .select { filter { eq("chat_id", membership.chatId) } }
                .decodeList<RealChatMemberRow>()
                .firstOrNull { it.userId != myId } ?: continue

            val profile = supabase.postgrest["profiles"]
                .select { filter { eq("id", otherMember.userId) } }
                .decodeSingleOrNull<ProfileRow>() ?: continue

            summaries.add(
                RealChatSummary(
                    chatId = membership.chatId,
                    otherUserId = profile.id,
                    otherUserName = profile.name,
                    otherUserAvatar = profile.avatarUrl
                )
            )
        }
        return summaries
    }

    suspend fun getMessages(chatId: String): List<RealMessageRow> {
        return supabase.postgrest["messages"]
            .select { filter { eq("chat_id", chatId) }; order("created_at", io.github.jan.supabase.postgrest.query.Order.ASCENDING) }
            .decodeList<RealMessageRow>()
    }

    suspend fun sendMessage(chatId: String, content: String) {
        val myId = authRepository.currentUserId ?: return
        if (content.isBlank()) return
        supabase.postgrest["messages"].insert(
            NewMessageRow(chatId = chatId, senderId = myId, content = content.trim())
        )
    }

    /**
     * A cold Flow that emits every new message inserted into [chatId] in
     * real time, via Supabase Realtime.
     */
    fun observeNewMessages(chatId: String): Flow<RealMessageRow> {
        val realtimeChannel = supabase.channel("messages-$chatId")
        return realtimeChannel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "messages"
        }
            .filter { it.record["chat_id"]?.toString()?.trim('"') == chatId }
            .map { action -> action.decodeRecord<RealMessageRow>() }
    }

    suspend fun subscribeChannel(chatId: String) {
        val realtimeChannel = supabase.channel("messages-$chatId")
        realtimeChannel.subscribe()
    }
}
