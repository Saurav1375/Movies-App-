package com.example.moviesapp.presentation.mediadetails_screen.components.dialogComponents

import android.widget.CheckBox
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.unit.dp
import com.example.moviesapp.domain.model.MediaList
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsViewModel

@Composable
fun WatchListItem(
    viewModel: MediaDetailsViewModel,
    watchList: MediaList,
    mediaId : Int,
    modifier: Modifier = Modifier,
) {

    val alreadyPresent = watchList.list.find { it.id == mediaId} != null
    var isChecked by remember { mutableStateOf(alreadyPresent) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable (
                enabled = !alreadyPresent
            ){
                isChecked = !isChecked
                if (isChecked) {
                    viewModel.selectedListIds.value += watchList.id
                } else {
                    viewModel.selectedListIds.value -= watchList.id
                }
            },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            enabled = !alreadyPresent,
            checked = isChecked,
            onCheckedChange = {
                isChecked = !isChecked
                if (isChecked) {
                    viewModel.selectedListIds.value += watchList.id
                } else {
                    viewModel.selectedListIds.value -= watchList.id
                }
            }
        )

        Text(
            text = watchList.name,
            modifier = Modifier.padding(2.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        if(alreadyPresent){
            Text(
                text = "(Already Present)",
                modifier = Modifier.padding(2.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }



    }

}