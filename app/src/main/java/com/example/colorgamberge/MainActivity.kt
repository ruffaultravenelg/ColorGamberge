package com.example.colorgamberge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Elements
    private lateinit var bestScore: CloudCard
    private lateinit var newGameBtn: Button
    private lateinit var settingsBtn: Button
    private lateinit var rulesBtn: Button

    // First activity
    override fun onCreate(savedInstanceState: Bundle?) {

        // Init boilerplate stuff
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get xml elements
        bestScore = CloudCard(findViewById(R.id.bestScore))
        newGameBtn = findViewById(R.id.newGameBtn)
        settingsBtn = findViewById(R.id.settings)
        rulesBtn = findViewById(R.id.rules)

        // Set new game click event
        newGameBtn.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        // Set setting click
        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Set colors
        newGameBtn.setBackgroundColor(SESSION_SETTINGS.primarySlightlyDarker)
        rulesBtn.setBackgroundColor(SESSION_SETTINGS.primarySlightlyDarker)
        settingsBtn.setBackgroundColor(SESSION_SETTINGS.primarySlightlyDarker)
        bestScore.labelBackgroundColor = SESSION_SETTINGS.primaryDarker
        bestScore.contentBackgroundColor = SESSION_SETTINGS.primarySlightlyDarker

    }

    override fun onStart() {
        super.onStart()

        // Get bestscore
        bestScore.contentText = storageReadInt(this, "bestscore", 0).toString()

    }

}