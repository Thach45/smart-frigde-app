package com.example.android_app.feature.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.android_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editGoal by remember { mutableStateOf<String?>(null) }

    var isExpiryAlertEnabled by remember { mutableStateOf(true) }
    var isShoppingReminderEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiState.isUpdateSuccess) {
        if (uiState.isUpdateSuccess) {
            Toast.makeText(context, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
            viewModel.clearUpdateSuccess()
            showEditDialog = false
        }
    }

    // Update form fields when user data is loaded
    LaunchedEffect(uiState.user) {
        uiState.user?.let {
            editName = it.name ?: ""
            editGoal = it.healthGoal
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading && uiState.user == null) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            val user = uiState.user
            val displayName = user?.name ?: "Người dùng SmartBite"
            val emailStr = user?.email ?: "loading..."
            val roleStr = if (user?.role == "ADMIN") "Quản trị viên" else "Thành viên"

            // Avatar and User Info
            AsyncImage(
                model = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=150&auto=format&fit=crop", // Premium Unsplash female avatar
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainerHighest)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = displayName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextOnSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = emailStr,
                fontSize = 14.sp,
                color = TextSecondary
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
                Text(roleStr, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Primary)
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                        Text(
                            text = "THAY ĐỔI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            modifier = Modifier.clickable {
                                editName = user?.name ?: ""
                                editGoal = user?.healthGoal
                                showEditDialog = true
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mục tiêu hiện tại", fontSize = 14.sp, color = TextSecondary)
                        val goalText = when (user?.healthGoal) {
                            "LOSE_WEIGHT" -> "Giảm cân"
                            "GAIN_MUSCLE" -> "Tăng cơ"
                            "MAINTAIN" -> "Giữ dáng"
                            else -> "Chưa thiết lập"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFF1F8E9))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(goalText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SurfaceContainer)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Đề xuất Calo ngày", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                val targetCalories = when (user?.healthGoal) {
                                    "LOSE_WEIGHT" -> "1,500"
                                    "GAIN_MUSCLE" -> "2,400"
                                    "MAINTAIN" -> "1,900"
                                    else -> "1,800"
                                }
                                Text(targetCalories, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("kcal", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 2.dp))
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Gợi ý từ AI", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (user?.healthGoal) {
                                    "LOSE_WEIGHT" -> "Ưu tiên đạm & rau"
                                    "GAIN_MUSCLE" -> "Giàu Protein & Carb"
                                    "MAINTAIN" -> "Cân bằng dinh dưỡng"
                                    else -> "Dựa theo thực phẩm tủ"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextOnSurface
                            )
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
                                checkedTrackColor = Primary
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
                                checkedTrackColor = Primary
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
        }

        Spacer(modifier = Modifier.height(100.dp)) // padding for bottom nav
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Cập nhật thông tin", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Tên người dùng") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Column {
                        Text("Mục tiêu sức khỏe", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        listOf(
                            Pair("LOSE_WEIGHT", "Giảm cân (Lose Weight)"),
                            Pair("GAIN_MUSCLE", "Tăng cơ (Gain Muscle)"),
                            Pair("MAINTAIN", "Giữ dáng (Maintain)")
                        ).forEach { (goalCode, goalLabel) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { editGoal = goalCode }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = editGoal == goalCode,
                                    onClick = { editGoal = goalCode }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(goalLabel, fontSize = 15.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(editName.trim(), editGoal)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Lưu lại")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}
