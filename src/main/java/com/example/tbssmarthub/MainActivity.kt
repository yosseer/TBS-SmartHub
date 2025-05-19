package com.example.tbssmarthub

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.tbssmarthub.navigation.AppNavigation
import com.example.tbssmarthub.ui.theme.TbsSmartHubTheme

/**
 * Main Activity for the TBS SmartHub application
 * Entry point for the application that sets up the Compose UI
 * Supports both portrait and landscape orientations
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable orientation changes
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        
        setContent {
            // Use the orientation-aware composable
            OrientationAwareApp()
        }
    }
}

/**
 * Orientation-aware app container that adapts to device rotation
 * Applies appropriate padding and layout adjustments based on orientation
 */
@Composable
fun OrientationAwareApp() {
    // Get current orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    // Apply the TBS SmartHub theme to the entire application
    TbsSmartHubTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Apply responsive padding based on orientation
            val contentModifier = if (isLandscape) {
                Modifier.padding(horizontal = 32.dp)
            } else {
                Modifier
            }
            
            // Set up the navigation system for the app
            AppNavigation()
        }
    }
}
