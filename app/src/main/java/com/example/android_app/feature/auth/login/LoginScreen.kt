package com.example.android_app.feature.auth.login

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

val Primary = Color(0xFF506600)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFA4C639)
val OnPrimaryContainer = Color(0xFF3E5000)
val Background = Color(0xFFF9F9F9)
val OutlineVariant = Color(0xFFC5C9B1)
val TextOnSurface = Color(0xFF1A1C1C)
val TextSecondary = Color(0xFF586062)
val InputBackground = Color(0xFFF1F2F6)
val ErrorColor = Color(0xFFBA1A1A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    // Khi đăng nhập thành công -> về Home, xóa sạch back-stack để không bấm Back quay lại Login
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    val isLoading = uiState is LoginUiState.Loading
    val errorMessage = (uiState as? LoginUiState.Error)?.message

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("SmartBite", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Primary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAqbo53DXU-CxPQ--huBdChJOjjYnDxtGqNCLWcxQhFuws5jTVXaF_HtxWbLMx3cWcxgZPe00Qoy-jKrehIJ0ayx4ibEYYwagLJUT5oSSah-Tt2kcsYgFfm_fqPhe8fRx0gA5WsqqJdwtGDgE49hvLtfR1s45NljMzidTicpQ4Lyxnc8MhmrxeEVMLY3vOI9uSotH3BWmIyAIDjOEBfr1zqws4Gn8OyBt-Sh57DGcA8u8TKWXg8dsDmllnv1yiURTF2ABul9abIN2rQ",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.2f))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome Text
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Chào mừng trở lại!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Quản lý thực phẩm tươi ngon mỗi ngày cùng SmartBite.", fontSize = 16.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Error Message
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDAD6))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = "Warning", tint = ErrorColor)
                            Text(errorMessage, fontSize = 14.sp, color = ErrorColor, modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Email Input
            Column {
                Text("EMAIL", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = TextSecondary, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (uiState is LoginUiState.Error) viewModel.resetState()
                    },
                    placeholder = { Text("example@gmail.com", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    isError = errorMessage != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = InputBackground,
                        focusedBorderColor = PrimaryContainer,
                        unfocusedBorderColor = Color.Transparent,
                        errorBorderColor = ErrorColor,
                        errorContainerColor = Color(0xFFFFDAD6).copy(alpha = 0.3f)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("MẬT KHẨU", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = TextSecondary)
                    Text("Quên mật khẩu?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.clickable { })
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (uiState is LoginUiState.Error) viewModel.resetState()
                    },
                    placeholder = { Text("••••••••", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = TextSecondary) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = TextSecondary
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    isError = errorMessage != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = InputBackground,
                        focusedBorderColor = PrimaryContainer,
                        unfocusedBorderColor = Color.Transparent,
                        errorBorderColor = ErrorColor,
                        errorContainerColor = Color(0xFFFFDAD6).copy(alpha = 0.3f)
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainer,
                    contentColor = OnPrimary,
                    disabledContainerColor = PrimaryContainer.copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = OnPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Đang xử lý...", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Text("Đăng nhập", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant)
                Text("HOẶC TIẾP TỤC VỚI", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 16.dp))
                HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, OutlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCVMs7VSdPwxGSOXdyCS7s4Y3gEa_x9jRNkpdOAsqVoXt7Jec3WCW6xREmLoqMapAOHO44H8HGo5ch3q1sghPW6NHe7ktOKJcDQmSH4SDYoUV1I1A_e027aVFQ0w2Tjjx60f-UxPBw1aW4ZszHebEIvhNWiu1KBOOVbEiBbGi-HRpsiZsK-lTaXZmv4QBF8OuWqgtLtKhCGZCSGSG-_Teqd9WNPLK_rNq29jlQqI8GX4oNvJcdHqGolfF1NKVKFd1Nee_56X05nNWkc",
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google", fontSize = 16.sp, color = TextOnSurface)
                }

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, OutlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Text("🍎", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apple", fontSize = 16.sp, color = TextOnSurface)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Signup Link
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Chưa có tài khoản? ", fontSize = 14.sp, color = TextSecondary)
                Text("Đăng ký ngay", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.clickable {
                    navController.navigate("register")
                })
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
