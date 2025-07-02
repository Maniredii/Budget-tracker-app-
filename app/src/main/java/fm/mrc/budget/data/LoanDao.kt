package fm.mrc.budget.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY date DESC")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE type = :type ORDER BY date DESC")
    fun getLoansByType(type: LoanType): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE isPaid = 0 ORDER BY date DESC")
    fun getPendingLoans(): Flow<List<LoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity)

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanEntity)

    @Query("SELECT SUM(amount) FROM loans WHERE type = :type AND isPaid = 0")
    fun getTotalAmountByType(type: LoanType): Flow<Double?>
} 