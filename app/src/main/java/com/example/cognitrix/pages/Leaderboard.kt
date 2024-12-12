package com.example.cognitrix.pages

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

class Leaderboard {
    companion object {
        @OptIn(ExperimentalMaterial3Api::class)
        @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
        @Composable
        fun DisplayRecords(
            records: List<Triple<String, String, Int>>,
            onRecordClick: (Triple<String, String, Int>) -> Unit
        ) {
            records.forEach { (rank, name, coins) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRecordClick(Triple(rank, name, coins)) }
                ) {
                    Text(text = rank, modifier = Modifier.weight(1f).padding(8.dp))
                    Text(text = name, modifier = Modifier.weight(3f).padding(8.dp))
                    Text(text = coins.toString(), modifier = Modifier.weight(1f).padding(8.dp))
                }
            }
        }

        @Composable
        fun LeaderboardScreen(
            modifier: Modifier,
            courseViewModel: CourseViewModel,
            navController: NavHostController,
            context: Context
        ) {
            // Define state variables
            val selectedRecord = remember { mutableStateOf<Triple<String, String, Int>?>(null) }
            val showDialog = remember { mutableStateOf(false) }

            Column(modifier = modifier.padding(16.dp)) {
                // Table Headings
                Row(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black).padding(8.dp)) {
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

                val sampleData = listOf(
                    Triple("1", "Alice", 100),
                    Triple("2", "Bob", 200),
                    Triple("3", "Charlie", 150)
                )

                // Displaying Sample Data
                DisplayRecords(sampleData) { record ->
                    selectedRecord.value = record
                    showDialog.value = true
                }

                // Show dialog if needed
                if (showDialog.value) {
                    // Details Dialog
                    AlertDialog(
                        onDismissRequest = { showDialog.value = false },
                        title = { Text("Details") },
                        text = {
                            Column {
                                Text("Rank: 1")
                                Text("Name: Rahul Malhotra")
                                Text("Coins: 186")
                                Text("Badges: ")
                                Text("Courses Done: 2")
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
    }
}