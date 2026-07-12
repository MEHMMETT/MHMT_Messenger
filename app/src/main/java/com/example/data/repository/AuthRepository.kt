package com.example.data.repository

import com.example.data.api.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileRow(
    val id: String,
    val name: String,
    val email: String? = null,
    val username: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val phone: String? = null,
    val bio: String? = null
)

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository {
    private val supabase = SupabaseClientProvider.client

    val currentUserId: String?
        get() = supabase.auth.currentUserOrNull()?.id

    val isLoggedIn: Boolean
        get() = currentUserId != null

    suspend fun signUp(email: String, password: String, displayName: String, username: String): AuthResult {
    return try {
        val cleanUsername = username.trim().lowercase()
        if (cleanUsername.isBlank()) {
            return AuthResult.Error("لطفاً یک آیدی وارد کنید.")
        }

        val existing = supabase.postgrest["profiles"]
            .select { filter { eq("username", cleanUsername) } }
            .decodeSingleOrNull<ProfileRow>()
        if (existing != null) {
            return AuthResult.Error("این آیدی قبلاً استفاده شده، یکی دیگه انتخاب کنید.")
        }

        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        // After sign-up (email confirmations are disabled), the user is
        // already authenticated, so we can create their profile row.
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return AuthResult.Error("ثبت‌نام ناموفق بود، دوباره تلاش کنید.")

        supabase.postgrest["profiles"].insert(
            ProfileRow(id = userId, name = displayName, email = email, username = cleanUsername)
        )
        AuthResult.Success
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: "خطای ناشناخته در ثبت‌نام")
    }
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "ایمیل یا رمز عبور اشتباه است")
        }
    }

    suspend fun signOut() {
        try {
            supabase.auth.signOut()
        } catch (e: Exception) {
            // ignore, we clear local state regardless
        }
    }

    suspend fun getMyProfile(): ProfileRow? {
        val userId = currentUserId ?: return null
        return try {
            supabase.postgrest["profiles"]
                .select { filter { eq("id", userId) } }
                .decodeSingleOrNull<ProfileRow>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun findUserByEmail(email: String): ProfileRow? {
        return try {
            supabase.postgrest["profiles"]
                .select { filter { eq("email", email) } }
                .decodeSingleOrNull<ProfileRow>()
        } catch (e: Exception) {
            null
        }
    }
}
