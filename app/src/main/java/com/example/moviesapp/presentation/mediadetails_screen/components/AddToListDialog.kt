package com.example.moviesapp.presentation.mediadetails_screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToListDialog(
    onDismissRequest: () -> Unit,
    watchlists: List<String>,
    onAddToWatchlist: (String) -> Unit,
    onCreateWatchlist: (String) -> Unit,
    onAddToFavorites: () -> Unit,
    onAddToWatched: () -> Unit,
    isInFavorites: Boolean = false,
    isWatched: Boolean = false
) {
    var showCreateWatchlist by remember { mutableStateOf(false) }
    var newWatchlistName by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "Add to Collection",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quick Add Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Quick Add",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    // Favorites Option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == "favorites",
                            onClick = { selectedOption = "favorites" }
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = "Favorites",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                            if (isInFavorites) {
                                Text(
                                    text = "Already in Favorites",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Watched Option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == "watched",
                            onClick = { selectedOption = "watched" }
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = "Watched",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                            if (isWatched) {
                                Text(
                                    text = "Already marked as watched",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.2f))

                // Watchlists Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Add to Watchlist",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    if (watchlists.isEmpty()) {
                        TextButton(
                            onClick = { showCreateWatchlist = true }
                        ) {
                            Text(
                                text = "Create Your First Watchlist",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        watchlists.forEach { watchlist ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedOption == "watchlist:$watchlist",
                                    onClick = { selectedOption = "watchlist:$watchlist" }
                                )
                                Text(
                                    text = watchlist,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        TextButton(
                            onClick = { showCreateWatchlist = true }
                        ) {
                            Text(
                                text = "+ Create New Watchlist",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        selectedOption == "favorites" -> onAddToFavorites()
                        selectedOption == "watched" -> onAddToWatched()
                        selectedOption?.startsWith("watchlist:") == true -> {
                            val watchlistName = selectedOption!!.substringAfter("watchlist:")
                            onAddToWatchlist(watchlistName)
                        }
                    }
                    onDismissRequest()
                },
                enabled = selectedOption != null
            ) {
                Text(
                    "Add",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    "Cancel",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )

    // Create New Watchlist Dialog
    if (showCreateWatchlist) {
        AlertDialog(
            onDismissRequest = { showCreateWatchlist = false },
            title = {
                Text(
                    "Create New Watchlist",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            },
            text = {
                OutlinedTextField(
                    value = newWatchlistName,
                    onValueChange = { newWatchlistName = it },
                    label = {
                        Text(
                            "Watchlist Name",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newWatchlistName.isNotBlank()) {
                            onCreateWatchlist(newWatchlistName)
                            showCreateWatchlist = false
                            selectedOption = "watchlist:$newWatchlistName"
                            newWatchlistName = ""
                        }
                    }
                ) {
                    Text(
                        "Create",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateWatchlist = false }) {
                    Text(
                        "Cancel",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        )
    }
}