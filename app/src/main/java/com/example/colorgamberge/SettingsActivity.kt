package com.example.colorgamberge

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var returnBtn: Button
    private lateinit var titleCard: CardView
    private lateinit var threshold: SeekBar
    private lateinit var timepercolor: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        // Stuff
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load elements by ids
        returnBtn = findViewById(R.id.returnBtn)
        titleCard = findViewById(R.id.titleCard)
        threshold = findViewById(R.id.threshold)
        timepercolor = findViewById(R.id.timepercolor)

        // Set return click
        returnBtn.setOnClickListener {
            finish()
        }

        // Load colors
        returnBtn.setBackgroundColor(SESSION_SETTINGS.primaryColor)
        titleCard.backgroundTintList = ColorStateList.valueOf(SESSION_SETTINGS.primarySlightlyDarker)

    }

    override fun onStart() {
        super.onStart()

        // Load values
        threshold.progress = storageReadInt(this, "COLOR_MATCH_THRESHOLD", BASE_COLOR_MATCH_THRESHOLD);
        timepercolor.setText((storageReadInt(this, "TIME_PER_COLOR", BASE_TIME_PER_COLOR) / 1000).toString());

    }

    override fun onDestroy() {
        super.onDestroy()

        // Save data
        storageWriteInt(this, "COLOR_MATCH_THRESHOLD", threshold.progress)
        storageWriteInt(this, "TIME_PER_COLOR", Integer.parseInt(timepercolor.text.toString()) * 1000);

    }
}