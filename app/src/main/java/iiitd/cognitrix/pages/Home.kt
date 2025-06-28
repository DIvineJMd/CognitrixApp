package iiitd.cognitrix.pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.core.net.toUri
import iiitd.cognitrix.R
import iiitd.cognitrix.api.Api_data.LoginViewModel
import iiitd.cognitrix.api.Dataload.CourseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class Home {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun HomeScreen(
        context: Context,
        courseViewModel: CourseViewModel,
        navController: NavHostController,
        loginviewmodel: LoginViewModel
    ) {
        val pagerState = rememberPagerState(pageCount = { 3 })
        val coroutineScope = rememberCoroutineScope()
        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val fullName = sharedPref.getString("fullName", null)
        var isPopupVisible by rememberSaveable { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        // Back press handling variables
        var backPressedTime by remember { mutableLongStateOf(0L) }
        var showExitToast by remember { mutableStateOf(false) }

        // Handle back press
        BackHandler {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime > 2000) {
                backPressedTime = currentTime
                showExitToast = true
                Toast.makeText(context, "Press again to close Cognitrix", Toast.LENGTH_SHORT).show()
            } else {
                exitProcess(0)
            }
        }

        fun handleLogout() {
            // Clear login-related shared preferences
            sharedPref.edit { clear() }

            // Reset login state in ViewModel
            loginviewmodel.logout()

            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }

        Scaffold(
            bottomBar = {
                Column {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    BottomAppBar(
                        modifier = Modifier.height(68.dp),
                        containerColor = MaterialTheme.colorScheme.background,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            NavigationBox(
                                pagerState,
                                coroutineScope,
                                0,
                                R.drawable.home_filled,
                                R.drawable.home_outline,
                                "Home"
                            )
                            NavigationBox(
                                pagerState,
                                coroutineScope,
                                1,
                                R.drawable.leaderboard_filled,
                                R.drawable.leaderboard_outline,
                                "Rank"
                            )
                            NavigationBox(
                                pagerState,
                                coroutineScope,
                                2,
                                R.drawable.profile_filled,
                                R.drawable.profile_outline,
                                "Profile"
                            )
                        }
                    }
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text = when (pagerState.currentPage) {
                                0 -> "My learnings"
                                1 -> "Leaderboard"
                                2 -> "My Profile"
                                else -> "My learnings"
                            },
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        "https://forms.gle/Ldviece7SoKM751w7".toUri()
                                    ).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.report),
                                    contentDescription = "Report Issues",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    val intent =
                                        Intent(context, ChatWebViewActivity::class.java).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.chatbot),
                                    contentDescription = "Chat Assistant",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            IconButton(
                                onClick = { isPopupVisible = true },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.logout),
                                    contentDescription = "Logout",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(paddingValues)
            ) { page ->
                when (page) {
                    0 -> HomeScreen(
                        Modifier
                            .padding()
                            .fillMaxSize(),
                        courseViewModel,
                        navController,
                        context = context,
                    )

                    1 -> {
                        Leaderboard().LeaderboardScreen(
                            Modifier
                                .padding()
                                .fillMaxSize(),
                            courseViewModel,
                            loginviewmodel,
                            navController,
                            context = context,
                        )
                    }

                    2 -> {
                        Profile().ProfileScreen(
                            Modifier
                                .padding()
                                .fillMaxSize(),
                            context = context,
                            viewModel = loginviewmodel,
                            courseViewModel = courseViewModel
                        )
                    }
                }
            }
        }
        if (isPopupVisible) {
            ModalBottomSheet(
                onDismissRequest = {
                    isPopupVisible = false
                },
                sheetState = sheetState,
                shape = RoundedCornerShape(0.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                dragHandle = null,
                modifier = Modifier.fillMaxWidth()
            )
            {
                Text(
                    text = "Are you sure you want to logout?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 16.dp)
                )

                TextButton(
                    onClick = {
                        isPopupVisible = false
                        handleLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface, // Set background color for button
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Confirm Logout",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                TextButton(
                    onClick = {
                        isPopupVisible = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 1.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f), // Set background color for button
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Cancel",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    @Composable
    fun HomeScreen(
        modifier: Modifier = Modifier,
        courseViewModel: CourseViewModel,
        navController: NavHostController,
        context: Context
    ) {
        val dataload = courseViewModel.isLoading.observeAsState(false)
        val courses = courseViewModel.ongoingCourses.observeAsState()
        val recourses = courseViewModel.remainingCourses.observeAsState()

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Show loading when actually loading OR when data is null (initial state)
            if (dataload.value == true || (courses.value == null && recourses.value == null)) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading Courses...",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {

                // Ongoing Courses Section
                if (courses.value?.isNotEmpty() == true) {
                    item {
                        SectionHeader(
                            title = "Ongoing Courses",
                            subtitle = "Continue your learning journey"
                        )
                    }

                    items(courses.value?.size ?: 0) { index ->
                        courses.value?.get(index)?.let { data ->
                            EnhancedCourseCard(
                                courseTitle = data.title,
                                instructor = data.creator.fullName,
                                studentCount = data.numEnrolledStudents,
                                progress = data.progress?.toFloat(),
                                isEnrollable = false,
                                onClick = {
                                    val intent = Intent(context, CourseActivity::class.java).apply {
                                        putExtra("courseId", data._id)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }

                if (!recourses.value.isNullOrEmpty()) {
                    item {
                        SectionHeader(
                            title = "Available Courses",
                            subtitle = "Discover new learning opportunities"
                        )
                    }

                    items(recourses.value?.size ?: 0) { index ->
                        recourses.value?.get(index)?.let { data ->
                            EnhancedCourseCard(
                                courseTitle = data.title,
                                instructor = data.creator.fullName,
                                studentCount = data.numEnrolledStudents,
                                progress = data.progress?.toFloat(),
                                isEnrollable = true,
                                onClick = {
//                                    navController.navigate("video")
                                }
                                ,
                                onEnroll = {
                                    courseViewModel.enrollInCourse(
                                        context = context,
                                        courseId = data._id,
                                        onSuccess = {
                                            // Refresh the course lists after successful enrollment
                                            courseViewModel.fetchOngoingCourses(context)
                                            courseViewModel.fetchRemainingCourses(context)
                                            Toast.makeText(
                                                context,
                                                "Successfully enrolled in ${data.title}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onError = { errorMessage ->
                                            Toast.makeText(
                                                context,
                                                "Enrollment failed: $errorMessage",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            )
                        }
                    }
                }

                // No courses state - only show when data is loaded but empty
                if (courses.value?.isEmpty() == true && recourses.value?.isEmpty() == true) {
                    item {
                        EmptyStateMessage()
                    }
                }
            }
        }
    }

    @Composable
    fun SectionHeader(title: String, subtitle: String) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                modifier = Modifier.padding(end = 100.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        }
    }

    @Composable
    fun EmptyStateMessage() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.home_outline),
                    contentDescription = "No courses",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No courses found",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Explore available courses to start learning",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    fun EnhancedCourseCard(
        courseTitle: String,
        instructor: String,
        studentCount: Int,
        progress: Float?,
        isEnrollable: Boolean,
        onClick: () -> Unit = {},
        onEnroll: () -> Unit = {}
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
//            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Course Category Indicator
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.anuj_grover),
                            contentDescription = "Course Image",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = courseTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = instructor,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.student),
                            contentDescription = "Student Count",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$studentCount enrolled",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isEnrollable) {
                        Button(
                            onClick = onEnroll,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Enroll Now",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        progress?.let {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    val calculatedProgress = it / 100
                                    CircularProgressIndicator(
                                        progress = { calculatedProgress },
                                        modifier = Modifier.fillMaxSize(),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        strokeWidth = 5.dp,
                                        trackColor = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = "${it.toInt()}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Continue Learning",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun NavigationBox(
        pagerState: PagerState,
        coroutineScope: CoroutineScope,
        pageIndex: Int,
        iconFilledRes: Int,
        iconOutlineRes: Int,
        label: String
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pageIndex)
                }
            },
//            modifier = Modifier.padding(2.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (pagerState.currentPage == pageIndex)
                    MaterialTheme.colorScheme.surface else Color.Transparent
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = if (pagerState.currentPage == pageIndex) iconFilledRes else iconOutlineRes),
                    contentDescription = label,
                    modifier = Modifier
                        .size(28.dp)
                        .padding(start = 2.dp),
                    tint = if (pagerState.currentPage == pageIndex)
                        MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.tertiary,
                )

                AnimatedVisibility(
                    visible = pagerState.currentPage == pageIndex,
                    enter = fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) +
                            expandHorizontally(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)),
                    exit = fadeOut(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) +
                            shrinkHorizontally(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
                ) {
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 4.dp, end = 6.dp)
                    )
                }
            }
        }
    }
}
