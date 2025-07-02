package fm.mrc.budget.data

import kotlinx.coroutines.flow.Flow

class LoanRepository(private val loanDao: LoanDao) {
    // Observe all loans as Flow
    val allLoans: Flow<List<LoanEntity>> = loanDao.getAllLoans()

    // Insert loan
    suspend fun insertLoan(loan: LoanEntity) {
        loanDao.insertLoan(loan)
    }

    // Update loan
    suspend fun updateLoan(loan: LoanEntity) {
        loanDao.updateLoan(loan)
    }

    // Delete loan
    suspend fun deleteLoan(loan: LoanEntity) {
        loanDao.deleteLoan(loan)
    }
} 