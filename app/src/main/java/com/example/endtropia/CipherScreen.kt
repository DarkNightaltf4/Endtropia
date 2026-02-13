package com.example.endtropia

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endtropia.ui.theme.ElectricBlue
import com.example.endtropia.ui.theme.NeonCyan
import com.example.endtropia.ui.theme.SoftPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CipherScreen(
    initialDecryptMode: Boolean = false,
    onBack: () -> Unit
) {
    var isEncryptionMode by remember { mutableStateOf(!initialDecryptMode) }

    var caesarInput by remember { mutableStateOf("") }
    var keyCipherInput by remember { mutableStateOf("") }
    var keyCipherKey by remember { mutableStateOf("") }
    var transInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEncryptionMode) "Шифрование" else "Дешифрование",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Назад", color = ElectricBlue)
                    }
                },
                actions = {
                    // Toggle Mode Button
                    TextButton(onClick = { isEncryptionMode = !isEncryptionMode }) {
                        Text(
                            if (isEncryptionMode) "В Дешифр." else "В Шифр.",
                            color = SoftPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                            if (isEncryptionMode) ElectricBlue.copy(alpha = 0.05f) else SoftPurple.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp, top = 16.dp)
            ) {
                // Task 1: Caesar
                item {
                    CipherTaskCard(
                        title = "Задание 1: Шифр Цезаря (сдвиг 3)",
                        color = ElectricBlue
                    ) {
                        OutlinedTextField(
                            value = caesarInput,
                            onValueChange = { caesarInput = it },
                            label = { Text(if (isEncryptionMode) "Текст для шифровки" else "Текст для дешифровки") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val result = if (isEncryptionMode) 
                            CipherCalculator.caesarEncrypt(caesarInput) 
                        else 
                            CipherCalculator.caesarDecrypt(caesarInput)

                        ResultDisplay(
                            label = if (isEncryptionMode) "Зашифровано:" else "Расшифровано:",
                            value = result,
                            color = ElectricBlue
                        )
                    }
                }

                // Task 2: Key Cipher
                item {
                    CipherTaskCard(
                        title = "Задание 2: Шифр с ключом",
                        color = SoftPurple
                    ) {
                        OutlinedTextField(
                            value = keyCipherInput,
                            onValueChange = { keyCipherInput = it },
                            label = { Text("Текст (RU)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = keyCipherKey,
                            onValueChange = { keyCipherKey = it },
                            label = { Text("Ключ (RU)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val result = if (isEncryptionMode)
                            CipherCalculator.keyEncrypt(keyCipherInput, keyCipherKey)
                        else
                            CipherCalculator.keyDecrypt(keyCipherInput, keyCipherKey)

                        ResultDisplay(
                            label = if (isEncryptionMode) "Зашифровано:" else "Расшифровано:",
                            value = result,
                            color = SoftPurple
                        )
                    }
                }

                // Task 3: Transposition
                item {
                    CipherTaskCard(
                        title = "Задание 3: Перестановка (4 ст.)",
                        color = NeonCyan
                    ) {
                        OutlinedTextField(
                            value = transInput,
                            onValueChange = { transInput = it },
                            label = { Text("Введите текст") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val result = if (isEncryptionMode)
                            CipherCalculator.transpositionEncrypt(transInput)
                        else
                            CipherCalculator.transpositionDecrypt(transInput)
                        
                        if (isEncryptionMode && transInput.isNotEmpty()) {
                            Text(
                                "Прямоугольник шифрования:",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TranspositionMatrix(transInput)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        ResultDisplay(
                            label = if (isEncryptionMode) "Зашифровано:" else "Расшифровано:",
                            value = result,
                            color = NeonCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CipherTaskCard(
    title: String,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun ResultDisplay(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
        Text(
            value.ifEmpty { "..." },
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            color = Color.Black
        )
    }
}

@Composable
fun TranspositionMatrix(text: String) {
    val cols = 4
    var paddedText = text.replace(" ", "_")
    while (paddedText.length % cols != 0) {
        paddedText += "_"
    }
    val rows = paddedText.length / cols
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.5f))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (r in 0 until rows) {
            Row {
                for (c in 0 until cols) {
                    val char = paddedText[r * cols + c]
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(2.dp)
                            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            char.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
