package com.example.cognitrix.pages


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class FullScreenVideoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoId = intent.getStringExtra("VIDEO_ID") ?: ""
//        enableEdgeToEdge()
        setContent {
            val configuration = LocalConfiguration.current
            val isPotrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

            FullScreenVideoPlayer(videoId = videoId)
            if(isPotrait){
                finish()
            }

        }
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FullScreenVideoPlayer(videoId: String) {
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
    Scaffold(){
    Column(modifier = Modifier.fillMaxSize()) {
        // Fullscreen YouTube Player
        AndroidView(
            factory = { youTubePlayerView },
            modifier = Modifier

        )
    }
    }
}
