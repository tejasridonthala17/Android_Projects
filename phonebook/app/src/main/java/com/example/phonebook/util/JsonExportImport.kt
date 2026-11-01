package com.example.phonebook.util

import android.content.Context
import android.net.Uri
import com.example.phonebook.data.Contact
import com.example.phonebook.data.Message
import com.example.phonebook.data.SmsEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonExportImport {
    private val gson = Gson()

    fun export(context: Context, uri: Uri, contacts: List<Contact>) {
        context.contentResolver.openOutputStream(uri)?.use {
            it.write(gson.toJson(contacts).toByteArray())
        }
    }

    fun import(context: Context, uri: Uri): List<Contact> {
        val text = context.contentResolver.openInputStream(uri)?.use {
            it.readBytes().toString(Charsets.UTF_8)
        } ?: return emptyList()
        val type = object : TypeToken<List<Contact>>() {}.type
        return gson.fromJson<List<Contact>>(text, type) ?: emptyList()
    }

    fun exportSms(context: Context, uri: Uri, smsList: List<SmsEntry>) {
        context.contentResolver.openOutputStream(uri)?.use {
            it.write(gson.toJson(smsList).toByteArray())
        }
    }

    fun importSms(context: Context, uri: Uri): List<SmsEntry> {
        val text = context.contentResolver.openInputStream(uri)?.use {
            it.readBytes().toString(Charsets.UTF_8)
        } ?: return emptyList()

        try {
            val type = object : TypeToken<List<SmsEntry>>() {}.type
            val list: List<SmsEntry>? = gson.fromJson(text, type)
            if (!list.isNullOrEmpty() && list.firstOrNull()?.sender != null) {
                return list
            }
        } catch (_: Exception) {}

        try {
            val type = object : TypeToken<List<Message>>() {}.type
            val list: List<Message>? = gson.fromJson(text, type)
            if (!list.isNullOrEmpty()) {
                return list.map {
                    SmsEntry(
                        sender = it.senderOrRecipient,
                        body = it.messageText,
                        date = if (it.timestamp > 0) it.timestamp else System.currentTimeMillis()
                    )
                }
            }
        } catch (_: Exception) {}

        return emptyList()
    }
}
