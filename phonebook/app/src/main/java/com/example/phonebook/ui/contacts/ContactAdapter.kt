package com.example.phonebook.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.R
import com.example.phonebook.data.Contact

class ContactAdapter(
    private var items: List<Contact>,
    private val onEdit: (Contact) -> Unit,
    private val onDelete: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvContactName)
        val phone: TextView = view.findViewById(R.id.tvContactPhone)
        val edit: ImageButton = view.findViewById(R.id.btnEdit)
        val delete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = items[position]
        holder.name.text = c.name
        holder.phone.text = c.phoneNumber
        holder.edit.setOnClickListener { onEdit(c) }
        holder.delete.setOnClickListener { onDelete(c) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Contact>) {
        items = newItems
        notifyDataSetChanged()
    }
}
