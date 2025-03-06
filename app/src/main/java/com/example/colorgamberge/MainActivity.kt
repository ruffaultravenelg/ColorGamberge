package com.example.colorgamberge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Elements
    private lateinit var bestScore: CloudCard
    private lateinit var newGameBtn: Button

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

        // Get bestscore
        val sharedPreferences = getSharedPreferences("gamedata", MODE_PRIVATE)
        bestScore.contentText = sharedPreferences.getInt("bestscore", 0).toString()

        // Set new game click event
        newGameBtn.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }


    }

}