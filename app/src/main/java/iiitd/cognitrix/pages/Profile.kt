package iiitd.cognitrix.pages

import iiitd.cognitrix.api.Api_data.LoginViewModel
import iiitd.cognitrix.api.Dataload.CourseViewModel
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import iiitd.cognitrix.R

class Profile {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfileScreen(
        modifier: Modifier = Modifier,
        context: Context,
        viewModel: LoginViewModel,
        courseViewModel: CourseViewModel
    ) {
        val userData = viewModel.getStudentInfo(context)
        val scrollState = rememberScrollState()
        val ongoingCourses = courseViewModel.ongoingCourses.observeAsState()

        // Refresh student info when screen is composed
        LaunchedEffect(Unit) {
            viewModel.refreshStudentInfo(context)
        }

        // Calculate total videos watched from ongoing courses
        val totalVideosWatched = remember(ongoingCourses.value) {
            val courses = ongoingCourses.value
            if (courses.isNullOrEmpty()) {
                0
            } else {
                courses.sumOf { it.numWatchedVideos ?: 0 }
            }
        }

        // Calculate progress based on videos watched vs total videos
        val videoProgress = remember(ongoingCourses.value) {
            val courses = ongoingCourses.value
            if (courses.isNullOrEmpty()) {
                0f
            } else {
                val totalWatched = courses.sumOf { it.numWatchedVideos ?: 0 }
                val totalVideos = courses.sumOf { it.numVideosInCourse }
                if (totalVideos > 0) {
                    (totalWatched.toFloat() / totalVideos.toFloat())
                } else {
                    0f
                }
            }
        }

        Box(
            modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                // .background(
                //     brush = Brush.verticalGradient(
                //         colors = listOf(
                //             MaterialTheme.colorScheme.surface,
                //             MaterialTheme.colorScheme.onSurface
                //         )
                //     )
                // )
            )

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(10.dp),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(
                        4.dp,
                        MaterialTheme.colorScheme.surface
                    ),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 8.dp
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.person_dummy),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    )
                }

                // Profile Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
                    ) {
                        // Name
                        Text(
                            text = userData?.fullName?.takeIf { it.isNotBlank() } ?: "Name not available",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                        )

                        // Email
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = "Email",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = userData?.email?.takeIf { it.isNotBlank() } ?: "Email not available",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier
                                .padding(vertical = 16.dp, horizontal = 32.dp)
                                .fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                icon = painterResource(R.drawable.rank),
                                label = "Rank",
                                value = if (userData?.rank != null && userData.rank != 0) "#${userData.rank}" else "N/A"
                            )

                            StatItem(
                                icon = painterResource(R.drawable.badge),
                                label = "Badge",
                                value = userData?.badge?.takeIf { it.isNotBlank() } ?: "N/A"
                                                                                                          )

                            StatItem(
                                icon = painterResource(R.drawable.courses),
                                label = "Courses",
                                value = "${ongoingCourses.value?.size ?: 0}"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Contact Info Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Contact Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Phone Number
                        InfoRow(
                            icon = Icons.Filled.Phone,
                            label = "Phone",
                            value = userData?.phoneNumber?.takeIf { it.isNotBlank() }
                                ?: "Not added",
                            valueColor = if (userData?.phoneNumber.isNullOrBlank())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )

                        // Discord
                        InfoRow(
                            iconResId = R.drawable.discord,
                            label = "Discord",
                            value = userData?.discordId?.takeIf { it.isNotBlank() } ?: "Not added",
                            valueColor = if (userData?.discordId.isNullOrBlank())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Learning Progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Progress metrics, using total videos watched
                        Text(
                            text = "Total videos watched: $totalVideosWatched",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        LinearProgressIndicator(
                        progress = { videoProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.background,
                        )

                        Text(
                            text = "Keep learning and watching more videos!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    @Composable
    fun StatItem(
        icon:Painter,
        label: String,
        value: String
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }

    @Composable
    fun InfoRow(
        icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
        iconResId: Int? = null,
        label: String,
        value: String,
        valueColor: Color = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                } else if (iconResId != null) {
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = valueColor
                )
            }
        }
    }
}
