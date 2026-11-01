package com.example.phonebook.util

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import com.example.phonebook.data.SmsEntry

object SmsReader {
    /** Reads inbox SMS via the system SMS Content Provider. Requires READ_SMS. */
    fun readDeviceSms(context: Context): List<SmsEntry> {
        val list = mutableListOf<SmsEntry>()
        val uri: Uri = Telephony.Sms.Inbox.CONTENT_URI
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
            null, null, "${Telephony.Sms.DATE} DESC"
        )
        cursor?.use {
            val a = it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val b = it.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val d = it.getColumnIndexOrThrow(Telephony.Sms.DATE)
            while (it.moveToNext()) {
                list.add(SmsEntry(it.getString(a) ?: "Unknown", it.getString(b) ?: "", it.getLong(d)))
            }
        }
        return list
    }
}
