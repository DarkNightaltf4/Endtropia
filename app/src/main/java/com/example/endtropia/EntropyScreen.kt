package com.example.endtropia

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endtropia.ui.theme.ElectricBlue
import com.example.endtropia.ui.theme.SoftPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntropyScreen() {
    val probabilities = remember { mutableStateListOf("0.5", "0.5") }
    var result by remember { mutableStateOf<EntropyCalculator.EntropyResults?>(null) }
    var sumError by remember { mutableStateOf(false) }

    LaunchedEffect(probabilities.toList()) {
        val probs = probabilities.mapNotNull { it.trim().toDoubleOrNull() }
        val sum = probs.sum()
        
        if (probs.size == probabilities.size && Math.abs(sum - 1.0) < 0.001) {
            result = EntropyCalculator.calculateAllFromProbs(probs)
            sumError = false
        } else {
            result = null
            sumError = probabilities.any { it.isNotEmpty() } && Math.abs(sum - 1.0) >= 0.001
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Калькулятор Энтропии", 
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    ) 
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
                            ElectricBlue.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Formula Display
                FormulaDisplay()

                Spacer(modifier = Modifier.height(24.dp))

                // Input Section Label
                Text(
                    "Введите вероятности pᵢ",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dynamic Inputs
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp)
                    ) {
                        itemsIndexed(probabilities) { index, prob ->
                            ProbabilityInputRow(
                                value = prob,
                                index = index,
                                totalCount = probabilities.size,
                                onValueChange = { probabilities[index] = it },
                                onRemove = if (probabilities.size > 1) { 
                                    { probabilities.removeAt(index) } 
                                } else null,
                                onAdd = if (index == probabilities.size - 1) {
                                    { probabilities.add("") }
                                } else null
                            )
                        }
                    }
                }

                // Error Message
                if (sumError) {
                    Text(
                        "Сумма вероятностей должна быть равна 1.0 (сейчас: ${probabilities.mapNotNull { it.toDoubleOrNull() }.sum()})",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Results Container
                AnimatedVisibility(
                    visible = result != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        result?.let { ResultGrid(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun FormulaDisplay() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
            .padding(24.dp)
    ) {
        Text(
            "Формула Шеннона",
            style = MaterialTheme.typography.labelMedium,
            color = ElectricBlue,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "H(X) = − ∑ pᵢ log pᵢ",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            ),
            color = Color.Black
        )
    }
}

@Composable
fun ProbabilityInputRow(
    value: String,
    index: Int,
    totalCount: Int,
    onValueChange: (String) -> Unit,
    onRemove: (() -> Unit)?,
    onAdd: (() -> Unit)?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label like p1, p2
        Text(
            text = "p${index + 1}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = ElectricBlue
            ),
            modifier = Modifier.width(32.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = { Text("0.0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f)
            )
        )
        
        // Remove Button
        if (onRemove != null) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                )
            }
        } else {
            // Spacer to keep alignment when no delete button
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Add Button (Circle with Plus)
        if (onAdd != null) {
            IconButton(
                onClick = onAdd,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(40.dp)
                    .background(ElectricBlue, CircleShape)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Добавить",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            // Spacer for alignment
            Spacer(modifier = Modifier.width(44.dp))
        }
    }
}

// Removed the old AddButton composable as it's merged into the row


@Composable
fun ResultGrid(result: EntropyCalculator.EntropyResults) {
    val items = listOf(
        ResultItem("Двоичная (log2)", result.bits, "бит", ElectricBlue),
        ResultItem("Натуральная (ln)", result.nats, "нат", SoftPurple),
        ResultItem("Десятичная (log10)", result.hartleys, "дит / хартли", Color(0xFF06B6D4)),
        ResultItem("Алфавитная (logM)", result.alphabetUnits, "ед. (база ${result.alphabetSize})", Color(0xFFF59E0B))
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().height(260.dp) // Fixed height for grid to avoid scroll conflicts
    ) {
        items(items) { item ->
            ResultCard(item)
        }
    }
}

data class ResultItem(
    val title: String,
    val value: Double,
    val unit: String,
    val color: Color
)

@Composable
fun ResultCard(item: ResultItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                item.title,
                style = MaterialTheme.typography.labelSmall,
                color = item.color,
                fontWeight = FontWeight.Bold
            )
            
            Column {
                Text(
                    String.format("%.4f", item.value),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = Color.Black
                )
                Text(
                    item.unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }
        }
    }
}
