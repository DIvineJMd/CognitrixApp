package com.example.cognitrix.pages

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cognitrix.api.Dataload.CourseViewModel

class Leaderboard {
    companion object {
        @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
        @Composable
        fun LeaderboardScreen(
            modifier: Modifier,
            courseViewModel: CourseViewModel,
            navController: NavHostController,
            context: Context
        ) {
            var showDialog by remember { mutableStateOf(false) }
            var selectedRecord by remember { mutableStateOf<Pair<String, String>?>(null) }

            Column(modifier = modifier.padding(16.dp)) {
                // Table Headings in Boxes
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text(text = "Rank", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(3f)
                            .padding(4.dp)
                            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text(text = "Name", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text(text = "Coins", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Sample Data (Replace with actual data from your ViewModel)
                val sampleData = listOf(
                    Pair("1", "Alice"),
                    Pair("2", "Bob"),
                    Pair("3", "Charlie")
                )

                // Displaying Sample Data
                sampleData.forEach { (rank, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedRecord = Pair(rank, name)
                                showDialog = true
                            }
                    ) {
                        Text(text = rank, modifier = Modifier.weight(1f).padding(8.dp))
                        Text(text = name, modifier = Modifier.weight(3f).padding(8.dp))
                        Text(text = "100", modifier = Modifier.weight(1f).padding(8.dp)) // Sample coins
                    }
                }
            }

            // Popup Dialog
            if (showDialog && selectedRecord != null) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Record Details") },
                    text = {
                        Text(text = "Rank: ${selectedRecord!!.first}\nName: ${selectedRecord!!.second}\nCoins: 100")
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}