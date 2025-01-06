package com.example.a20_firebase_basic_chatroom.ui.views

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.a20_firebase_basic_chatroom.data.dataModels.Message
import com.example.a20_firebase_basic_chatroom.databinding.ChatMsgItemBinding
import com.example.a20_firebase_basic_chatroom.databinding.ChatMsgItemOutgoingBinding

class ChatAdapter(private val currentUserId: String) :
    ListAdapter<Message, ChatAdapter.ChatAdapterViewHolder>(DiffUtil()) {

    inner class ChatAdapterViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(message: Message) {
            when (binding) {
                is ChatMsgItemBinding -> {
                    binding.textMessageIncoming.text = message.senderName + "\n" + message.message
                }
                is ChatMsgItemOutgoingBinding -> {
                    binding.textMessageOutgoing.text = message.message
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapterViewHolder {
        val binding = when (viewType) {
            VIEW_TYPE_INCOMING -> ChatMsgItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VIEW_TYPE_OUTGOING -> ChatMsgItemOutgoingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return ChatAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_OUTGOING
        } else {
            VIEW_TYPE_INCOMING
        }
    }

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.msgId == newItem.msgId
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_INCOMING = 1
        private const val VIEW_TYPE_OUTGOING = 2
    }
}