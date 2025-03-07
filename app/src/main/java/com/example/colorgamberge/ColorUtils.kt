package com.example.colorgamberge

import android.graphics.Color
import kotlin.random.Random
import kotlin.math.abs

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
    // Conversion des couleurs RGB en HSL
    val hsl1 = FloatArray(3)
    val hsl2 = FloatArray(3)
    Color.colorToHSV(color1, hsl1)
    Color.colorToHSV(color2, hsl2)

    // Calcul des différences pour chaque composante
    val hueDiff = abs(hsl1[0] - hsl2[0]) / 360f // Teinte (0 à 360 degrés)
    val satDiff = abs(hsl1[1] - hsl2[1])        // Saturation (0.0 à 1.0)
    val lightDiff = abs(hsl1[2] - hsl2[2])      // Luminosité (0.0 à 1.0)

    // Pondération : la teinte est souvent plus importante que la saturation et la luminosité
    val hueWeight = 0.6f
    val satWeight = 0.25f
    val lightWeight = 0.15f

    // Calcul de la différence globale (0 = identique, 1 = totalement différent)
    val difference = (hueDiff * hueWeight) + (satDiff * satWeight) + (lightDiff * lightWeight)

    // Conversion en pourcentage de similarité (100% = identique, 0% = totalement différent)
    val similarity = 100 * (1 - difference)

    return similarity.toInt().coerceIn(0, 100)
}
