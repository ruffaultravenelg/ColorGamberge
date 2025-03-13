package com.example.colorgamberge

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.TextureView
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.edit

const val COLR_MATCH_THRESHOLD = 70

@RequiresApi(Build.VERSION_CODES.P)
class GameActivity : AppCompatActivity(), PreviewColorCallback {

    // Chronomètre
    private lateinit var countDownTimer: CountDownTimer
    private var timeLeftInMillis: Long = 15000 // 15 secondes

    // Éléments de l'interface
    private lateinit var base: ConstraintLayout
    private lateinit var timingCard: CloudCard
    private lateinit var correspondanceCard: CloudCard
    private lateinit var previewContainer: CardView
    private lateinit var preview: TextureView
    private lateinit var hitboxDrawable: GradientDrawable

    // Propriété du jeu
    private var currentColor: Int = Color.WHITE
    private var score: Int = 0

    // Instance de CameraHandler
    private lateinit var cameraHandler: CameraHandler

    @RequiresPermission(CAMERA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialisation des vues
        base = findViewById(R.id.main)
        timingCard = CloudCard(findViewById(R.id.timingCard))
        correspondanceCard = CloudCard(findViewById(R.id.correspondanceCard))
        previewContainer = findViewById(R.id.previewContainer)
        preview = findViewById(R.id.preview)
        val hitbox: View = findViewById(R.id.hitbox)
        hitboxDrawable = hitbox.background as GradientDrawable

        // Demande de la permission caméra
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA), PackageManager.PERMISSION_GRANTED)

        // Initialisation du CameraHandler et démarrage de la caméra
        cameraHandler = CameraHandler(this, preview, this)
        cameraHandler.startCamera()

        // Get new color
        newColor();

    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()

        // Sauvegarde du meilleur score
        if (score > storageReadInt(this, "bestscore")) {
            storageWriteInt(this, "bestscore", score)
        }
    }

    private fun newColor(){

        // Get a new color
        currentColor = generateRandomPastelColor()
        val (slightlyDarker, darker) = generateShades(currentColor)

        // Paramétrage des textes
        timingCard.labelText = "Score: 0"
        timingCard.contentText = "15.00s"
        correspondanceCard.labelText = "Correspondence"
        correspondanceCard.contentText = "0%"

        // Set widjets color
        base.backgroundTintList = ColorStateList.valueOf(currentColor)
        timingCard.contentBackgroundColor = slightlyDarker
        timingCard.labelBackgroundColor = darker
        correspondanceCard.contentBackgroundColor = slightlyDarker
        correspondanceCard.labelBackgroundColor = darker

        // Set hitbox stroke color
        hitboxDrawable.setStroke(10, currentColor)

        // Réinitialiser le chronomètre à 15 secondes
        timeLeftInMillis = 15000
        startTimer()
        timingCard.labelText = "$score"
    }

    private fun startTimer() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel() // Annuler le timer précédent seulement s'il est initialisé
        }
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 10) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val seconds = millisUntilFinished / 1000.0
                timingCard.contentText = String.format("%.2fs", seconds)
            }

            override fun onFinish() {
                timingCard.contentText = "0.00s"
                gameOver()
            }
        }
        countDownTimer.start()
    }

    private fun gameOver() {
        countDownTimer.cancel()
        // Afficher un message ou lancer une nouvelle activité de Game Over
        timingCard.labelText = "Game Over!"
        correspondanceCard.contentText = "Score final: $score"
    }

    /**
     * Implémentation du callback de couleur détectée.
     */
    override fun colorFound(color: Int) {

        // Update hitbox color
        hitboxDrawable.setColor(color)

        // Update percent
        val percent = compare(color, currentColor);
        correspondanceCard.contentText = "$percent%"

        // Check if color
        if (percent > COLR_MATCH_THRESHOLD){
            score++
            newColor()
        }

    }


}
