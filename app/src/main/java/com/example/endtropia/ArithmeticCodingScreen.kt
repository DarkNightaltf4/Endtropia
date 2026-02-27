package com.example.endtropia

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArithmeticCodingScreen(onBack: () -> Unit) {
    var input by remember { mutableStateOf("КОШКА") }
    
    val symbols = remember(input) { ArithmeticCalculator.getSymbols(input) }
    val encodingSteps = remember(input, symbols) { ArithmeticCalculator.encode(input, symbols) }
    val finalCode = encodingSteps.lastOrNull()?.newLow ?: BigDecimal.ZERO
    val decodingSteps = remember(finalCode, input.length, symbols) { 
        ArithmeticCalculator.decode(finalCode, input.length, symbols) 
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Арифметическое кодирование",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Назад", color = ElectricBlue)
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
                            ElectricBlue.copy(alpha = 0.05f)
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
                // Input Section
                item {
                    ArithmeticTaskCard(title = "Входные данные", color = ElectricBlue) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it.uppercase() },
                            label = { Text("Текст для кодирования") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Symbols Table
                item {
                    ArithmeticTaskCard(title = "Таблица частот и интервалов", color = SoftPurple) {
                        SymbolsTable(symbols)
                    }
                }

                // Encoding Steps
                item {
                    ArithmeticTaskCard(title = "Процесс кодирования", color = NeonCyan) {
                        EncodingStepsTable(encodingSteps)
                        Spacer(modifier = Modifier.height(16.dp))
                        ArithmeticResultDisplay(
                            label = "Результат кодирования (левая граница):",
                            value = finalCode.toPlainString(),
                            color = NeonCyan
                        )
                    }
                }

                // Decoding Steps
                item {
                    ArithmeticTaskCard(title = "Процесс декодирования", color = ElectricBlue) {
                        DecodingStepsTable(decodingSteps)
                        Spacer(modifier = Modifier.height(12.dp))
                        val decodedWord = decodingSteps.map { it.char }.joinToString("")
                        ArithmeticResultDisplay(
                            label = "Восстановленное слово:",
                            value = decodedWord,
                            color = ElectricBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SymbolsTable(symbols: List<ArithmeticSymbol>) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.horizontalScroll(scrollState)) {
        Row(modifier = Modifier.fillMaxWidth().background(SoftPurple.copy(alpha = 0.1f)).padding(8.dp)) {
            Text("Симв", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Част", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Верят", modifier = Modifier.width(60.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Интервал", modifier = Modifier.width(300.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
        symbols.forEach { symbol ->
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(symbol.char.toString(), modifier = Modifier.width(40.dp), fontSize = 12.sp)
                Text(symbol.frequency.toString(), modifier = Modifier.width(40.dp), fontSize = 12.sp)
                Text(symbol.probability.toPlainString(), modifier = Modifier.width(60.dp), fontSize = 12.sp)
                Text("[${symbol.lowRange.toPlainString()}; ${symbol.highRange.toPlainString()})", modifier = Modifier.width(300.dp), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
            Divider(color = Color.Gray.copy(alpha = 0.1f))
        }
    }
}

@Composable
fun EncodingStepsTable(steps: List<EncodingStep>) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.horizontalScroll(scrollState)) {
        Row(modifier = Modifier.fillMaxWidth().background(NeonCyan.copy(alpha = 0.1f)).padding(8.dp)) {
            Text("С", modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text("Старый интервал [Low, High)", modifier = Modifier.width(250.dp), fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text("Новый интервал [Low, High)", modifier = Modifier.width(250.dp), fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
        steps.forEach { step ->
            Column(modifier = Modifier.width(530.dp).padding(vertical = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(step.char.toString(), modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 16.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Column(modifier = Modifier.width(250.dp)) {
                        Text("L: ${step.oldLow.toPlainString()}", fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("H: ${step.oldHigh.toPlainString()}", fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                    Column(modifier = Modifier.width(250.dp)) {
                        Text("L: ${step.newLow.toPlainString()}", fontSize = 9.sp, color = NeonCyan, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("H: ${step.newHigh.toPlainString()}", fontSize = 9.sp, color = NeonCyan, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
                Divider(color = Color.Gray.copy(alpha = 0.2f), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun DecodingStepsTable(steps: List<DecodingStep>) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.horizontalScroll(scrollState)) {
        Row(modifier = Modifier.fillMaxWidth().background(ElectricBlue.copy(alpha = 0.1f)).padding(8.dp)) {
            Text("Текущий код", modifier = Modifier.width(200.dp), fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text("С", modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text("Нормализованный код", modifier = Modifier.width(200.dp), fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
        steps.forEach { step ->
            Row(modifier = Modifier.width(430.dp).padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(step.code.toPlainString(), modifier = Modifier.width(200.dp), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                Text(step.char.toString(), modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold, color = ElectricBlue, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text(step.nextCode.toPlainString(), modifier = Modifier.width(200.dp), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
            Divider(color = Color.Gray.copy(alpha = 0.2f), thickness = 0.5.dp)
        }
    }
}

@Composable
fun ArithmeticTaskCard(
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
            modifier = Modifier.padding(16.dp)
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
fun ArithmeticResultDisplay(label: String, value: String, color: Color) {
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
