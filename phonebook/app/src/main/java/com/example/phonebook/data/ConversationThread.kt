package com.example.phonebook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "threads")
data class ConversationThread(
    @PrimaryKey(autoGenerate = true) val threadId: Long = 0,
    val contactName: String,
    val phoneNumber: String,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis()
)
