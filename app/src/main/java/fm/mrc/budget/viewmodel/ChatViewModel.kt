package fm.mrc.budget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fm.mrc.budget.data.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    fun addUserMessage(content: String) {
        viewModelScope.launch {
            val message = Message.createUserMessage(content)
            _messages.value = _messages.value + message
            // Here you would typically trigger the assistant's response
            // For now, we'll just add a mock response
            addAssistantMessage("This is a mock response from the assistant.")
        }
    }

    private fun addAssistantMessage(content: String) {
        viewModelScope.launch {
            val message = Message.createAssistantMessage(content)
            _messages.value = _messages.value + message
        }
    }
} 