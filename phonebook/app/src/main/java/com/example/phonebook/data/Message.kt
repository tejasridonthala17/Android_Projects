package com.example.phonebook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val messageId: Long = 0,
    val threadId: Long,
    val senderOrRecipient: String, // phone number
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val direction: String // "SENT" or "RECEIVED"
)
