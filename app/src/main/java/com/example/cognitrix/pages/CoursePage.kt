package com.example.cognitrix.pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest


import com.example.cognitrix.R
import com.example.cognitrix.api.Dataload.CourseDetailsResponse
import com.example.cognitrix.api.Dataload.CourseViewModel
import com.example.cognitrix.api.Dataload.RecommendationVideo
import com.example.cognitrix.api.Dataload.Resource
import com.example.cognitrix.api.Dataload.VideoDetail
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class CoursePage {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun CourseScreen(viewModel: CourseViewModel, context: Context, courseId: String) {
        val pagerState = rememberPagerState(pageCount = { 5 })
        val coroutineScope = rememberCoroutineScope()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val configuration = LocalConfiguration.current
        val courseData by viewModel.courseDetails.observeAsState(Resource.Loading())
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
            var videoid by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                when (videoData) {
                    is Resource.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top=50.dp))
                    }

                    is Resource.Error -> {
                        Text(text = "Error: ${(videoData as Resource.Error<VideoDetail>).message}")
                    }

                    is Resource.Success -> {
                        val data = (videoData as Resource.Success<VideoDetail>).data
                        videoid = data.url.substringAfter("youtu.be/")
                        AndroidView(
                            factory = { context ->
                                YouTubePlayerView(context).apply {
                                    enableAutomaticInitialization = false

                                    addYouTubePlayerListener(object :
                                        AbstractYouTubePlayerListener() {
                                        override fun onReady(initializedYouTubePlayer: YouTubePlayer) {
                                            youTubePlayer.value = initializedYouTubePlayer
                                            initializedYouTubePlayer.loadVideo(
                                                videoid, 0f
                                            )
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
                                .padding(top=25.dp)
                                .clip(RoundedCornerShape(5.dp))
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (!isLandscape) {
                            Text(
                                text = data.title,
                                modifier = Modifier.padding(top = 30.dp, start = 5.dp),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF37ADA6
                                    )
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                onClick = {
                                    (videoData as Resource.Success<VideoDetail>).data.nextVideo?.let { it1 ->
                                        viewModel.fetchVideoDetails(
                                            context,
                                            it1.id
                                        )
                                    }
                                }
                            ) {
                                Text(text = "Next Video")
                            }

                            val tabs =
                                listOf(
                                    "Description",
                                    "Lectures",
                                    "Recommendations",
                                    "My Notes",
                                    "Shared Notes"
                                )

                            ScrollableTabRow(
                                selectedTabIndex = pagerState.currentPage,
                                edgePadding = 16.dp,
                                contentColor = Color.Gray,
                                indicator = { tabPositions ->
                                    TabRowDefaults.Indicator(
                                        color = Color.Black,
                                        modifier = Modifier
                                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                            .fillMaxWidth()
                                    )
                                }
                            ) {
                                tabs.forEachIndexed { index, tab ->
                                    Tab(
                                        selected = pagerState.currentPage == index,
                                        onClick = {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        }
                                    ) {
                                        // Tab text
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(10.dp)
                                        ) {
                                            Text(
                                                text = tab,
                                                color = if (pagerState.currentPage == index) Color(0xFF37ADA6) else Color.DarkGray,
                                                fontSize = 14.sp,
                                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                                            )

                                        }
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
                                    1 -> {
                                        Lecture(courseData, onVideoSelected = {
                                            viewModel.fetchVideoDetails(context, it)
                                        })
                                    }

                                    2 -> {
                                        RecommendationScreen(
                                            viewModel,
                                            data.id,
                                            context,
                                            onVideoSelected = { url ->
                                                viewModel.fetchVideoDetails(context, url)
                                            })
                                    }

                                    3 -> Text(
                                        "My Notes Content",
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    4 -> Text(
                                        "Additional Recommendations Content",
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    5 -> Text(
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

    @Composable
    fun Lecture(courseData: Resource<CourseDetailsResponse?>, onVideoSelected: (String) -> Unit) {
        when (courseData) {
            is Resource.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                )
            }

            is Resource.Error -> {
                Text(
                    "Error loading course data",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                )
            }

            is Resource.Success -> {
                val videos = (courseData as Resource.Success<CourseDetailsResponse?>).data?.videos

                if (videos.isNullOrEmpty()) {
                    Text(
                        "No lectures available",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 500.dp) // Limit the height
                    ) {
                        videos.forEach { (lectureNumber, videoList) ->
                            item {
                                var expanded by remember { mutableStateOf(false) }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp),
//                                    colors = CardDefaults.cardColors(
//                                        containerColor = Color(0xFF37ADA6)
//                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Lecture $lectureNumber",
                                            style = MaterialTheme.typography.labelLarge,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { expanded = !expanded }
                                                .padding(16.dp)
                                        )

                                        AnimatedVisibility(visible = expanded) {
                                            Column {
                                                videoList.forEach { video ->
                                                    Card(
                                                        Modifier
                                                            .padding(5.dp)
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                onVideoSelected(video.id)
                                                            },

                                                        ) {
                                                        Text(
                                                            text = "${video.videoNumber}. ${video.title} - ${video.duration}",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            modifier = Modifier.padding(
                                                                start = 20.dp,
                                                                top = 4.dp,
                                                                bottom = 4.dp
                                                            )
                                                        )
                                                    }
                                                    HorizontalLine()
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HorizontalLine() {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp), // Optional: add padding around the line
            color = Color.LightGray,
            thickness = 1.dp
        )
    }

    @Composable
    fun RecommendationScreen(
        viewModel: CourseViewModel,
        videoId: String,
        context: Context,
        modifier: Modifier = Modifier,
        onVideoSelected: (String) -> Unit
    ) {
        val relatedVideos by viewModel.relatedVideos.observeAsState(emptyList())
        val isLoading by viewModel.isLoading.observeAsState(false)

        // Remembering LazyListState
        val listState = rememberLazyListState()

        // Pagination logic
        val shouldLoadMore = remember {
            derivedStateOf {
                val lastVisibleItemIndex =
                    listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                lastVisibleItemIndex >= relatedVideos.size - 1
            }
        }

        // Load initial recommendations
        LaunchedEffect(videoId) {
            viewModel.loadRecommendations(videoId, context)
        }

        // Load more when reaching end of list
        LaunchedEffect(shouldLoadMore.value) {
            if (shouldLoadMore.value && !isLoading) {
                viewModel.loadRecommendations(videoId, context)
            }
        }

        // Use Box with explicit height constraints
        Box(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = 600.dp)  // Set explicit height constraints
        ) {
            if (relatedVideos.isEmpty() && !isLoading) {
                Text(
                    text = "No recommendations available",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Video items
                    items(relatedVideos) { video ->
                        VideoItem(video, context, onVideoSelected)
                    }

                    // Loading indicator at the end
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun VideoItem(video: RecommendationVideo, context: Context, onVideoSelected: (String) -> Unit) {
        // Extract videoId more robustly
        val videoId = extractVideoId(video.url)
        val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"
        println(thumbnailUrl)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onVideoSelected(video._id) },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//            colors = CardDefaults.cardColors(
//                containerColor = Color(0xFFC2C2C2)
//            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Use a nested Composable for loading state
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Thumbnail for ${video.title}",
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(3f / 2f)
                            .clip(RoundedCornerShape(25.dp)) // Apply rounded corners
                    )




                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

            }
        }
    }


    // Utility function to extract video ID more robustly
    fun extractVideoId(url: String): String {
        return when {
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").takeWhile { it != '?' }
            url.contains("youtube.com/watch?v=") -> url.substringAfter("v=").takeWhile { it != '&' }
            url.contains("youtube.com/embed/") -> url.substringAfter("embed/")
                .takeWhile { it != '?' }

            else -> url // Fallback to original URL if no match
        }
    }
//    @Preview
//    @Composable
//    fun CourseScreenPreview() {
//        CourseScreen()
//    }
}



