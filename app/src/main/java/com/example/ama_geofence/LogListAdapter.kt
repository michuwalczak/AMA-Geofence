package com.example.ama_geofence

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ama_geofence.internaldatabase.IntDataBaseEntity

class LogListAdapter internal constructor(context: Context) : RecyclerView.Adapter<LogListAdapter.MessageViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var messages = emptyList<IntDataBaseEntity>() // Cached copy of words

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return MessageViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val current = messages[position]
        holder.messageItemView.text = current.date + " " + current.time + " " + current.text
    }

    internal fun setMessages(words: List<IntDataBaseEntity>) {
        this.messages = words
        notifyDataSetChanged()
    }

    override fun getItemCount() = messages.size
}