package fm.mrc.budget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.budget.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: BudgetViewModel
) {
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val selectedDate by viewModel.selectedDate.collectAsState()
    var showEditDialog by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                TransactionCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { viewModel.setSelectedDate(it) }
                )
            }

            item {
                Text(
                    "Transactions for ${selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            val selectedDateTransactions = transactions.filter { transaction ->
                val transactionDate = transaction.date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                transactionDate == selectedDate
            }

            if (selectedDateTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "No transactions for this date",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                items(selectedDateTransactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onDelete = { viewModel.deleteTransaction(transaction) },
                        onEdit = { showEditDialog = transaction }
                    )
                }
            }
        }
    }

    // Edit Dialog
    showEditDialog?.let { transaction ->
        EditTransactionDialog(
            transaction = transaction,
            onDismiss = { showEditDialog = null },
            onUpdateTransaction = { updatedTransaction ->
                viewModel.updateTransaction(updatedTransaction)
                showEditDialog = null
            }
        )
    }
} 