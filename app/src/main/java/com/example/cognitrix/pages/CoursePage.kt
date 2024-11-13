package com.example.cognitrix.pages

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.cognitrix.R
import com.example.cognitrix.api.Dataload.CourseViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch

class CoursePage {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun CourseScreen(

    ) {
        val pagerState = rememberPagerState(pageCount = { 5 })
        val coroutineScope = rememberCoroutineScope()
//        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
//        val fullName = sharedPref.getString("fullName", null)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier.padding(horizontal = 2.dp),
                            text = "Digital VLSI & Memory Design",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF37ADA6),
                        titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = {

                        }) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* List click */ }) {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = "Bell",
                                tint = Color.White
                            )
                        }

                    }
                )
            }
        ) {
            val videoId = "E_8LHkn4g-Q"
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)) {
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
                                override fun onStateChanged(
                                    source: LifecycleOwner,
                                    event: Lifecycle.Event
                                ) {
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp)
                        .align(Alignment.Start)
                        .clip(
                            RoundedCornerShape(5.dp)
                        )
                )
                Text(text = "SoC Design Steps - Design Implementation", modifier = Modifier.padding(5.dp), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37ADA6)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    onClick = {}
                ) {
                    Text(text = "Next Video")
                }
                // LazyRow for tabs
                val tabs = listOf("Description", "Recommendations","My Notes" , "Shared Notes")
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(tabs.size) { index ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        ) {
                            // Tab text
                            Text(
                                text = tabs[index],
                                color = if (pagerState.currentPage == index) Color.Black else Color.DarkGray,
                                fontSize = 14.sp
                            )

                            // Line below the selected tab
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .height(4.dp)
                                    .requiredWidth(60.dp)
                                    .background(
                                        color = if (pagerState.currentPage == index) Color(0xFF37ADA6) else Color.Transparent,
                                        shape = RoundedCornerShape(2.dp) // Optional: adds rounded corners to the underline
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // HorizontalPager for content
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> Text("Description Content", modifier = Modifier.padding(16.dp))
                        1 -> Text("Recommendations Content", modifier = Modifier.padding(16.dp))
                        2 -> Text("My Notes Content", modifier = Modifier.padding(16.dp))
                        3 -> Text("Additional Recommendations Content", modifier = Modifier.padding(16.dp))
                        4 -> Text("Shared Notes Content", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
            }

    @Preview
    @Composable
    fun CourseScreenPreview() {
        CourseScreen()
    }
}