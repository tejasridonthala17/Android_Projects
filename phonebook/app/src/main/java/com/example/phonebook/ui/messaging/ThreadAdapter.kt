package com.example.phonebook.ui.messaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.R
import com.example.phonebook.data.ConversationThread

class ThreadAdapter(
    private var items: List<ConversationThread>,
    private val onClick: (ConversationThread) -> Unit,
    private val onEdit: (ConversationThread) -> Unit,
    private val onDelete: (ConversationThread) -> Unit
) : RecyclerView.Adapter<ThreadAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvThreadName)
        val lastMsg: TextView = view.findViewById(R.id.tvThreadLastMessage)
        val edit: ImageButton = view.findViewById(R.id.btnEdit)
        val delete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_thread, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val thread = items[position]
        holder.name.text = thread.contactName
        holder.lastMsg.text = thread.lastMessage
        holder.itemView.setOnClickListener { onClick(thread) }
        holder.edit.setOnClickListener { onEdit(thread) }
        holder.delete.setOnClickListener { onDelete(thread) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<ConversationThread>) {
        items = newItems
        notifyDataSetChanged()
    }
}
