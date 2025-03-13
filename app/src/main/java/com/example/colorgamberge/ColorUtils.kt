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

// Conversion d'une valeur RGB dans la gamme [0, 255] à la gamme [0, 1]
fun normalizeRgb(rgb: Int): Double {
    return rgb / 255.0
}

// Fonction de conversion de RGB à XYZ
fun rgbToXyz(r: Int, g: Int, b: Int): Triple<Double, Double, Double> {
    // Normaliser les valeurs RGB de 0-255 à 0-1
    val rNorm = normalizeRgb(r)
    val gNorm = normalizeRgb(g)
    val bNorm = normalizeRgb(b)

    // Application de la correction gamma (si la valeur normalisée dépasse un certain seuil, on applique un gamma correction)
    val rLinear = if (rNorm <= 0.04045) rNorm / 12.92 else Math.pow((rNorm + 0.055) / 1.055, 2.4)
    val gLinear = if (gNorm <= 0.04045) gNorm / 12.92 else Math.pow((gNorm + 0.055) / 1.055, 2.4)
    val bLinear = if (bNorm <= 0.04045) bNorm / 12.92 else Math.pow((bNorm + 0.055) / 1.055, 2.4)

    // Calcul des composantes XYZ avec les matrices de conversion sRGB -> XYZ
    val x = rLinear * 0.4124564 + gLinear * 0.3575761 + bLinear * 0.1804375
    val y = rLinear * 0.2126729 + gLinear * 0.7151522 + bLinear * 0.0721750
    val z = rLinear * 0.0193339 + gLinear * 0.1191920 + bLinear * 0.9503041

    return Triple(x, y, z)
}

// Fonction de conversion de XYZ à L*a*b*
fun xyzToLab(x: Double, y: Double, z: Double): Triple<Double, Double, Double> {
    // Les valeurs d'XYZ doivent être normalisées par rapport à un illuminant de référence D65
    val xNorm = x / 0.95047
    val yNorm = y / 1.00000
    val zNorm = z / 1.08883

    // Fonction de transformation selon la norme CIE
    fun pivot(t: Double): Double {
        return if (t > 0.008856) Math.pow(t, 1.0 / 3.0) else (t * 903.3 + 16.0) / 116.0
    }

    val l = 116.0 * pivot(yNorm) - 16.0
    val a = 500.0 * (pivot(xNorm) - pivot(yNorm))
    val b = 200.0 * (pivot(yNorm) - pivot(zNorm))

    return Triple(l, a, b)
}

// Fonction principale de conversion RGB à L*a*b*
fun rgbToLab(r: Int, g: Int, b: Int): Triple<Double, Double, Double> {
    val (x, y, z) = rgbToXyz(r, g, b)
    return xyzToLab(x, y, z)
}


// Calcul de la différence Delta E entre deux couleurs L*a*b*
fun deltaE(lab1: Triple<Double, Double, Double>, lab2: Triple<Double, Double, Double>): Double {
    val deltaL = lab1.first - lab2.first
    val deltaA = lab1.second - lab2.second
    val deltaB = lab1.third - lab2.third
    return Math.sqrt(deltaL * deltaL + deltaA * deltaA + deltaB * deltaB)
}

// Fonction de comparaison
fun compare(a: Int, b: Int): Int {
    val labA = rgbToLab((a shr 16) and 0xFF, (a shr 8) and 0xFF, a and 0xFF)
    val labB = rgbToLab((b shr 16) and 0xFF, (b shr 8) and 0xFF, b and 0xFF)

    val delta = deltaE(labA, labB)

    // Exemple d'une fonction qui renvoie un pourcentage (cela dépend de ton seuil de tolérance)
    val maxDelta = 100.0 // valeur théorique maximale pour une grande différence
    return ((1 - delta / maxDelta) * 100).toInt()
}


class test{
    val test: Int = (Math.random() * 100).toInt()
}
val A = test()