package com.example.colorgamberge

import android.graphics.Color
import kotlin.random.Random
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

fun generateRandomPastelColor(): Int {
    val base = 128 // Valeur minimum pour assurer une teinte pastel (entre 128 et 255)
    val red = base + Random.nextInt(128)
    val green = base + Random.nextInt(128)
    val blue = base + Random.nextInt(128)
    return Color.rgb(red, green, blue)
}

fun generateShades(color: Int): Pair<Int, Int> {
    // Récupération des composants RGB
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)

    // Fonction pour assombrir une couleur selon un facteur donné (0.0 à 1.0)
    fun darken(colorValue: Int, factor: Float): Int {
        return (colorValue * factor).coerceIn(0f, 255f).toInt()
    }

    // Générer les deux teintes : une 15% plus foncée, l'autre 35% plus foncée
    val slightlyDarker = Color.rgb(
        darken(red, 0.85f),
        darken(green, 0.85f),
        darken(blue, 0.85f)
    )

    val darker = Color.rgb(
        darken(red, 0.65f),
        darken(green, 0.65f),
        darken(blue, 0.65f)
    )

    return Pair(slightlyDarker, darker)
}

fun colorSimilarity(color1: Int, color2: Int): Int {
    // Extraire les composantes RGB
    val r1 = Color.red(color1) / 255.0
    val g1 = Color.green(color1) / 255.0
    val b1 = Color.blue(color1) / 255.0

    val r2 = Color.red(color2) / 255.0
    val g2 = Color.green(color2) / 255.0
    val b2 = Color.blue(color2) / 255.0

    // Calculer la luminance relative (pondération perceptuelle)
    val luminance1 = 0.299 * r1 + 0.587 * g1 + 0.114 * b1
    val luminance2 = 0.299 * r2 + 0.587 * g2 + 0.114 * b2

    // Ajustement pour réduire l'impact des couleurs très claires
    val weight = 1.0 - ((luminance1 + luminance2) / 2.0)

    // Distance euclidienne ajustée
    val distance = sqrt(
        weight * (r2 - r1).pow(2.0) +
                weight * (g2 - g1).pow(2.0) +
                weight * (b2 - b1).pow(2.0)
    )

    // Distance maximale ajustée (pondérée)
    val maxDistance = sqrt(3.0) // distance max normalisée (1.0 pour chaque canal)

    // Calcul du pourcentage de similarité
    val similarity = (1 - (distance / maxDistance)) * 100

    return similarity.toInt().coerceIn(0, 100)
}
