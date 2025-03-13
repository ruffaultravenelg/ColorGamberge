package com.example.colorgamberge

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Build
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.palette.graphics.Palette

/**
 * Interface de callback pour notifier la couleur dominante détectée dans le preview.
 */
interface PreviewColorCallback {
    fun colorFound(color: Int)
}

/**
 * Classe qui gère l'ouverture de la caméra, la création de la session de capture
 * et l'analyse des images du preview afin d'extraire la couleur dominante.
 */
@RequiresApi(Build.VERSION_CODES.P)
class CameraHandler(
    private val context: Context,
    private val textureView: TextureView,
    private val callback: PreviewColorCallback
) {
    private val cameraManager: CameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var cameraId: String

    // Callback de la CameraDevice
    private val stateCallback = object : CameraDevice.StateCallback() {
        @RequiresPermission(CAMERA)
        override fun onOpened(cd: CameraDevice) {
            cameraDevice = cd
            startPreview()
        }

        override fun onDisconnected(cd: CameraDevice) {
            cd.close()
        }

        override fun onError(cd: CameraDevice, error: Int) {
            cd.close()
        }
    }

    // Handle image bitmap
    private fun handleBitman(img: Bitmap){

        val size = 50
        val centerX = img.width / 2
        val centerY = img.height / 2

        // Déterminer les coordonnées du carré centré
        val left = (centerX - size / 2).coerceIn(0, img.width)
        val top = (centerY - size / 2).coerceIn(0, img.height)
        val right = (centerX + size / 2).coerceIn(0, img.width)
        val bottom = (centerY + size / 2).coerceIn(0, img.height)

        // Rogner l'image
        val croppedBitmap = Bitmap.createBitmap(img, left, top, right - left, bottom - top)

        // Analyser la couleur dominante
        Palette.from(croppedBitmap).generate { palette ->
            val color = palette?.dominantSwatch?.rgb ?: 0
            callback.colorFound(color)
        }

    }

    /**
     * Démarre la caméra en s'assurant que le TextureView est disponible.
     */
    fun startCamera() {
        if (textureView.isAvailable) {
            openCamera()
        } else {
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                @RequiresPermission(CAMERA)
                override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                    openCamera()
                }

                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                    // Analyse de l'image actuelle et notification via le callback.
                    val bitmap = textureView.bitmap
                    if (bitmap != null) {
                        handleBitman(bitmap)
                    }
                }
            }
        }
    }

    /**
     * Ouvre la caméra arrière.
     */
    private fun openCamera() {
        try {
            val rearCameraId = getRearCameraId() ?: throw RuntimeException("Pas de caméra arrière trouvée")
            cameraId = rearCameraId
            if (context.checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraId, stateCallback, null)
            } else {
                Log.e("CameraHandler", "Permission CAMERA non accordée")
            }
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Recherche l'ID de la caméra arrière.
     */
    private fun getRearCameraId(): String? {
        try {
            for (id in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                if (characteristics.get(CameraCharacteristics.LENS_FACING) ==
                    CameraCharacteristics.LENS_FACING_BACK
                ) {
                    return id
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Configure la session de capture et démarre la prévisualisation.
     */
    private fun startPreview() {
        val surfaceTexture = textureView.surfaceTexture
        if (surfaceTexture == null) {
            Log.e("CameraHandler", "SurfaceTexture est null")
            return
        }
        surfaceTexture.setDefaultBufferSize(textureView.width, textureView.height)
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
                        Log.e("CameraHandler", "Échec de la configuration de la session de capture")
                    }
                }, null
            )
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Arrête la caméra.
     */
    fun stopCamera() {
        cameraCaptureSession.abortCaptures()
    }
}
