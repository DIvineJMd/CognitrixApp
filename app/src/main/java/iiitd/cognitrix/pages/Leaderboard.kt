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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.shadow
import iiitd.cognitrix.api.Api_data.LoginViewModel

class Leaderboard {

    @Composable
    fun LeaderboardScreen(
        modifier: Modifier,
        courseViewModel: CourseViewModel,
        loginViewModel: LoginViewModel,
        navController: NavHostController,
        context: Context
    ) {
        // Define state variables
        val selectedRecord = remember { mutableStateOf<LeaderData?>(null) }
        val showDialog = rememberSaveable { mutableStateOf(false) }
        val leaderboard = courseViewModel.leaderboard.observeAsState(emptyList())
        val leaderboardError = courseViewModel.leaderboardError.observeAsState("")

        // Get user rank from shared preferences
        val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val userRank = sharedPref.getInt("rank", 0)

        LaunchedEffect(Unit) {
            courseViewModel.fetchLeaderboard(context)
            loginViewModel.refreshStudentInfo(context)
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
                        DisplayTopUsers(leaderboard.value.take(20)) { record ->
                            selectedRecord.value = record
                            showDialog.value = true
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Sticky header - will appear below Top 3 initially and then stick when scrolled
                    @OptIn(ExperimentalFoundationApi::class)
                    stickyHeader {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rank",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Name",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(3f),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Coins",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
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
                                else -> 4.dp
                            },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp) // Add this line to set card height
                                .padding(vertical = 4.dp)
//                                .shadow(
//                                    elevation = 4.dp,
//                                    shape = RoundedCornerShape(4.dp))
                                .clickable {
                                    selectedRecord.value = record; showDialog.value = true
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isCurrentUser -> MaterialTheme.colorScheme.surface
                                    else -> MaterialTheme.colorScheme.primaryContainer
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (record.rank <= 3) {
                                        val badgeResource = when (record.rank) {
                                            1 -> R.drawable.gold
                                            2 -> R.drawable.silver
                                            3 -> R.drawable.bronze
                                            else -> R.drawable.coin
                                        }
                                        Icon(
                                            painter = painterResource(badgeResource),
                                            contentDescription = "Badge",
                                            modifier = Modifier.size(36.dp),
                                            tint = Color.Unspecified
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    MaterialTheme.colorScheme.primary.copy(
                                                        alpha = 0.1f
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = record.rank.toString(),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = record.fullName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(4f),
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
                                        text = "${record.coins} ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.coin),
                                        contentDescription = "Coin",
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.Unspecified
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
                            tint = MaterialTheme.colorScheme.primary,
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
                            LeaderboardDetailItem("Badges", "\uD83C\uDF96\uFE0F ${record.badge}")
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
                            Text(
                                "Close",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.background
                            )
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
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }

    @Composable
    fun DisplayTopUsers(
        topUsers: List<LeaderData>,
        onUserClick: (LeaderData) -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
//                .shadow(
//                    elevation = 4.dp,
//                    shape = RoundedCornerShape(4.dp))
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
                                // Profile Image - remove gradient border for top 3
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
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

                                // Medal for top 3, rank badge for others
                                if (user.rank <= 3) {
                                    val medalResource = when (user.rank) {
                                        1 -> R.drawable.gold
                                        2 -> R.drawable.silver
                                        3 -> R.drawable.bronze
                                        else -> R.drawable.coin// fallback, shouldn't happen
                                    }

                                    Icon(
                                        painter = painterResource(medalResource),
                                        contentDescription = "Medal",
                                        modifier = Modifier
                                            .size(28.dp)
                                            .align(Alignment.BottomEnd),
                                        tint = Color.Unspecified
                                    )
                                } else {
                                    // Rank Badge for positions beyond top 3
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
                                            color = MaterialTheme.colorScheme.background,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // User Name
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
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
                                    text = "${user.coins} ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                                Icon(
                                    painter = painterResource(R.drawable.coin),
                                    contentDescription = "Coin",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
