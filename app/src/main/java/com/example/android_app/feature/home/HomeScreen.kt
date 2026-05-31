package com.example.android_app.feature.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.android_app.ui.theme.Primary
import com.example.android_app.ui.theme.PrimaryContainer
import com.example.android_app.ui.theme.SurfaceContainerLowest
import com.example.android_app.ui.theme.TertiaryContainer
import com.example.android_app.ui.theme.TextSecondary
import com.example.android_app.feature.assistant.voice.SpeechRecognizerManager
import com.example.android_app.feature.assistant.voice.VoiceViewModel

data class FoodItem(
    val id: String,
    val name: String,
    val quantity: String,
    val progress: Float,
    val statusText: String,
    val statusColor: Color,
    val statusTextColor: Color = statusColor,
    val badgeIcon: ImageVector,
    val imageUrl: String
)

data class FoodCategory(
    val name: String,
    val imageUrl: String
)

val defaultCategories = listOf(
    FoodCategory("Tất cả", "https://images.unsplash.com/photo-1542838132-92c53300491e?q=80&w=600&auto=format&fit=crop"),
    FoodCategory("Rau củ", "https://images.unsplash.com/photo-1540420773420-3366772f4999?q=80&w=600&auto=format&fit=crop"),
    FoodCategory("Thịt cá", "https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?q=80&w=600&auto=format&fit=crop"),
    FoodCategory("Trái cây", "https://images.unsplash.com/photo-1610832958506-aa56368176cf?q=80&w=600&auto=format&fit=crop"),
    FoodCategory("Đồ uống", "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?q=80&w=600&auto=format&fit=crop"),
    FoodCategory("Gia vị", "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?q=80&w=600&auto=format&fit=crop"),
    FoodCategory("Khác", "https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b?q=80&w=600&auto=format&fit=crop")
)


@Composable
fun HomeScreen(
    onCategoryClick: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    voiceViewModel: VoiceViewModel = hiltViewModel()
) {
    // We still load items to pre-cache or maybe show a summary, but for now we just show categories
    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item(span = { GridItemSpan(2) }) {
            VoiceAssistantHeroCard(voiceViewModel = voiceViewModel)
        }
        
        item(span = { GridItemSpan(2) }) {
            Text(
                text = "Danh Mục Thực Phẩm",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        items(defaultCategories) { category ->
            CategoryCard(category = category, onClick = { onCategoryClick(category.name) })
        }

        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom nav
        }
    }
}

@Composable
fun CategoryCard(category: FoodCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Thumbnail Image
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient Overlay for Text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 150f
                        )
                    )
            )
            
            // Category Name
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun VoiceAssistantHeroCard(voiceViewModel: VoiceViewModel) {
    val context = LocalContext.current
    val uiState by voiceViewModel.uiState.collectAsState()

    var isRecording by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("Thử nói \"Nhà còn trứng không?\"") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Pulsing animation for recording state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val manager = remember {
        SpeechRecognizerManager(
            context = context,
            onStart = {
                isRecording = true
                statusMessage = "Đang nghe bạn nói..."
                errorMessage = null
            },
            onResult = { result ->
                isRecording = false
                recognizedText = result
                statusMessage = "Bấm lại để nói tiếp"
                if (result.isNotEmpty()) {
                    voiceViewModel.sendTextToBackend(result)
                }
            },
            onError = { err ->
                isRecording = false
                statusMessage = "Không nghe rõ, vui lòng thử lại"
                errorMessage = err
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            manager.startListening()
        } else {
            errorMessage = "Vui lòng cấp quyền ghi âm trong Cài đặt"
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            manager.destroy()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceContainerLowest)
            .padding(24.dp)
    ) {
        // Decorative blobs
        Box(
            modifier = Modifier
                .size(128.dp)
                .offset(x = (-40).dp, y = (-40).dp)
                .background(PrimaryContainer.copy(alpha = 0.2f), CircleShape)
                .blur(30.dp)
                .align(Alignment.TopStart)
        )
        Box(
            modifier = Modifier
                .size(128.dp)
                .offset(x = 40.dp, y = 40.dp)
                .background(TertiaryContainer.copy(alpha = 0.2f), CircleShape)
                .blur(30.dp)
                .align(Alignment.BottomEnd)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Title
            Text(
                text = "Trợ Lý Giọng Nói AI",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            // Mic button with pulsing circle if recording
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(110.dp)
            ) {
                if (isRecording) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.2f))
                    )
                }
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRecording) Color(0xFFE53935) else PrimaryContainer
                        )
                        .shadow(elevation = 4.dp, shape = CircleShape)
                        .clickable {
                            if (isRecording) {
                                manager.stopListening()
                                isRecording = false
                            } else {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) {
                                    manager.startListening()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = if (isRecording) Color.White else Primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Status or Helper input
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(Color(0xFFF1F2F6))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Hearing else Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusMessage,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            errorMessage?.let { err ->
                Text(
                    text = err,
                    color = Color(0xFFD32F2F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Recognized Text
            if (recognizedText.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF9F9F9))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Bạn nói:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recognizedText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }

            // Loading state
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Backend Response
            if (uiState.reply.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Trợ lý phản hồi:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.reply,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}
