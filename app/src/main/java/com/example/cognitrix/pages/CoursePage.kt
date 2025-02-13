package com.example.cognitrix.pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cognitrix.api.Dataload.CourseDetailsResponse
import com.example.cognitrix.api.Dataload.CourseViewModel
import com.example.cognitrix.api.Dataload.RecommendationVideo
import com.example.cognitrix.api.Dataload.Resource
import com.example.cognitrix.api.Dataload.VideoDetail
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.Typography
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.cognitrix.ui.theme.Turquoise

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
        val screenWidth = configuration.screenWidthDp.dp

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
                                text = "Course",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Turquoise,
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
        ) { paddingValues ->
            val videoData by viewModel.videoDetails.observeAsState(Resource.Loading())
            val youTubePlayer = remember { mutableStateOf<YouTubePlayer?>(null) }
            val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
            var videoid by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                when (videoData) {
                    is Resource.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 50.dp)
                        )
                    }

                    is Resource.Error -> {
                        Text(text = "Error: ${(videoData as Resource.Error<VideoDetail>).message}")
                    }

                    is Resource.Success -> {
                        val data = (videoData as Resource.Success<VideoDetail>).data
                        videoid = data.url.substringAfter("youtu.be/")

                        // Video Player Container
                        Box (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (isLandscape) 400.dp else 250.dp)
                                .padding(horizontal = if (isLandscape) 32.dp else 16.dp)
                        ) {
                            AndroidView(
                                factory = { context ->
                                    YouTubePlayerView(context).apply {
                                        enableAutomaticInitialization = false
                                        addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                                youTubePlayer.loadVideo(videoid, 5f)
                                            }
                                        })
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
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
//                        Spacer(modifier = Modifier.height(96.dp))

                        // Video Title and Next Button Container
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = if (isLandscape) 32.dp else 16.dp)
                        ) {
                            Text(
                                text = data.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = Turquoise),
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    data.nextVideo?.let { nextVideo ->
                                        viewModel.fetchVideoDetails(context, nextVideo.id)
                                    }
                                }
                            ) {
                                Text(text = "Next Video")
                            }
                        }

                        // Tabs Container
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            val tabs = listOf("Description", "Lectures", "Recommendations", "My Notes", "Shared Notes")

                            ScrollableTabRow(
                                selectedTabIndex = pagerState.currentPage,
                                contentColor = Color.Gray,
                                edgePadding = if (isLandscape) 32.dp else 16.dp,
                                modifier = Modifier.fillMaxWidth(),
                                indicator = { tabPositions ->
                                    SecondaryIndicator(
                                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                        color = Color.Black
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
                                        },
                                        modifier = Modifier.width(screenWidth / 3) // This makes tabs take up 1/3 of screen width
                                    ) {
                                        Text(
                                            text = tab,
                                            color = if (pagerState.currentPage == index) Turquoise else Color.DarkGray,
                                            fontSize = 16.sp,
                                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            // Tab Content Container
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxSize()
                                ) { page ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = if (isLandscape) 32.dp else 16.dp)
                                    ) {
                                        when (page) {
                                            0 -> Text(
                                                text = data.description,
                                                modifier = Modifier.padding(vertical = 16.dp),
                                                overflow = TextOverflow.Clip,
                                            )
                                            1 -> Lecture(courseData, onVideoSelected = {
                                                viewModel.fetchVideoDetails(context, it)
                                            })
                                            2 -> RecommendationScreen(
                                                viewModel = viewModel,
                                                videoId = data.id,
                                                context = context,
                                                modifier = Modifier.fillMaxSize(),
                                                onVideoSelected = { url ->
                                                    viewModel.fetchVideoDetails(context, url)
                                                }
                                            )
                                            3 -> Text("To be Implemented")
                                            4 -> Text("Shared Notes Content")
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
                            .fillMaxSize()
                            .heightIn(max = 500.dp) // Limit the height
                    ) {
                        videos.forEach { (lectureNumber, videoList) ->
                            item {
                                var expanded by remember { mutableStateOf(false) }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Lecture $lectureNumber",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color =Color.Black,
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
                                                            color =Color.Black,
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
        val lazyState = rememberLazyGridState()
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val shouldLoadMore = remember {
            derivedStateOf {
                val lastVisibleItemIndex = lazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                lastVisibleItemIndex >= relatedVideos.size - 1
            }
        }

        LaunchedEffect(videoId) {
            viewModel.loadRecommendations(videoId, context)
        }

        LaunchedEffect(shouldLoadMore.value) {
            if (shouldLoadMore.value && !isLoading) {
                viewModel.loadRecommendations(videoId, context)
            }
        }

        Box(modifier = modifier) {
            if (relatedVideos.isEmpty() && !isLoading) {
                Text(
                    text = "No recommendations available",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (isLandscape) 3 else 2),
                    state = lazyState,
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(relatedVideos) { video ->
                        VideoItem(video, context, onVideoSelected)
                    }

                    if (isLoading) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentSize()
                            )
                        }
                    }
                }
            }
        }
    }
    @Composable
    fun VideoItem(
        video: RecommendationVideo,
        context: Context,
        onVideoSelected: (String) -> Unit
    ) {
        // Extract videoId more robustly
        val videoId = extractVideoId(video.url)
        val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"
        println(thumbnailUrl)

        // Card for the entire video item
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onVideoSelected(video._id) },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            // Column layout inside the Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Add space between elements
            ) {
                // Display the video thumbnail
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Thumbnail for ${video.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(6f / 5f)
                )


                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    modifier = Modifier,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (video.watched) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                    )
                ) {
                    Text(
                        text = if (video.watched) "Watched" else "Not Watched",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
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



