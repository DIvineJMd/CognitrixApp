package com.example.cognitrix.pages

import LoginViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.sharp.Lock
import androidx.compose.material.icons.twotone.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(viewModel: LoginViewModel,context:Context,navController: NavController){
    Scaffold(topBar = {}) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isStudent by remember { mutableStateOf(true) } // Toggle state for Student/Professor
        var clicked by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Toggle for Student and Professor
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Student Section
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .padding(vertical = 16.dp)
                        .clickable { isStudent = true }
                ) {
                    Text(
                        text = "Student",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isStudent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(if (isStudent) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .align(Alignment.BottomCenter)
                    )
                }

                // Professor Section
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .padding(vertical = 16.dp)
                        .clickable { isStudent = false }
                ) {
                    Text(
                        text = "Professor",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isStudent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(if (!isStudent) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .align(Alignment.BottomCenter)
                    )
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 20.sp)) { // Increase text size
                                append("Email") // Add the main text
                            }
                            withStyle(style = SpanStyle(color = Color.Red, fontSize = 20.sp)) { // Red color for the asterisk
                                append("*")
                            }
                        },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.Start)
                    )
                    // Email input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email= it },
                        placeholder = { Text("Enter Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = "Email Icon",
                                tint = MaterialTheme.colorScheme.tertiary // Set the icon color
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent, // Removes the background color
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary, // Hides the underline when focused
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary

                        )
                    )

                    // Password input
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 20.sp)) { // Increase text size
                                append("Password") // Add the main text
                            }
                            withStyle(style = SpanStyle(color = Color.Red, fontSize = 20.sp)) { // Red color for the asterisk
                                append("*")
                            }
                        },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.Start)
                    )
                    var passwordVisible by remember { mutableStateOf(false) } // State to track password visibility
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Password Icon",
                                tint = MaterialTheme.colorScheme.tertiary // Set the icon color
                            )
                        },
                        trailingIcon = {
                            // Icon to toggle password visibility
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                                contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                                modifier = Modifier.clickable { passwordVisible = !passwordVisible }, // Toggle visibility on click
                                tint = MaterialTheme.colorScheme.tertiary // Set the icon color
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent, // Removes the background color
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary, // Hides the underline when focused
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary
                        )
                    )

                    // Forgot password text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End // Aligns items to the end (right side)
                    ) {
                        TextButton(onClick = {
                            // forget password
                        }) {
                            Text(
                                text = "Forgot password?",
                                color = Color.Blue,
                                fontSize = 14.sp
                            )
                        }
                    }
                    if (clicked) {
                        LoginAlertDialog(
                            viewModel = viewModel,
                            onDismiss = { clicked = false
                                       },
                            navController

                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login button
                    Button(
                        onClick = {
                            clicked=true
                        viewModel.login(email, password, context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Log in")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sign Up section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Not Registered? Please")
                        TextButton(onClick = {
                            navController.navigate("signup")
                        }) {
                            Text(
                                text = "Sign Up",
                                color = Color.Blue
                            )
                        }
                    }
                }
            }

        }
    }
}
@Composable
fun LoginAlertDialog(viewModel: LoginViewModel,  onDismiss: () -> Unit,navController: NavController) {
    val loginState by viewModel.loginState.collectAsState()



    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (loginState) {
                    is Resource.Loading -> {
                        Text(
                            text = "Hold On...",
                            textAlign = TextAlign.Center,
                        )
                        CircularProgressIndicator()
                    }
                    is Resource.Success<*> -> {
                        Text(text = (loginState as Resource.Success<String>).data)
                        onDismiss()
                        navController.navigate("home")

                    }
                    is Resource.Error -> {
                        val errorMessage = (loginState as? Resource.Error)?.message?.let { msg ->
                            // Extract the main error message
                            val errorRegex = """"message":"(.*?)"""".toRegex()
                            val matchResult = errorRegex.find(msg)
                            matchResult?.groups?.get(1)?.value ?: "An error occurred"
                        } ?: "An error occurred"

                        Text(text = errorMessage, color = Color.Red, fontWeight = FontWeight.Bold)
                    }

                    else -> {}
                }
            }
        }
    }
}



