package fm.mrc.budget.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import fm.mrc.budget.ExpenseCategory
import fm.mrc.budget.PaymentMethod
import java.util.Date

@Entity(tableName = "transactions")
@TypeConverters(Converters::class)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val merchant: String,
    val date: Date,
    val icon: String,
    val iconBackgroundColor: Long,
    val category: ExpenseCategory,
    val paymentMethod: PaymentMethod? = null,
    val description: String? = null
) {
    init {
        require(amount >= 0) { "Amount must be non-negative" }
        require(merchant.isNotBlank()) { "Merchant name cannot be blank" }
    }
} 