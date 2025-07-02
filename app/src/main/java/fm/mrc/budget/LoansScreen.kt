package fm.mrc.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.budget.data.LoanEntity
import fm.mrc.budget.data.LoanType
import fm.mrc.budget.ui.theme.*
import fm.mrc.budget.utils.format
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansScreen(viewModel: BudgetViewModel) {
    var showAddLoanDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Given", "Taken", "Pending")
    
    val loans by viewModel.loans.collectAsState(initial = emptyList())
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Loans Summary",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Money Given
                        Column {
                            Text(
                                text = "Money Given",
                                color = TextGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "₹${loans.filter { it.type == LoanType.GIVEN }.sumOf { it.amount }.format()}",
                                color = Color.Green,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        // Money Taken
                        Column {
                            Text(
                                text = "Money Taken",
                                color = TextGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "₹${loans.filter { it.type == LoanType.TAKEN }.sumOf { it.amount }.format()}",
                                color = Color.Red,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        // Pending
                        Column {
                            Text(
                                text = "Pending",
                                color = TextGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "₹${loans.filter { !it.isPaid }.sumOf { it.amount }.format()}",
                                color = Purple,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
            
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = Purple
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Loans list
            val filteredLoans = when (selectedTab) {
                1 -> loans.filter { it.type == LoanType.GIVEN }
                2 -> loans.filter { it.type == LoanType.TAKEN }
                3 -> loans.filter { !it.isPaid }
                else -> loans
            }
            
            if (filteredLoans.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No loans found",
                        color = TextGray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredLoans) { loan ->
                        LoanCard(
                            loan = loan,
                            onMarkPaid = { 
                                viewModel.updateLoan(loan.copy(
                                    isPaid = true,
                                    paidDate = Date()
                                ))
                            }
                        )
                    }
                }
            }
        }

        // FAB for adding loans
        FloatingActionButton(
            onClick = { showAddLoanDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Purple,
            contentColor = TextWhite
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Loan"
            )
        }
    }
    
    if (showAddLoanDialog) {
        AddLoanDialog(
            onDismiss = { showAddLoanDialog = false },
            onLoanAdded = { loan ->
                viewModel.addLoan(loan)
                showAddLoanDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanCard(
    loan: LoanEntity,
    onMarkPaid: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = loan.personName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                    loan.description?.let { desc ->
                        Text(
                            text = desc,
                            fontSize = 14.sp,
                            color = TextGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Text(
                    text = "₹${loan.amount.format()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (loan.type) {
                        LoanType.GIVEN -> Color.Green
                        LoanType.TAKEN -> Color.Red
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Due Date",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(loan.date),
                        fontSize = 14.sp,
                        color = TextWhite
                    )
                }
                if (!loan.isPaid) {
                    Button(
                        onClick = onMarkPaid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Mark as Paid")
                    }
                } else {
                    Text(
                        text = "Paid on ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(loan.paidDate!!)}",
                        fontSize = 14.sp,
                        color = Color.Green
                    )
                }
            }
        }
    }
} 