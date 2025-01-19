package com.example.moviesapp.presentation.mediadetails_screen.components.dialogComponents

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.moviesapp.domain.model.MediaList
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsViewModel

@Composable
fun OtherMediaView(
    title: String,
    icon: ImageVector,
    mediaType: MediaList?,
    mediaId: Int,
    viewModel: MediaDetailsViewModel,
    modifier: Modifier = Modifier
) {
    // Check if the media is already present in the list
    val alreadyPresent = mediaType?.list?.any { it.id == mediaId } == true
    var isSelected by remember { mutableStateOf(alreadyPresent) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                enabled = !alreadyPresent
            ) {
                isSelected = !isSelected
                if (mediaType != null) {
                    if (isSelected) {
                        viewModel.selectedListIds.value += mediaType.id
                    } else {
                        viewModel.selectedListIds.value -= mediaType.id
                    }
                }
            }
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "List Icon",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(8.dp))

            Text(
                text = if (alreadyPresent) "Already Present" else title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
