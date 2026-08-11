package com.gchat.app

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gchat.app.data.ContactManager
import com.gchat.app.model.GChatContact
import com.gchat.app.qr.QRData

class ContactActivity : AppCompatActivity() {

    private lateinit var contactManager: ContactManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactManager = ContactManager(this)

        val qrData = intent.getStringExtra("qr_data")

        val data = qrData?.let {
            QRData.decode(it)
        }

        if (data == null) {
            Toast.makeText(
                this,
                "QR kod etibarsızdır",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        showContact(data)
    }

    private fun showContact(data: QRData) {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "GChat istifadəçisi"
            textSize = 26f
        }

        val name = TextView(this).apply {
            text = "Ad: ${data.name}"
            textSize = 20f
            setPadding(0, 32, 0, 16)
        }

        val id = TextView(this).apply {
            text = "ID: @${data.id}"
            textSize = 18f
            setPadding(0, 0, 0, 32)
        }

        val addButton = Button(this).apply {
            text = "Kontakta əlavə et"
        }

        addButton.setOnClickListener {

            contactManager.addContact(
                GChatContact(
                    id = data.id,
                    name = data.name
                )
            )

            Toast.makeText(
                this,
                "${data.name} kontaktlara əlavə edildi",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        root.addView(title)
        root.addView(name)
        root.addView(id)
        root.addView(addButton)

        setContentView(root)
    }
}
