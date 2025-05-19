package com.example.tbssmarthub.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tbssmarthub.ui.HomeScreen
import com.example.tbssmarthub.ui.admin.AdminDashboardScreen
import com.example.tbssmarthub.ui.auth.LoginScreen
import com.example.tbssmarthub.ui.auth.SignUpScreen
import com.example.tbssmarthub.ui.calendar.CalendarScreen
import com.example.tbssmarthub.ui.chatbot.ChatScreen
import com.example.tbssmarthub.ui.comingsoon.ComingSoonScreen
import com.example.tbssmarthub.ui.feedback.AnonymousFeedbackScreen
import com.example.tbssmarthub.ui.settings.SettingsScreen

/**
 * Navigation routes for the application
 * These constants are used to navigate between different screens
 */
object NavRoutes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val CALENDAR = "calendar"
    const val CHATBOT = "chatbot"
    const val TEACHERS = "teachers"
    const val EVENTS = "events"
    const val SETTINGS = "settings"
    const val ANONYMOUS_FEEDBACK = "anonymous_feedback"
    const val COMING_SOON = "coming_soon"
}

/**
 * Main navigation component for the application
 * Handles routing between different screens based on the navigation graph
 * Includes routing to Coming Soon page for unimplemented features
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.LOGIN
) {
    // Create a navigation graph using NavHost
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication routes
        composable(NavRoutes.LOGIN) { 
            LoginScreen(navController) 
        }
        composable(NavRoutes.SIGNUP) { 
            SignUpScreen(navController) 
        }
        
        // Main application routes
        composable(NavRoutes.HOME) { 
            HomeScreen(navController) 
        }
        composable(NavRoutes.ADMIN_DASHBOARD) { 
            AdminDashboardScreen(navController) 
        }
        composable(NavRoutes.CALENDAR) { 
            CalendarScreen(navController) 
        }
        composable(NavRoutes.CHATBOT) { 
            ChatScreen(navController) 
        }
        
        // Additional features
        composable(NavRoutes.SETTINGS) { 
            SettingsScreen(navController) 
        }
        composable(NavRoutes.ANONYMOUS_FEEDBACK) { 
            AnonymousFeedbackScreen(navController) 
        }
        
        // Coming soon placeholder for unimplemented features
        composable(
            route = "${NavRoutes.COMING_SOON}/{feature}",
            arguments = listOf(navArgument("feature") { type = NavType.StringType })
        ) { backStackEntry ->
            val feature = backStackEntry.arguments?.getString("feature") ?: "Feature"
            ComingSoonScreen(navController, feature)
        }
        
        // Default coming soon route
        composable(NavRoutes.COMING_SOON) {
            ComingSoonScreen(navController, "Feature")
        }
        
        // Routes that will use the coming soon screen for now
        composable(NavRoutes.TEACHERS) {
            ComingSoonScreen(navController, "Teachers")
        }
        composable(NavRoutes.EVENTS) {
            ComingSoonScreen(navController, "Events")
        }
    }
}

/**
 * Extension function to navigate to a route with safety checks
 * @param route The destination route
 */
fun NavHostController.navigateSafely(route: String) {
    this.navigate(route) {
        // Pop up to the start destination of the graph to avoid building up a large stack
        // of destinations on the back stack as users select items
        popUpTo(NavRoutes.HOME) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

/**
 * Extension function to navigate to the coming soon screen for unimplemented features
 * @param feature The name of the unimplemented feature
 */
fun NavHostController.navigateToComingSoon(feature: String) {
    this.navigate("${NavRoutes.COMING_SOON}/$feature")
}
