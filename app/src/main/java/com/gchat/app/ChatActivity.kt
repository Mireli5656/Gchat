package com.gchat.app

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gchat.app.data.MessageManager
import com.gchat.app.data.UserManager
import com.gchat.app.model.ChatMessage

class ChatActivity : AppCompatActivity() {

    private lateinit var messageManager: MessageManager
    private lateinit var userManager: UserManager

    private lateinit var messagesLayout: LinearLayout
    private lateinit var messageInput: EditText

    private var contactId: String = ""
    private var contactName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactId = intent.getStringExtra("contact_id") ?: ""
        contactName = intent.getStringExtra("contact_name") ?: "Contact"

        messageManager = MessageManager(this)
        userManager = UserManager(this)

        createChatUI()
        loadMessages()
    }

    private fun createChatUI() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val header = TextView(this).apply {
            text = contactName
            textSize = 24f
            setPadding(24, 24, 24, 24)
        }

        val scrollView = ScrollView(this)

        messagesLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        scrollView.addView(messagesLayout)

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(12, 12, 12, 12)
        }

        messageInput = EditText(this).apply {
            hint = "Mesaj yaz..."
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val sendButton = Button(this).apply {
            text = "Göndər"
        }

        sendButton.setOnClickListener {

            val text = messageInput.text
                .toString()
                .trim()

            if (text.isEmpty()) return@setOnClickListener

            val user = userManager.getUser()
                ?: return@setOnClickListener

            messageManager.addMessage(
                contactId = contactId,
                senderId = user.id,
                text = text
            )

            messageInput.text.clear()

            loadMessages()
        }

        bottom.addView(messageInput)
        bottom.addView(sendButton)

        root.addView(header)
        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )
        root.addView(bottom)

        setContentView(root)
    }

    private fun loadMessages() {

        messagesLayout.removeAllViews()

        val messages = messageManager.getMessages(contactId)
        val user = userManager.getUser()

        messages.forEach { message ->

            val textView = TextView(this).apply {
                text = message.text
                textSize = 17f
                setPadding(16, 12, 16, 12)

                gravity =
                    if (message.senderId == user?.id) {
                        Gravity.END
                    } else {
                        Gravity.START
                    }
            }

            messagesLayout.addView(textView)
        }
    }
}
