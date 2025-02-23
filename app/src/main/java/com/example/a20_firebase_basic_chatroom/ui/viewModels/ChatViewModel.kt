package com.example.a20_firebase_basic_chatroom.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a20_firebase_basic_chatroom.data.api.messagesDao
import com.example.a20_firebase_basic_chatroom.data.dataModels.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// In ChatViewModel.kt
@HiltViewModel
class ChatViewModel @Inject constructor(private val dao: messagesDao) : ViewModel() {
    val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    var currentPage = 0
    private val pageSize = 50
    private var isLoading = false
    private var hasMoreMessages = true

    fun initializeChat(roomId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadMoreMessages(roomId)
        }
    }

    suspend fun loadMoreMessages(roomId: String) {
        if (isLoading || !hasMoreMessages) return

        isLoading = true

        try {
            val offset = currentPage * pageSize
            val newMessages = dao.getPagedChatsForRoom(roomId, pageSize, offset)

            if (newMessages.isEmpty()) {
                hasMoreMessages = false
                return
            }

            val currentMessages = _messages.value ?: emptyList()
            _messages.postValue(currentMessages + newMessages)
            currentPage++

        } finally {
            isLoading = false
        }
    }
}