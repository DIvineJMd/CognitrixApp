package com.example.cognitrix.pages

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun VideoPlayerScreen(
    videoId: String,
    lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current
    val youTubePlayerView = remember { YouTubePlayerView(context) }
    var youTubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }

    DisposableEffect(videoId) {
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                youTubePlayer = player
                player.loadVideo(videoId, 0f)
            }
        })
        onDispose {
            youTubePlayer?.pause()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> youTubePlayer?.pause()
                Lifecycle.Event.ON_RESUME -> youTubePlayer?.play()
                Lifecycle.Event.ON_DESTROY -> {
                    youTubePlayer?.pause()
                    youTubePlayerView.release()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // YouTube Player
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            AndroidView(
                factory = { youTubePlayerView },
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Fullscreen Button
        Button(
            onClick = {
                val intent = Intent(context, FullScreenVideoActivity::class.java)
                intent.putExtra("VIDEO_ID", videoId)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fullscreen")
        }
    }
}
