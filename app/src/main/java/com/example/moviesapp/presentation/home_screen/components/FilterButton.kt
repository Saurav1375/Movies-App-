package com.example.moviesapp.presentation.home_screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun FilterButton(modifier: Modifier = Modifier, onClick: () -> Unit) {

    IconButton(modifier = modifier, onClick = {onClick()}) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Filter",
            tint = MaterialTheme.colorScheme.onBackground
        )
    }

}