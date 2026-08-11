package com.gchat.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class ScannerActivity : AppCompatActivity() {

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result ->

        if (result.contents != null) {
            val data = result.contents

            val intent = android.content.Intent(
                this,
                ContactActivity::class.java
            )

            intent.putExtra("qr_data", data)

            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("GChat QR kodunu skan et")
            setBeepEnabled(true)
            setOrientationLocked(false)
        }

        barcodeLauncher.launch(options)
    }
}
