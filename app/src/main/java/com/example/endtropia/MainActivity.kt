package com.example.endtropia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import com.example.endtropia.ui.theme.EndtropiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EndtropiaTheme {
                var currentScreen by remember { mutableStateOf("welcome") }
                
                Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                    when (screen) {
                        "welcome" -> WelcomeScreen(
                            onNavigateToEntropy = { currentScreen = "entropy" },
                            onNavigateToCipher = { currentScreen = "cipher" },
                            onNavigateToDecrypt = { currentScreen = "decrypt" }
                        )
                        "entropy" -> EntropyScreen(onBack = { currentScreen = "welcome" })
                        "cipher" -> CipherScreen(initialDecryptMode = false, onBack = { currentScreen = "welcome" })
                        "decrypt" -> CipherScreen(initialDecryptMode = true, onBack = { currentScreen = "welcome" })
                    }
                }
            }
        }
    }
}