package com.juice.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.juice.app.ui.component.StallCard
import com.juice.app.viewmodel.StallListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StallListScreen(
    viewModel: StallListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedStall by remember { mutableStateOf<Stall?>(null) }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.loadStalls() },
        modifier = Modifier.fillMaxSize()
    ) {
        if (uiState.stalls.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.error != null) "Error: ${uiState.error}"
                    else "No stalls found nearby",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.stalls, key = { it.id }) { stall ->
                    StallCard(
                        stall = stall,
                        onClick = { selectedStall = stall }
                    )
                }
            }
        }
    }

    if (selectedStall != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedStall = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            StallDetailSheet(
                stall = selectedStall!!,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
