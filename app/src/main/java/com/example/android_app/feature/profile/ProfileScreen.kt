package com.example.android_app.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.android_app.feature.shopping.ShoppingScreen
import com.example.android_app.ui.theme.*

@Composable
fun ProfileScreen(onLogout: () -> Unit = {}) {
    var isExpiryAlertEnabled by remember { mutableStateOf(true) }
    var isShoppingReminderEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Avatar and User Info
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBC6u3T2jEw9rO6n5y_3w_qIu2Y10k1x8Yp6Y4xPqYn9B3eC6r7q1t7cT6o8p5E6d9y7eT6j3F7Q2v3V6f5uR8i0V1B_9V9Xy7x1c3C8R0k0v8w6p7w2W6i5L7t4x1O9A_6V9b5yIu9H6e2S4GfP2X0-R0L6QG8W4T0oFw3Vv3I0QxNlC4F9M_sA_Xp6K4X2f_N4J_o3B8T5I0kF_3g", // Placeholder girl avatar
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(SurfaceContainerHighest)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Elena Rodriguez",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextOnSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFE8F5E9)) // Light green
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Eco, contentDescription = "Eco", tint = Primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Eco-Master", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Primary)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Waste Stats Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F8E8)), // Very light yellow-green tint
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Eco, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Thống kê lãng phí", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$124", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Primary, letterSpacing = (-1).sp)
                    Text(".50", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.padding(bottom = 6.dp))
                }
                Text("Đã tiết kiệm trong tháng này", fontSize = 14.sp, color = TextSecondary)
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TƯƠNG ĐƯƠNG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("15 bữa ăn", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextOnSurface)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("THỰC PHẨM CỨU", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("8.2 kg", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextOnSurface)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Health Goals Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFE3F2FD), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFF1E88E5), modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Mục tiêu sức khỏe", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                    }
                    Text("SỬA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Chế độ ăn", fontSize = 14.sp, color = TextSecondary)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFF1F8E9))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Eat Clean", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = SurfaceContainer)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Chỉ số BMI", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("21.5", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bình\nthường", fontSize = 10.sp, color = Primary, lineHeight = 12.sp, modifier = Modifier.padding(bottom = 2.dp))
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Calo mục tiêu", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("1,850", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("kcal", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 2.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Notifications Settings Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFFFF3E0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFF57C00), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Thiết lập thông báo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cảnh báo hết hạn", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextOnSurface)
                        Text("Thông báo trước 2 ngày", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = isExpiryAlertEnabled,
                        onCheckedChange = { isExpiryAlertEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2196F3) // Blue switch as per mockup
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = SurfaceContainer)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nhắc nhở đi chợ", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextOnSurface)
                        Text("Khi tủ lạnh dưới 20%", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = isShoppingReminderEnabled,
                        onCheckedChange = { isShoppingReminderEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2196F3)
                        )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // Logout Button
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
            border = BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Đăng xuất", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(100.dp)) // padding for bottom nav
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
