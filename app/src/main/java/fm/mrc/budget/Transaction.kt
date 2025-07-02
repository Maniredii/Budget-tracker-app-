package fm.mrc.budget

import androidx.compose.ui.graphics.Color
import java.util.*

data class Transaction(
    val amount: Double,
    val merchant: String,
    val date: Date,
    val icon: String,
    val iconBackgroundColor: Long,
    val category: ExpenseCategory,
    val paymentMethod: PaymentMethod? = null,
    val description: String? = null
) {
    fun getIconBackgroundColor(): Color {
        return Color(iconBackgroundColor)
    }
} 