package com.example.endtropia

import java.math.BigDecimal
import java.math.RoundingMode

data class ArithmeticSymbol(
    val char: Char,
    val frequency: Int,
    val probability: BigDecimal,
    val lowRange: BigDecimal,
    val highRange: BigDecimal
)

data class EncodingStep(
    val char: Char,
    val oldLow: BigDecimal,
    val oldHigh: BigDecimal,
    val newLow: BigDecimal,
    val newHigh: BigDecimal
)

data class DecodingStep(
    val code: BigDecimal,
    val char: Char,
    val lowRange: BigDecimal,
    val highRange: BigDecimal,
    val nextCode: BigDecimal
)

object ArithmeticCalculator {
    private val PRECISION = 100 // High precision to avoid truncation

    fun getSymbols(text: String): List<ArithmeticSymbol> {
        val frequencies = text.groupingBy { it }.eachCount()
        val total = text.length.toBigDecimal()
        var currentLow = BigDecimal.ZERO
        
        // Sort symbols to have a consistent order (e.g., as in the example or alphabetical)
        // Usually, it's alphabetical or by frequency. Let's use alphabetical for "КОШКА".
        // In the example "SWISS MISS", it's ' ', 'M', 'I', 'W', 'S'.
        // In "КОШКА", it would be 'А', 'К', 'О', 'Ш'. Wait, K occurs twice.
        val sortedChars = frequencies.keys.sorted()
        
        return sortedChars.map { char ->
            val freq = frequencies[char]!!
            val prob = freq.toBigDecimal().divide(total, PRECISION, RoundingMode.HALF_UP).stripTrailingZeros()
            val low = currentLow
            val high = currentLow.add(prob)
            currentLow = high
            ArithmeticSymbol(char, freq, prob, low, high)
        }
    }

    fun encode(text: String, symbols: List<ArithmeticSymbol>): List<EncodingStep> {
        val symbolMap = symbols.associateBy { it.char }
        val steps = mutableListOf<EncodingStep>()
        
        var low = BigDecimal.ZERO
        var high = BigDecimal.ONE
        
        text.forEach { char ->
            val symbol = symbolMap[char] ?: return@forEach
            val range = high.subtract(low)
            
            val oldLow = low
            val oldHigh = high
            
            // NewHigh = OldLow + (OldHigh-OldLow)*HighRange(X)
            // NewLow = OldLow + (OldHigh-OldLow)*LowRange(X)
            val newLow = oldLow.add(range.multiply(symbol.lowRange))
            val newHigh = oldLow.add(range.multiply(symbol.highRange))
            
            steps.add(EncodingStep(char, oldLow.stripTrailingZeros(), oldHigh.stripTrailingZeros(), newLow.stripTrailingZeros(), newHigh.stripTrailingZeros()))
            
            low = newLow
            high = newHigh
        }
        
        return steps
    }

    fun decode(code: BigDecimal, length: Int, symbols: List<ArithmeticSymbol>): List<DecodingStep> {
        val steps = mutableListOf<DecodingStep>()
        var currentCode = code
        
        repeat(length) {
            val symbol = symbols.find { 
                currentCode >= it.lowRange && currentCode < it.highRange 
            } ?: symbols.last() // Fallback
            
            val low = symbol.lowRange
            val high = symbol.highRange
            
            // Code = (Code-LowRange(X))/(HighRange(X)-LowRange(X))
            val nextCode = currentCode.subtract(low)
                .divide(high.subtract(low), PRECISION, RoundingMode.HALF_UP)
                .stripTrailingZeros()
            
            steps.add(DecodingStep(currentCode.stripTrailingZeros(), symbol.char, low, high, nextCode))
            currentCode = nextCode
        }
        
        return steps
    }
}
