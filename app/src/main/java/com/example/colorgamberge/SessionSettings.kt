package com.example.colorgamberge

var SESSION_SETTINGS = SessionSettings()

class SessionSettings() {

    var primaryColor: Int = generateRandomPastelColor()
    var primarySlightlyDarker: Int = 0
    var primaryDarker: Int = 0

    init {
        val (a, b) = com.example.colorgamberge.generateShades(primaryColor)
        primarySlightlyDarker = a
        primaryDarker = b
    }


}