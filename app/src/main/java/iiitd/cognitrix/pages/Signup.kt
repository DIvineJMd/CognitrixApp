package iiitd.cognitrix.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import iiitd.cognitrix.R
import iiitd.cognitrix.api.Api_data.LoginViewModel
import iiitd.cognitrix.api.Api_data.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(navController: NavController, viewModel: LoginViewModel) {
    Scaffold(topBar = {}) {
        var fullName by rememberSaveable { mutableStateOf("") }
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var phoneNumber by rememberSaveable { mutableStateOf("") }
        var discordId by rememberSaveable { mutableStateOf("") }
        var isStudent by rememberSaveable { mutableStateOf(true) }

        // Error states for validation
        var fullNameError by rememberSaveable { mutableStateOf("") }
        var emailError by rememberSaveable { mutableStateOf("") }
        var passwordError by rememberSaveable { mutableStateOf("") }

        val context = LocalContext.current

        val signupState by viewModel.signupState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
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
                        fontWeight = FontWeight.Bold,
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
                        fontWeight = FontWeight.Bold,
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
                    .padding(start = 16.dp, end = 16.dp, bottom = 60.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InputFieldWithError(
                        heading = "Full Name",
                        showAsterisk = true,
                        icon = R.drawable.profile_outline,
                        placeholder = "Enter Full Name",
                        input = fullName,
                        onInputChange = {
                            fullName = it
                            fullNameError = ""
                        },
                        errorMessage = fullNameError
                    )
                    InputFieldWithError(
                        heading = "Email",
                        showAsterisk = true,
                        icon = R.drawable.mail,
                        placeholder = "Enter Email",
                        input = email,
                        onInputChange = {
                            email = it
                            emailError = ""
                        },
                        errorMessage = emailError
                    )
                    InputFieldWithError(
                        heading = "Password",
                        showAsterisk = true,
                        icon = R.drawable.lock,
                        placeholder = "Enter Password",
                        input = password,
                        onInputChange = {
                            password = it
                            passwordError = ""
                        },
                        errorMessage = passwordError
                    )
                    Text(
                        text = "Your Password must be at least 8 characters long.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    iiitd.cognitrix.pages.InputField(
                        heading = "Phone Number",
                        showAsterisk = false,
                        icon = R.drawable.phone,
                        placeholder = "Enter Phone Number",
                        input = phoneNumber,
                        onInputChange = { phoneNumber = it }
                    )
                    iiitd.cognitrix.pages.InputField(
                        heading = "Discord ID",
                        showAsterisk = false,
                        icon = R.drawable.discord,
                        placeholder = "Enter Discord ID",
                        input = discordId,
                        onInputChange = { discordId = it }
                    )

                    // Sign Up state handling
                    when (signupState) {
                        is Resource.Idle -> {
                            // Show nothing when in idle state
                        }
                        is Resource.Loading -> {
                            Row(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Creating Account...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        is Resource.Success<*> -> {
                            Text(
                                text = (signupState as Resource.Success<String>).data,
                                modifier = Modifier.padding(top = 16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            // Navigate to login after successful signup
                            LaunchedEffect(signupState) {
                                navController.navigate("login")
                            }
                        }
                        is Resource.Error -> {
                            LaunchedEffect(signupState) {
                                Toast.makeText(context, "Signup Failed", Toast.LENGTH_SHORT).show()
                            }
                            val errorMessage = (signupState as Resource.Error).message
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Sign Up button
                    Button(
                        onClick = {
                            // Validate required fields
                            var hasError = false

                            if (fullName.isBlank()) {
                                fullNameError = "Name is required"
                                hasError = true
                            }

                            if (email.isBlank()) {
                                emailError = "Email is required"
                                hasError = true
                            }

                            if (password.isBlank()) {
                                passwordError = "Password is required"
                                hasError = true
                            } else if (password.length < 8) {
                                passwordError = "Password must be at least 8 characters long"
                                hasError = true
                            }

                            // Only proceed if no validation errors
                            if (!hasError) {
                                viewModel.signup(
                                    fullName = fullName,
                                    email = email,
                                    password = password,
                                    phoneNumber = phoneNumber,
                                    discordId = discordId,
                                    role = if (isStudent) "student" else "professor"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        enabled = signupState !is Resource.Loading
                    ) {
                        Text("Sign Up",
                            color= MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Already Registered? Please",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " Login",
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("login")
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
fun InputFieldWithError(
    heading: String,
    showAsterisk: Boolean,
    icon: Int,
    placeholder: String,
    input: String,
    onInputChange: (String) -> Unit,
    errorMessage: String = ""
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Column {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = MaterialTheme.typography.titleMedium.toSpanStyle()
                        .copy(color = MaterialTheme.colorScheme.primary)
                ) {
                    append(heading)
                }
                if (showAsterisk) {
                    withStyle(
                        style = MaterialTheme.typography.titleMedium.toSpanStyle()
                            .copy(color = Color.Red)
                    ) {
                        append("*")
                    }
                }
            },
            modifier = Modifier
                .padding(top = 24.dp, bottom = 8.dp)
                .align(Alignment.Start)
        )

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
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "Icon",
                    tint = if (errorMessage.isNotEmpty()) Color.Red else MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = if (heading == "Password") {
                {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.visibility_on else R.drawable.visibility_off),
                        contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible },
                        tint = if (errorMessage.isNotEmpty()) Color.Red else MaterialTheme.colorScheme.primary
                    )
                }
            } else null,
            visualTransformation = if (heading == "Password" && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = if (errorMessage.isNotEmpty()) Color.Red else Color.Transparent,
                unfocusedIndicatorColor = if (errorMessage.isNotEmpty()) Color.Red else Color.Transparent,
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            isError = errorMessage.isNotEmpty()
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
