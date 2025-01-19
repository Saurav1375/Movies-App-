package com.example.moviesapp.presentation.friends_profile_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moviesapp.domain.model.ListType
import com.example.moviesapp.presentation.profile_screen.components.MediaItemView
import com.example.moviesapp.presentation.shared.UserListViewModel

@Composable
fun FriendsWatchListDetailedView(
    modifier: Modifier = Modifier,
    listId: String,
    viewModel: FriendsProfileViewModel,
    onBackPress: () -> Unit = {},
    onClick: (Int, String) -> Unit,
) {
    val mediaList = viewModel.mediaList.collectAsState()
    val watchList = mediaList.value.find { it.id == listId && it.type == ListType.WATCHLIST.name }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPress) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }

            Text(
                text = watchList?.name ?: "Watchlist",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                watchList?.list?.distinct()?.let { mediaItems ->
                    items(
                        count = mediaItems.size,
                        key = { index -> mediaItems[index].id }
                    ) { index ->
                        val media = mediaItems[index]
                        MediaItemView(
                            item = media,
                            modifier = Modifier
                                .size(110.dp, 230.dp)
                                .padding(4.dp)
                                .clickable { onClick(media.id, media.type) }
                        )
                    }
                }
            }
        }
    }
}
