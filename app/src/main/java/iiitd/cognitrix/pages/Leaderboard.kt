package iiitd.cognitrix.pages

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import iiitd.cognitrix.api.Dataload.CourseViewModel
import iiitd.cognitrix.api.Dataload.LeaderData
import androidx.compose.foundation.ExperimentalFoundationApi
import iiitd.cognitrix.R
import iiitd.cognitrix.ui.theme.bronze
import iiitd.cognitrix.ui.theme.gold
import iiitd.cognitrix.ui.theme.silver

class Leaderboard {

    @Composable
    fun LeaderboardScreen(
        modifier: Modifier,
        courseViewModel: CourseViewModel,
        navController: NavHostController,
        context: Context
    ) {
        // Define state variables
        val selectedRecord = remember { mutableStateOf<LeaderData?>(null) }
        val showDialog = remember { mutableStateOf(false) }
        val leaderboard = courseViewModel.leaderboard.observeAsState(emptyList())
        val leaderboardError = courseViewModel.leaderboardError.observeAsState("")

        // Get user rank from shared preferences
        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val userRank = sharedPref.getInt("rank", 0)

        LaunchedEffect(Unit) {
            courseViewModel.fetchLeaderboard(context)
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Show error message if there's an error
            AnimatedVisibility(visible = leaderboardError.value.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = leaderboardError.value,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (leaderboard.value.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading leaderboard...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top 10 Card
                    item {
                        DisplayTopTen(leaderboard.value.take(10)) { record ->
                            selectedRecord.value = record
                            showDialog.value = true
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Sticky header - will appear below Top 3 initially and then stick when scrolled
                    @OptIn(ExperimentalFoundationApi::class)
                    stickyHeader {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rank",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Name",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(3f),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Coins",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Filter leaderboard to show user's position with people above and below
                    val filteredLeaderboard = if (userRank > 0) {
                        // Find the index of the user in the leaderboard
                        val userIndex = leaderboard.value.indexOfFirst { it.rank == userRank }

                        if (userIndex != -1) {
                            // Calculate the start and end indices for the slice
                            val startIndex = (userIndex - 20).coerceAtLeast(0)
                            val endIndex = (userIndex + 21).coerceAtMost(leaderboard.value.size)

                            leaderboard.value.subList(startIndex, endIndex)
                        } else {
                            // If user not found, just display first 41 entries or all if less
                            leaderboard.value.take(41)
                        }
                    } else {
                        // If no user data, show first 41 entries or all if less
                        leaderboard.value.take(41)
                    }

                    // List items
                    items(filteredLeaderboard) { record ->
                        val isCurrentUser = record.rank == userRank
                        val elevation by animateDpAsState(
                            targetValue = when {
                                isCurrentUser -> 8.dp
                                record.rank <= 3 -> 6.dp
                                else -> 2.dp
                            },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedRecord.value = record; showDialog.value = true
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isCurrentUser -> MaterialTheme.colorScheme.surface
                                    record.rank == 1 -> gold
                                    record.rank == 2 -> silver
                                    record.rank == 3 -> bronze
                                    else -> MaterialTheme.colorScheme.primaryContainer
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (record.rank) {
                                                    1 -> gold
                                                    2 -> silver
                                                    3 -> bronze
                                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = record.rank.toString(),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }

                                Text(
                                    text = record.fullName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(3f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                // Coin count with icon
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${record.coins}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = " 🪙",
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            if (showDialog.value && selectedRecord.value != null) {
                val record = selectedRecord.value!!
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.account_circle),
                            contentDescription = "Profile Icon",
                            modifier = Modifier.size(120.dp)
                        )
                    },
                    title = {
                        Text(
                            text = record.fullName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            LeaderboardDetailItem("Rank", "#${record.rank}")
                            LeaderboardDetailItem("Coins", "${record.coins}")
                            LeaderboardDetailItem("Badges", "\uD83E\uDD47 ${record.badge}")
                            LeaderboardDetailItem("Courses", "${record.ongoingCourses.size}")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showDialog.value = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Close")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }

    @Composable
    fun LeaderboardDetailItem(label: String, value: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }

    @Composable
    fun DisplayTopTen(
        topUsers: List<LeaderData>,
        onUserClick: (LeaderData) -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Top Learners",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(topUsers) { user ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(80.dp)
                                .clickable { onUserClick(user) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                // Profile Image with gradient border for top 3
                                val borderColor = when (user.rank) {
                                    1 -> gold
                                    2 -> silver
                                    3 -> bronze
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }

                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(borderColor)
                                        .padding(if (user.rank <= 3) 4.dp else 0.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.tertiary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AccountCircle,
                                            contentDescription = "User Avatar",
                                            modifier = Modifier.size(32.dp),
                                            tint = MaterialTheme.colorScheme.onTertiary
                                        )
                                    }
                                }

                                // Rank Badge
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .align(Alignment.BottomEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${user.rank}",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // User Name
                            Text(
                                text = user.fullName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Coin Count
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${user.coins}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "🪙",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
