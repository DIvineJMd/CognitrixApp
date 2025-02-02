package com.example.cognitrix

import LoginViewModel
import SignUpPage
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cognitrix.api.Dataload.CourseViewModel
import com.example.cognitrix.pages.CoursePage
import com.example.cognitrix.pages.Home
import com.example.cognitrix.pages.LoginPage
import com.example.cognitrix.ui.theme.CognitrixTheme

class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private val courseViewmodel: CourseViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CognitrixTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = if (courseViewmodel.getAuthToken(applicationContext)
                                .isNullOrEmpty()
                        ) "login" else "home"
                    ) {
                        composable("login") {
                            LoginPage(
                                viewModel = loginViewModel, applicationContext, navController
                            )
                        }

                        composable("home") {
                            courseViewmodel.fetchOngoingCourses(applicationContext)
                            courseViewmodel.fetchRemainingCourses(applicationContext)
                            courseViewmodel.fetchAllCourse(applicationContext)
                            Home().HomeScreen(applicationContext,courseViewmodel, navController, loginviewmodel = loginViewModel)
                        }
                        composable("signup") {
                            SignUpPage(navController)
                        }
                        composable(
                            route = "Lecture/{courseId}",
                            arguments = listOf(navArgument("courseId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val courseId = backStackEntry.arguments?.getString("courseId")

                            if (courseId != null) {
                                CoursePage().CourseScreen(courseViewmodel, applicationContext, courseId)
                            }
                        }

                    }

                }
            }
        }
    }
}


