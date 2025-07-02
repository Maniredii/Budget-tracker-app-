package fm.mrc.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import fm.mrc.budget.data.TransactionEntity
import fm.mrc.budget.ui.theme.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.time.format.DateTimeFormatter
import fm.mrc.budget.components.AIInsightsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyExpensesScreen(
    viewModel: BudgetViewModel,
    onAddTransaction: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val monthlyBudget by viewModel.monthlyBudget.collectAsState()
    val remainingBudget = monthlyBudget - totalExpenses
    val aiResponse by viewModel.aiResponse.collectAsState(initial = "")
    val isLoading by viewModel.isLoading.collectAsState()
    var showEditDialog by remember { mutableStateOf<Transaction?>(null) }
    var showChatDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Expenses") }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButton(
                    onClick = { showChatDialog = true },
                    containerColor = Purple,
                    contentColor = TextWhite,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = "Chat with AI")
                }
                FloatingActionButton(
                    onClick = onAddTransaction,
                    containerColor = Purple,
                    contentColor = TextWhite,
                    modifier = Modifier.padding(bottom = 80.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                BudgetSummaryCard(
                    totalExpenses = totalExpenses,
                    monthlyBudget = monthlyBudget,
                    remainingBudget = remainingBudget,
                    viewModel = viewModel
                )
            }

            item {
                ExpenseCharts(transactions = transactions)
            }

            item {
                Text(
                    "Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(transactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onDelete = { viewModel.deleteTransaction(transaction) },
                    onEdit = { showEditDialog = transaction }
                )
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

    // Chat Dialog
    if (showChatDialog) {
        AIBudgetChatDialog(
            onDismiss = { showChatDialog = false },
            aiResponse = aiResponse,
            isLoading = isLoading,
            onSendMessage = { viewModel.getAIBudgetAdvice() }
        )
    }
}

@Composable
fun BudgetSummaryCard(
    totalExpenses: Double,
    monthlyBudget: Double,
    remainingBudget: Double,
    viewModel: BudgetViewModel
) {
    var showEditBudgetDialog by remember { mutableStateOf(false) }
    var editedBudget by remember { mutableStateOf(monthlyBudget.toString()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Budget Summary",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { showEditBudgetDialog = true }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Budget",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Monthly Budget")
                    Text(
                        "₹${monthlyBudget.format()}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column {
                    Text("Total Expenses")
                    Text(
                        "₹${totalExpenses.format()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Column {
                    Text("Remaining")
                    Text(
                        "₹${remainingBudget.format()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (remainingBudget >= 0) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (totalExpenses / monthlyBudget).toFloat().coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (remainingBudget >= 0) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.error
            )
        }
    }

    if (showEditBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showEditBudgetDialog = false },
            title = { Text("Edit Monthly Budget") },
            text = {
                OutlinedTextField(
                    value = editedBudget,
                    onValueChange = { editedBudget = it },
                    label = { Text("Monthly Budget") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = TextGray,
                        focusedLabelColor = Purple,
                        unfocusedLabelColor = TextGray
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            val newBudget = editedBudget.toDouble()
                            viewModel.updateMonthlyBudget(newBudget)
                            showEditBudgetDialog = false
                        } catch (e: NumberFormatException) {
                            // Handle invalid input
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBudgetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit,
    onEdit: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    transaction.description ?: transaction.merchant,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    transaction.category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    transaction.paymentMethod?.name ?: "Unknown",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "₹${transaction.amount.format()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    formatDate(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    if (onEdit != null) {
                        IconButton(onClick = onEdit) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Transaction",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Transaction",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

fun Double.format(): String = String.format("%.2f", this)

fun formatDate(date: Date): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
}

@Composable
fun AIBudgetChatDialog(
    onDismiss: () -> Unit,
    aiResponse: String?,
    isLoading: Boolean,
    onSendMessage: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "AI Budget Assistant",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                // Recent Expenses Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Recent Activity",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            Text(
                                text = aiResponse ?: "Ask me anything about your budget!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onSendMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple,
                            contentColor = TextWhite
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Get Budget Advice")
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Close")
                    }
                }
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        confirmButton = { }
    )
} 