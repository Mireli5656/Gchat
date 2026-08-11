package com.gchat.app.data

import android.content.Context
import com.gchat.app.model.ChatMessage
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class MessageManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "gchat_messages",
        Context.MODE_PRIVATE
    )

    fun addMessage(
        contactId: String,
        senderId: String,
        text: String
    ): ChatMessage {

        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            contactId = contactId,
            senderId = senderId,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        val messages = getMessages().toMutableList()
        messages.add(message)

        saveMessages(messages)

        return message
    }

    fun getMessages(
        contactId: String
    ): List<ChatMessage> {

        return getMessages()
            .filter { it.contactId == contactId }
            .sortedBy { it.timestamp }
    }

    private fun getMessages(): List<ChatMessage> {

        val json = prefs.getString(
            "messages",
            "[]"
        ) ?: "[]"

        val array = JSONArray(json)
        val messages = mutableListOf<ChatMessage>()

        for (i in 0 until array.length()) {

            val item = array.getJSONObject(i)

            messages.add(
                ChatMessage(
                    id = item.getString("id"),
                    contactId = item.getString("contactId"),
                    senderId = item.getString("senderId"),
                    text = item.getString("text"),
                    timestamp = item.getLong("timestamp")
                )
            )
        }

        return messages
    }

    private fun saveMessages(
        messages: List<ChatMessage>
    ) {

        val array = JSONArray()

        messages.forEach { message ->

            val item = JSONObject()

            item.put("id", message.id)
            item.put("contactId", message.contactId)
            item.put("senderId", message.senderId)
            item.put("text", message.text)
            item.put("timestamp", message.timestamp)

            array.put(item)
        }

        prefs.edit()
            .putString(
                "messages",
                array.toString()
            )
            .apply()
    }
}
