package com.example.cognitrix.pages

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import okhttp3.internal.wait


class CoursePage {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun CourseScreen(
        viewModel: CourseViewModel,
        context: Context,
        courseId: String,
//        navController: NavController
    ) {
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
            modifier = Modifier.fillMaxSize()
            //            topBar = {
//                if (!isLandscape) {
//                    TopAppBar(
//                        title = {
//                            Text(
//                                modifier = Modifier.padding(horizontal = 2.dp),
//                                text = "Course",
//                                fontWeight = FontWeight.Bold
//                            )
//                        },
//                        scrollBehavior = scrollBehavior,
//                        colors = TopAppBarDefaults.topAppBarColors(
//                            containerColor = MaterialTheme.colorScheme.surface,
//                            titleContentColor = MaterialTheme.colorScheme.onSurface
//                        ),
//                        navigationIcon = {
//                            IconButton(onClick = { navController.navigateUp() }) {
//                                Icon(
//                                    Icons.Default.Home,
//                                    contentDescription = "Home",
//                                    tint = MaterialTheme.colorScheme.onSurface
//                                )
//                            }
//                        },
//                        actions = {
//                            IconButton(onClick = { /* List click */ }) {
//                                Icon(
//                                    Icons.AutoMirrored.Filled.List,
//                                    contentDescription = "Bell",
//                                    tint = MaterialTheme.colorScheme.onSurface
//                                )
//                            }
//                        }
//                    )
//                }
//            }
        ) { paddingValues ->
            val videoData by viewModel.videoDetails.observeAsState(Resource.Loading())
//            var videoid by remember { mutableStateOf("") }
//            var player: YouTubePlayer? = null
//            val activity = LocalContext.current as Activity
            var isFullscreen by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .nestedScroll(rememberNestedScrollInteropConnection())
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
//                        videoid = data.url.substringAfter("youtu.be/")
//                        YouTubePlayerScreen(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(250.dp),
//                            videoId = videoid
//                        )

                        if (!isFullscreen) {
                            Log.d("Fullscreen", "Not Fullscreen : ${!isFullscreen}")
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
//                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp, bottom = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    onClick = {
                                        data.nextVideo?.let { nextVideo ->
                                            viewModel.fetchVideoDetails(context, nextVideo.id)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "Next Video",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }

                            // Tabs Container
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            ) {
                                val tabs = listOf(
                                    "Description",
                                    "Lectures",
                                    "Recommendations",
                                    "My Notes",
                                    "Shared Notes"
                                )

                                ScrollableTabRow(
                                    selectedTabIndex = pagerState.currentPage,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    edgePadding = if (isLandscape) 32.dp else 16.dp,
                                    modifier = Modifier.fillMaxWidth(),
                                    indicator = { tabPositions ->
                                        SecondaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                            modifier = Modifier.width(screenWidth / 3)
                                        ) {
                                            Text(
                                                text = tab,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(vertical = 12.dp),
                                                maxLines = 1,
                                                overflow = TextOverflow.Visible,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
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
                                                0 -> {
                                                    Text(
                                                        text = "In this video, the following topics have been discussed: ${data.description}",
                                                        modifier = Modifier.padding(vertical = 16.dp),
                                                        overflow = TextOverflow.Visible,
                                                    )
                                                }

                                                1 -> {
                                                    Box(modifier = Modifier.fillMaxSize())
                                                    {
                                                        Lecture(courseData, onVideoSelected = {
                                                            viewModel.fetchVideoDetails(context, it)
                                                        }, viewModel, context)
                                                    }
                                                }


                                                2 -> RecommendationScreen(
                                                    viewModel = viewModel,
                                                    videoId = data.id,
                                                    context = context,
                                                    modifier = Modifier.fillMaxSize(),
                                                    onVideoSelected = { url ->
                                                        viewModel.fetchVideoDetails(context, url)
                                                        viewModel.markWatched(
                                                            context,
                                                            url,
                                                            onSuccess = {})
                                                    }
                                                )

                                                3 -> {
                                                    NotesScreen(
                                                        viewModel = viewModel,
                                                        context = context,
                                                        videoId = data.id
                                                    )
                                                }

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
    }


    @SuppressLint("RememberReturnType")
    @Composable
    fun Lecture(
        courseData: Resource<CourseDetailsResponse?>,
        onVideoSelected: (String) -> Unit,
        viewModel: CourseViewModel,
        context: Context,
    ) {
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
                val videos = courseData.data?.videos

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

                    )
                    {
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
                                            color = Color.Black,
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
                                                                if (video.watched.not()) {
                                                                    viewModel.markWatched(
                                                                        context,
                                                                        video.id,
                                                                        onSuccess = {
                                                                            video.watched = true
                                                                        }
                                                                    )
                                                                }
                                                                onVideoSelected(video.id)
                                                            },

                                                        ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Start
                                                        ) {
                                                            var check by remember {
                                                                mutableStateOf(
                                                                    video.watched
                                                                )
                                                            }

                                                            Checkbox(
                                                                checked = check,
                                                                onCheckedChange = { isChecked ->
                                                                    check = isChecked
                                                                    if (isChecked) {
                                                                        viewModel.markWatched(
                                                                            context,
                                                                            video.id,
                                                                            onSuccess = {
                                                                                video.watched = true
                                                                            })
                                                                    } else {
                                                                        viewModel.unmarkWatched(
                                                                            context,
                                                                            video.id,
                                                                            onSuccess = {
                                                                                video.watched =
                                                                                    false
                                                                            })
                                                                    }
                                                                }
                                                            )

                                                            Text(
                                                                text = "${video.videoNumber}. ${video.title} - ${video.duration}",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                color = Color.Black,
                                                                modifier = Modifier.padding(
                                                                    start = 20.dp,
                                                                    top = 4.dp,
                                                                    bottom = 4.dp
                                                                )
                                                            )
                                                        }

                                                    }
                                                    HorizontalDivider(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 3.dp),
                                                        thickness = 1.dp,
                                                        color = Color.LightGray
                                                    )
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
    fun RecommendationScreen(
        viewModel: CourseViewModel,
        lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
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
                val lastVisibleItemIndex =
                    lazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                lastVisibleItemIndex >= relatedVideos.size - 1
            }
        }

