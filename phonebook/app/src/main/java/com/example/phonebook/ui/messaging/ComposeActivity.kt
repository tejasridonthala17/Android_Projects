package com.example.phonebook.ui.messaging

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.phonebook.R
import com.example.phonebook.data.AppDatabase
import com.example.phonebook.data.ConversationThread
import com.example.phonebook.data.Message
import com.example.phonebook.provider.PhoneBookProvider
import kotlinx.coroutines.launch

class ComposeActivity : AppCompatActivity() {

    data class ContactRef(val id: Long, val name: String, val phone: String)
    private val contactList = mutableListOf<ContactRef>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_compose)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.composeRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinner = findViewById<Spinner>(R.id.spinnerContacts)
        val phoneInput = findViewById<EditText>(R.id.etPhoneManual)
        val messageInput = findViewById<EditText>(R.id.etMessageBody)
        val sendBtn = findViewById<Button>(R.id.btnSend)

        // Query the custom Content Provider via ContentResolver, not the DB directly
        val cursor = contentResolver.query(PhoneBookProvider.CONTENT_URI, null, null, null, null)
        val names = mutableListOf("-- pick a contact --")
        cursor?.use {
            val idIdx = it.getColumnIndex("contactId")
            val nameIdx = it.getColumnIndex("name")
            val phoneIdx = it.getColumnIndex("phoneNumber")
            while (it.moveToNext()) {
                val ref = ContactRef(it.getLong(idIdx), it.getString(nameIdx), it.getString(phoneIdx))
                contactList.add(ref)
                names.add("${ref.name} (${ref.phone})")
            }
        }
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names)

        sendBtn.setOnClickListener {
            val selectedPos = spinner.selectedItemPosition
            val phone: String
            val name: String
            if (selectedPos > 0) {
                val ref = contactList[selectedPos - 1]
                phone = ref.phone
                name = ref.name
            } else {
                phone = phoneInput.text.toString().trim()
                name = phone
            }
            val text = messageInput.text.toString().trim()
            if (phone.isEmpty() || text.isEmpty()) {
                Toast.makeText(this, "Enter recipient and message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = AppDatabase.getInstance(this)
            lifecycleScope.launch {
                val thread = db.threadDao().findByPhone(phone)
                val now = System.currentTimeMillis()
                val threadId = if (thread == null) {
                    db.threadDao().insert(
                        ConversationThread(
                            contactName = name, phoneNumber = phone,
                            lastMessage = text, lastMessageTime = now
                        )
                    )
                } else {
                    db.threadDao().update(thread.copy(lastMessage = text, lastMessageTime = now))
                    thread.threadId
                }
                db.messageDao().insert(
                    Message(
                        threadId = threadId,
                        senderOrRecipient = phone,
                        messageText = text,
                        direction = "SENT"
                    )
                )
                finish()
            }
        }
    }
}
