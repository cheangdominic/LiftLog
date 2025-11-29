package com.example.liftlog.userinterface.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

// Annotation required for using experimental Material 3 features like CenterAlignedTopAppBar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    // A top app bar that centers its title element.
    CenterAlignedTopAppBar(
        title = {
            // Display the application title "LiftLog".
            Text(
                "LiftLog",
                // Apply headlineSmall typography style and make the text bold.
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        // Customize the colors of the top app bar.
        colors = TopAppBarDefaults.topAppBarColors(
            // Set the background color using the primaryContainer scheme color.
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            // Set the title text color using the onSurface scheme color.
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}