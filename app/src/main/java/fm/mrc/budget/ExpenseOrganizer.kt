package fm.mrc.budget

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.budget.ui.theme.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import java.text.SimpleDateFormat

data class ExpenseSummary(
    val totalAmount: Double,
    val transactions: List<Transaction>
)

object ExpenseOrganizer {
    private val zoneId = ZoneId.systemDefault()

    fun getDailyExpenses(transactions: List<Transaction>): List<Pair<LocalDate, ExpenseSummary>> {
        return try {
            transactions
                .groupBy { transaction ->
                    transaction.date.toInstant()
                        .atZone(zoneId)
                        .toLocalDate()
                }
                .map { (date, dailyTransactions) ->
                    date to ExpenseSummary(
                        totalAmount = dailyTransactions.sumOf { it.amount },
                        transactions = dailyTransactions
                    )
                }
                .sortedByDescending { it.first }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getWeeklyExpenses(transactions: List<Transaction>): List<Pair<LocalDate, ExpenseSummary>> {
        return try {
            transactions
                .groupBy { transaction ->
                    transaction.date.toInstant()
                        .atZone(zoneId)
                        .toLocalDate()
                        .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
                }
                .map { (weekStart, weeklyTransactions) ->
                    weekStart to ExpenseSummary(
                        totalAmount = weeklyTransactions.sumOf { it.amount },
                        transactions = weeklyTransactions
                    )
                }
                .sortedByDescending { it.first }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getMonthlyExpenses(transactions: List<Transaction>): List<Pair<LocalDate, ExpenseSummary>> {
        return try {
            transactions
                .groupBy { transaction ->
                    transaction.date.toInstant()
                        .atZone(zoneId)
                        .toLocalDate()
                        .withDayOfMonth(1)
                }
                .map { (monthStart, monthlyTransactions) ->
                    monthStart to ExpenseSummary(
                        totalAmount = monthlyTransactions.sumOf { it.amount },
                        transactions = monthlyTransactions
                    )
                }
                .sortedByDescending { it.first }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun formatDate(date: Date): String {
        return try {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    @Composable
    fun ExpensePeriodCard(
        period: TimePeriod,
        date: LocalDate,
        summary: ExpenseSummary
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = try {
                            when (period) {
                                TimePeriod.DAILY -> date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                                TimePeriod.WEEKLY -> "Week of ${date.format(DateTimeFormatter.ofPattern("MMM dd"))}"
                                TimePeriod.MONTHLY -> date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                            }
                        } catch (e: Exception) {
                            "Invalid date"
                        },
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "₹${String.format("%.2f", summary.totalAmount)}",
                        color = Purple,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${summary.transactions.size} transactions",
                    color = TextGray,
                    fontSize = 14.sp
                )
            }
        }
    }
} 