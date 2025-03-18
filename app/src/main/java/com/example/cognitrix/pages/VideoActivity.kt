package com.example.cognitrix.pages
//
//import android.content.pm.ActivityInfo
//import android.content.res.Configuration
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.*
//import androidx.compose.runtime.*
//import com.example.cognitrix.R
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlaybackRate
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
//
//class VideoActivity : ComponentActivity() {
//
//    private lateinit var youTubePlayerView: YouTubePlayerView
//    private var youTubePlayer: YouTubePlayer? = null
//    private var isFullscreen by mutableStateOf(false)
//    private lateinit var videoId: String
//
//    private lateinit var playerUiContainer: View
//    private lateinit var fullscreenViewContainer: View
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            FullscreenExample()
//        }
//
//        // Retrieve the video ID from the intent
//        videoId = intent.getStringExtra("VIDEO_ID") ?: "default_video_id" // Use a default if not provided
//
//        // Initialize YouTube player view
//        youTubePlayerView = findViewById(R.id.youtube_player_view)
//        fullscreenViewContainer = findViewById(R.id.full_screen_view_container)
//        playerUiContainer = findViewById(R.id.player_ui_container)
//
//        initYouTubePlayerView(youTubePlayerView)
//    }
//
//    // On Configuration Change (landscape to portrait or vice versa)
//    override fun onConfigurationChanged(configuration: Configuration) {
//        super.onConfigurationChanged(configuration)
//        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            if (!isFullscreen) {
//                youTubePlayer?.toggleFullscreen()
//            }
//        } else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            if (isFullscreen) {
//                youTubePlayer?.toggleFullscreen()
//            }
//        }
//    }
//
//    override fun onBackPressed() {
//        if (isFullscreen) {
//            youTubePlayer?.toggleFullscreen()
//        } else {
//            super.onBackPressed()
//        }
//    }
//
//    private fun initYouTubePlayerView(youTubePlayerView: YouTubePlayerView) {
//        lifecycle.addObserver(youTubePlayerView)
//
//        youTubePlayerView.addFullscreenListener(object : FullscreenListener {
//            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
//                isFullscreen = true
//                playerUiContainer.visibility = View.GONE
//                fullscreenViewContainer.visibility = View.VISIBLE
//                fullscreenViewContainer.addView(fullscreenView)
//
//                if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
//                }
//            }
//
//            override fun onExitFullscreen() {
//                isFullscreen = false
//                playerUiContainer.visibility = View.VISIBLE
//                fullscreenViewContainer.visibility = View.GONE
//                fullscreenViewContainer.removeAllViews()
//
//                if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
//                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
//                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
//                }
//            }
//        })
//
//        val youTubePlayerListener = object : AbstractYouTubePlayerListener() {
//            override fun onReady(youTubePlayer: YouTubePlayer) {
//                this@VideoActivity.youTubePlayer = youTubePlayer
//                youTubePlayer.loadVideo(videoId, 0f)
//            }
//
//            override fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, playbackRate: PlaybackRate) {
//                // Handle playback speed change
//                Toast.makeText(this@VideoActivity, "Playback speed: $playbackRate", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        val iFramePlayerOptions = IFramePlayerOptions.Builder()
//            .controls(1)
//            .fullscreen(1)
//            .build()
//
//        youTubePlayerView.enableAutomaticInitialization = false
//        youTubePlayerView.initialize(youTubePlayerListener, iFramePlayerOptions)
//    }
