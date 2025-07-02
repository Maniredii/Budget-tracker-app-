package fm.mrc.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fm.mrc.budget.ui.theme.*

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Expenses : Screen("expenses", Icons.Default.AccountBalance, "Expenses")
    object Calendar : Screen("calendar", Icons.Default.CalendarMonth, "Calendar")
    object Loans : Screen("loans", Icons.Default.CreditCard, "Loans")
    object Settings : Screen("settings", Icons.Default.Settings, "Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: BudgetViewModel,
    onAddTransaction: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val screens = listOf(Screen.Expenses, Screen.Calendar, Screen.Loans, Screen.Settings)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Tracker", color = TextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface
            ) {
                screens.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label,
                                tint = if (selectedTab == index) TextWhite else TextWhite.copy(alpha = 0.6f)
                            )
                        },
                        label = {
                            Text(
                                text = screen.label,
                                color = if (selectedTab == index) TextWhite else TextWhite.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> MonthlyExpensesScreen(
                    viewModel = viewModel,
                    onAddTransaction = onAddTransaction
                )
                1 -> CalendarScreen(
                    viewModel = viewModel
                )
                2 -> LoansScreen(viewModel = viewModel)
                3 -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
} 