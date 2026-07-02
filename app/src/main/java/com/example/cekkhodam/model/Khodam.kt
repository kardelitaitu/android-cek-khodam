package com.example.cekkhodam.model

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigInteger
import java.security.MessageDigest

data class Element(
    val id: Int,
    val name: String,
    val description: String,
    val financial: String,
    val romantic: String
)

data class Beast(
    val id: Int,
    val name: String,
    val description: String,
    val power: Int,
    val mysticism: Int,
    val agility: Int
)

data class Joke(
    val id: Int,
    val name: String,
    val description: String,
    val financial: String,
    val romantic: String,
    val power: Int,
    val mysticism: Int,
    val agility: Int
)

data class Flower(
    val id: Int,
    val name: String,
    val description: String,
    val financial: String,
    val romantic: String,
    val power: Int,
    val mysticism: Int,
    val agility: Int
)

data class Khodam(
    val name: String,
    val category: String,
    val description: String,
    val financial: String,
    val romantic: String,
    val power: Int,
    val mysticism: Int,
    val agility: Int,
    val glowColorHex: String
)

object KhodamEngine {

    // Parses a CSV line safely, respecting simple quote constraints
    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var currentToken = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '"') {
                inQuotes = !inQuotes
            } else if (c == ',' && !inQuotes) {
                result.add(currentToken.toString().trim())
                currentToken = StringBuilder()
            } else {
                currentToken.append(c)
            }
            i++
        }
        result.add(currentToken.toString().trim())
        return result
    }

    fun loadElements(context: Context): List<Element> {
        val list = mutableListOf<Element>()
        try {
            context.assets.open("elements.csv").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    // Skip header
                    reader.readLine()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val parts = parseCsvLine(line!!)
                        if (parts.size >= 5) {
                            list.add(
                                Element(
                                    id = parts[0].toIntOrNull() ?: 0,
                                    name = parts[1],
                                    description = parts[2],
                                    financial = parts[3],
                                    romantic = parts[4]
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun loadBeasts(context: Context): List<Beast> {
        val list = mutableListOf<Beast>()
        try {
            context.assets.open("beasts.csv").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readLine()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val parts = parseCsvLine(line!!)
                        if (parts.size >= 6) {
                            list.add(
                                Beast(
                                    id = parts[0].toIntOrNull() ?: 0,
                                    name = parts[1],
                                    description = parts[2],
                                    power = parts[3].toIntOrNull() ?: 50,
                                    mysticism = parts[4].toIntOrNull() ?: 50,
                                    agility = parts[5].toIntOrNull() ?: 50
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun loadJokes(context: Context): List<Joke> {
        val list = mutableListOf<Joke>()
        try {
            context.assets.open("jokes.csv").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readLine()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val parts = parseCsvLine(line!!)
                        if (parts.size >= 8) {
                            list.add(
                                Joke(
                                    id = parts[0].toIntOrNull() ?: 0,
                                    name = parts[1],
                                    description = parts[2],
                                    financial = parts[3],
                                    romantic = parts[4],
                                    power = parts[5].toIntOrNull() ?: 50,
                                    mysticism = parts[6].toIntOrNull() ?: 50,
                                    agility = parts[7].toIntOrNull() ?: 50
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun loadFlowers(context: Context): List<Flower> {
        val list = mutableListOf<Flower>()
        try {
            context.assets.open("flowers.csv").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readLine()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val parts = parseCsvLine(line!!)
                        if (parts.size >= 8) {
                            list.add(
                                Flower(
                                    id = parts[0].toIntOrNull() ?: 0,
                                    name = parts[1],
                                    description = parts[2],
                                    financial = parts[3],
                                    romantic = parts[4],
                                    power = parts[5].toIntOrNull() ?: 50,
                                    mysticism = parts[6].toIntOrNull() ?: 50,
                                    agility = parts[7].toIntOrNull() ?: 50
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    // Maps dynamic elements to glow colors
    private fun getGlowColorForElement(elementName: String): String {
        return when {
            elementName.contains("Wood", ignoreCase = true) -> "0xFF2ECC71" // Green
            elementName.contains("Fire", ignoreCase = true) -> "0xFFE74C3C" // Red
            elementName.contains("Water", ignoreCase = true) -> "0xFF3498DB" // Blue
            elementName.contains("Metal", ignoreCase = true) -> "0xFFBDC3C7" // Silver
            elementName.contains("Earth", ignoreCase = true) -> "0xFFF1C40F" // Gold
            elementName.contains("Cosmos", ignoreCase = true) -> "0xFF9B59B6" // Purple
            elementName.contains("Wind", ignoreCase = true) -> "0xFF1ABC9C" // Teal
            elementName.contains("Thunder", ignoreCase = true) -> "0xFFE67E22" // Orange
            elementName.contains("Light", ignoreCase = true) -> "0xFFF39C12" // Yellow
            else -> "0xFF7F8C8D" // Gray Shadow
        }
    }

    fun calculateKhodam(
        name: String,
        dob: String,
        elements: List<Element>,
        beasts: List<Beast>,
        jokes: List<Joke>,
        flowers: List<Flower>
    ): Khodam {
        // 1. Seed Normalization
        val normalizedName = name.trim().lowercase().replace("[^a-zA-Z0-9]".toRegex(), "")
        val seed = "${normalizedName}_${dob.trim()}"

        // 2. SHA-256 Hashing
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(seed.toByteArray(Charsets.UTF_8))
        val hashBigInt = BigInteger(1, hashBytes)

        // 3. Selection index modulus 450
        val totalSpace = 450
        val selectorValue = hashBigInt.mod(totalSpace.toBigInteger()).toInt()

        return when {
            // Pool 1: Element-Beast Combination (0 to 299)
            selectorValue < 300 -> {
                val elementIdx = if (elements.isNotEmpty()) selectorValue % elements.size else 0
                val beastIdx = if (beasts.isNotEmpty()) (selectorValue / 10) % beasts.size else 0
                
                val element = elements.getOrNull(elementIdx) ?: Element(0, "Cosmic", "energy", "Good fortune", "Bright love")
                val beast = beasts.getOrNull(beastIdx) ?: Beast(0, "Spirit", "guardian", 50, 50, 50)
                
                Khodam(
                    name = "${element.name} ${beast.name}",
                    category = "Element-Beast",
                    description = "${beast.name} which ${beast.description}, influenced by ${element.name} which ${element.description}.",
                    financial = element.financial,
                    romantic = element.romantic,
                    power = beast.power,
                    mysticism = beast.mysticism,
                    agility = beast.agility,
                    glowColorHex = getGlowColorForElement(element.name)
                )
            }
            // Pool 2: Jokes (300 to 349)
            selectorValue < 350 -> {
                val jokeIdx = if (jokes.isNotEmpty()) (selectorValue - 300) % jokes.size else 0
                val joke = jokes.getOrNull(jokeIdx) ?: Joke(0, "Brick", "stiff", "Safe", "None", 50, 10, 10)
                
                Khodam(
                    name = joke.name,
                    category = "Joke",
                    description = joke.description,
                    financial = joke.financial,
                    romantic = joke.romantic,
                    power = joke.power,
                    mysticism = joke.mysticism,
                    agility = joke.agility,
                    glowColorHex = "0xFFE67E22" // Fun orange glow for joke items
                )
            }
            // Pool 3: Flowers (350 to 449)
            else -> {
                val flowerIdx = if (flowers.isNotEmpty()) (selectorValue - 350) % flowers.size else 0
                val flower = flowers.getOrNull(flowerIdx) ?: Flower(0, "Rose", "thorny", "Wealthy", "Loving", 50, 50, 50)
                
                Khodam(
                    name = flower.name,
                    category = "Flower",
                    description = flower.description,
                    financial = flower.financial,
                    romantic = flower.romantic,
                    power = flower.power,
                    mysticism = flower.mysticism,
                    agility = flower.agility,
                    glowColorHex = "0xFFE91E63" // Pink/Magenta glow for flowers
                )
            }
        }
    }
}
