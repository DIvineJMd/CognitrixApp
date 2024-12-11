package com.example.cognitrix.pages

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cognitrix.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Image(
            painter = painterResource(id = R.drawable.student), // Replace with your image resource
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp)
        )

        // Name
        Text(
            text = "Adarsh Pandey",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Email
        Text(
            text = "adarsh21442@iiitd.ac.in",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Rank
        Text(
            text = "Rank: 51",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Badge
        Text(
            text = "Badge: 🥇", // Replace with your badge icon if needed
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Mobile Number
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_phone), // Replace with your phone icon resource
                contentDescription = "Phone Icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "+91 9911577403",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Discord Username
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_discord), // Replace with your Discord icon resource
                contentDescription = "Discord Icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "adarshjmd13",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Edit Profile Button
        Button(
            onClick = { /* Handle Edit Profile */ },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Edit Profile")
        }
    }
}