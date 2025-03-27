package com.example.colorgamberge

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreditActivity : AppCompatActivity() {

    private lateinit var returnBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        // Stuff
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_credit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load elements by ids
        returnBtn = findViewById(R.id.returnBtn)

        // Set return click
        returnBtn.setOnClickListener {
            finish()
        }

        // Load colors
        returnBtn.setBackgroundColor(SESSION_SETTINGS.primaryColor)

    }
}