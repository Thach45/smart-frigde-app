package com.example.android_app.feature.assistant.recipe

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.android_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSuggestionScreen(
    navController: NavController,
    targetItemId: String,
    viewModel: RecipeSuggestionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Load AI recipe suggestion on launch
    LaunchedEffect(targetItemId) {
        viewModel.loadSuggestion(targetItemId)
    }

    // Handle acceptance success
    LaunchedEffect(uiState.isAccepted) {
        if (uiState.isAccepted) {
            Toast.makeText(
                context,
                "Đã thêm vào Thực đơn & cập nhật nguyên liệu thiếu vào Danh sách đi chợ!",
                Toast.LENGTH_LONG
            ).show()
            // Go back to the main home
            navController.popBackStack("home", inclusive = false)
        }
    }

    // Show error toast
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    val meal = uiState.meal

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Gợi Ý Từ AI Gemini", fontWeight = FontWeight.Bold, color = Primary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            if (meal != null) {
                Surface(
                    color = SurfaceContainerLowest,
                    shadowElevation = 16.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, Primary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                            enabled = !uiState.isLoading
                        ) {
                            Text("Bỏ qua", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Button(
                            onClick = { viewModel.acceptMeal(meal.id) },
                            modifier = Modifier
                                .weight(1.5f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = OnPrimary, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Chốt")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Chốt nấu món này", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading && meal == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = Primary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Primary)
                    Text(
                        text = "Gemini đang lên công thức...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Đang tính toán các nguyên liệu tối ưu trong tủ",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        } else if (meal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.errorMessage ?: "Không tìm thấy gợi ý công thức",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        } else {
            RecipeContent(
                meal = meal,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
