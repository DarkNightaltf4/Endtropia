package com.example.endtropia

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endtropia.ui.theme.ElectricBlue
import com.example.endtropia.ui.theme.NeonCyan
import com.example.endtropia.ui.theme.SoftPurple

@Composable
fun WelcomeScreen(
    onNavigateToEntropy: () -> Unit,
    onNavigateToCipher: () -> Unit,
    onNavigateToDecrypt: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B0E14),
                        Color(0xFF1A1F2B)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo / Title Section
            Text(
                "not a virus",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                ),
                color = Color.White
            )
            Text(
                "Домашнее задание",
                style = MaterialTheme.typography.titleMedium,
                color = ElectricBlue,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Navigation Cards
            MenuCard(
                title = "Энтропия",
                subtitle = "Расчет энтропии по Шеннону",
                icon = Icons.Default.Info,
                color = ElectricBlue,
                onClick = onNavigateToEntropy
            )

            Spacer(modifier = Modifier.height(20.dp))

            MenuCard(
                title = "Шифрование",
                subtitle = "Цезарь, Ключ, Перестановка",
                icon = Icons.Default.Lock,
                color = SoftPurple,
                onClick = onNavigateToCipher
            )

            Spacer(modifier = Modifier.height(20.dp))

            MenuCard(
                title = "Дешифрование",
                subtitle = "Восстановление данных",
                icon = Icons.Default.Info, // A different icon might be better, like a key
                color = NeonCyan,
                onClick = onNavigateToDecrypt
            )

            Spacer(modifier = Modifier.height(64.dp))
            
            Text(
                "Выберите модуль для работы",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.Info, // Small arrow would be better but let's use Info for consistency if Arrow is not available
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
