package com.example.cognitrix.pages

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
    companion object {
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun ProfileScreen(
            modifier: Modifier = Modifier,
            courseViewModel: CourseViewModel,
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
            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = "Phone Icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "+91 9971501234",
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
                imageVector = Icons.Filled.AccountBox, // Replace with your Discord icon resource
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

                // State to control the visibility of the EditProfile screen
                var showEditProfile by remember { mutableStateOf(false) }

                // Edit Profile Button
                Button(
                    onClick = {
                        showEditProfile = true // Set the state to true to show EditProfile
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Edit Profile")
                }

                // Show EditProfile when the state is true
                if (showEditProfile) {
                    EditProfile(courseViewModel = courseViewModel, navController = navController, context = context)
                }
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun EditProfile(
            modifier: Modifier = Modifier,
            courseViewModel: CourseViewModel,
            navController: NavHostController,
            context: Context
        ) {
            // New UI elements for editing profile
            var fullName by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var passwordVisible by remember { mutableStateOf(false) }
            var phoneNumber by remember { mutableStateOf("") }
            var discordId by remember { mutableStateOf("") }

            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Full Name Input
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 20.sp)) {
                                append("Full Name")
                            }
                            withStyle(style = SpanStyle(color = Color.Red, fontSize = 20.sp)) {
                                append("*")
                            }
                        },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.Start)
                    )
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = { Text("Enter Full Name") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "Full Name"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary
                        )
                    )

                    // Email Input
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 20.sp)) {
                                append("Email")
                            }
                            withStyle(style = SpanStyle(color = Color.Red, fontSize = 20.sp)) {
                                append("*")
                            }
                        },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.Start)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Enter Email") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary
                        )
                    )

                    // Password Input
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 20.sp)) {
                                append("Password")
                            }
                            withStyle(style = SpanStyle(color = Color.Red, fontSize = 20.sp)) {
                                append("*")
                            }
                        },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.Start)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter Password") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Password")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                                contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                                modifier = Modifier.clickable {
                                    passwordVisible = !passwordVisible
                                },
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Text(
                        text = "Your Password must be at least 8 characters long.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp, bottom = 24.dp)
                    )

                    Text(
                        text = "Phone Number",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.Start)
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = { Text("Enter Phone Number") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Phone Number"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary
                        )
                    )

                    // Discord ID Input
                    Text(
                        text = "Discord ID",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.Start)
                    )
                    OutlinedTextField(
                        value = discordId,
                        onValueChange = { discordId = it },
                        placeholder = { Text("Enter Discord ID") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Discord ID"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }
    }
}
