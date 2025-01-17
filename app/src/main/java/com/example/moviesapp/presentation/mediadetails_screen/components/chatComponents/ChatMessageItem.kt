package com.example.moviesapp.presentation.mediadetails_screen.components.chatComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moviesapp.R
import com.example.moviesapp.domain.model.Message
import com.example.moviesapp.presentation.TimeAgoFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SocialPost(
    message: Message,
    isCurrentUser: Boolean,
    onLonPress: (Message, Boolean) -> Unit,
    msgLiked: Boolean,
    onLikeClick: (String) -> Unit,
    messageSelected: Boolean,
    modifier: Modifier = Modifier
) {

    var isSelected by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                if (isCurrentUser) {
                    detectTapGestures(
                        onLongPress = {
                            if (!messageSelected) {
                                isSelected = !isSelected
                                onLonPress(message, isSelected)
                            }

                        },
                        onTap = {
                            if (!messageSelected) {
                                isSelected = false
                                onLonPress(message, false)
                            }

                        }

                    )
                }

            }
            .background(
                if (!isSelected) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHighest
            )
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image

        if (isSelected) {
            IconButton(
                onClick = {
                    isSelected = false
                }
            ) {
                Icon(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(message.senderProfilePicture)
                    .placeholder(R.drawable.errorholder)
                    .build(),
                contentDescription = "Profile picture of ${message.senderName}",
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }


        Spacer(modifier = Modifier.width(16.dp))

        // Username and Time
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = TimeAgoFormatter.getTimeAgo(message.timestamp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Text(
                text = message.text.trimEnd(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

        }

        // Like Count
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    onLikeClick(message.id)
                }
            ) {
                Icon(
                    imageVector = if(!msgLiked) Icons.Outlined.ThumbUp else Icons.Filled.ThumbUp,
                    contentDescription = "Likes",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = message.likes.size.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

        }
    }
}

// Preview
//@Preview(showBackground = true)
//@Composable
//fun SocialPostPreview() {
//    MaterialTheme {
//        SocialPost(
//            username = "Heart_beat",
//            timeAgo = "3h ago",
//            likeCount = 255,
//            profileImage = painterResource(id = R.drawable.errorholder)
//        )
//    }
//}

//@Composable
//fun ChatMessageItem(
//    message: Message,
//    isCurrentUser: Boolean,
//    isEditing: Boolean,
//    editText: String,
//    onEditClick: () -> Unit,
//    onEditChange: (String) -> Unit,
//    onEditDone: () -> Unit,
//    onDeleteClick: () -> Unit,
//    onReactionClick: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var showOptions by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = modifier,
//        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
//        ) {
//            if (isEditing) {
//                OutlinedTextField(
//                    value = editText,
//                    onValueChange = onEditChange,
//                    modifier = Modifier
//                        .fillMaxWidth(0.8f)
//                        .padding(end = 8.dp),
//                    trailingIcon = {
//                        IconButton(onClick = onEditDone) {
//                            Icon(
//                                imageVector = Icons.Default.Check,
//                                contentDescription = "Save edit",
//                            )
//                        }
//                    }
//                )
//            } else {
//                Surface(
//                    color = if (isCurrentUser)
//                        MaterialTheme.colorScheme.primary
//                    else
//                        MaterialTheme.colorScheme.surfaceVariant,
//                    shape = RoundedCornerShape(12.dp),
//                    modifier = Modifier
//                        .widthIn(max = 280.dp)
//                        .pointerInput(Unit) {
//                            detectTapGestures(
//                                onLongPress = { showOptions = true }
//                            )
//                        }
//                ) {
//                    Column(
//                        modifier = Modifier.padding(8.dp)
//                    ) {
//                        Text(
//                            text = message.sender ,
//                            style = MaterialTheme.typography.labelSmall,
//                            color = if (isCurrentUser)
//                                MaterialTheme.colorScheme.onPrimary
//                            else
//                                MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                        Text(
//                            text = message.text,
//                            color = if (isCurrentUser)
//                                MaterialTheme.colorScheme.onPrimary
//                            else
//                                MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                        Text(
//                            text = formatTimestamp(message.timestamp),
//                            style = MaterialTheme.typography.labelSmall,
//                            color = if (isCurrentUser)
//                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
//                            else
//                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
//                        )
//                    }
//                }
//            }
//        }
//        if (showOptions && isCurrentUser) {
//            AlertDialog(
//                onDismissRequest = { showOptions = false },
//                title = { Text("Message Options") },
//                text = {
//                    Column {
//                        TextButton(
//                            onClick = {
//                                onEditClick()
//                                showOptions = false
//                            }
//                        ) {
//                            Text("Edit Message")
//                        }
//                        TextButton(
//                            onClick = {
//                                onDeleteClick()
//                                showOptions = false
//                            }
//                        ) {
//                            Text("Delete Message", color = MaterialTheme.colorScheme.error)
//                        }
//                    }
//                },
//                confirmButton = {},
//                dismissButton = {
//                    TextButton(onClick = { showOptions = false }) {
//                        Text("Cancel")
//                    }
//                }
//            )
//        }
//    }
//}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}