package com.example.moviesapp.presentation.mediadetails_screen.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moviesapp.domain.model.ListType
import com.example.moviesapp.domain.model.MediaList
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsEvent
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsViewModel
import com.example.moviesapp.presentation.mediadetails_screen.components.dialogComponents.DialogHeader
import com.example.moviesapp.presentation.mediadetails_screen.components.dialogComponents.OtherMediaView
import com.example.moviesapp.presentation.mediadetails_screen.components.dialogComponents.WatchListAddView

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemsDialog(
    viewModel: MediaDetailsViewModel,
    mediaLists: List<MediaList>,
    mediaId : Int,
    userId : String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {

    val selectedListIds by viewModel.selectedListIds.collectAsState()
    println("all: $mediaLists")
    BasicAlertDialog(
        modifier = Modifier
            .background(
                //dark gray
                Color(0xFF121212),
                RoundedCornerShape(20.dp),
            )
            .clip(RoundedCornerShape(20.dp))
            .fillMaxHeight(0.9f),
        onDismissRequest = onDismiss

    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DialogHeader {
                onDismiss()
            }

            WatchListAddView(viewModel, mediaLists = mediaLists, mediaId = mediaId)
            OtherMediaView(
                viewModel = viewModel,
                mediaId = mediaId,
                mediaType = mediaLists.firstOrNull{ it.id == userId+"FAVORITE" },
                title = "Add to Favourites",
                icon = Icons.Outlined.FavoriteBorder
            )

            OtherMediaView(
                title = "Add to Watched",
                icon = Icons.Outlined.Movie,
                mediaId = mediaId,
                viewModel = viewModel,
                mediaType = mediaLists.firstOrNull { it.id == userId+"WATCHED"},
            )

            if(selectedListIds.isNotEmpty()) {
                TextButton(
                    modifier = Modifier.padding(16.dp).width(80.dp),
                    onClick = {
                        viewModel.OnEvent(MediaDetailsEvent.OnAddClick)
                        viewModel.selectedListIds.value = emptyList()
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(MaterialTheme.colorScheme.primary),
                ) {
                    Text(
                        text = "Add",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddItemsDialogPreview() {
    AddItemsDialog(
        viewModel = hiltViewModel(),
        mediaLists = listOf(),
        onDismiss = {},
        userId = "",
        mediaId = 0
    )
}