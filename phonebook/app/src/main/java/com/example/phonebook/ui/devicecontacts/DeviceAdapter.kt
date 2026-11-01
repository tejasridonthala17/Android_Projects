package com.example.phonebook.ui.devicecontacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.R
import com.example.phonebook.util.DeviceContactsSync.DeviceContact

class DeviceAdapter(private var items: List<DeviceContact>) : RecyclerView.Adapter<DeviceAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvContactName)
        val phone: TextView = view.findViewById(R.id.tvContactPhone)
        val edit: View = view.findViewById(R.id.btnEdit)
        val delete: View = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = items[position]
        holder.name.text = c.name
        holder.phone.text = c.phone
        // device list is read-only; hide the edit/delete buttons
        holder.edit.visibility = View.GONE
        holder.delete.visibility = View.GONE
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<DeviceContact>) {
        items = newItems
        notifyDataSetChanged()
    }
}
