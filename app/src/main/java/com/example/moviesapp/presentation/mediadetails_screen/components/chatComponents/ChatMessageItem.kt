package com.example.moviesapp.presentation.mediadetails_screen.components.chatComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.moviesapp.domain.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatMessageItem(
    message: Message,
    isCurrentUser: Boolean,
    isEditing: Boolean,
    editText: String,
    onEditClick: () -> Unit,
    onEditChange: (String) -> Unit,
    onEditDone: () -> Unit,
    onDeleteClick: () -> Unit,
    onReactionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOptions by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = editText,
                    onValueChange = onEditChange,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(end = 8.dp),
                    trailingIcon = {
                        IconButton(onClick = onEditDone) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save edit",
                            )
                        }
                    }
                )
            } else {
                Surface(
                    color = if (isCurrentUser)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = { showOptions = true }
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = message.sender ,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isCurrentUser)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = message.text,
                            color = if (isCurrentUser)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatTimestamp(message.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isCurrentUser)
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Reactions Row
//        if (message.reactions.isNotEmpty()) {
//            Row(
//                modifier = Modifier.padding(top = 4.dp),
//                horizontalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                message.reactions.forEach { (emoji, count) ->
//                    Surface(
//                        color = MaterialTheme.colorScheme.surfaceVariant,
//                        shape = RoundedCornerShape(12.dp),
//                        modifier = Modifier.clickable { onReactionClick(emoji) }
//                    ) {
//                        Text(
//                            text = "$emoji $count",
//                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                        )
//                    }
//                }
//            }
//        }
        if (showOptions && isCurrentUser) {
            AlertDialog(
                onDismissRequest = { showOptions = false },
                title = { Text("Message Options") },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                onEditClick()
                                showOptions = false
                            }
                        ) {
                            Text("Edit Message")
                        }
                        TextButton(
                            onClick = {
                                onDeleteClick()
                                showOptions = false
                            }
                        ) {
                            Text("Delete Message", color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showOptions = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}