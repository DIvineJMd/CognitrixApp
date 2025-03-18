package com.example.cognitrix.pages

import LoginViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.Theme
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import com.example.cognitrix.R
import com.example.cognitrix.api.Dataload.CourseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Home {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun HomeScreen(
        context: Context, courseViewModel: CourseViewModel, navController: NavHostController,loginviewmodel: LoginViewModel
    ) {
        val pagerState = rememberPagerState(pageCount = { 3 })
        val coroutineScope = rememberCoroutineScope()
        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val fullName = sharedPref.getString("fullName", null)
        var isPopupVisible by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        fun handleLogout() {
            // Clear login-related shared preferences
            sharedPref.edit().clear().apply()
            
            // Reset login state in ViewModel if applicable
//            loginviewmodel.logout()
            
            // Navigate back to login screen
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }

        Scaffold(
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.tertiary, thickness = 1.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            NavigationBox(pagerState, coroutineScope, 0, R.drawable.home_filled, R.drawable.home_outline, "Home")
                            NavigationBox(pagerState, coroutineScope, 1, R.drawable.leaderboard_filled, R.drawable.leaderboard_outline, "Leaderboard")
                            NavigationBox(pagerState, coroutineScope, 2, R.drawable.profile_filled, R.drawable.profile_outline, "Profile")
                        }
                    }
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxHeight(), // Take full height to center vertically
                            contentAlignment = Alignment.CenterStart // Center content vertically
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 2.dp),
                                text = "Hello $fullName!",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.height(88.dp),
                    actions = {
                        Box(
                            modifier = Modifier.fillMaxHeight(), // Take full height for vertical centering
                            contentAlignment = Alignment.CenterEnd // Align icons to the end and center vertically
                        ) {
                            IconButton(onClick = { /* Bell click */ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.bell),
                                    contentDescription = "Notification",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxHeight(), // Take full height for vertical centering
                            contentAlignment = Alignment.CenterEnd // Align icons to the end and center vertically
                        ) {
                            IconButton(onClick = { isPopupVisible = true }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.logout),
                                    contentDescription = "Logout",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(28.dp)
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
                        Leaderboard.LeaderboardScreen(
                            Modifier
                                .padding()
                                .fillMaxSize(),
                            courseViewModel,
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
                            viewModel = loginviewmodel
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
                shape= RoundedCornerShape(0.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                dragHandle = null,
                modifier = Modifier.fillMaxWidth()
            )
            {
                Text(
                    text = "Are you sure you want to logout?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(top=16.dp, bottom = 16.dp, start = 16.dp)
                )
                
                TextButton(
                    onClick = {
                        isPopupVisible = false
                        handleLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical= 2.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface, // Set background color for button
                        contentColor = MaterialTheme.colorScheme.onSurface // Set text color for button
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm Logout", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium, fontSize = 18.sp)
                }

                TextButton(
                    onClick = {
                        isPopupVisible = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical= 1.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary, // Set background color for button
                        contentColor = MaterialTheme.colorScheme.primary // Set text color for button
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium, fontSize = 18.sp)
                }
            }
        }
    }

    @Composable
    fun HomeScreen(
        modifier: Modifier,
        courseViewModel: CourseViewModel,
        navController: NavHostController,
        context: Context
    ) {
        Column(modifier = modifier.padding(8.dp)) {
            val dataload = courseViewModel.isLoading.observeAsState()
            val courses = courseViewModel.ongoingCourses.observeAsState()
            val recourses = courseViewModel.remainingCourses.observeAsState()
            val allCourseDataclass = courseViewModel.courses.observeAsState()

            if (dataload.value == true) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(vertical = 20.dp)
                )
            } else {
                if (courses.value?.isNotEmpty() == true) {
                    Text(
                        text = "Ongoing Courses",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
                courses.value?.let { ongoingCourses ->
                    ongoingCourses.forEach { data ->
                        CourseCard(
                            courseTitle = data.title,
                            onClick = {
                                navController.navigate("Lecture/${data._id}")
                            },
                            instructor = data.creator.fullName,
                            studentCount = data.numEnrolledStudents,
                            progress = data.progress?.toFloat(),
                            enroll = false
                        )
                    }
                }
                if (!recourses.value.isNullOrEmpty()) {
                    Text(
                        text = "Available Courses ",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                recourses.value?.let { remainCourse ->
                    remainCourse.forEach { data ->
                        CourseCard(
                            courseTitle = data.title,
                            instructor = data.creator.fullName,
                            studentCount = data.numEnrolledStudents,
                            progress = data.progress?.toFloat(),
                            enroll = true,
                            onClick = { navController.navigate("video") },
                            Onenroll = {
                                courseViewModel.enrollInCourse(context, data._id)
                                courseViewModel.fetchOngoingCourses(context)
                                courseViewModel.fetchRemainingCourses(context)

                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun CourseCard(
        courseTitle: String,
        instructor: String,
        studentCount: Int,
        progress: Float?,
        enroll: Boolean?,
        onClick: () -> Unit = {},
        Onenroll: () -> Unit = {}
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.anuj_grover),
                        contentDescription = "Instructor Avatar",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = courseTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = instructor,
                            color = MaterialTheme.colorScheme.primary,
                            style =  MaterialTheme.typography.titleSmall
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.student),
                                contentDescription = "Student Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = " $studentCount",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }

                    Box(contentAlignment = Alignment.Center) {
                        if (enroll == true) {
                            Button(
                                onClick = {
                                    Onenroll()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Enrol",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                        progress?.let {
                            CircularProgressIndicator(
                                progress = { it / 100 },
                                trackColor = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(60.dp),
                                color = MaterialTheme.colorScheme.surface,
                                strokeWidth = 8.dp,

                                )
                            Text(
                                text = "${String.format("%.1f", it)}%",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge
                            )
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
        Box(
            modifier = Modifier
                .clickable {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pageIndex)
                    }
                }
                .padding(4.dp)
                .background(
                    color = if (pagerState.currentPage == pageIndex) MaterialTheme.colorScheme.surface else Color.Transparent,
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = if (pagerState.currentPage == pageIndex) iconFilledRes else iconOutlineRes),
                    contentDescription = label,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(start = 8.dp, top = 6.dp, bottom = 6.dp),
                    tint = if (pagerState.currentPage == pageIndex) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.tertiary,
                )
                if (pagerState.currentPage == pageIndex) {
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
//                        fontSize = 18.sp,
                        modifier = Modifier.padding(top=6.dp, bottom =6.dp, end = 16.dp)
                    )
                }
            }
        }
    }
}

