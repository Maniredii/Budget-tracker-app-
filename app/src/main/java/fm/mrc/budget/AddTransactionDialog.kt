package fm.mrc.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fm.mrc.budget.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onAddTransaction: (Transaction) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var showPaymentMethodMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add Transaction",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Field
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        amount = it
                        hasError = false
                    },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = hasError,
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

                Spacer(modifier = Modifier.height(8.dp))

                // Description Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
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

                Spacer(modifier = Modifier.height(8.dp))

                // Payment Method Dropdown
                ExposedDropdownMenuBox(
                    expanded = showPaymentMethodMenu,
                    onExpandedChange = { showPaymentMethodMenu = it }
                ) {
                    OutlinedTextField(
                        value = selectedPaymentMethod?.name?.replace("_", " ") ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Method") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = TextGray,
                            focusedLabelColor = Purple,
                            unfocusedLabelColor = TextGray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = showPaymentMethodMenu,
                        onDismissRequest = { showPaymentMethodMenu = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        PaymentMethod.values().forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method.name.replace("_", " "), color = TextWhite) },
                                onClick = {
                                    selectedPaymentMethod = method
                                    showPaymentMethodMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = showCategoryMenu,
                    onExpandedChange = { showCategoryMenu = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name?.replace("_", " ") ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = TextGray,
                            focusedLabelColor = Purple,
                            unfocusedLabelColor = TextGray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        ExpenseCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name.replace("_", " "), color = TextWhite) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = TextGray)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (amount.isBlank() || selectedPaymentMethod == null || selectedCategory == null) {
                                hasError = true
                                return@Button
                            }
                            
                            try {
                                val amountValue = amount.toDouble()
                                val transaction = Transaction(
                                    amount = amountValue,
                                    merchant = description.ifBlank { selectedCategory?.name ?: "Unknown" },
                                    date = Date(),
                                    icon = when (selectedCategory) {
                                        ExpenseCategory.FOOD -> "🍽️"
                                        ExpenseCategory.SHOPPING -> "🛍️"
                                        ExpenseCategory.TRANSPORTATION -> "🚗"
                                        ExpenseCategory.ENTERTAINMENT -> "🎬"
                                        ExpenseCategory.UTILITIES -> "💡"
                                        ExpenseCategory.HEALTH -> "🏥"
                                        ExpenseCategory.EDUCATION -> "📚"
                                        ExpenseCategory.TRAVEL -> "✈️"
                                        ExpenseCategory.HOUSING -> "🏠"
                                        else -> "💰"
                                    },
                                    iconBackgroundColor = 0xFF6750A4,
                                    category = selectedCategory ?: ExpenseCategory.OTHER,
                                    paymentMethod = selectedPaymentMethod,
                                    description = if (description.isBlank()) null else description
                                )
                                onAddTransaction(transaction)
                            } catch (e: NumberFormatException) {
                                hasError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple,
                            contentColor = TextWhite
                        )
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
} 