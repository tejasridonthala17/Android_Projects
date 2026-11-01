package com.example.phonebook.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.phonebook.data.AppDatabase
import com.example.phonebook.data.Contact
import kotlinx.coroutines.runBlocking

/**
 * Custom Content Provider exposing the contacts table.
 * Other components (e.g. ComposeActivity) access data ONLY via ContentResolver.
 */
class PhoneBookProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.phonebook.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/contacts")

        private const val CONTACTS = 1
        private const val CONTACT_ID = 2

        private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "contacts", CONTACTS)
            addURI(AUTHORITY, "contacts/#", CONTACT_ID)
        }

        val COLUMNS = arrayOf("contactId", "name", "phoneNumber", "source")
    }

    private val db by lazy { AppDatabase.getInstance(context!!) }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri, projection: Array<out String>?, selection: String?,
        selectionArgs: Array<out String>?, sortOrder: String?
    ): Cursor? {
        val cursor = MatrixCursor(COLUMNS)
        runBlocking {
            when (matcher.match(uri)) {
                CONTACTS -> db.contactDao().getAllOnce().forEach { cursor.addContact(it) }
                CONTACT_ID -> db.contactDao().getById(ContentUris.parseId(uri))
                    ?.let { cursor.addContact(it) }
                else -> throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    private fun MatrixCursor.addContact(c: Contact) {
        addRow(arrayOf(c.contactId, c.name, c.phoneNumber, c.source))
    }

    override fun getType(uri: Uri): String? = when (matcher.match(uri)) {
        CONTACTS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.contacts"
        CONTACT_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.contacts"
        else -> null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (matcher.match(uri) != CONTACTS || values == null) {
            throw IllegalArgumentException("Unsupported insert URI: $uri")
        }
        val contact = Contact(
            name = values.getAsString("name") ?: return null,
            phoneNumber = values.getAsString("phoneNumber") ?: return null,
            source = values.getAsString("source") ?: "MANUAL"
        )
        val id = runBlocking { db.contactDao().insert(contact) }
        if (id == -1L) return null // duplicate phone number ignored
        context!!.contentResolver.notifyChange(CONTENT_URI, null)
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?
    ): Int {
        if (matcher.match(uri) != CONTACT_ID || values == null) return 0
        val id = ContentUris.parseId(uri)
        var rows = 0
        runBlocking {
            val existing = db.contactDao().getById(id) ?: return@runBlocking
            db.contactDao().update(
                existing.copy(
                    name = values.getAsString("name") ?: existing.name,
                    phoneNumber = values.getAsString("phoneNumber") ?: existing.phoneNumber
                )
            )
            rows = 1
        }
        if (rows > 0) context!!.contentResolver.notifyChange(CONTENT_URI, null)
        return rows
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        if (matcher.match(uri) != CONTACT_ID) return 0
        runBlocking { db.contactDao().deleteById(ContentUris.parseId(uri)) }
        context!!.contentResolver.notifyChange(CONTENT_URI, null)
        return 1
    }
}
