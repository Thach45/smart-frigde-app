package com.example.android_app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.example.android_app.data.local.TokenStore
import com.example.android_app.feature.inventory.add.AddManualScreen
import com.example.android_app.feature.auth.login.LoginScreen
import com.example.android_app.feature.auth.register.RegisterScreen
import com.example.android_app.feature.health.mealplan.*
import com.example.android_app.feature.shopping.ShoppingScreen
import com.example.android_app.feature.profile.ProfileScreen
import com.example.android_app.ui.components.VoiceAssistantBottomSheet
import com.example.android_app.ui.theme.OnPrimary
import com.example.android_app.ui.theme.PrimaryContainer
import com.example.android_app.ui.theme.Background
import com.example.android_app.ui.theme.TextSecondary
import com.example.android_app.ui.theme.SurfaceContainerLowest
import com.example.android_app.ui.theme.Primary
import com.example.android_app.ui.theme.OutlineVariant
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.android_app.feature.home.HomeScreen
import com.example.android_app.feature.home.CategoryDetailScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Login    : Screen("login",      "Đăng Nhập",   Icons.Default.Login)
    object Home     : Screen("home",       "Tủ Lạnh",     Icons.Default.Kitchen)
    object Menu     : Screen("menu",       "Thực Đơn",    Icons.Default.RestaurantMenu)
    object Shopping : Screen("shopping",   "Đi Chợ",      Icons.Default.ShoppingCart)
    object Profile  : Screen("profile",    "Cá Nhân",     Icons.Default.Person)
    object AddManual: Screen("add_manual", "Thêm Thủ Công",Icons.Default.Add)
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    // Đọc token từ SharedPreferences ngay khi compose lần đầu
    val context = LocalContext.current
    val tokenStore = remember { TokenStore(context) }
    val startDestination = if (tokenStore.isLoggedIn) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ── Màn hình Login (KHÔNG có bottom bar) ──────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }

        // ── Các màn hình chính (CÓ bottom bar) ───────────────────────────────
        composable(Screen.Home.route) {
            MainScaffold(navController) {
                HomeScreen(onCategoryClick = { categoryName ->
                    navController.navigate("category_detail/$categoryName")
                })
            }
        }
        composable(Screen.Menu.route) {
            MainScaffold(navController) { MealScreen(navController) }
        }
        composable(Screen.Shopping.route) {
            MainScaffold(navController) { ShoppingScreen() }
        }
        composable(Screen.Profile.route) {
            MainScaffold(navController) {
                ProfileScreen(onLogout = {
                    tokenStore.clear()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                })
            }
        }

        // ── Màn hình phụ (KHÔNG có bottom bar) ───────────────────────────────
        composable(Screen.AddManual.route) {
            AddManualScreen(navController)
        }
        composable("scanner") {
            com.example.android_app.feature.inventory.scanner.ScannerScreen(navController)
        }
        composable("category_detail/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Khác"
            CategoryDetailScreen(navController, categoryName)
        }
        composable("food_detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            com.example.android_app.feature.inventory.detail.FoodDetailScreen(navController, id)
        }
        composable("recipe_suggestion/{targetItemId}") { backStackEntry ->
            val targetItemId = backStackEntry.arguments?.getString("targetItemId") ?: ""
            com.example.android_app.feature.assistant.recipe.RecipeSuggestionScreen(navController, targetItemId)
        }
        composable("recipe_detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            com.example.android_app.feature.assistant.recipe.RecipeDetailScreen(navController, id)
        }
    }
}

/** Scaffold dùng chung cho các tab chính — chứa TopBar + BottomBar */
@Composable
fun MainScaffold(navController: NavHostController, content: @Composable () -> Unit) {
    var showVoiceAssistant by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = { TopNavBarGlass(onMicClick = { showVoiceAssistant = true }) },
        bottomBar = { BottomNavBarGlass(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }

    if (showVoiceAssistant) {
        VoiceAssistantBottomSheet(onDismissRequest = { showVoiceAssistant = false })
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, fontSize = 24.sp, color = TextSecondary)
    }
}

@Composable
fun TopNavBarGlass(onMicClick: () -> Unit = {}) {
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
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Primary)
            }
            Text(
                "SmartBite",
                style = MaterialTheme.typography.titleMedium,
                color = Primary
            )
            IconButton(onClick = onMicClick) {
                Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Primary)
            }
        }
    }
}

@Composable
fun BottomNavBarGlass(navController: NavHostController) {
    val items = listOf(Screen.Home, Screen.Menu, Screen.Shopping, Screen.Profile)

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
                    // Spacer cho FAB ở giữa
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

        // FAB Thêm ở giữa
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
                        .clickable { navController.navigate("scanner") }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = OnPrimary, modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Thêm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }
        }
    }
}

@Composable
fun NavBarItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = label, tint = if (isSelected) Primary else TextSecondary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Primary else TextSecondary
        )
    }
}