        LaunchedEffect(videoId) {
            Log.d("Fetchingdat", "first time")
            viewModel.loadRecommendations(videoId, context, true)
        }
        DisposableEffect(lifecycleOwner) {
            onDispose {
                viewModel.reloadRecommendation()
            }
        }
        LaunchedEffect(shouldLoadMore.value) {
            Log.d("Fetchingdat", "pagination time")

            if (shouldLoadMore.value && !isLoading) {
                viewModel.loadRecommendations(videoId, context, false)
            }
        }


        Box(modifier = modifier.fillMaxSize()) {
            if (relatedVideos.isEmpty() && !isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No recommendations available",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Try watching more videos to get personalized recommendations",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (isLandscape) 3 else 2),
                    state = lazyState,
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(relatedVideos.size) { index ->
                        VideoItem(relatedVideos[index], context, onVideoSelected)
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
        val videoId = extractVideoId(video.url)
        val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"

        val cardHeight = 280.dp
        val thumbnailHeight = 140.dp

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .clickable { onVideoSelected(video._id) },
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(thumbnailHeight)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Thumbnail for ${video.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (video.watched) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Watched",
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(16.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .height(24.dp)
                            .background(
                                color = if (video.watched)
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                                else
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (video.watched) "Watched" else "Unwatched",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    fun extractVideoId(url: String): String {
        return when {
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").takeWhile { it != '?' }
            url.contains("youtube.com/watch?v=") -> url.substringAfter("v=").takeWhile { it != '&' }
            url.contains("youtube.com/embed/") -> url.substringAfter("embed/")
                .takeWhile { it != '?' }

            else -> url
        }
    }
}


