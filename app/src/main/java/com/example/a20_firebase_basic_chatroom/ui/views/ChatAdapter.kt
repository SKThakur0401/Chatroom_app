package com.example.a20_firebase_basic_chatroom.ui.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a20_firebase_basic_chatroom.data.dataModels.Message
import com.example.a20_firebase_basic_chatroom.databinding.ChatMsgItemBinding

class ChatAdapter() : ListAdapter<Message, ChatAdapter.ChatAdapterViewHolder>(DiffUtil()) {

    inner class ChatAdapterViewHolder(private val binding: ChatMsgItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(){
            val msg = getItem(adapterPosition)
            binding.tvName.text = msg.senderName
            binding.tvPhoneNumber.text = msg.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapterViewHolder {
        val binding = ChatMsgItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatAdapterViewHolder, position: Int) {
        holder.bind()
    }

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<Message>()
    {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.msgId == newItem.msgId
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
