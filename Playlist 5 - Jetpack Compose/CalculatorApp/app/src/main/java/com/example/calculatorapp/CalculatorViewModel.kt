package com.example.calculatorapp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {
    
    private val _equationText = mutableStateOf("")
    val equationText: State<String> = _equationText
    
    private val _resultText = mutableStateOf("0")
    val resultText: State<String> = _resultText
    
    fun onButtonClick(symbol: String) {
        when (symbol) {
            "AC" -> {
                _equationText.value = ""
                _resultText.value = "0"
            }
            
            "C" -> {
                if (_equationText.value.isNotEmpty()) {
                    _equationText.value = _equationText.value.dropLast(1)
                    calculateResult()
                }
            }
            
            "=" -> {
                _equationText.value = _resultText.value
            }
            
            else -> {
                _equationText.value += symbol
                calculateResult()
            }
        }
    }
    
    private fun calculateResult() {
        val expression = _equationText.value
            .replace("×", "*")
            .replace("÷", "/")
        
        if (expression.isBlank()) {
            _resultText.value = "0"
            return
        }
        
        try {
            val raw = ExpressionEvaluator.eval(expression)
            
            val formatted = if (raw % 1 == 0.0) {
                raw.toLong().toString()
            } else {
                // Format dengan jumlah desimal yang masuk akal
                String.format("%.10f", raw).trimEnd('0').trimEnd('.')
            }
            
            _resultText.value = formatted
            
        } catch (e: Exception) {
            _resultText.value = "Error"
        }
    }
}
