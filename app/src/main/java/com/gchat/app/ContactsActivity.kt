package com.gchat.app

import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
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
            setBackgroundColor(Color.WHITE)
        }

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 20)
            setBackgroundColor(Color.rgb(20, 110, 180))
        }

        val title = TextView(this).apply {
            text = "GChat"
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
        }

        val subtitle = TextView(this).apply {
            text = "Kontaktlar"
            textSize = 15f
            setTextColor(Color.WHITE)
        }

        header.addView(title)
        header.addView(subtitle)

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                90.dp()
            )
        )

        val contacts = contactManager.getContacts()

        if (contacts.isEmpty()) {

            val empty = TextView(this).apply {
                text = "Hələ kontakt yoxdur"
                textSize = 18f
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)
            }

            root.addView(
                empty,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            )

        } else {

            contacts.forEach { contact ->

                val item = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(20, 12, 20, 12)
                    setBackgroundColor(Color.WHITE)

                    isClickable = true
                    isFocusable = true
                }

                // Avatar
                val avatar = TextView(this).apply {
                    text = contact.name
                        .take(1)
                        .uppercase()

                    textSize = 22f
                    gravity = Gravity.CENTER
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.rgb(20, 110, 180))
                }

                item.addView(
                    avatar,
                    LinearLayout.LayoutParams(
                        56.dp(),
                        56.dp()
                    )
                )

                // Name + ID
                val info = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 0, 0, 0)
                }

                val name = TextView(this).apply {
                    text = contact.name
                    textSize = 18f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(Color.BLACK)
                }

                val id = TextView(this).apply {
                    text = "@${contact.id}"
                    textSize = 14f
                    setTextColor(Color.GRAY)
                }

                info.addView(name)
                info.addView(id)

                item.addView(
                    info,
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                )

                // Kontaktun üstünə basanda ChatActivity açılır
                item.setOnClickListener {

                    val intent = Intent(
                        this,
                        ChatActivity::class.java
                    )

                    intent.putExtra(
                        "contact_id",
                        contact.id
                    )

                    intent.putExtra(
                        "contact_name",
                        contact.name
                    )

                    startActivity(intent)
                }

                root.addView(
                    item,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        80.dp()
                    )
                )
            }
        }

        setContentView(root)
    }

    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
