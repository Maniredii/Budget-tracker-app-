package fm.mrc.budget.data

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatBotService {
    private var generativeModel: GenerativeModel? = null

    private fun initializeModel() {
        if (ApiConfig.GEMINI_API_KEY.isNotBlank() && generativeModel == null) {
            generativeModel = GenerativeModel(
                modelName = "gemini-pro",
                apiKey = ApiConfig.GEMINI_API_KEY
            )
        }
    }

    suspend fun getChatResponse(message: String): String = withContext(Dispatchers.IO) {
        try {
            initializeModel()
            
            if (generativeModel == null) {
                return@withContext "Please set up your Gemini API key first."
            }

            val prompt = """
                You are a helpful budget assistant. Your role is to help users manage their finances, track expenses, and provide budgeting advice.
                User message: $message
                
                Provide a helpful, concise response focused on budgeting and personal finance.
            """.trimIndent()

            val response = generativeModel!!.generateContent(prompt)
            return@withContext response.text ?: "I apologize, but I couldn't generate a response. Please try again."
            
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "I encountered an error. Please try again later."
        }
    }
} 