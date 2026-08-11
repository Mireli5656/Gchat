package com.gchat.app

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gchat.app.qr.QRData

class ContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val qrData = intent.getStringExtra("qr_data")

        val data = qrData?.let {
            QRData.decode(it)
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "GChat Contact"
            textSize = 28f
        }

        val name = TextView(this).apply {
            text = data?.name ?: "Naməlum istifadəçi"
            textSize = 22f
            setPadding(0, 32, 0, 8)
        }

        val id = TextView(this).apply {
            text = data?.id?.let { "@$it" } ?: "Yanlış QR kod"
            textSize = 16f
        }

        val addButton = Button(this).apply {
            text = "Add Contact"
            isEnabled = data != null
        }

        layout.addView(title)
        layout.addView(name)
        layout.addView(id)
        layout.addView(addButton)

        setContentView(layout)
    }
}
