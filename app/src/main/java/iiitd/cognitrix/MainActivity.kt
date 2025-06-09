package iiitd.cognitrix

import iiitd.cognitrix.api.Api_data.LoginViewModel
import iiitd.cognitrix.pages.SignUpPage
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
import androidx.compose.ui.graphics.ShaderBrush
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import iiitd.cognitrix.api.Dataload.CourseViewModel
import iiitd.cognitrix.pages.CoursePage
import iiitd.cognitrix.pages.Home
import iiitd.cognitrix.pages.LoginPage
import iiitd.cognitrix.ui.theme.CognitrixTheme

class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private val courseViewmodel: CourseViewModel by viewModels()

    object SharedViewModelHolder {
        var courseViewModel: CourseViewModel? = null
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharedViewModelHolder.courseViewModel = courseViewmodel
            CognitrixTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = if (SharedViewModelHolder.courseViewModel?.getAuthToken(applicationContext)
                                .isNullOrEmpty()
                        ) "login" else "home"
                    ) {
                        composable("login") {
                            LoginPage(
                                viewModel = loginViewModel, applicationContext, navController
                            )
                        }

                        composable("home") {
                            SharedViewModelHolder.courseViewModel?.fetchOngoingCourses(
                                applicationContext
                            )
                            SharedViewModelHolder.courseViewModel?.fetchRemainingCourses(
                                applicationContext
                            )
                            SharedViewModelHolder.courseViewModel?.fetchAllCourse(applicationContext)

                            Home().HomeScreen(
                                applicationContext,
                                SharedViewModelHolder.courseViewModel!!,
                                navController,
                                loginviewmodel = loginViewModel
                            )
                        }
                        composable("signup") {
                            SignUpPage(navController, loginViewModel)
                        }
                        composable(
                            route = "Lecture/{courseId}",
                            arguments = listOf(navArgument("courseId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val courseId = backStackEntry.arguments?.getString("courseId")

                            if (courseId != null) {
                                CoursePage().CourseScreen(
                                    SharedViewModelHolder.courseViewModel!!,
                                    applicationContext,
                                    courseId
                                )
                            }
                        }

                    }

                }
            }
        }
    }
}
