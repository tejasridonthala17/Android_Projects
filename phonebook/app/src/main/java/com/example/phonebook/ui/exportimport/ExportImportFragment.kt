package com.example.phonebook.ui.exportimport

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.phonebook.R
import com.example.phonebook.data.AppDatabase
import com.example.phonebook.data.ConversationThread
import com.example.phonebook.data.Message
import com.example.phonebook.util.JsonExportImport
import com.example.phonebook.util.SmsReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExportImportFragment : Fragment() {

    private lateinit var db: AppDatabase

    private val exportContactsLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            lifecycleScope.launch {
                val contacts = withContext(Dispatchers.IO) { db.contactDao().getAllOnce() }
                JsonExportImport.export(requireContext(), it, contacts)
                Toast.makeText(requireContext(), "Exported ${contacts.size} contacts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val exportSmsLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            val currentList = if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_SMS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                SmsReader.readDeviceSms(requireContext())
            } else emptyList()

            JsonExportImport.exportSms(requireContext(), it, currentList)
            Toast.makeText(requireContext(), "Exported ${currentList.size} messages", Toast.LENGTH_SHORT).show()
        }
    }

    private val importContactsLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            lifecycleScope.launch {
                val imported = JsonExportImport.import(requireContext(), it)
                var added = 0
                for (c in imported) {
                    if (db.contactDao().findByPhone(c.phoneNumber) == null) {
                        db.contactDao().insert(c.copy(contactId = 0))
                        added++
                    }
                }
                Toast.makeText(requireContext(), "Imported $added new contacts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val importSmsLauncher = registerForActivityResult(
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
    ): View = inflater.inflate(R.layout.fragment_export_import, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())

        view.findViewById<Button>(R.id.btnExportPhoneContacts).setOnClickListener {
            exportContactsLauncher.launch("contacts_export.json")
        }

        view.findViewById<Button>(R.id.btnExportDeviceSms).setOnClickListener {
            exportSmsLauncher.launch("sms_export.json")
        }

        view.findViewById<Button>(R.id.btnImportPhoneContacts).setOnClickListener {
            importContactsLauncher.launch(arrayOf("application/json"))
        }

        view.findViewById<Button>(R.id.btnImportDeviceSms).setOnClickListener {
            importSmsLauncher.launch(arrayOf("application/json"))
        }
    }
}
