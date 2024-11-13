package com.example.cognitrix.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class VideoScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YouTubeVideoScreen(videoId = "E_8LHkn4g-Q")
        }
    }
}

@Composable
fun YouTubeVideoScreen(videoId: String) {
    val youTubePlayer = remember { mutableStateOf<YouTubePlayer?>(null) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(initializedYouTubePlayer: YouTubePlayer) {
                        // Store the initialized YouTubePlayer in the remember state
                        youTubePlayer.value = initializedYouTubePlayer
                        initializedYouTubePlayer.loadVideo(videoId, 0f)
                    }
                })

                // Handle lifecycle events to release the player
                lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
                    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                        when (event) {
                            Lifecycle.Event.ON_PAUSE -> youTubePlayer.value?.pause()
                            Lifecycle.Event.ON_RESUME -> youTubePlayer.value?.play()
                            Lifecycle.Event.ON_DESTROY -> release()
                            else -> {}
                        }
                    }
                })
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
@Preview(showBackground = true)
@Composable
fun YouTubeVideoScreenPreview() {
    YouTubeVideoScreen(videoId = "E_8LHkn4g-Q") // Rick Astley - Never Gonna Give You Up
}