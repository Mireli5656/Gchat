package com.project.gchat.qr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.project.gchat.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRScannerActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewFinder: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)

        viewFinder = findViewById(R.id.viewFinder)
        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QRAnalyzer { qrCodeValue ->
                        Log.d("GChat_QR", "Kod: $qrCodeValue")
                        runOnUiThread {
                            Toast.makeText(this, "Açar oxundu!", Toast.LENGTH_SHORT).show()
                            
                            // YENİLƏNƏN HİSSƏ: Açarı cibinə qoyub əsas ekrana göndərir
                            val resultIntent = Intent()
                            resultIntent.putExtra("SCAN_RESULT", qrCodeValue)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                Log.e("GChat_Error", "Kamera xətası", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

class QRAnalyzer(private val onQrCodeDetected: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { onQrCodeDetected(it) }
                    }
                }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }
}
