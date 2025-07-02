package fm.mrc.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onUpdateTransaction: (Transaction) -> Unit
) {
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var description by remember { mutableStateOf(transaction.description ?: "") }
    var selectedPaymentMethod by remember { mutableStateOf(transaction.paymentMethod ?: PaymentMethod.CASH) }
    var selectedCategory by remember { mutableStateOf(transaction.category) }
    var selectedDate by remember { 
        mutableStateOf(
            transaction.date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        ) 
    }
    var showPaymentMethodMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
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
                    text = "Edit Transaction",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Amount TextField
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        amount = it
                        hasError = false
                    },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = hasError,
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

                // Description TextField
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
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

                // Date Selector
                OutlinedTextField(
                    value = selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = TextGray,
                        focusedLabelColor = Purple,
                        unfocusedLabelColor = TextGray
                    )
                )

                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = selectedDate
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
                    )

                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        selectedDate = Instant.ofEpochMilli(millis)
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate()
                                    }
                                    showDatePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDatePicker = false }
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Payment Method Dropdown
                ExposedDropdownMenuBox(
                    expanded = showPaymentMethodMenu,
                    onExpandedChange = { showPaymentMethodMenu = it }
                ) {
                    OutlinedTextField(
                        value = selectedPaymentMethod.name.replace("_", " "),
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
                        value = selectedCategory.name.replace("_", " "),
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
                            try {
                                val amountValue = amount.toDoubleOrNull()
                                if (amountValue == null || amountValue <= 0) {
                                    hasError = true
                                    return@Button
                                }

                                val updatedTransaction = transaction.copy(
                                    amount = amountValue,
                                    description = description.takeIf { it.isNotBlank() },
                                    paymentMethod = selectedPaymentMethod,
                                    category = selectedCategory,
                                    date = Date.from(
                                        selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                    )
                                )
                                onUpdateTransaction(updatedTransaction)
                                onDismiss()
                            } catch (e: Exception) {
                                hasError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
} 