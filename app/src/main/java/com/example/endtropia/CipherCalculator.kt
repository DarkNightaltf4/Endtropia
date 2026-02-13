package com.example.endtropia

object CipherCalculator {
    private val russianAlphabet = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
    private val alphabetSize = russianAlphabet.length

    // --- Caesar ---
    fun caesarEncrypt(text: String, shift: Int = 3): String = caesarProcess(text, shift)
    fun caesarDecrypt(text: String, shift: Int = 3): String = caesarProcess(text, -shift)

    private fun caesarProcess(text: String, shift: Int): String {
        return text.map { char ->
            val upperChar = char.uppercaseChar()
            val index = russianAlphabet.indexOf(upperChar)
            if (index != -1) {
                var newIndex = (index + shift) % alphabetSize
                if (newIndex < 0) newIndex += alphabetSize
                val resultChar = russianAlphabet[newIndex]
                if (char.isLowerCase()) resultChar.lowercaseChar() else resultChar
            } else {
                char
            }
        }.joinToString("")
    }

    // --- Key Cipher (Vigenere-like but matches assignment logic) ---
    fun keyEncrypt(text: String, key: String): String {
        val cleanText = text.uppercase().filter { it in russianAlphabet }
        val cleanKey = key.uppercase().filter { it in russianAlphabet }
        if (cleanKey.isEmpty() || cleanText.isEmpty()) return ""

        return cleanText.mapIndexed { index, char ->
            val textIdx = russianAlphabet.indexOf(char) + 1 // 1-based
            val keyChar = cleanKey[index % cleanKey.length]
            val keyIdx = russianAlphabet.indexOf(keyChar) + 1 // 1-based
            
            var sum = textIdx + keyIdx
            if (sum > alphabetSize) sum -= alphabetSize
            
            russianAlphabet[sum - 1]
        }.joinToString("")
    }

    fun keyDecrypt(text: String, key: String): String {
        val cleanText = text.uppercase().filter { it in russianAlphabet }
        val cleanKey = key.uppercase().filter { it in russianAlphabet }
        if (cleanKey.isEmpty() || cleanText.isEmpty()) return ""

        return cleanText.mapIndexed { index, char ->
            val textIdx = russianAlphabet.indexOf(char) + 1 // 1-based
            val keyChar = cleanKey[index % cleanKey.length]
            val keyIdx = russianAlphabet.indexOf(keyChar) + 1 // 1-based
            
            var diff = textIdx - keyIdx
            if (diff <= 0) diff += alphabetSize
            
            russianAlphabet[diff - 1]
        }.joinToString("")
    }

    // --- Transposition ---
    fun transpositionEncrypt(text: String, cols: Int = 4): String {
        var paddedText = text.replace(" ", "_")
        while (paddedText.length % cols != 0) {
            paddedText += "_"
        }
        
        val rows = paddedText.length / cols
        val matrix = Array(rows) { r ->
            paddedText.substring(r * cols, (r + 1) * cols)
        }
        
        val result = StringBuilder()
        for (c in 0 until cols) {
            for (r in 0 until rows) {
                result.append(matrix[r][c])
            }
        }
        return result.toString()
    }

    fun transpositionDecrypt(text: String, cols: Int = 4): String {
        if (text.isEmpty() || text.length % cols != 0) return text
        
        val rows = text.length / cols
        val matrix = Array(rows) { CharArray(cols) }
        
        var charIdx = 0
        for (c in 0 until cols) {
            for (r in 0 until rows) {
                matrix[r][c] = text[charIdx++]
            }
        }
        
        val result = StringBuilder()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                result.append(matrix[r][c])
            }
        }
        return result.toString()
    }
}
