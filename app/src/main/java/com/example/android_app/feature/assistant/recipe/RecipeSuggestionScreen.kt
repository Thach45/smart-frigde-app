package com.example.android_app.feature.assistant.recipe

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.android_app.ui.theme.*
import kotlin.math.absoluteValue

data class RecipeSuggestion(
    val id: Int,
    val name: String,
    val time: String,
    val matchPercentage: Int,
    val imageUrl: String,
    val keyIngredients: List<String>
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RecipeSuggestionScreen(navController: NavController) {
    val recipes = remember {
        listOf(
            RecipeSuggestion(1, "Salad Bơ Trứng", "15 phút", 95, "https://lh3.googleusercontent.com/aida-public/AB6AXuDqhCzRCVj--6_NDrYvm4zb68Vt3EbOQZjSNReTWxubKbDn7VUurXn5wXQ7nwtJNy-9w6yAvzuVN2ACdPmQ7ZhWcNhwD3aEXoCsj5ZvLm6n21dxLQkaCID5CAfa9KRTF23Y403rawnSDJlEv6JoxBVmpnTQCcHpSh9CjmVlMOdW1wd48LqOnvgNfYDZgv4b1bnUjriYLmGau3OgoP73jlPkzZnCY2wSGcLJAgIt1RCDVY0t5rHW0JHfqjxg-vlCmNHOvWbGNUqj90r8", listOf("Bơ sắp hỏng", "Trứng gà")),
            RecipeSuggestion(2, "Bò Xào Cà Chua", "25 phút", 80, "https://lh3.googleusercontent.com/aida-public/AB6AXuAqbo53DXU-CxPQ--huBdChJOjjYnDxtGqNCLWcxQhFuws5jTVXaF_HtxWbLMx3cWcxgZPe00Qoy-jKrehIJ0ayx4ibEYYwagLJUT5oSSah-Tt2kcsYgFfm_fqPhe8fRx0gA5WsqqJdwtGDgE49hvLtfR1s45NljMzidTicpQ4Lyxnc8MhmrxeEVMLY3vOI9uSotH3BWmIyAIDjOEBfr1zqws4Gn8OyBt-Sh57DGcA8u8TKWXg8dsDmllnv1yiURTF2ABul9abIN2rQ", listOf("Thịt bò băm", "Cà chua bi")),
            RecipeSuggestion(3, "Cá Hồi Áp Chảo", "20 phút", 70, "https://lh3.googleusercontent.com/aida-public/AB6AXuAzm-w9vP-3n73e3J8i_qBpxv7M4H7pC9b3iXgC44o3b4wF8y1j4D5D-P0x-H_9mQ2kR7133O_8O1u3V8Bf0tGzXn5mK_9pC5s1gT2hI3Vq0iN2Xz-E1mUaQ6l9WjP2pXyq_Y6H_6V9b5yIu9H6e2S4GfP2X0-R0L6QG8W4T0oFw3Vv3I0QxNlC4F9M_sA_Xp6K4X2f_N4J_o3B8T5I0kF_3g", listOf("Cá hồi phi lê", "Xà lách"))
        )
    }

    val pagerState = rememberPagerState(pageCount = { recipes.size })

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Nấu Gì Hôm Nay?", fontWeight = FontWeight.Bold, color = Primary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(PrimaryContainer.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gợi ý từ đồ sắp hỏng", color = Primary, fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val recipe = recipes[page]
                
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

                            // We animate the scaleX + scaleY, between 85% and 100%
                            lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale
                            }

                            // We animate the alpha, between 50% and 100%
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        },
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = recipe.imageUrl,
                            contentDescription = recipe.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        // Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                        startY = 300f
                                    )
                                )
                        )
                        
                        // Content
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(PrimaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Phù hợp ${recipe.matchPercentage}%", color = OnPrimaryContainer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(recipe.name, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = "Time", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(recipe.time, color = Color.White.copy(alpha = 0.7f))
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Dùng nguyên liệu:", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                recipe.keyIngredients.forEach { ingredient ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(ingredient, color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Swipe actions (Dislike / Like)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White, CircleShape)
                        // .border(1.dp, Color(0xFFE53935).copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Bỏ qua", tint = Color(0xFFE53935), modifier = Modifier.size(32.dp))
                }
                
                IconButton(
                    onClick = { 
                        navController.navigate("recipe_detail/${recipes[pagerState.currentPage].id}") 
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .background(PrimaryContainer, CircleShape)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = "Lưu lại", tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
