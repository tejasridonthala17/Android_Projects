package com.example.phonebook.ui.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.R
import com.example.phonebook.data.AppDatabase
import com.example.phonebook.data.Contact
import com.example.phonebook.util.DeviceContactsSync
import com.example.phonebook.util.JsonExportImport
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsFragment : Fragment() {

    private lateinit var adapter: ContactAdapter
    private lateinit var db: AppDatabase
    private var current: List<Contact> = emptyList()
    private val deletedContactsStack = mutableListOf<Contact>()

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            JsonExportImport.export(requireContext(), it, current)
            Toast.makeText(requireContext(), "Exported ${current.size} contacts", Toast.LENGTH_SHORT).show()
        }
    }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            lifecycleScope.launch {
                val imported = JsonExportImport.import(requireContext(), it)
                var added = 0
                for (c in imported) {
                    // dedupe by phone number
                    if (db.contactDao().findByPhone(c.phoneNumber) == null) {
                        db.contactDao().insert(c.copy(contactId = 0))
                        added++
                    }
                }
                Toast.makeText(requireContext(), "Imported $added new contacts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_contacts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())

        val fabUndo = view.findViewById<FloatingActionButton>(R.id.fabUndo)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerContacts)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = ContactAdapter(
            emptyList(),
            onEdit = { c ->
                AddEditContactDialog.show(requireContext(), c) { name, phone ->
                    lifecycleScope.launch {
                        val clash = db.contactDao().findByPhone(phone)
                        if (clash != null && clash.contactId != c.contactId) {
                            Toast.makeText(requireContext(), "That number already exists", Toast.LENGTH_SHORT).show()
                        } else {
                            db.contactDao().update(c.copy(name = name, phoneNumber = phone))
                        }
                    }
                }
            },
            onDelete = { c ->
                lifecycleScope.launch {
                    deletedContactsStack.add(c)
                    db.contactDao().delete(c)
                    fabUndo.visibility = View.VISIBLE
                    Snackbar.make(view, "Deleted ${c.name}", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") { undoDeleteContact(fabUndo) }
                        .show()
                }
            }
        )
        recycler.adapter = adapter

        db.contactDao().getAll().observe(viewLifecycleOwner) {
            current = it
            adapter.updateData(it)
        }

        fabUndo.setOnClickListener {
            undoDeleteContact(fabUndo)
        }

        view.findViewById<View>(R.id.btnImportDeviceContacts).setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), "Grant Contacts permission first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val added = withContext(Dispatchers.IO) { DeviceContactsSync.syncToApp(requireContext()) }
                Toast.makeText(requireContext(), "Imported $added new contacts from device", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.fabAddContact).setOnClickListener {
            AddEditContactDialog.show(requireContext(), null) { name, phone ->
                lifecycleScope.launch {
                    if (db.contactDao().findByPhone(phone) != null) {
                        Toast.makeText(requireContext(), "That number already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        db.contactDao().insert(Contact(name = name, phoneNumber = phone))
                    }
                }
            }
        }

        view.findViewById<View>(R.id.btnExportJson).setOnClickListener {
            exportLauncher.launch("contacts_export.json")
        }
        view.findViewById<View>(R.id.btnImportJson).setOnClickListener {
            importLauncher.launch(arrayOf("application/json"))
        }
    }

    private fun undoDeleteContact(fabUndo: FloatingActionButton) {
        if (deletedContactsStack.isNotEmpty()) {
            val contactToRestore = deletedContactsStack.removeAt(deletedContactsStack.lastIndex)
            lifecycleScope.launch {
                db.contactDao().insert(contactToRestore.copy(contactId = 0))
                Toast.makeText(requireContext(), "Restored ${contactToRestore.name}", Toast.LENGTH_SHORT).show()
                if (deletedContactsStack.isEmpty()) {
                    fabUndo.visibility = View.GONE
                }
            }
        }
    }
}
