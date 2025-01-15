package com.example.moviesapp.presentation.mediadetails_screen.components.chatComponents
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.moviesapp.domain.model.Message
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsEvent
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsViewModel

@Composable
fun MovieChatSection(
    modifier: Modifier = Modifier,
    viewModel: MediaDetailsViewModel,
    currentUser: UserData,
    messages : List<Message>,
    maxHeight: Dp = 400.dp
) {
    var messageText by remember { mutableStateOf("") }
    var editingMessageId by remember { mutableStateOf<String?>(null) }
    var editText by remember { mutableStateOf("") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Discussion",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
               items(messages.reversed()) { message ->
                    ChatMessageItem(
                        message = message,
                        isCurrentUser = message.sender == currentUser.username,
                        isEditing = editingMessageId == message.id,
                        editText = if (editingMessageId == message.id) editText else "",
                        onEditClick = {
                            editingMessageId = message.id
                            editText = message.text
                        },
                        onEditChange = { editText = it },
                        onEditDone = {
                            viewModel.OnEvent(MediaDetailsEvent.OnUpdateMessage(message.id, editText))
                            editingMessageId = null
                            editText = ""
                        },
                        onDeleteClick = {
                            viewModel.OnEvent(MediaDetailsEvent.OnDeleteMessage(message.id))
                        },
                        onReactionClick = { emoji ->
                            viewModel.OnEvent(MediaDetailsEvent.OnAddReaction(message.id, emoji))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }

            // Message Input
            ChatInputField(
                value = messageText,
                onValueChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.OnEvent(MediaDetailsEvent.OnSendMessage(messageText, currentUser))
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}