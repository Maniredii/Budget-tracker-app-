package fm.mrc.budget.data

data class Message(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun createUserMessage(content: String): Message {
            return Message(
                content = content,
                isFromUser = true
            )
        }

        fun createAssistantMessage(content: String): Message {
            return Message(
                content = content,
                isFromUser = false
            )
        }
    }
} 