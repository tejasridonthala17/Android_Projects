package com.example.phonebook.ui.messaging

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.R
import com.example.phonebook.data.AppDatabase
import com.example.phonebook.data.ConversationThread
import com.example.phonebook.data.Message
import com.example.phonebook.data.SmsEntry
import com.example.phonebook.util.JsonExportImport
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ThreadsFragment : Fragment() {

    private lateinit var adapter: ThreadAdapter
    private lateinit var db: AppDatabase
    private val deletedThreadsStack = mutableListOf<Pair<ConversationThread, List<Message>>>()

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            lifecycleScope.launch {
                val appMessages = db.messageDao().getAllMessages()
                val smsEntries = appMessages.map { msg ->
                    SmsEntry(
                        sender = msg.senderOrRecipient,
                        body = msg.messageText,
                        date = msg.timestamp
                    )
                }
                JsonExportImport.exportSms(requireContext(), it, smsEntries)
                Toast.makeText(requireContext(), "Exported ${smsEntries.size} messages", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            lifecycleScope.launch {
                val importedSms = JsonExportImport.importSms(requireContext(), it)
                var count = 0
                for (sms in importedSms) {
                    val phone = sms.sender
                    val text = sms.body
                    val timestamp = if (sms.date > 0) sms.date else System.currentTimeMillis()

                    val existingThread = db.threadDao().findByPhone(phone)
                    val threadId = if (existingThread == null) {
                        val contact = db.contactDao().findByPhone(phone)
                        val name = contact?.name ?: phone
                        db.threadDao().insert(
                            ConversationThread(
                                contactName = name,
                                phoneNumber = phone,
                                lastMessage = text,
                                lastMessageTime = timestamp
                            )
                        )
                    } else {
                        if (timestamp >= existingThread.lastMessageTime) {
                            db.threadDao().update(
                                existingThread.copy(
                                    lastMessage = text,
                                    lastMessageTime = timestamp
                                )
                            )
                        }
                        existingThread.threadId
                    }

                    db.messageDao().insert(
                        Message(
                            threadId = threadId,
                            senderOrRecipient = phone,
                            messageText = text,
                            timestamp = timestamp,
                            direction = "RECEIVED"
                        )
                    )
                    count++
                }
                Toast.makeText(requireContext(), "Imported $count messages", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_threads, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())

        val fabUndo = view.findViewById<FloatingActionButton>(R.id.fabUndo)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerThreads)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = ThreadAdapter(
            emptyList(),
            onClick = { thread ->
                val intent = Intent(requireContext(), ConversationActivity::class.java)
                intent.putExtra("threadId", thread.threadId)
                intent.putExtra("contactName", thread.contactName)
                intent.putExtra("phoneNumber", thread.phoneNumber)
                startActivity(intent)
            },
            onEdit = { thread -> showEditThreadDialog(thread) },
            onDelete = { thread -> showDeleteThreadDialog(thread, fabUndo, view) }
        )
        recycler.adapter = adapter

        db.threadDao().getAll().observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }

        fabUndo.setOnClickListener {
            undoDeleteThread(fabUndo)
        }

        view.findViewById<Button>(R.id.btnExportSms).setOnClickListener {
            exportLauncher.launch("sms_export.json")
        }

        view.findViewById<Button>(R.id.btnImportSms).setOnClickListener {
            importLauncher.launch(arrayOf("application/json"))
        }

        view.findViewById<FloatingActionButton>(R.id.fabCompose).setOnClickListener {
            startActivity(Intent(requireContext(), ComposeActivity::class.java))
        }
    }

    private fun showEditThreadDialog(thread: ConversationThread) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_thread, null)
        val etName = dialogView.findViewById<EditText>(R.id.etThreadName)
        val etPhone = dialogView.findViewById<EditText>(R.id.etThreadPhone)
        val etMessage = dialogView.findViewById<EditText>(R.id.etThreadMessage)
        etName.setText(thread.contactName)
        etPhone.setText(thread.phoneNumber)
        etMessage.setText(thread.lastMessage)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Conversation")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val message = etMessage.text.toString().trim()
                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    lifecycleScope.launch {
                        db.threadDao().update(
                            thread.copy(
                                contactName = name,
                                phoneNumber = phone,
                                lastMessage = message
                            )
                        )
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteThreadDialog(thread: ConversationThread, fabUndo: FloatingActionButton, rootView: View) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Conversation")
            .setMessage("Are you sure you want to delete this conversation with ${thread.contactName}?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    val messages = db.messageDao().getMessagesForThreadSync(thread.threadId)
                    deletedThreadsStack.add(Pair(thread, messages))
                    db.messageDao().deleteMessagesForThread(thread.threadId)
                    db.threadDao().delete(thread)
                    fabUndo.visibility = View.VISIBLE
                    Snackbar.make(rootView, "Deleted conversation with ${thread.contactName}", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") { undoDeleteThread(fabUndo) }
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun undoDeleteThread(fabUndo: FloatingActionButton) {
        if (deletedThreadsStack.isNotEmpty()) {
            val (threadToRestore, msgsToRestore) = deletedThreadsStack.removeAt(deletedThreadsStack.lastIndex)
            lifecycleScope.launch {
                val restoredThreadId = db.threadDao().insert(threadToRestore.copy(threadId = 0))
                for (msg in msgsToRestore) {
                    db.messageDao().insert(msg.copy(messageId = 0, threadId = restoredThreadId))
                }
                Toast.makeText(requireContext(), "Restored conversation with ${threadToRestore.contactName}", Toast.LENGTH_SHORT).show()
                if (deletedThreadsStack.isEmpty()) {
                    fabUndo.visibility = View.GONE
                }
            }
        }
    }
}
