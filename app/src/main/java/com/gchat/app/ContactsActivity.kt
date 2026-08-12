package com.gchat.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gchat.app.data.ContactManager

class ContactsActivity : AppCompatActivity() {

    private lateinit var contactManager: ContactManager
    private lateinit var contactsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactManager = ContactManager(this)

        createUI()
        loadContacts()
    }

    private fun createUI() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
        }

        // HEADER
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(18.dp(), 0, 12.dp(), 0)
            setBackgroundColor(Color.rgb(20, 110, 180))
        }

        val back = TextView(this).apply {
            text = "‹"
            textSize = 40f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setOnClickListener { finish() }
        }

        header.addView(
            back,
            LinearLayout.LayoutParams(
                48.dp(),
                64.dp()
            )
        )

        val title = TextView(this).apply {
            text = "Kontaktlar"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
        }

        header.addView(
            title,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                68.dp()
            )
        )

        // SEARCH
        val search = EditText(this).apply {
            hint = "Kontakt axtar..."
            textSize = 16f
            singleLine = true
            setPadding(18.dp(), 0, 18.dp(), 0)

            background = GradientDrawable().apply {
                setColor(Color.rgb(245, 245, 245))
                cornerRadius = 40.dp().toFloat()
            }
        }

        val searchParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            52.dp()
        )

        searchParams.setMargins(
            14.dp(),
            12.dp(),
            14.dp(),
            10.dp()
        )

        root.addView(search, searchParams)

        // CONTACTS
        contactsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(
            contactsContainer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // BOTTOM NAV
        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.rgb(250, 250, 250))
        }

        val chats = createNavItem("💬\nChats")
        val contacts = createNavItem("👥\nContacts")
        val profile = createNavItem("👤\nProfile")

        bottom.addView(chats)
        bottom.addView(contacts)
        bottom.addView(profile)

        chats.setOnClickListener {
            finish()
        }

        root.addView(
            bottom,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                68.dp()
            )
        )

        setContentView(root)

        // SEARCH FILTER
        search.addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    filterContacts(s.toString())
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {}
            }
        )
    }

    private var allContacts = listOf<com.gchat.app.data.Contact>()

    private fun loadContacts() {

        allContacts = contactManager.getContacts()

        displayContacts(allContacts)
    }

    private fun filterContacts(query: String) {

        val filtered = allContacts.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.id.contains(query, ignoreCase = true)
        }

        displayContacts(filtered)
    }

    private fun displayContacts(
        contacts: List<com.gchat.app.data.Contact>
    ) {

        contactsContainer.removeAllViews()

        if (contacts.isEmpty()) {

            val empty = TextView(this).apply {
                text = "Hələ kontakt yoxdur"
                textSize = 17f
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)
            }

            contactsContainer.addView(
                empty,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            )

            return
        }

        contacts.forEach { contact ->

            val item = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(
                    18.dp(),
                    8.dp(),
                    18.dp(),
                    8.dp()
                )
                isClickable = true
                isFocusable = true
            }

            // AVATAR
            val avatar = TextView(this).apply {
                text = contact.name
                    .take(1)
                    .uppercase()

                textSize = 21f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)

                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.rgb(20, 110, 180))
                }
            }

            item.addView(
                avatar,
                LinearLayout.LayoutParams(
                    56.dp(),
                    56.dp()
                )
            )

            // INFO
            val info = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16.dp(), 0, 0, 0)
            }

            val name = TextView(this).apply {
                text = contact.name
                textSize = 18f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.rgb(30, 30, 30))
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

            // CONTACT → CHAT
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

            contactsContainer.addView(
                item,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    76.dp()
                )
            )
        }
    }

    private fun createNavItem(text: String): TextView {

        return TextView(this).apply {

            this.text = text
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(50, 50, 50))

            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }
    }

    private fun Int.dp(): Int {
        return (
            this * resources.displayMetrics.density
        ).toInt()
    }
}
