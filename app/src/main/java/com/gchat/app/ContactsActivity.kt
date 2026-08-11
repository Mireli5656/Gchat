package com.gchat.app

import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gchat.app.data.ContactManager

class ContactsActivity : AppCompatActivity() {

    private lateinit var contactManager: ContactManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactManager = ContactManager(this)

        showContacts()
    }

    private fun showContacts() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val title = TextView(this).apply {
            text = "Contacts"
            textSize = 28f
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 32)
        }

        root.addView(title)

        val contacts = contactManager.getContacts()

        if (contacts.isEmpty()) {

            val empty = TextView(this).apply {
                text = "Hələ kontakt yoxdur"
                textSize = 18f
                gravity = Gravity.CENTER
            }

            root.addView(empty)

        } else {

            contacts.forEach { contact ->

                val item = TextView(this).apply {
                    text = "${contact.name}\n@${contact.id}"
                    textSize = 18f
                    setTextColor(Color.BLACK)
                    setPadding(16, 20, 16, 20)
                }

                root.addView(item)
            }
        }

        setContentView(root)
    }
}
