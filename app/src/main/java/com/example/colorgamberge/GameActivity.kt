package com.example.colorgamberge

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameActivity : AppCompatActivity() {

    // Elements
    private lateinit var scoreCard: CloudCard
    private lateinit var correspondanceCard: CloudCard
    private lateinit var previewContainer: CardView

    // Game properties
    private var score: Int = 0

    // Game activity launched
    override fun onCreate(savedInstanceState: Bundle?) {

        // Again stuff i didn't tried to understand, this doesn't look great anyway
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Goofy ahhh lateinits
        scoreCard = CloudCard(findViewById(R.id.scoreCard))
        correspondanceCard = CloudCard(findViewById(R.id.correspondanceCard))
        previewContainer = findViewById(R.id.previewContainer)



    }

    // A la fermeture de l'activité
    override fun onDestroy() {
        super.onDestroy()

        // Get best score
        val sharedPreferences = getSharedPreferences("gamedata", MODE_PRIVATE)
        val currentBestScore = sharedPreferences.getInt("bestscore", 0)

        // Set best score if necessary
        if (score > currentBestScore){
            val editor = sharedPreferences.edit()
            editor.putInt("bestscore", score)
            editor.apply()
        }

    }

}