package com.juice.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.juice.app.ui.screen.AdminFormScreen
import com.juice.app.ui.screen.AdminScreen
import com.juice.app.ui.screen.MapScreen
import com.juice.app.ui.screen.StallListScreen
import kotlinx.serialization.Serializable

@Serializable object MapRoute
@Serializable object StallListRoute
@Serializable object AdminRoute
@Serializable data class AdminFormRoute(val stallId: Int = -1)

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Any
)

@Composable
fun JuiceNavGraph() {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem("Map", Icons.Default.Map, MapRoute),
        BottomNavItem("List", Icons.AutoMirrored.Filled.List, StallListRoute),
        BottomNavItem("Admin", Icons.Default.Settings, AdminRoute)
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            // Hide bottom bar on form screen
            val onFormScreen = currentDestination?.hasRoute<AdminFormRoute>() == true
            if (!onFormScreen) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any {
                                when (item.route) {
                                    is MapRoute -> it.hasRoute<MapRoute>()
                                    is StallListRoute -> it.hasRoute<StallListRoute>()
                                    is AdminRoute -> it.hasRoute<AdminRoute>()
                                    else -> false
                                }
                            } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MapRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<MapRoute> {
                MapScreen()
            }

            composable<StallListRoute> {
                StallListScreen()
            }

            composable<AdminRoute> {
                AdminScreen(
                    onAddStall = {
                        navController.navigate(AdminFormRoute())
                    },
                    onEditStall = { id ->
                        navController.navigate(AdminFormRoute(stallId = id))
                    }
                )
            }

            composable<AdminFormRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<AdminFormRoute>()
                val stallId = if (args.stallId == -1) null else args.stallId
                AdminFormScreen(
                    stallId = stallId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
