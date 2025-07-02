package fm.mrc.budget.data

object ApiConfig {
    // TODO: Replace with your Gemini API key
    var GEMINI_API_KEY: String = ""
        private set

    fun setGeminiApiKey(apiKey: String) {
        require(apiKey.isNotBlank()) { "API key cannot be blank" }
        GEMINI_API_KEY = apiKey.trim()
    }

    fun isGeminiApiKeySet(): Boolean = GEMINI_API_KEY.isNotBlank()
} 