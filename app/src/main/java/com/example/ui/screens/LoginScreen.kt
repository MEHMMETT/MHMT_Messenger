package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.Trans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: ChatViewModel) {
    val settings by viewModel.settings.collectAsState()
    val isLoading by viewModel.authLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val isFa = settings.language == "fa"
    val layoutDir = if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr

    var isSignUpMode by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var usernameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A0C10),
                            Color(0xFF161920),
                            Color(0xFF0A0C10)
                        )
                    )
                )
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Language Selector Top Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            viewModel.updateSettings(
                                language = if (isFa) "en" else "fa",
                                theme = settings.theme,
                                isLastSeen = settings.isLastSeenEnabled,
                                wallpaperType = settings.wallpaperType,
                                wallpaperValue = settings.wallpaperValue
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Switch Language",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // App Branding Title - Serif Elegant Typography
                Text(
                    text = "MHMT Messenger",
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = Color(0xFFD1E1FF),
                        letterSpacing = 1.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "SECURE & GLASSMORPHIC",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 3.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 32.dp),
                    textAlign = TextAlign.Center
                )

                // Glassmorphic Login Form
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    alpha = 0.05f
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isSignUpMode) {
                                if (isFa) "ساخت حساب کاربری جدید" else "Create a New Account"
                            } else {
                                Trans.get(settings.language, "login")
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White
                            ),
                            modifier = Modifier.padding(bottom = 20.dp),
                            textAlign = TextAlign.Center
                        )

                        // Display name field, only shown while signing up
                        AnimatedVisibility(visible = isSignUpMode) {
                            Column {
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it },
                                    label = {
                                        Text(
                                            if (isFa) "نام نمایشی" else "Display Name",
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFD1E1FF))
                                    },
                                    singleLine = true,
                                    colors = loginFieldColors(),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = usernameInput,
                                    onValueChange = { usernameInput = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' } },
                                    label = {
                                        Text(
                                            if (isFa) "آیدی (username)" else "Username",
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFD1E1FF))
                                    },
                                    singleLine = true,
                                    colors = loginFieldColors(),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // Email input
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = {
                                Text(
                                    if (isFa) "ایمیل" else "Email",
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFD1E1FF))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = loginFieldColors(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password input
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text(Trans.get(settings.language, "password"), color = Color.White.copy(alpha = 0.6f)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFD1E1FF)
                                )
                            },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = loginFieldColors(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Error message, if any
                        AnimatedVisibility(visible = authError != null, enter = fadeIn(), exit = fadeOut()) {
                            Text(
                                text = authError ?: "",
                                color = Color(0xFFFF8A8A),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 12.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        val canSubmit = emailInput.isNotBlank() && passwordInput.isNotBlank() &&
                            (!isSignUpMode || (nameInput.isNotBlank() && usernameInput.isNotBlank())) && !isLoading
                        // Submit Action Button
                        Button(
                            onClick = {
                                viewModel.clearAuthError()
                                if (isSignUpMode) {
                                    viewModel.signUp(emailInput, passwordInput, nameInput, usernameInput)
                                } else {
                                    viewModel.signIn(emailInput, passwordInput)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD1E1FF),
                                contentColor = Color(0xFF0A0C10)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = canSubmit
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color(0xFF0A0C10),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (isSignUpMode) {
                                        if (isFa) "ساخت حساب" else "Sign Up"
                                    } else {
                                        Trans.get(settings.language, "enter")
                                    },
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = {
                                viewModel.clearAuthError()
                                isSignUpMode = !isSignUpMode
                            }
                        ) {
                            Text(
                                text = if (isSignUpMode) {
                                    if (isFa) "قبلاً حساب دارید؟ وارد شوید" else "Already have an account? Log in"
                                } else {
                                    Trans.get(settings.language, "no_account")
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.7f)
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Color(0xFFD1E1FF),
    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
    focusedLabelColor = Color(0xFFD1E1FF),
    unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
)
