package com.gchat.app

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gchat.app.data.MessageManager
import com.gchat.app.data.UserManager

class ChatActivity : AppCompatActivity() {

    private lateinit var messageManager: MessageManager
    private lateinit var userManager: UserManager

    private lateinit var messagesLayout: LinearLayout
    private lateinit var messageInput: EditText
    private lateinit var scrollView: ScrollView

    private var contactId = ""
    private var contactName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactId = intent.getStringExtra("contact_id") ?: ""
        contactName = intent.getStringExtra("contact_name") ?: "GChat"

        messageManager = MessageManager(this)
        userManager = UserManager(this)

        createChatUI()
        loadMessages()
    }

    private fun createChatUI() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
        }

        // HEADER
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 12, 12, 12)
            setBackgroundColor(Color.rgb(20, 110, 180))
        }

        val backButton = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_revert)
            setBackgroundColor(Color.TRANSPARENT)
            setOnClickListener { finish() }
        }

        val headerText = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 0, 0, 0)
        }

        val nameText = TextView(this).apply {
            text = contactName
            textSize = 20f
            setTextColor(Color.WHITE)
        }

        val statusText = TextView(this).apply {
            text = "GChat"
            textSize = 13f
            setTextColor(Color.WHITE)
        }

        headerText.addView(nameText)
        headerText.addView(statusText)

        header.addView(
            backButton,
            LinearLayout.LayoutParams(52, 52)
        )

        header.addView(
            headerText,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        // MESSAGES
        scrollView = ScrollView(this)

        messagesLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 16, 12, 16)
        }

        scrollView.addView(messagesLayout)

        // MESSAGE BAR
        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 8, 8, 8)
            setBackgroundColor(Color.rgb(245, 245, 245))
        }

        messageInput = EditText(this).apply {
            hint = "Mesaj yaz..."
            textSize = 16f
            setSingleLine(true)
            imeOptions = EditorInfo.IME_ACTION_SEND

            setPadding(20, 12, 20, 12)

            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = 60f
            }
        }

        val sendButton = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_send)
            setBackgroundColor(Color.TRANSPARENT)
        }

        val inputParams = LinearLayout.LayoutParams(
            0,
            56.dp(),
            1f
        )

        inputParams.setMargins(4, 0, 4, 0)

        bottom.addView(messageInput, inputParams)

        bottom.addView(
            sendButton,
            LinearLayout.LayoutParams(52, 52)
        )

        sendButton.setOnClickListener {
            sendMessage()
        }

        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                68.dp()
            )
        )

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(
            bottom,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                72.dp()
            )
        )

        setContentView(root)
    }

    private fun sendMessage() {

        val text = messageInput.text.toString().trim()

        if (text.isEmpty()) return

        val user = userManager.getUser() ?: return

        messageManager.addMessage(
            contactId = contactId,
            senderId = user.id,
            text = text
        )

        messageInput.text.clear()

        loadMessages()
    }

    private fun loadMessages() {

        messagesLayout.removeAllViews()

        val messages = messageManager.getMessages(contactId)
        val user = userManager.getUser()

        messages.forEach { message ->

            val bubble = TextView(this).apply {

                text = message.text
                textSize = 16f

                setTextColor(Color.BLACK)

                setPadding(
                    18,
                    12,
                    18,
                    12
                )

                background =
                    android.graphics.drawable.GradientDrawable().apply {

                        setColor(
                            if (message.senderId == user?.id)
                                Color.rgb(220, 248, 198)
                            else
                                Color.rgb(240, 240, 240)
                        )

                        cornerRadius = 28f
                    }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(8, 5, 8, 5)

            params.gravity =
                if (message.senderId == user?.id)
                    Gravity.END
                else
                    Gravity.START

            messagesLayout.addView(
                bubble,
                params
            )
        }

        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
