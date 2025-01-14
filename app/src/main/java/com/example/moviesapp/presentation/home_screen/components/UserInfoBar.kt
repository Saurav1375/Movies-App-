package com.example.moviesapp.presentation.home_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.moviesapp.domain.model.User

@Composable
fun UserInfoBar(user: User?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = ("Hi, " + (user?.name ?: "Unknown")) ,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = user?.photoUrl,
                contentDescription = "Profile picture",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserInfoBarPreview() {
    UserInfoBar(
        modifier = Modifier.fillMaxWidth(),
        user = User(
            id = "",
            name = "Saurav Gupta",
            email = "william.henry.harrison@example-pet-store.com",
            photoUrl = "https://firebasestorage.googleapis.com/v0/b/getwel-8ce59.appspot.com/o/profile_images%2Fguest_image.jpg?alt=media&token=e69a44ed-4950-4891-8983-4a781d076535"
        )
    )
}