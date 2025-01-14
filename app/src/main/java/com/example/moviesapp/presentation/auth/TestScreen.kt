package com.example.moviesapp.presentation.auth

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.moviesapp.domain.model.UserStateHolder
import com.example.moviesapp.presentation.shared.SharedUserViewModel

@Composable
fun TestScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    sharedUserViewModel: SharedUserViewModel = hiltViewModel(),
    onSignOut: () -> Unit
) {
    val user by UserStateHolder.user.collectAsState()
    Log.d("TestScreen", "User: $user")
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        user?.let { currentUser ->
            AsyncImage(
                model = currentUser.photoUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentUser.name ?: "No name",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = currentUser.email ?: "No email",
                style = MaterialTheme.typography.bodyLarge
            )


            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                authViewModel.signOut()
                onSignOut()
            }) {
                Text("Sign Out")
            }
        }
    }

}