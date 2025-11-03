package com.aditya.accesscontrolapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aditya.accesscontrolapp.models.ModuleAccessState
import com.aditya.accesscontrolapp.viewmodel.ModuleAccessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleAccessScreen(
    viewModel: ModuleAccessViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔐 Demo: Module Access UI") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Cooling Period Banner
            uiState.coolingMessage?.let { message ->
                CoolingBanner(message)
            }

            // Module Cards
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.modules.forEach { moduleState ->
                        ModuleCard(
                            moduleState = moduleState,
                            onClick = {
                                val message = viewModel.onModuleClicked(moduleState)
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CoolingBanner(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleCard(
    moduleState: ModuleAccessState,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (moduleState.isAccessible)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = moduleState.module.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (moduleState.isAccessible) {
                    "✅ Allowed - Tap to navigate"
                } else {
                    "❌ ${moduleState.denialReason}"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (moduleState.isAccessible)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}