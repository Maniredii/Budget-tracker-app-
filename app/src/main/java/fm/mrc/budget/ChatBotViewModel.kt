package fm.mrc.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fm.mrc.budget.data.ChatBotService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatBotUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatBotViewModel : ViewModel() {
    private val chatBotService = ChatBotService()
    private val _uiState = MutableStateFlow(ChatBotUiState())
    val uiState: StateFlow<ChatBotUiState> = _uiState.asStateFlow()

    init {
        // Add initial greeting message
        addMessage(
            ChatMessage(
                message = "Hello! I'm your budget assistant. How can I help you manage your finances today?",
                isFromUser = false
            )
        )
    }

    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            try {
                // Add user message
                addMessage(ChatMessage(message = userMessage, isFromUser = true))
                
                // Show loading state
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Get response from ChatBot service
                val response = chatBotService.getChatResponse(userMessage)
                
                // Add bot response
                addMessage(ChatMessage(message = response, isFromUser = false))
                
                // Clear loading state
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to get response: ${e.message}"
                )
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + message
        )
    }
} 