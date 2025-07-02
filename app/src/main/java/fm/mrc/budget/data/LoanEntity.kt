package fm.mrc.budget.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class LoanType {
    GIVEN, // Money given to someone
    TAKEN  // Money taken from someone
}

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val personName: String,
    val amount: Double,
    val date: Date,
    val type: LoanType,
    val description: String? = null,
    val isPaid: Boolean = false,
    val paidDate: Date? = null
) 