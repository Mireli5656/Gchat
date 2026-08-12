package com.gchat.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class ScannerActivity : AppCompatActivity() {

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result ->

        if (result.contents != null) {

            val intent = android.content.Intent(
                this,
                ContactActivity::class.java
            )

            intent.putExtra("qr_data", result.contents)

            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Scanner həmişə portret vəziyyətində açılsın
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("GChat QR kodunu skan et")
            setBeepEnabled(true)

            // Ekranın dönməsinə icazə vermə
            setOrientationLocked(true)
        }

        barcodeLauncher.launch(options)
    }
}
