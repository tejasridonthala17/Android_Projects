package com.example.phonebook.ui.messaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.R
import com.example.phonebook.data.Message

class MessageAdapter(
    private var items: List<Message>
) : RecyclerView.Adapter<MessageAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.tvMessageText)
        val direction: TextView = view.findViewById(R.id.tvMessageDirection)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val msg = items[position]
        holder.text.text = msg.messageText
        holder.direction.text = msg.direction
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Message>) {
        items = newItems
        notifyDataSetChanged()
    }
}
