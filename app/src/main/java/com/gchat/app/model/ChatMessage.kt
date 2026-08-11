package com.gchat.app.model

data class ChatMessage(
    val id: String,
    val contactId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long
)
