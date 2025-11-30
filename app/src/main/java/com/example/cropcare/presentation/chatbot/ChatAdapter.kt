package com.example.cropcare.presentation.chatbot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcare.R
import android.widget.TextView
import android.widget.LinearLayout

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val USER = 1
    private val BOT = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) USER else BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_user, parent, false) as LinearLayout
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_bot, parent, false) as LinearLayout
            BotViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserViewHolder) holder.tv.text = messages[position].message
        else if (holder is BotViewHolder) holder.tv.text = messages[position].message
    }

    override fun getItemCount(): Int = messages.size

    class UserViewHolder(view: LinearLayout) : RecyclerView.ViewHolder(view) {
        val tv: TextView = view.findViewById(R.id.tvUserMsg)
    }

    class BotViewHolder(view: LinearLayout) : RecyclerView.ViewHolder(view) {
        val tv: TextView = view.findViewById(R.id.tvBotMsg)
    }
}

