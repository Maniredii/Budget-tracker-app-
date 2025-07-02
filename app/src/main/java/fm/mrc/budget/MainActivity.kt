package fm.mrc.budget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import fm.mrc.budget.data.ApiConfig
import fm.mrc.budget.ui.theme.BudgetTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: BudgetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Initialize ViewModel using ViewModelProvider
            viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[BudgetViewModel::class.java]
            
            // Set Gemini API key
            ApiConfig.setGeminiApiKey("add your api key")
            
            setContent {
                BudgetTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        var showAddDialog by remember { mutableStateOf(false) }
                        
                        MainScreen(
                            viewModel = viewModel,
                            onAddTransaction = { showAddDialog = true }
                        )

                        // Show Add Transaction Dialog when FAB is clicked
                        if (showAddDialog) {
                            AddTransactionDialog(
                                onDismiss = { showAddDialog = false },
                                onAddTransaction = { transaction ->
                                    viewModel.addTransaction(transaction)
                                    showAddDialog = false
                                }
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle initialization error
            setContent {
                BudgetTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ErrorScreen(message = "Failed to initialize app. Please restart.")
                    }
                }
            }
        }
    }
}
