package com.example.phonebook.util

import android.content.Context
import android.provider.ContactsContract
import com.example.phonebook.data.AppDatabase
import com.example.phonebook.data.Contact

object DeviceContactsSync {

    data class DeviceContact(val name: String, val phone: String)

    /** Reads contacts from the real device Contacts Provider. Requires READ_CONTACTS. */
    fun readDeviceContacts(context: Context): List<DeviceContact> {
        val result = mutableListOf<DeviceContact>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )
        cursor?.use {
            val n = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val p = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                result.add(DeviceContact(it.getString(n) ?: "", normalize(it.getString(p) ?: "")))
            }
        }
        // dedupe within the device list itself
        return result.filter { it.phone.isNotEmpty() }.distinctBy { it.phone }
    }

    /**
     * Copies device contacts into the app DB. Phone number is the unique key,
     * so running this repeatedly never creates duplicates.
     * Returns how many NEW contacts were added.
     */
    suspend fun syncToApp(context: Context): Int {
        val dao = AppDatabase.getInstance(context).contactDao()
        var added = 0
        for (dc in readDeviceContacts(context)) {
            if (dao.findByPhone(dc.phone) == null) {
                if (dao.insert(Contact(name = dc.name, phoneNumber = dc.phone, source = "DEVICE")) != -1L) {
                    added++
                }
            }
        }
        return added
    }

    fun normalize(number: String): String = number.replace(Regex("[\\s\\-()]"), "")
}
