package com.example.moviesapp.presentation.mediadetails_screen.components


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class TrailerFragment : Fragment() {

    private var youTubePlayerView: YouTubePlayerView? = null
    private lateinit var videoId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        videoId = arguments?.getString("videoId") ?: ""
        return ComposeView(requireContext()).apply {
            setContent {
                TrailerView(
                    videoId = videoId,
                    onYouTubePlayerCreated = { youTubePlayerView = it }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        youTubePlayerView?.release()
        youTubePlayerView = null
    }
}

@Composable
fun TrailerView(
    videoId: String,
    modifier: Modifier = Modifier,
    onYouTubePlayerCreated: (YouTubePlayerView) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            text = "Trailer",
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 24.dp, top = 8.dp),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )

        AndroidView(
            modifier = modifier,
            factory = { context ->
                val youTubePlayerView = YouTubePlayerView(context)
                youTubePlayerView.addYouTubePlayerListener(object :
                    AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                })
                onYouTubePlayerCreated(youTubePlayerView) // Notify fragment
                youTubePlayerView
            }
        )
    }


}
