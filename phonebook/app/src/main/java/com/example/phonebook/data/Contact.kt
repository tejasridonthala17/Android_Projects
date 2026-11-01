package com.example.phonebook.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contacts",
    indices = [Index(value = ["phoneNumber"], unique = true)]
)
data class Contact(
    @PrimaryKey(autoGenerate = true) val contactId: Long = 0,
    val name: String,
    val phoneNumber: String,
    // "MANUAL" or "DEVICE" (synced from the device Contacts Provider)
    val source: String = "MANUAL"
)
