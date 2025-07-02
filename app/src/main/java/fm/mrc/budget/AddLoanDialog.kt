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
import fm.mrc.budget.data.LoanEntity
import fm.mrc.budget.data.LoanType
import fm.mrc.budget.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLoanDialog(
    onDismiss: () -> Unit,
    onLoanAdded: (LoanEntity) -> Unit
) {
    var personName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<LoanType?>(null) }
    var showTypeMenu by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var dueDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

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
                    text = "Add Loan",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Person Name
                OutlinedTextField(
                    value = personName,
                    onValueChange = { 
                        personName = it
                        hasError = false
                    },
                    label = { Text("Person/App Name") },
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

                // Amount
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

                // Loan Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = showTypeMenu,
                    onExpandedChange = { showTypeMenu = it }
                ) {
                    OutlinedTextField(
                        value = selectedType?.name?.replace("_", " ") ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Loan Type") },
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
                        expanded = showTypeMenu,
                        onDismissRequest = { showTypeMenu = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        LoanType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        when (type) {
                                            LoanType.GIVEN -> "Money Given"
                                            LoanType.TAKEN -> "Money Taken"
                                        },
                                        color = TextWhite
                                    )
                                },
                                onClick = {
                                    selectedType = type
                                    showTypeMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Due Date
                OutlinedTextField(
                    value = dateFormatter.format(dueDate),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Due Date") },
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
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDatePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(
                            state = rememberDatePickerState(
                                initialSelectedDateMillis = dueDate.time
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description
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
                            if (personName.isBlank() || amount.isBlank() || selectedType == null) {
                                hasError = true
                                return@Button
                            }
                            
                            try {
                                val amountValue = amount.toDouble()
                                val loan = LoanEntity(
                                    personName = personName,
                                    amount = amountValue,
                                    date = dueDate,
                                    type = selectedType!!,
                                    description = description.takeIf { it.isNotBlank() }
                                )
                                onLoanAdded(loan)
                            } catch (e: NumberFormatException) {
                                hasError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
} 