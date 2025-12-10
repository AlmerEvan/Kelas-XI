package com.example.calculatorapp

/**
 * Parser sederhana untuk mengevaluasi ekspresi matematika
 * Mendukung +, -, *, /, dan prioritas operasi
 */
object ExpressionEvaluator {
    
    fun eval(expression: String): Double {
        return Parser(expression.trim()).parse()
    }
    
    private class Parser(private val input: String) {
        private var position = 0
        
        fun parse(): Double {
            return parseAddSubtract()
        }
        
        // Level 1: Penjumlahan dan pengurangan (prioritas terendah)
        private fun parseAddSubtract(): Double {
            var result = parseMultiplyDivide()
            
            while (position < input.length) {
                val op = input[position]
                if (op == '+' || op == '-') {
                    position++
                    val right = parseMultiplyDivide()
                    result = when (op) {
                        '+' -> result + right
                        '-' -> result - right
                        else -> result
                    }
                } else {
                    break
                }
            }
            
            return result
        }
        
        // Level 2: Perkalian dan pembagian (prioritas lebih tinggi)
        private fun parseMultiplyDivide(): Double {
            var result = parsePrimary()
            
            while (position < input.length) {
                val op = input[position]
                if (op == '*' || op == '/') {
                    position++
                    val right = parsePrimary()
                    result = when (op) {
                        '*' -> result * right
                        '/' -> {
                            if (right == 0.0) {
                                throw ArithmeticException("Pembagian dengan nol")
                            }
                            result / right
                        }
                        else -> result
                    }
                } else {
                    break
                }
            }
            
            return result
        }
        
        // Level 3: Nilai utama (angka, parenthesis, negasi)
        private fun parsePrimary(): Double {
            // Handle spasi
            skipWhitespace()
            
            if (position >= input.length) {
                throw IllegalArgumentException("Ekspresi tidak lengkap")
            }
            
            val ch = input[position]
            
            // Handle angka negatif atau tanda minus di depan
            if (ch == '-') {
                position++
                return -parsePrimary()
            }
            
            // Handle tanda plus di depan
            if (ch == '+') {
                position++
                return parsePrimary()
            }
            
            // Handle parenthesis
            if (ch == '(') {
                position++ // skip '('
                val result = parseAddSubtract()
                skipWhitespace()
                if (position >= input.length || input[position] != ')') {
                    throw IllegalArgumentException("Parenthesis tidak ditutup")
                }
                position++ // skip ')'
                return result
            }
            
            // Parse angka (integer atau desimal)
            return parseNumber()
        }
        
        private fun parseNumber(): Double {
            skipWhitespace()
            
            var num = ""
            var hasDecimalPoint = false
            
            while (position < input.length) {
                val ch = input[position]
                
                when {
                    ch.isDigit() -> {
                        num += ch
                        position++
                    }
                    ch == '.' && !hasDecimalPoint -> {
                        hasDecimalPoint = true
                        num += ch
                        position++
                    }
                    else -> break
                }
            }
            
            if (num.isEmpty()) {
                throw IllegalArgumentException("Angka tidak ditemukan")
            }
            
            return num.toDouble()
        }
        
        private fun skipWhitespace() {
            while (position < input.length && input[position] == ' ') {
                position++
            }
        }
    }
}
