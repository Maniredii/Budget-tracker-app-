package fm.mrc.budget.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fm.mrc.budget.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIInsightsCard(
    insights: String?,
    isLoading: Boolean,
    onRequestInsights: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Insights",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite
                )
                IconButton(onClick = onRequestInsights) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Get AI Insights",
                        tint = Purple
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Purple
                )
            } else if (insights != null) {
                Text(
                    text = insights,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Tap the brain icon to get AI-powered insights about your finances",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
} 