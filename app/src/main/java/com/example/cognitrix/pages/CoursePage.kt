package com.example.cognitrix.pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.cognitrix.api.Dataload.CourseViewModel
import com.example.cognitrix.api.Dataload.Resource
import com.example.cognitrix.api.Dataload.VideoDetail
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch

class CoursePage {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun CourseScreen(viewModel: CourseViewModel, context: Context, courseId: String) {
        val pagerState = rememberPagerState(pageCount = { 5 })
        val coroutineScope = rememberCoroutineScope()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val configuration = LocalConfiguration.current
        val courseData by  viewModel.courseDetails.observeAsState(Resource.Loading())
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        LaunchedEffect(Unit) {
            viewModel.fetchCourseDetails(context = context, courseId)
        }
        Scaffold(
            topBar = {
                if (!isLandscape) {
                    TopAppBar(
                        title = {
                            Text(
                                modifier = Modifier.padding(horizontal = 2.dp),
                                text = "Digital VLSI & Memory Design",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF37ADA6),
                            titleContentColor = Color.White
                        ),
                        navigationIcon = {
                            IconButton(onClick = {}) {
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
            }
        ) {
            val videoData by viewModel.videoDetails.observeAsState(Resource.Loading())
            val youTubePlayer = remember { mutableStateOf<YouTubePlayer?>(null) }
            val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
//            val screenHeightPx = configuration.screenHeightDp

            // Convert to dp using LocalDensity
//            val density = LocalDensity.current
//            val screenHeightDp = with(density) { screenHeightPx.dp }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
            ) {
                when(videoData){
                    is Resource.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                    }

                    is Resource.Error -> {
                        Text(text = "Error: ${(videoData as Resource.Error<VideoDetail>).message}")
                    }
                    is Resource.Success -> {
                        val data = (videoData as Resource.Success<VideoDetail>).data
                        AndroidView(
                            factory = { context ->
                                YouTubePlayerView(context).apply {
                                    enableAutomaticInitialization = false

                                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                        override fun onReady(initializedYouTubePlayer: YouTubePlayer) {
                                            youTubePlayer.value = initializedYouTubePlayer
                                            println(data.url.substringAfter("/"))
                                            initializedYouTubePlayer.loadVideo(data.url.substringAfter("/youtu.be/"), 0f)
                                        }
                                    })

                                    lifecycleOwner.lifecycle.addObserver(object :
                                        LifecycleEventObserver {
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
                                .height(if (isLandscape) 275.dp else 200.dp)
                                .padding(0.dp)
                                .clip(RoundedCornerShape(5.dp))
                        )

                        if (!isLandscape) {
                            Text(
                                text =data.title,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 15.dp),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37ADA6)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                onClick = {
                                    viewModel.fetchVideoDetails(context, data.nextVideo!!.id)
                                }
                            ) {
                                Text(text = "Next Video")
                            }

                            val tabs =
                                listOf("Description", "Recommendations", "My Notes", "Shared Notes")
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
                                                    color = if (pagerState.currentPage == index) Color(
                                                        0xFF37ADA6
                                                    ) else Color.Transparent,
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
                                    0 -> Text(data.description, modifier = Modifier.padding(16.dp))
                                    1 -> Text(
                                        "Recommendations Content",
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    2 -> Text("My Notes Content", modifier = Modifier.padding(16.dp))
                                    3 -> Text(
                                        "Additional Recommendations Content",
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    4 -> Text(
                                        "Shared Notes Content",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }


            }
        }

    }

//    @Preview
//    @Composable
//    fun CourseScreenPreview() {
//        CourseScreen()
//    }
}



