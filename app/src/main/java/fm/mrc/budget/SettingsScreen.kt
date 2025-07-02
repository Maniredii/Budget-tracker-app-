package fm.mrc.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fm.mrc.budget.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: BudgetViewModel) {
    val uriHandler = LocalUriHandler.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            color = TextWhite,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Developer Information Section
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
                    text = "Developer",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Manideep Reddy Eevuri",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Social Links
                ListItem(
                    headlineContent = { Text("LinkedIn", color = TextWhite) },
                    leadingContent = { Icon(Icons.Default.Person, contentDescription = "LinkedIn", tint = Purple) },
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://www.linkedin.com/in/manideep-reddy-eevuri-661659268")
                    }
                )

                ListItem(
                    headlineContent = { Text("GitHub", color = TextWhite) },
                    leadingContent = { Icon(Icons.Default.Code, contentDescription = "GitHub", tint = Purple) },
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://github.com/Maniredii")
                    }
                )
            }
        }

        // Sponsorship Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Support the Development",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Help keep this app running smoothly for all users by supporting its development.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        uriHandler.openUri("https://buymeacoffee.com/manideep")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Sponsor",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Buy me a coffee")
                }
            }
        }

        // App Version
        Text(
            text = "Version 1.0.0",
            color = TextGray,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
} 