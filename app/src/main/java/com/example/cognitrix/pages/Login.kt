package com.example.cognitrix.pages

import LoginViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.cognitrix.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(viewModel: LoginViewModel,context:Context,navController: NavController){
    Scaffold(topBar = {}) {
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var isStudent by rememberSaveable { mutableStateOf(true) } // Toggle state for Student/Professor
        var clicked by rememberSaveable { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState()) // Make the Column scrollable
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Toggle for Student and Professor
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Student Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .padding(vertical = 16.dp)
                        .clickable { isStudent = true }
                ) {
                    Text(
                        text = "Student",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (isStudent) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(if (isStudent) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .align(Alignment.BottomCenter)
                    )
                }

                // Professor Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .padding(vertical = 16.dp)
                        .clickable { isStudent = false }
                ) {
                    Text(
                        text = "Professor",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (!isStudent) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(if (!isStudent) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .align(Alignment.BottomCenter)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InputField(
                        heading = "Email",
                        showAsterisk = true,
                        icon = R.drawable.mail,
                        placeholder = "Enter Email",
                        input = email,
                        onInputChange = { email = it }
                    )

                    InputField(
                        heading = "Password",
                        showAsterisk = true,
                        icon = R.drawable.lock,
                        placeholder = "Enter Password",
                        input = password,
                        onInputChange = { password = it }
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
                                text = "Forgot Password?",
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.bodySmall
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

                    // Log In button
                    Button(
                        onClick = {
                            clicked=true
                            viewModel.login(email, password, context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "Login",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sign Up section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Not Registered? Please", 
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " Sign Up",
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("signup") // Navigate on click
                                }
                        )
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    heading: String,
    showAsterisk: Boolean,
    icon: Int,
    placeholder: String,
    input: String,
    onInputChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Column {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = MaterialTheme.typography.titleMedium.toSpanStyle().copy(color = MaterialTheme.colorScheme.primary)
                ) {
                    append(heading)  // This applies headlineSmall style while keeping the existing text color
                }
                if (showAsterisk) {
                    withStyle(
                        style = MaterialTheme.typography.titleMedium.toSpanStyle().copy(color = Color.Red) )
                    {
                        append("*")
                    }
                }
            },
            modifier = Modifier
                .padding(top = 24.dp, bottom = 8.dp)
                .align(Alignment.Start)
        )

        // Email input
        TextField(
            value = input,
            onValueChange = onInputChange,
            placeholder = {
                Text(
                    placeholder,
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp))  // Set light gray background
                .padding(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = if (heading == "Password") {
                {Icon(
                    painter = painterResource(id = if (passwordVisible) R.drawable.visibility_on else R.drawable.visibility_off),
                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                    modifier = Modifier.clickable { passwordVisible = !passwordVisible }, // Toggle visibility on click
                    tint = MaterialTheme.colorScheme.primary // Set the icon color
                )
                }
            } else null,
            visualTransformation = if (heading == "Password" && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,  // No background for the container
                focusedIndicatorColor = Color.Transparent,  // Remove the focused indicator
                unfocusedIndicatorColor = Color.Transparent,  // Remove the unfocused indicator
                cursorColor = MaterialTheme.colorScheme.primary,  // Set cursor color
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun LoginAlertDialog(viewModel: LoginViewModel,  onDismiss: () -> Unit,navController: NavController) {
    val loginState by viewModel.loginState.collectAsState()
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
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



