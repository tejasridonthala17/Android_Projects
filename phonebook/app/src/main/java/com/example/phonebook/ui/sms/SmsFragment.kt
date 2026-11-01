package com.example.phonebook.ui.sms

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.R
import com.example.phonebook.data.SmsEntry
import com.example.phonebook.util.JsonExportImport
import com.example.phonebook.util.SmsReader
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class SmsFragment : Fragment() {

    private lateinit var adapter: SmsAdapter
    private var currentList: MutableList<SmsEntry> = mutableListOf()
    private val deletedSmsStack = mutableListOf<Pair<SmsEntry, Int>>()

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            JsonExportImport.exportSms(requireContext(), it, currentList)
            Toast.makeText(requireContext(), "Exported ${currentList.size} messages", Toast.LENGTH_SHORT).show()
        }
    }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val imported = JsonExportImport.importSms(requireContext(), it)
            if (imported.isNotEmpty()) {
                currentList.addAll(0, imported)
                adapter.updateData(currentList)
                Toast.makeText(requireContext(), "Imported ${imported.size} messages", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_sms, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fabUndo = view.findViewById<FloatingActionButton>(R.id.fabUndo)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSms)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = SmsAdapter(
            emptyList(),
            onEdit = { sms, pos -> showEditSmsDialog(sms, pos) },
            onDelete = { sms, pos ->
                if (pos in currentList.indices) {
                    deletedSmsStack.add(Pair(sms, pos))
                    currentList.removeAt(pos)
                    adapter.updateData(currentList)
                    fabUndo.visibility = View.VISIBLE
                    Snackbar.make(view, "Deleted message", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") { undoDeleteSms(fabUndo) }
                        .show()
                }
            }
        )
        recycler.adapter = adapter

        fabUndo.setOnClickListener {
            undoDeleteSms(fabUndo)
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            currentList = SmsReader.readDeviceSms(requireContext()).toMutableList()
            adapter.updateData(currentList)
        } else {
            Toast.makeText(requireContext(), "SMS permission not granted", Toast.LENGTH_LONG).show()
        }

        view.findViewById<View>(R.id.btnExportSms).setOnClickListener {
            exportLauncher.launch("sms_export.json")
        }

        view.findViewById<View>(R.id.btnImportSms).setOnClickListener {
            importLauncher.launch(arrayOf("application/json"))
        }
    }

    private fun undoDeleteSms(fabUndo: FloatingActionButton) {
        if (deletedSmsStack.isNotEmpty()) {
            val (smsToRestore, pos) = deletedSmsStack.removeAt(deletedSmsStack.lastIndex)
            val insertIndex = if (pos in 0..currentList.size) pos else 0
            currentList.add(insertIndex, smsToRestore)
            adapter.updateData(currentList)
            Toast.makeText(requireContext(), "Restored message from ${smsToRestore.sender}", Toast.LENGTH_SHORT).show()
            if (deletedSmsStack.isEmpty()) {
                fabUndo.visibility = View.GONE
            }
        }
    }

    private fun showEditSmsDialog(sms: SmsEntry, position: Int) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_edit_contact, null)
        val etSender = view.findViewById<EditText>(R.id.etName)
        val etBody = view.findViewById<EditText>(R.id.etPhone)
        etSender.hint = "Sender"
        etSender.setText(sms.sender)
        etBody.hint = "Message Body"
        etBody.setText(sms.body)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Message")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val newSender = etSender.text.toString().trim()
                val newBody = etBody.text.toString().trim()
                if (newSender.isNotEmpty() && newBody.isNotEmpty() && position in currentList.indices) {
                    currentList[position] = sms.copy(sender = newSender, body = newBody)
                    adapter.updateData(currentList)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
