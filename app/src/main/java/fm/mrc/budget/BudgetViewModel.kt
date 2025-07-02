package fm.mrc.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fm.mrc.budget.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.time.LocalDate
import java.time.ZoneId
import fm.mrc.budget.service.ChatBotService

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(
            BudgetDatabase.getDatabase(application).transactionDao()
        )
    }

    private val loanRepository: LoanRepository by lazy {
        LoanRepository(
            BudgetDatabase.getDatabase(application).loanDao()
        )
    }

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _loans = MutableStateFlow<List<LoanEntity>>(emptyList())
    val loans: StateFlow<List<LoanEntity>> = _loans.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _monthlyBudget = MutableStateFlow(50000.0) // Default monthly budget
    val monthlyBudget: StateFlow<Double> = _monthlyBudget.asStateFlow()

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val exceptionHandler = BudgetApplication.instance.coroutineExceptionHandler
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob + exceptionHandler)

    private val chatBotService = ChatBotService()
    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

    init {
        loadTransactions()
        loadLoans()
        calculateTotalExpenses()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            transactionRepository.allTransactions.collect { transactions ->
                _transactions.value = transactions
                calculateTotalExpenses()
            }
        }
    }

    private fun loadLoans() {
        viewModelScope.launch {
            loanRepository.allLoans.collect { loans ->
                _loans.value = loans
            }
        }
    }

    private fun calculateTotalExpenses() {
        val currentMonth = LocalDate.now().withDayOfMonth(1)
        val startDate = Date.from(currentMonth.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endDate = Date.from(currentMonth.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        
        viewModelScope.launch {
            transactionRepository.getTotalExpensesBetweenDates(startDate, endDate).collect { total ->
                _totalExpenses.value = total ?: 0.0
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setMonthlyBudget(budget: Double) {
        _monthlyBudget.value = budget
    }

    fun updateMonthlyBudget(newBudget: Double) {
        viewModelScope.launch {
            _monthlyBudget.value = newBudget
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transaction)
            calculateTotalExpenses()
            // Automatically refresh AI advice when a new transaction is added
            getAIBudgetAdvice()
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
            calculateTotalExpenses()
            // Refresh AI advice when a transaction is updated
            getAIBudgetAdvice()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
            calculateTotalExpenses()
            // Refresh AI advice when a transaction is deleted
            getAIBudgetAdvice()
        }
    }

    fun addLoan(loan: LoanEntity) {
        viewModelScope.launch {
            loanRepository.insertLoan(loan)
        }
    }

    fun updateLoan(loan: LoanEntity) {
        viewModelScope.launch {
            loanRepository.updateLoan(loan)
        }
    }

    fun deleteLoan(loan: LoanEntity) {
        viewModelScope.launch {
            loanRepository.deleteLoan(loan)
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            // Add user message
            _chatMessages.value = _chatMessages.value + ChatMessage(message, true)
            
            // TODO: Add AI response logic here
            // For now, just echo the message
            _chatMessages.value = _chatMessages.value + ChatMessage(
                "You said: $message",
                false
            )
        }
    }

    fun getAIBudgetAdvice() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val categoryTotals = transactions.value
                    .groupBy { it.category }
                    .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
                    .mapKeys { it.key.name }

                val advice = chatBotService.getBudgetAdvice(
                    monthlyIncome = monthlyBudget.value,
                    currentExpenses = totalExpenses.value,
                    expenseCategories = categoryTotals
                )
                _aiResponse.value = advice
            } catch (e: Exception) {
                _error.value = "Failed to get AI advice: ${e.message}"
                _aiResponse.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAIExpenseAnalysis(transaction: Transaction) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val analysis = chatBotService.getExpenseAnalysis(
                    transaction = transaction.merchant,
                    amount = transaction.amount,
                    category = transaction.category.name
                )
                _aiResponse.value = analysis
            } catch (e: Exception) {
                _error.value = "Failed to analyze expense: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAISavingsAdvice(savingsGoal: Double, timeframe: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val advice = chatBotService.getSavingsGoalAdvice(
                    monthlyIncome = monthlyBudget.value,
                    currentExpenses = totalExpenses.value,
                    savingsGoal = savingsGoal,
                    timeframe = timeframe
                )
                _aiResponse.value = advice
            } catch (e: Exception) {
                _error.value = "Failed to get savings advice: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAILoanAdvice(loan: LoanEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val totalLoans = loans.value.filter { !it.isPaid }.sumOf { it.amount }
                val advice = chatBotService.getLoanAdvice(
                    loanAmount = loan.amount,
                    income = monthlyBudget.value,
                    existingLoans = totalLoans,
                    purpose = loan.description ?: "Not specified"
                )
                _aiResponse.value = advice
            } catch (e: Exception) {
                _error.value = "Failed to get loan advice: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
} 