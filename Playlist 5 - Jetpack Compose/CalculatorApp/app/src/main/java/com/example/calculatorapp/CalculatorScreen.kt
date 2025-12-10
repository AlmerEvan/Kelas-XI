package com.example.calculatorapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val buttonList = listOf(
        "AC", "C", "÷", "×",
        "7", "8", "9", "-",
        "4", "5", "6", "+",
        "1", "2", "3", "=",
        "0", ".", "", ""
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Text Equation
        Text(
            text = viewModel.equationText.value,
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.End
        )
        
        // Text Result
        Text(
            text = viewModel.resultText.value,
            fontSize = 60.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.End
        )
        
        // Spacer untuk mendorong tombol ke bawah
        Spacer(
            modifier = Modifier.weight(1f)
        )
        
        // Grid Tombol
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false
        ) {
            items(buttonList) { symbol ->
                if (symbol.isNotEmpty()) {
                    CalculatorButton(
                        symbol = symbol,
                        onClick = { viewModel.onButtonClick(symbol) }
                    )
                } else {
                    // Spacer untuk empty button positions
                    Spacer(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(80.dp)
                    )
                }
            }
        }
    }
}
