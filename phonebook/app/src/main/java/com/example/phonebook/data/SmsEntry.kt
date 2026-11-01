package com.example.phonebook.data

/** Plain (non-Room) model for an SMS read from the device inbox. */
data class SmsEntry(
    val sender: String,
    val body: String,
    val date: Long
)
