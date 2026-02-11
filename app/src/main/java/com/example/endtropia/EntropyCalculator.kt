package com.example.endtropia

import kotlin.math.ln
import kotlin.math.log
import kotlin.math.log10
import kotlin.math.log2

object EntropyCalculator {

    fun calculateShannon(probabilities: List<Double>, base: Double): Double {
        if (probabilities.isEmpty()) return 0.0
        return -probabilities.filter { it > 0 }.sumOf { p -> p * (ln(p) / ln(base)) }
    }

    fun getProbabilitiesFromText(text: String): List<Double> {
        if (text.isEmpty()) return emptyList()
        val frequencies = text.groupingBy { it }.eachCount()
        val total = text.length.toDouble()
        return frequencies.values.map { it / total }
    }

    data class EntropyResults(
        val bits: Double,
        val nats: Double,
        val hartleys: Double,
        val alphabetUnits: Double,
        val alphabetSize: Int
    )

    fun calculateAll(text: String): EntropyResults {
        val probs = getProbabilitiesFromText(text)
        val m = probs.size.toDouble()
        
        return EntropyResults(
            bits = calculateShannon(probs, 2.0),
            nats = calculateShannon(probs, Math.E),
            hartleys = calculateShannon(probs, 10.0),
            alphabetUnits = if (m > 1.0) calculateShannon(probs, m) else 0.0,
            alphabetSize = probs.size
        )
    }

    fun calculateAllFromProbs(probs: List<Double>): EntropyResults {
        val m = probs.size.toDouble()
        return EntropyResults(
            bits = calculateShannon(probs, 2.0),
            nats = calculateShannon(probs, Math.E),
            hartleys = calculateShannon(probs, 10.0),
            alphabetUnits = if (m > 1.0) calculateShannon(probs, m) else 0.0,
            alphabetSize = probs.size
        )
    }
}
