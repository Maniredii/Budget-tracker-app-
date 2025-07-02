package fm.mrc.budget

import androidx.compose.ui.graphics.Color
import fm.mrc.budget.ui.theme.*

fun getCategoryIcon(category: ExpenseCategory): String {
    return when (category) {
        ExpenseCategory.FOOD -> "🍽️"
        ExpenseCategory.TRANSPORTATION -> "🚗"
        ExpenseCategory.HOUSING -> "🏠"
        ExpenseCategory.UTILITIES -> "💡"
        ExpenseCategory.ENTERTAINMENT -> "🎬"
        ExpenseCategory.SHOPPING -> "🛍️"
        ExpenseCategory.HEALTH -> "🏥"
        ExpenseCategory.EDUCATION -> "📚"
        ExpenseCategory.TRAVEL -> "✈️"
        ExpenseCategory.OTHER -> "📌"
    }
}

fun getCategoryColor(category: ExpenseCategory): Color {
    return when (category) {
        ExpenseCategory.FOOD -> Orange
        ExpenseCategory.TRANSPORTATION -> Cyan
        ExpenseCategory.HOUSING -> Purple
        ExpenseCategory.UTILITIES -> PinelabsGreen
        ExpenseCategory.ENTERTAINMENT -> Purple
        ExpenseCategory.SHOPPING -> AmazonBlue
        ExpenseCategory.HEALTH -> Cyan
        ExpenseCategory.EDUCATION -> Orange
        ExpenseCategory.TRAVEL -> ShellYellow
        ExpenseCategory.OTHER -> TextGray
    }
} 