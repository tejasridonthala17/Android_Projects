package com.example.phonebook.ui.sms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.R
import com.example.phonebook.data.SmsEntry
import java.text.SimpleDateFormat
import java.util.*

class SmsAdapter(
    private var items: List<SmsEntry>,
    private val onEdit: (SmsEntry, Int) -> Unit = { _, _ -> },
    private val onDelete: (SmsEntry, Int) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<SmsAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val sender: TextView = view.findViewById(R.id.tvSmsSender)
        val body: TextView = view.findViewById(R.id.tvSmsBody)
        val date: TextView = view.findViewById(R.id.tvSmsDate)
        val edit: ImageButton = view.findViewById(R.id.btnEdit)
        val delete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sms, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.sender.text = item.sender
        holder.body.text = item.body
        holder.date.text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(Date(item.date))
        holder.edit.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) onEdit(items[pos], pos)
        }
        holder.delete.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) onDelete(items[pos], pos)
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<SmsEntry>) {
        items = newItems
        notifyDataSetChanged()
    }
}
