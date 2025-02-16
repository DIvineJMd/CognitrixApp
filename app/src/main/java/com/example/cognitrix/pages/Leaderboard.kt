package com.example.cognitrix.pages

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cognitrix.api.Dataload.CourseViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import com.example.cognitrix.api.Dataload.LeaderData

class Leaderboard {
    companion object {
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
            val leaderboard = courseViewModel.leaderboard.observeAsState(emptyList()) // Observe leaderboard data
            val leaderboardError = courseViewModel.leaderboardError.observeAsState("") // Observe error

            LaunchedEffect(Unit) {
                courseViewModel.fetchLeaderboard(context) // Fetch leaderboard data when screen is loaded
            }

            Column(modifier = modifier.padding(16.dp)) {
                // Table Headings
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black)
                    .padding(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        Text(text = "Rank", fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(3f)
                            .padding(8.dp)
                    ) {
                        Text(text = "Name", fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        Text(text = "Coins", fontWeight = FontWeight.Bold)
                    }
                }

                // Show error if there is an issue fetching the leaderboard
                if (leaderboardError.value.isNotEmpty()) {
                    Text(text = leaderboardError.value, color = Color.Red)
                }

                // Displaying Leaderboard Data
                if (leaderboard.value.isNotEmpty()) {
                    DisplayRecords(leaderboard.value) { record ->
                        selectedRecord.value = record
                        showDialog.value = true
                    }
                } else {
                    // Show a loading message or something when data is still being fetched
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center){ CircularProgressIndicator() }
                }

                // Show dialog if needed
                if (showDialog.value && selectedRecord.value != null) {
                    // Details Dialog
                    AlertDialog(
                        onDismissRequest = { showDialog.value = false },
                        title = { Text("Details") },
                        text = {
                            val record = selectedRecord.value!!
                            Column {
                                Text("Rank: ${record.rank}")
                                Text("Name: ${record.fullName}")
                                Text("Coins: ${record.coins}")
                                Text("Badges: \uD83E\uDD47 ${record.badge}")
                                Text("Courses Done: ${record.ongoingCourses.size}")
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showDialog.value = false }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }

        @Composable
        fun DisplayRecords(
             records: List<LeaderData>,
            onRecordClick: (LeaderData) -> Unit
        ) {
            LazyColumn {
                items(records) { record -> // Use 'items' instead of 'forEach'
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRecordClick(record) }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = record.rank.toString(),
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        )
                        Text(
                            text = record.fullName,
                            modifier = Modifier
                                .weight(3f)
                                .padding(8.dp)
                        )
                        Text(
                            text = record.coins.toString(),
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }


    }
}