package fm.mrc.budget.service

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import fm.mrc.budget.data.ApiConfig
import fm.mrc.budget.utils.BudgetSuggestions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

class ChatBotService {
    private var model: GenerativeModel? = null

    init {
        initializeModel()
    }

    private fun initializeModel() {
        try {
            if (ApiConfig.GEMINI_API_KEY.isNotBlank()) {
                model = GenerativeModel(
                    modelName = "gemini-pro",
                    apiKey = ApiConfig.GEMINI_API_KEY
                )
            }
        } catch (e: Exception) {
            model = null
        }
    }

    suspend fun getBudgetAdvice(
        monthlyIncome: Double,
        currentExpenses: Double,
        expenseCategories: Map<String, Double>
    ): String = withContext(Dispatchers.IO) {
        try {
            // If model is null or API key is not set, use offline suggestions
            if (model == null || ApiConfig.GEMINI_API_KEY.isBlank()) {
                return@withContext getOfflineSuggestions(monthlyIncome, currentExpenses, expenseCategories)
            }

            val prompt = """
                As a financial advisor, analyze my monthly budget:
                Monthly Income: ₹$monthlyIncome
                Total Expenses: ₹$currentExpenses
                
                Expense Breakdown:
                ${expenseCategories.entries.joinToString("\n") { "- ${it.key}: ₹${it.value}" }}
                
                Please provide:
                1. Analysis of spending patterns
                2. Suggestions for better budget allocation
                3. Specific tips for reducing expenses
                4. Savings recommendations
            """.trimIndent()

            val response = model?.generateContent(prompt)
            val responseText = response?.text
            
            return@withContext if (responseText != null) {
                responseText
            } else {
                getOfflineSuggestions(monthlyIncome, currentExpenses, expenseCategories)
            }
        } catch (e: UnknownHostException) {
            // No internet connection, use offline suggestions
            return@withContext getOfflineSuggestions(monthlyIncome, currentExpenses, expenseCategories)
        } catch (e: Exception) {
            // Any other error, use offline suggestions
            return@withContext getOfflineSuggestions(monthlyIncome, currentExpenses, expenseCategories)
        }
    }

    private fun getOfflineSuggestions(
        monthlyIncome: Double,
        currentExpenses: Double,
        expenseCategories: Map<String, Double>
    ): String {
        return BudgetSuggestions.getOfflineBudgetAdvice(
            monthlyIncome = monthlyIncome,
            currentExpenses = currentExpenses,
            expenseCategories = expenseCategories
        )
    }

    suspend fun getExpenseAnalysis(
        transaction: String,
        amount: Double,
        category: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Analyze this expense:
                Transaction: $transaction
                Amount: ₹$amount
                Category: $category
                
                Please provide:
                1. Is this expense necessary or discretionary?
                2. Suggestions for potential savings
                3. Alternative options if applicable
            """.trimIndent()

            val response = model?.generateContent(prompt)
            val responseText = response?.text
            
            return@withContext responseText ?: "Unable to analyze expense at the moment. Please try again later."
        } catch (e: Exception) {
            return@withContext "Error analyzing expense: ${e.message}"
        }
    }

    suspend fun getSavingsGoalAdvice(
        monthlyIncome: Double,
        currentExpenses: Double,
        savingsGoal: Double,
        timeframe: Int
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Help me reach my savings goal:
                Monthly Income: ₹$monthlyIncome
                Current Monthly Expenses: ₹$currentExpenses
                Savings Goal: ₹$savingsGoal
                Timeframe: $timeframe months
                
                Please provide:
                1. Is this goal realistic?
                2. Monthly savings needed
                3. Specific strategies to reach the goal
                4. Potential obstacles and solutions
            """.trimIndent()

            val response = model?.generateContent(prompt)
            val responseText = response?.text
            
            return@withContext responseText ?: "Unable to generate savings advice at the moment. Please try again later."
        } catch (e: Exception) {
            return@withContext "Error generating savings advice: ${e.message}"
        }
    }

    suspend fun getLoanAdvice(
        loanAmount: Double,
        income: Double,
        existingLoans: Double,
        purpose: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Analyze this loan scenario:
                Loan Amount: ₹$loanAmount
                Monthly Income: ₹$income
                Existing Loans: ₹$existingLoans
                Purpose: $purpose
                
                Please provide:
                1. Debt-to-income ratio analysis
                2. Loan affordability assessment
                3. Recommendations and alternatives
                4. Risk factors to consider
            """.trimIndent()

            val response = model?.generateContent(prompt)
            val responseText = response?.text
            
            return@withContext responseText ?: "Unable to generate loan advice at the moment. Please try again later."
        } catch (e: Exception) {
            return@withContext "Error generating loan advice: ${e.message}"
        }
    }
} 