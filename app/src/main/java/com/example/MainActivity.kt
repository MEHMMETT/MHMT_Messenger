package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CallingScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ChatViewModel = viewModel()
            val settings by viewModel.settings.collectAsState()
            val isLoggedIn by viewModel.isLoggedIn.collectAsState()
            val activeChatId by viewModel.activeChatId.collectAsState()
            val activeCall by viewModel.activeCall.collectAsState()

            MyApplicationTheme(
                darkTheme = settings.theme == "dark"
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Crossfade provides elegant animated transitions between core application layouts
                    Crossfade(
                        targetState = Triple(isLoggedIn, activeCall != null, activeChatId != null),
                        label = "mainScreenTransition"
                    ) { state ->
                        val (loggedIn, onCall, inChat) = state
                        when {
                            !loggedIn -> {
                                LoginScreen(viewModel = viewModel)
                            }
                            onCall -> {
                                CallingScreen(viewModel = viewModel)
                            }
                            inChat -> {
                                ChatDetailScreen(viewModel = viewModel)
                            }
                            else -> {
                                HomeScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
