package com.example.calculatorapp

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

@Composable
fun CalculatorButton(
    symbol: String,
    onClick: (String) -> Unit
) {
    FloatingActionButton(
        onClick = { onClick(symbol) },
        shape = CircleShape,
        containerColor = getButtonColor(symbol),
        modifier = Modifier
            .padding(8.dp)
            .size(80.dp),
    ) {
        Text(text = symbol, fontSize = 24.sp)
    }
}

@Composable
fun getButtonColor(symbol: String): Color {
    return when (symbol) {
        "AC", "C" -> MaterialTheme.colorScheme.error
        "+", "-", "×", "÷", "=" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
}
