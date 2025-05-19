package com.example.tbssmarthub.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.tbssmarthub.ui.theme.TbsSmartHubTheme
import kotlinx.coroutines.launch

/**
 * DrawerScaffold component that provides a consistent UI structure with drawer navigation
 * Used across all main screens in the application
 * Includes support for dark mode toggle
 * 
 * @param navController Navigation controller for screen navigation
 * @param title Title to display in the top app bar
 * @param snackbarHostState Optional snackbar host state for displaying messages
 * @param content Content to display in the main area of the scaffold
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScaffold(
    navController: NavController,
    title: String,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Dark mode state
    val isDarkMode = remember { mutableStateOf(false) }
    
    // Modal navigation drawer with app drawer content
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AppDrawer(
                    navController = navController,
                    onCloseDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    isDarkMode = isDarkMode.value,
                    onDarkModeChange = { newValue ->
                        isDarkMode.value = newValue
                    }
                )
            }
        }
    ) {
        // Apply theme based on dark mode state
        TbsSmartHubTheme(darkTheme = isDarkMode.value) {
            // Main scaffold with top app bar
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(title) },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                content = content
            )
        }
    }
}
