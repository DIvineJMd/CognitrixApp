package com.example.cognitrix.pages

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cognitrix.R
import com.example.cognitrix.api.Dataload.CourseViewModel
import kotlinx.coroutines.launch

class Home {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun HomeScreen(
        context: Context, courseViewModel: CourseViewModel, navController: NavHostController
    ) {
        val pagerState = rememberPagerState(pageCount = { 3 })
        val coroutineScope = rememberCoroutineScope()
        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val fullName = sharedPref.getString("fullName", null)

        Scaffold(
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentColor = Color.Black,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    0
                                )
                            }
                        }) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                tint = if (pagerState.currentPage == 0) Color(0xFF37ADA6) else Color(
                                    0xFF616161
                                ),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        IconButton(onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    1
                                )
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.podium),
                                contentDescription = "List",
                                tint = if (pagerState.currentPage == 1) Color(0xFF37ADA6) else Color(
                                    0xFF616161
                                ),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        IconButton(onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    2
                                )
                            }
                        }) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = if (pagerState.currentPage == 2) Color(0xFF37ADA6) else Color(
                                    0xFF616161
                                ),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier.padding(horizontal = 2.dp),
                            text = "Hello! $fullName",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF37ADA6),
                        titleContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = { /* Bell click */ }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Bell",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { /* Logout click */ }) {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                painter = painterResource(id = R.drawable.exit),
                                contentDescription = "logout",
                                tint = Color.White
                            )
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
                        navController
                    )

                    1 -> {
                        Text(text = "leader")
                    }

                    2 -> {
                        Text(text = "profile")
                    }
                }
            }
        }
    }

    @Composable
    fun HomeScreen(
        modifier: Modifier,
        courseViewModel: CourseViewModel,
        navController: NavHostController
    ) {
        Column(modifier = modifier) {
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
                        text = "OnGoing Courses",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
                courses.value?.let { ongoingCourses ->
                    ongoingCourses.forEach { data ->
                        CourseCard(
                            courseTitle = data.title,
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
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
                recourses.value?.let { remainCourse ->
                    remainCourse.forEach { data ->
                        CourseCard(
                            courseTitle = data.title,
                            instructor = data.creator.fullName,
                            studentCount = data.numEnrolledStudents,
                            progress = data.progress?.toFloat(),
                            enroll = false,
                            onClick = { navController.navigate("video") }
                        )
                    }
                }
//                    // Filter the available courses in one iteration
//                    val availableCourses = allCourseDataclass.value?.filter { data ->
//                        data._id !in (courses.value?.map { it._id } ?: emptyList())
//                    }.orEmpty()
//
//// Show "Available Course" text only if there are available courses
//                    if (availableCourses.isNotEmpty()) {
//                        Text(
//                            text = "Available Course",
//                            color = Color.Gray,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 25.sp,
//                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
//                        )
//
//                        // Render the filtered list of available courses
//                        availableCourses.forEach { data ->
//                            CourseCard(
//                                courseTitle = data.title,
//                                instructor = data.creator.fullName,
//                                studentCount = data.numEnrolledStudents,
//                                enroll = true,
//                                progress = null
//                            )
//                        }
//                    }

            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CourseCard(
        courseTitle: String,
        instructor: String,
        studentCount: Int,
        progress: Float?,
        enroll: Boolean?,
        onClick: () -> Unit = {}
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.proff),
                        contentDescription = "Instructor Avatar",
                        modifier = Modifier
                            .size(55.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray, shape = CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = courseTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Text(
                            text = instructor,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.student),
                                contentDescription = "Student Icon",
                                tint = Color.DarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = " $studentCount",
                                color = Color.DarkGray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Box(contentAlignment = Alignment.Center) {
                        progress?.let {
                            CircularProgressIndicator(
                                progress = { it / 100 },
                                trackColor = Color.LightGray,
                                modifier = Modifier.size(50.dp),
                                color = Color(0xFF37ADA6),
                                strokeWidth = 6.dp,

                                )
                            Text(
                                text = "${String.format("%.1f", it)}%",
                                color = Color.DarkGray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

}
