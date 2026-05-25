package com.example.android_app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.android_app.ui.sceens.home.*
import com.example.android_app.ui.sceens.meal.Background
import com.example.android_app.ui.sceens.meal.MealScreen
import com.example.android_app.ui.sceens.meal.OutlineVariant
import com.example.android_app.ui.sceens.meal.Primary
import com.example.android_app.ui.sceens.meal.SurfaceContainerLowest
import com.example.android_app.ui.sceens.meal.TextSecondary
import com.example.android_app.ui.theme.OnPrimary
import com.example.android_app.ui.theme.PrimaryContainer

sealed class Screen(val route: String, val label: String, val icon: String) {
    object Home : Screen("home", "Tủ Lạnh", "🧊")
    object Menu : Screen("menu", "Thực Đơn", "🍽")
    object Shopping : Screen("shopping", "Đi Chợ", "🛒")
    object Profile : Screen("profile", "Cá Nhân", "👤")
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    Scaffold(
        containerColor = Background,
        topBar = { TopNavBarGlass() },
        bottomBar = { BottomNavBarGlass(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Menu.route) {
                MealScreen()
            }
            composable(Screen.Shopping.route) {
                PlaceholderScreen("Màn hình Đi Chợ")
            }
            composable(Screen.Profile.route) {
                PlaceholderScreen("Màn hình Cá Nhân")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, fontSize = 24.sp, color = TextSecondary)
    }
}

@Composable
fun TopNavBarGlass() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(SurfaceContainerLowest.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {}) {
                Text("☰", fontSize = 24.sp, color = Primary)
            }
            Text("SmartBite", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
            IconButton(onClick = {}) {
                Text("🎤", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun BottomNavBarGlass(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Menu,
        Screen.Shopping,
        Screen.Profile
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest.copy(alpha = 0.8f))
            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 8.dp)
                .height(60.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, screen ->
                if (index == 2) {
                    // Spacer for FAB
                    Box(modifier = Modifier.width(64.dp))
                }
                
                val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                
                NavBarItem(
                    icon = screen.icon, 
                    label = screen.label, 
                    isSelected = isSelected,
                    onClick = {
                        navController.navigate(screen.route) {
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
        
        // Custom FAB overlapping the top
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-32).dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(SurfaceContainerLowest, CircleShape)
                        .padding(4.dp)
                        .clickable { /* Handle FAB click */ }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", fontSize = 32.sp, color = OnPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Thêm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }
        }
    }
}

@Composable
fun NavBarItem(icon: String, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick)
    ) {
        Text(icon, fontSize = 24.sp, color = if (isSelected) Primary else TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Primary else TextSecondary
        )
    }
}