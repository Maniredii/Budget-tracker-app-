package fm.mrc.budget.data

import android.util.Log
import fm.mrc.budget.ExpenseCategory
import fm.mrc.budget.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.Date

class TransactionRepository(private val transactionDao: TransactionDao) {

    // Observe all transactions as Flow
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
        .onEach { entities -> 
            Log.d("TransactionRepository", "Loaded ${entities.size} transactions from database")
        }
        .map { entities -> entities.map { it.toTransaction() } }
        .onEach { transactions ->
            Log.d("TransactionRepository", "Mapped to ${transactions.size} domain transactions")
        }

    // Fetch all transactions once (suspending)
    suspend fun getAllTransactions(): List<Transaction> {
        return transactionDao.getAllTransactionsSync()
            .map { it.toTransaction() }
    }

    // Insert transaction
    suspend fun insertTransaction(transaction: Transaction) {
        Log.d("TransactionRepository", "Inserting transaction: $transaction")
        val entity = transaction.toEntity()
        transactionDao.insertTransaction(entity)
        Log.d("TransactionRepository", "Transaction inserted successfully")
    }

    // Update transaction
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    // Delete transaction
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    // Transactions between dates as Flow
    fun getTransactionsBetweenDates(startDate: Date, endDate: Date): Flow<List<Transaction>> {
        return transactionDao.getTransactionsBetweenDates(startDate, endDate)
            .map { entities -> entities.map { it.toTransaction() } }
    }

    // Total expenses between dates
    fun getTotalExpensesBetweenDates(startDate: Date, endDate: Date): Flow<Double?> {
        return transactionDao.getTotalExpensesBetweenDates(startDate, endDate)
    }

    // Transactions by category as Flow
    fun getTransactionsByCategory(category: ExpenseCategory): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByCategory(category)
            .map { entities -> entities.map { it.toTransaction() } }
    }
}

// Extension functions to convert between Domain and Entity models
fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        amount = amount,
        merchant = merchant,
        date = date,
        icon = icon,
        iconBackgroundColor = iconBackgroundColor,
        category = category,
        paymentMethod = paymentMethod,
        description = description
    )
}

fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        amount = amount,
        merchant = merchant,
        date = date,
        icon = icon,
        iconBackgroundColor = iconBackgroundColor,
        category = category,
        paymentMethod = paymentMethod,
        description = description
    )
}
