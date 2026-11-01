package com.example.phonebook.ui.contacts

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.phonebook.R
import com.example.phonebook.data.Contact

object AddEditContactDialog {

    /** Pass existing = null for "add", or a Contact for "edit". */
    fun show(context: Context, existing: Contact?, onSave: (name: String, phone: String) -> Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_contact, null)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etPhone = view.findViewById<EditText>(R.id.etPhone)
        existing?.let {
            etName.setText(it.name)
            etPhone.setText(it.phoneNumber)
        }

        AlertDialog.Builder(context)
            .setTitle(if (existing == null) "Add Contact" else "Edit Contact")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().replace(Regex("[\\s\\-()]"), "")
                if (name.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(context, "Name and phone are required", Toast.LENGTH_SHORT).show()
                } else {
                    onSave(name, phone)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
