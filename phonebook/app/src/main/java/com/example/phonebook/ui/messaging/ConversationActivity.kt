package com.example.phonebook.ui.messaging

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.R
import com.example.phonebook.data.AppDatabase
import com.example.phonebook.data.Message
import kotlinx.coroutines.launch

class ConversationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_conversation)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.conversationRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val threadId = intent.getLongExtra("threadId", -1)
        val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
        title = intent.getStringExtra("contactName") ?: "Conversation"

        val db = AppDatabase.getInstance(this)
        val recycler = findViewById<RecyclerView>(R.id.recyclerMessages)
        recycler.layoutManager = LinearLayoutManager(this)

        val adapter = MessageAdapter(emptyList())
        recycler.adapter = adapter

        db.messageDao().getMessagesForThread(threadId).observe(this) { list ->
            adapter.updateData(list)
        }

        val input = findViewById<EditText>(R.id.etReply)
        findViewById<Button>(R.id.btnReplySend).setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isNotEmpty()) {
                lifecycleScope.launch {
                    val now = System.currentTimeMillis()
                    db.messageDao().insert(
                        Message(
                            threadId = threadId,
                            senderOrRecipient = phoneNumber,
                            messageText = text,
                            timestamp = now,
                            direction = "SENT"
                        )
                    )
                    val thread = db.threadDao().getById(threadId)
                    if (thread != null) {
                        db.threadDao().update(thread.copy(lastMessage = text, lastMessageTime = now))
                    }
                    input.text.clear()
                }
            }
        }
    }
}
