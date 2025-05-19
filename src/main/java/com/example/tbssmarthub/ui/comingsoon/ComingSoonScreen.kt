package com.example.tbssmarthub.ui.comingsoon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tbssmarthub.R
import com.example.tbssmarthub.ui.theme.TbsPrimaryGold

/**
 * Coming Soon screen for features that are not yet implemented
 * Displays a placeholder with the TBS logo and a message
 * 
 * @param navController Navigation controller for screen navigation
 * @param featureName Name of the feature that is coming soon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComingSoonScreen(
    navController: NavController,
    featureName: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coming Soon") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // TBS Logo
            Image(
                painter = painterResource(id = R.drawable.logo_tbs),
                contentDescription = "TBS Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )
            
            // Coming soon message
            Text(
                text = "$featureName",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Coming Soon",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TbsPrimaryGold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "We're working hard to bring you this feature!",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "This feature is currently under development and will be available in a future update. Thank you for your patience.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Return button
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Return to Previous Screen")
            }
        }
    }
}
