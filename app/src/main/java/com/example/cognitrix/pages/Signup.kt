import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateOf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.sharp.Lock
import androidx.compose.material.icons.twotone.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavController
import com.example.cognitrix.R
import com.example.cognitrix.pages.InputField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(navController: NavController) {
    Scaffold(topBar = {}) {
        var fullName by rememberSaveable { mutableStateOf("") }
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var phoneNumber by rememberSaveable { mutableStateOf("") }
        var discordId by rememberSaveable { mutableStateOf("") }
        var isStudent by rememberSaveable { mutableStateOf(true) } // Toggle state for Student/Professor
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

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
                        fontSize = 20.sp,
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
                        fontSize = 20.sp,
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
                    .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                     Full Name Input
                    InputField(
                        heading = "Full Name",
                        showAsterisk = true,
                        icon = R.drawable.profile_outline,
                        placeholder = "Enter Full Name",
                        input = fullName,
                        onInputChange = { fullName = it }
                    )
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
                    Text(
                        text = "Your Password must be at least 8 characters long.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.Start)
                    )
                    InputField(
                        heading = "Phone Number",
                        showAsterisk = false,
                        icon = R.drawable.phone,
                        placeholder = "Enter Phone Number",
                        input = phoneNumber,
                        onInputChange = { phoneNumber = it }
                    )
                    InputField(
                        heading = "Discord ID",
                        showAsterisk = false,
                        icon = R.drawable.discord,
                        placeholder = "Enter Discord ID",
                        input = discordId,
                        onInputChange = { discordId = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sign Up button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                signUp(
                                    fullName = fullName,
                                    email = email,
                                    password = password,
                                    phoneNumber = phoneNumber,
                                    discordId = discordId,
                                    role = if (isStudent) "student" else "professor",
                                    context = context,
                                    navController=navController
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text("Sign Up",
                            color= MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(fontFamily = FontFamily.Default)
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
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " Login",
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("login") // Navigate on click
                                }
                        )
                    }
                }
            }
        }
    }
}

fun signUp(
    fullName: String,
    email: String,
    password: String,
    phoneNumber: String,
    discordId: String,
    role: String,
    context: Context,
    navController: NavController
) {
    val url = "https://szuumq8b3e.execute-api.ap-south-1.amazonaws.com/prod/api/auth/signup"
    val client = OkHttpClient()

    val json = JSONObject().apply {
        put("fullName", fullName)
        put("email", email)
        put("password", password)
        put("phoneNumber", phoneNumber)
        put("discordId", discordId)
        put("role", role)
    }

    // Create JSON request body
    val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() // Accessing response body
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "User created successfully", Toast.LENGTH_LONG).show()
                        navController.navigate("login")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        println(response.message)
                        Toast.makeText(context, "Sign up failed: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


