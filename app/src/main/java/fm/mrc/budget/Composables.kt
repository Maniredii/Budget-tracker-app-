package fm.mrc.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.budget.ui.theme.DarkSurface
import fm.mrc.budget.ui.theme.Purple
import fm.mrc.budget.ui.theme.TextGray
import fm.mrc.budget.ui.theme.TextWhite

@Composable
fun TransactionCard(
    transaction: Transaction,
    onEditClick: (() -> Unit)? = null,
    categoryColor: Color = Purple
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = transaction.merchant,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                    if (onEditClick != null) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Transaction",
                                tint = TextGray
                            )
                        }
                    }
                }
                if (transaction.description != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.description,
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = categoryColor,
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = transaction.category.name,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                    Text(
                        text = transaction.paymentMethod?.name?.replace("_", " ") ?: "Unknown",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }
            Text(
                text = "₹${String.format("%.2f", transaction.amount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = categoryColor
            )
        }
    }
} 