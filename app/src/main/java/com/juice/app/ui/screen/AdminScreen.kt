package com.juice.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juice.app.model.Stall
import com.juice.app.viewmodel.AdminViewModel

@Composable
fun AdminScreen(
    viewModel: AdminViewModel = viewModel(),
    onAddStall: () -> Unit,
    onEditStall: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var stallToDelete by remember { mutableStateOf<Stall?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddStall) {
                Icon(Icons.Default.Add, contentDescription = "Add stall")
            }
        }
    ) { padding ->
        if (uiState.stalls.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No stalls yet. Tap + to add one.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.stalls, key = { it.id }) { stall ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stall.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (!stall.description.isNullOrBlank()) {
                                    Text(
                                        text = stall.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1
                                    )
                                }
                            }
                            IconButton(onClick = { onEditStall(stall.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(onClick = { stallToDelete = stall }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (stallToDelete != null) {
        AlertDialog(
            onDismissRequest = { stallToDelete = null },
            title = { Text("Delete Stall") },
            text = { Text("Delete \"${stallToDelete!!.name}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteStall(stallToDelete!!.id)
                    stallToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { stallToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
