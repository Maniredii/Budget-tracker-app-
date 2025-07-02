package fm.mrc.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fm.mrc.budget.ui.theme.DarkBackground
import fm.mrc.budget.ui.theme.DarkSurface
import fm.mrc.budget.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val chatBotViewModel = remember { ChatBotViewModel() }
    val uiState by chatBotViewModel.uiState.collectAsStateWithLifecycle()

    ChatBotScreen(
        messages = uiState.messages,
        isLoading = uiState.isLoading,
        onSendMessage = { message -> chatBotViewModel.sendMessage(message) }
    )
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (message.isFromUser) DarkSurface else TextWhite
        )
    ) {
        Text(
            text = message.message,
            color = if (message.isFromUser) TextWhite else DarkBackground,
            modifier = Modifier.padding(16.dp)
        )
    }
} 