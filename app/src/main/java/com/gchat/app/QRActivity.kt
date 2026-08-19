package com.gchat.app

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gchat.app.qr.QRData
import com.gchat.app.qr.QRGenerator

class QRActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "qr_id"
        const val EXTRA_NAME = "qr_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation =
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val id = intent.getStringExtra(EXTRA_ID)
        val name = intent.getStringExtra(EXTRA_NAME)

        if (id.isNullOrEmpty() || name.isNullOrEmpty()) {
            finish()
            return
        }

        val qrData = QRData(
            id = id,
            name = name
        )

        val bitmap: Bitmap = QRGenerator.generate(
            qrData.encode()
        )

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
        }

        val title = TextView(this).apply {
            text = "Mənim GChat QR"
            textSize = 24f
            gravity = Gravity.CENTER
        }

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val imageView = ImageView(this).apply {
            setImageBitmap(bitmap)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(16, 32, 16, 32)
        }

        root.addView(
            imageView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val nameText = TextView(this).apply {
            text = name
            textSize = 20f
            gravity = Gravity.CENTER
        }

        root.addView(nameText)

        val idText = TextView(this).apply {
            text = "@$id"
            textSize = 16f
            gravity = Gravity.CENTER
        }

        root.addView(idText)

        setContentView(root)
    }
}
