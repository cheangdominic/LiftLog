package com.example.liftlog.userinterface.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.liftlog.navigation.Screen

// Data class to define the structure of a single navigation item.
data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

// Composable function to display the navigation bar at the bottom of the screen.
@Composable
fun BottomNav(nav: NavController) {
    // Define the list of items (tabs) that will appear in the navigation bar.
    val items = listOf(
        NavItem(Screen.Exercises.route, "Exercises", Icons.Default.Search),
        NavItem(Screen.History.route, "History", Icons.Default.History)
    )

    // Get the current back stack entry as State, allowing Compose to recompose
    // when the current destination changes.
    val navBackStackEntry by nav.currentBackStackEntryAsState()
    // Extract the route of the current destination.
    val currentRoute = navBackStackEntry?.destination?.route

    // The Material Design component for the bottom navigation bar.
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        // Iterate over the defined NavItems to create a NavigationBarItem for each.
        items.forEach { item ->
            // Determine if the current item is selected based on the current route.
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // Only navigate if the user is not already on the selected route.
                    if (currentRoute != item.route) {
                        nav.navigate(item.route) {
                            // Pop up to the start destination of the graph to avoid building
                            // up a large stack of destinations on the back stack as users select tabs.
                            popUpTo(nav.graph.startDestinationId) {
                                // Save the state of the screens we pop up from.
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when reselecting the same item.
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item.
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(item.icon, contentDescription = item.label)
                },
                label = {
                    Text(item.label)
                },
                // Custom colors for selected and unselected states using the MaterialTheme.
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}