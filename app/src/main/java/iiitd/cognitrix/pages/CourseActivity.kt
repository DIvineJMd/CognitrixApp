package iiitd.cognitrix.pages

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import iiitd.cognitrix.MainActivity.SharedViewModelHolder
import iiitd.cognitrix.api.Dataload.CourseViewModel
import iiitd.cognitrix.api.Dataload.Resource
import iiitd.cognitrix.databinding.ActivityCompleteExampleBinding
import iiitd.cognitrix.ui.theme.CognitrixTheme
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseActivity : ComponentActivity() {
    private var youTubePlayerView: YouTubePlayerView? = null
    private var youTubePlayer: YouTubePlayer? = null
    private var isFullscreen = false
    private lateinit var fullscreenViewContainer: ViewGroup
    private lateinit var binding: ActivityCompleteExampleBinding

    private var courseViewModel: CourseViewModel? = null
    private var courseId: String = ""

    // Track initialization state to prevent multiple init attempts
    private var playerInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            courseViewModel = SharedViewModelHolder.courseViewModel
            courseId = intent.getStringExtra("courseId") ?: ""

            binding = ActivityCompleteExampleBinding.inflate(layoutInflater)
            setContentView(binding.root)

            fullscreenViewContainer = binding.fullScreenViewContainer

            binding.myComposable.setContent {
                CognitrixTheme {
                    courseViewModel?.let { vm ->
                        CoursePage().CourseScreen(
                            viewModel = vm,
                            context = applicationContext,
                            courseId = courseId
                        )
                    }
                }
            }

            // Initialize YouTube Player on a delay to ensure proper view initialization
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    try {
                        youTubePlayerView = binding.youtubePlayerView
                        youTubePlayerView?.let { playerView ->
                            lifecycle.addObserver(playerView)
                            initYouTubePlayerView(playerView)
                        }
                    } catch (e: Exception) {
                        Log.e("CourseActivity", "Error initializing YouTubePlayerView: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CourseActivity", "Error in onCreate: ${e.message}")
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && !isFullscreen) {
                youTubePlayer?.toggleFullscreen()
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && isFullscreen) {
                youTubePlayer?.toggleFullscreen()
            }
        } catch (e: Exception) {
            Log.e("CourseActivity", "Error in onConfigurationChanged: ${e.message}")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        try {
            if (isFullscreen) {
                youTubePlayer?.toggleFullscreen()
            } else {
                super.onBackPressed()
            }
        } catch (e: Exception) {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        try {
            youTubePlayer?.pause()
        } catch (e: Exception) {
            Log.e("CourseActivity", "Error in onPause: ${e.message}")
        }
        super.onPause()
    }

    override fun onDestroy() {
        try {
            youTubePlayer = null
            youTubePlayerView?.release()
            youTubePlayerView = null
        } catch (e: Exception) {
            Log.e("CourseActivity", "Error in onDestroy: ${e.message}")
        }
        super.onDestroy()
    }



    @SuppressLint("RestrictedApi")
    private fun initYouTubePlayerView(playerView: YouTubePlayerView) {
        if (playerInitialized) return
        playerInitialized = true

        try {
            // Configure fullscreen behavior
            playerView.addFullscreenListener(object : FullscreenListener {
                override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                    try {
                        isFullscreen = true
                        binding.myComposable.visibility = View.GONE

                        // Don't hide the original player view to prevent WebView recreation
                        // binding.youtubePlayerView.visibility = View.GONE

                        fullscreenViewContainer.visibility = View.VISIBLE
                        fullscreenViewContainer.addView(fullscreenView)

                        // Set landscape orientation but do it safely
                        try {
                            if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            }
                        } catch (e: Exception) {
                            Log.e("CourseActivity", "Error setting orientation: ${e.message}")
                        }
                    } catch (e: Exception) {
                        Log.e("CourseActivity", "Error entering fullscreen: ${e.message}")
                    }
                }

                override fun onExitFullscreen() {
                    try {
                        isFullscreen = false
                        binding.myComposable.visibility = View.VISIBLE

                        fullscreenViewContainer.removeAllViews()
                        fullscreenViewContainer.visibility = View.GONE

                        try {
                            if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
                                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                            }
                        } catch (e: Exception) {
                            Log.e("CourseActivity", "Error setting orientation: ${e.message}")
                        }
                    } catch (e: Exception) {
                        Log.e("CourseActivity", "Error exiting fullscreen: ${e.message}")
                    }
                }
            })

            val playerOptions = IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1)
                .build()

            playerView.enableAutomaticInitialization = false
            playerView.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(player: YouTubePlayer) {
                    youTubePlayer = player

                    loadCurrentVideo()

                    courseViewModel?.videoDetails?.observe(this@CourseActivity) { resource ->
                        try {
                            if (resource is Resource.Success) {
                                val videoId = extractVideoId(resource.data.url)
                                if (videoId.isNotEmpty()) {
                                    player.loadVideo(videoId, 0f)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("CourseActivity", "Error in video observer: ${e.message}")
                        }
                    }
                }
            }, playerOptions)
        } catch (e: Exception) {
            Log.e("CourseActivity", "Error initializing YouTube player: ${e.message}")
        }
    }

    private fun loadCurrentVideo() {
        try {
            val currentResource = courseViewModel?.videoDetails?.value
            if (currentResource is Resource.Success) {
                val videoId = extractVideoId(currentResource.data.url)
                if (videoId.isNotEmpty()) {
                    youTubePlayer?.loadVideo(videoId, 0f)
                }
            }
        } catch (e: Exception) {
            Log.e("CourseActivity", "Error loading video: ${e.message}")
        }
    }

    private fun extractVideoId(url: String): String {
        return try {
            when {
                url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
                url.contains("youtube.com/watch?v=") -> url.substringAfter("v=").substringBefore("&")
                else -> ""
            }
        } catch (e: Exception) {
            Log.e("CourseActivity", "Error extracting video ID: ${e.message}")
            ""
        }
    }
}
