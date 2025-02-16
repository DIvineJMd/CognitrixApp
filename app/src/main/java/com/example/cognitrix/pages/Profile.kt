package com.example.cognitrix.pages

import LoginViewModel
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cognitrix.R
import com.example.cognitrix.api.Dataload.CourseViewModel

class Profile {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfileScreen(
        modifier: Modifier = Modifier,
        context: Context,
        viewModel: LoginViewModel
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Profile Image
            Image(
                painter = painterResource(id = R.drawable.student), // Replace with your image resource
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )

            val userData = viewModel.getStudentInfo(context)

            // Name
            Text(
                text = userData?.fullName?.takeIf { it.isNotBlank() } ?: "Name not available",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Email
            Text(
                text = userData?.email?.takeIf { it.isNotBlank() } ?: "Email not available",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Rank
            if (userData != null) {
                Text(
                    text = if (userData?.rank != 0) "Rank: ${userData.rank}" else "Rank not available",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Badge
            Text(
                text = ("\uD83E\uDD47: " + userData?.badge?.takeIf { it.isNotBlank() })
                    ?: "Badge not available",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Mobile Number
            userData?.phoneNumber?.takeIf { it.isNotBlank() }?.let { phoneNumber ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Phone Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = phoneNumber,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Discord Username
            userData?.discordId?.takeIf { it.isNotBlank() }?.let { discordId ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.discordicon),
                        contentDescription = "Discord Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = discordId,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }

}
