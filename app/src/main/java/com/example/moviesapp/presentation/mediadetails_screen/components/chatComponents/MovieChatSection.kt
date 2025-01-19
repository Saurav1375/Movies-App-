package com.example.moviesapp.presentation.mediadetails_screen.components.chatComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moviesapp.domain.model.Message
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsEvent
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsViewModel
import com.example.moviesapp.presentation.shared.UserListViewModel

@Composable
fun MovieChatSection(
    modifier: Modifier = Modifier,
    viewModel: MediaDetailsViewModel,
    currentUser: UserData,
    messages: List<Message>,
    maxHeight: Dp = 400.dp
) {
    var messageText by remember { mutableStateOf("") }
    val selectedMessageId by viewModel.selectedMessageId.collectAsState()
    val showTextOption = !selectedMessageId.isNullOrEmpty()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Reviews",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )

                    if (showTextOption) {
                        AnimatedVisibility(
                            visible = showTextOption,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            Row(
                                modifier = Modifier.padding(end = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Delete icon
                                IconButton(
                                    onClick = {
                                        viewModel.OnEvent(
                                            MediaDetailsEvent.OnDeleteMessage(
                                                selectedMessageId!!
                                            )
                                        )
                                        viewModel.selectMessage(null)

                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            val userListViewModel: UserListViewModel = hiltViewModel()
            val userState by userListViewModel.userData.collectAsState()

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
                userData = userState.userData,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp)
            )

            // Messages List
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
                items(messages) { message ->
                    SocialPost(
                        message = message,
                        viewModel = viewModel,
                        msgLiked = message.likes.contains(currentUser.userId),
                        isCurrentUser = message.senderName == currentUser.username,
                        onLikeClick = {
                            viewModel.OnEvent(MediaDetailsEvent.OnLikedButtonClicked(it))
                        },
                        selectedMessageId = selectedMessageId
                    )
                }
            }
        }
    }
}
