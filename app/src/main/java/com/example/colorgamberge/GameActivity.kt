package com.example.colorgamberge

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    // Éléments de l'interface
    private lateinit var base: ConstraintLayout
    private lateinit var timingCard: CloudCard
    private lateinit var correspondanceCard: CloudCard
    private lateinit var previewContainer: CardView
    private lateinit var preview: TextureView

    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var stringCameraID: String
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureRequestBuilder: CaptureRequest.Builder

    // Propriété du jeu
    private var score: Int = 0

    @RequiresPermission(CAMERA)
    @RequiresApi(Build.VERSION_CODES.P)
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

        // Paramétrage des textes
        timingCard.labelText = "Temps restant"
        timingCard.contentText = "15.00s"
        correspondanceCard.labelText = "Correspondance"
        correspondanceCard.contentText = "0%"

        // Demande de la permission caméra
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA), PackageManager.PERMISSION_GRANTED)

        // Initialisation du CameraManager
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager

        // Démarrage de la caméra (en s'assurant que le TextureView est prêt)
        startCamera()

        val color = generateRandomPastelColor()
        val (slightlyDarker, darker) = generateShades(color)

        base.backgroundTintList = ColorStateList.valueOf(color)
        timingCard.contentBackgroundColor = slightlyDarker
        timingCard.labelBackgroundColor = darker
        correspondanceCard.contentBackgroundColor = slightlyDarker
        correspondanceCard.labelBackgroundColor = darker


    }

    override fun onDestroy() {
        super.onDestroy()
        // Sauvegarde du meilleur score
        val sharedPreferences = getSharedPreferences("gamedata", MODE_PRIVATE)
        val currentBestScore = sharedPreferences.getInt("bestscore", 0)
        if (score > currentBestScore) {
            val editor = sharedPreferences.edit()
            editor.putInt("bestscore", score)
            editor.apply()
        }
    }

    // Callback de la caméra
    private val stateCallback = object : CameraDevice.StateCallback() {
        @RequiresApi(Build.VERSION_CODES.P)
        override fun onOpened(cd: CameraDevice) {
            cameraDevice = cd
            // Appel explicite à la fonction de l'activité
            this@GameActivity.startPreview()
        }

        override fun onDisconnected(cd: CameraDevice) {
            cameraDevice.close()
        }

        override fun onError(cd: CameraDevice, error: Int) {
            cameraDevice.close()
        }
    }

    // Démarre la caméra en s'assurant que le TextureView est prêt
    @RequiresPermission(CAMERA)
    @RequiresApi(Build.VERSION_CODES.P)
    fun startCamera() {
        if (preview.isAvailable) {
            openCamera()
        } else {
            preview.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                @RequiresPermission(CAMERA)
                override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                    // Appel explicite à la fonction de l'activité
                    this@GameActivity.openCamera()
                }

                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
            }
        }
    }

    // Ouvre la caméra
    @RequiresPermission(CAMERA)
    @RequiresApi(Build.VERSION_CODES.P)
    private fun openCamera() {
        try {
            // Récupération de l'ID de la caméra arrière
            val rearCameraId = getRearCameraId() ?: throw RuntimeException("Pas de caméra arrière trouvée")
            stringCameraID = rearCameraId
            cameraManager.openCamera(stringCameraID, stateCallback, null)
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    // Fonction utilitaire pour obtenir l'ID de la caméra arrière
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getRearCameraId(): String? {
        try {
            for (id in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                if (characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING) ==
                    android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK) {
                    return id
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }


    // Configure la session de capture et démarre la prévisualisation
    @RequiresApi(Build.VERSION_CODES.P)
    private fun startPreview() {
        val surfaceTexture = preview.surfaceTexture
        if (surfaceTexture == null) {
            Log.e("GameActivity", "SurfaceTexture est null")
            return
        }
        surfaceTexture.setDefaultBufferSize(preview.width, preview.height)
        val surface = Surface(surfaceTexture)
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)

            cameraDevice.createCaptureSession(listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        cameraCaptureSession = session
                        captureRequestBuilder.set(
                            CaptureRequest.CONTROL_AE_MODE,
                            CameraMetadata.CONTROL_MODE_AUTO
                        )
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("GameActivity", "Échec de la configuration de la session de capture")
                    }
                }, null
            )
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    // Arrête la caméra
    fun stopCamera() {
        cameraCaptureSession.abortCaptures()
    }

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

        // Générer les deux shades : une 15% plus foncée, l'autre 35% plus foncée
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

}
