package com.gchat.app

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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
            setBackgroundColor(Color.rgb(245, 247, 249))
        }

        // HEADER
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(6.dp(), 0, 10.dp(), 0)
            setBackgroundColor(Color.rgb(20, 110, 180))
        }

        val backButton = TextView(this).apply {
            text = "‹"
            textSize = 40f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)

            setOnClickListener {
                finish()
            }
        }

        header.addView(
            backButton,
            LinearLayout.LayoutParams(
                52.dp(),
                68.dp()
            )
        )

        // AVATAR
        val avatar = TextView(this).apply {
            text = contactName
                .take(1)
                .uppercase()

            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(20, 110, 180))

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.WHITE)
            }
        }

        header.addView(
            avatar,
            LinearLayout.LayoutParams(
                46.dp(),
                46.dp()
            )
        )

        // NAME + STATUS
        val headerText = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12.dp(), 0, 0, 0)
        }

        val nameText = TextView(this).apply {
            text = contactName
            textSize = 19f
            typeface = Typeface.DEFAULT_BOLD
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
            headerText,
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

        // MESSAGES
        scrollView = ScrollView(this).apply {
            isFillViewport = true
        }

        messagesLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                10.dp(),
                16.dp(),
                10.dp(),
                16.dp()
            )
        }

        scrollView.addView(messagesLayout)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // MESSAGE BAR
        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                8.dp(),
                8.dp(),
                8.dp(),
                8.dp()
            )
            setBackgroundColor(Color.WHITE)
        }

        messageInput = EditText(this).apply {
            hint = "Mesaj yaz..."
            textSize = 16f
            setSingleLine(true)
            imeOptions = EditorInfo.IME_ACTION_SEND

            setTextColor(Color.rgb(30, 30, 30))
            setHintTextColor(Color.GRAY)

            setPadding(
                18.dp(),
                0,
                18.dp(),
                0
            )

            background = GradientDrawable().apply {
                setColor(Color.rgb(242, 244, 246))
                cornerRadius = 30.dp().toFloat()
            }
        }

        val inputParams = LinearLayout.LayoutParams(
            0,
            54.dp(),
            1f
        )

        inputParams.setMargins(
            0,
            0,
            6.dp(),
            0
        )

        bottom.addView(
            messageInput,
            inputParams
        )

        // SEND BUTTON
        val sendButton = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_send)
            setColorFilter(Color.WHITE)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.rgb(20, 110, 180))
            }

            contentDescription = "Göndər"
        }

        bottom.addView(
            sendButton,
            LinearLayout.LayoutParams(
                54.dp(),
                54.dp()
            )
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
            bottom,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                70.dp()
            )
        )

        setContentView(root)
    }

    private fun sendMessage() {

        val text = messageInput.text
            .toString()
            .trim()

        if (text.isEmpty()) return

        val user = userManager.getUser()
            ?: return

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

        val messages =
            messageManager.getMessages(contactId)

        val user = userManager.getUser()

        messages.forEach { message ->

            val bubble = TextView(this).apply {

                text = message.text
                textSize = 16f

                setTextColor(Color.rgb(25, 25, 25))

                setPadding(
                    16.dp(),
                    11.dp(),
                    16.dp(),
                    11.dp()
                )

                background = GradientDrawable().apply {

                    val mine =
                        message.senderId == user?.id

                    setColor(
                        if (mine)
                            Color.rgb(210, 241, 205)
                        else
                            Color.WHITE
                    )

                    cornerRadius = 20.dp().toFloat()
                }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(
                8.dp(),
                4.dp(),
                8.dp(),
                4.dp()
            )

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
            scrollView.fullScroll(
                ScrollView.FOCUS_DOWN
            )
        }
    }

    private fun Int.dp(): Int {
        return (
            this * resources.displayMetrics.density
        ).toInt()
    }
}
