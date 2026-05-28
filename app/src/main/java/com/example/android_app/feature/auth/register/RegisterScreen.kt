package com.example.android_app.feature.auth.register

import androidx.hilt.navigation.compose.hiltViewModel

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.android_app.feature.shopping.ShoppingScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val OutlineVariantReg = Color(0xFFC5C9B1)
val TextOnSurfaceReg = Color(0xFF1A1C1C)
val TextSecondaryReg = Color(0xFF586062)
val InputBackgroundReg = Color(0xFFF1F2F6)
val ErrorColorReg = Color(0xFFBA1A1A)
val PrimaryReg = Color(0xFF506600)
val PrimaryContainerReg = Color(0xFFA4C639)
val OnPrimaryReg = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }

    val isLoading = uiState.isLoading
    var localErrorMessage by remember { mutableStateOf<String?>(null) }
    val errorMessage = localErrorMessage ?: uiState.errorMessage
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_LONG).show()
            viewModel.resetState()
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("SmartBite", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = PrimaryReg)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryReg)
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF9F9F9))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryContainerReg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Restaurant,
                    contentDescription = "Logo",
                    tint = PrimaryReg,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Welcome Text
            Text("Tham gia SmartBite", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextOnSurfaceReg)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Bắt đầu hành trình giảm lãng phí thực phẩm ngay hôm nay.",
                fontSize = 14.sp,
                color = TextSecondaryReg,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

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
                            Icon(Icons.Default.Warning, contentDescription = "Warning", tint = ErrorColorReg)
                            Text(errorMessage!!, fontSize = 14.sp, color = ErrorColorReg, modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Name Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Full Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryReg, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; localErrorMessage = null; viewModel.resetState() },
                    placeholder = { Text("Nguyễn Văn A", color = TextSecondaryReg) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = TextSecondaryReg) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = InputBackgroundReg,
                        focusedBorderColor = PrimaryContainerReg,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Email", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryReg, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; localErrorMessage = null; viewModel.resetState() },
                    placeholder = { Text("email@vi-du.com", color = TextSecondaryReg) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = TextSecondaryReg) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = InputBackgroundReg,
                        focusedBorderColor = PrimaryContainerReg,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Password", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryReg, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; localErrorMessage = null; viewModel.resetState() },
                    placeholder = { Text("••••••••", color = TextSecondaryReg) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = TextSecondaryReg) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password",
                                tint = TextSecondaryReg
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = InputBackgroundReg,
                        focusedBorderColor = PrimaryContainerReg,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryContainerReg)
                )
                val annotatedString = buildAnnotatedString {
                    append("Tôi đồng ý với các ")
                    withStyle(style = SpanStyle(color = PrimaryReg, fontWeight = FontWeight.Bold)) {
                        append("Điều khoản Dịch vụ")
                    }
                    append(" và ")
                    withStyle(style = SpanStyle(color = PrimaryReg, fontWeight = FontWeight.Bold)) {
                        append("Chính sách Bảo mật")
                    }
                    append(" của SmartBite.")
                }
                Text(
                    text = annotatedString,
                    fontSize = 12.sp,
                    color = TextSecondaryReg,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        localErrorMessage = "Vui lòng điền đầy đủ thông tin"
                        return@Button
                    }
                    if (password.length < 6) {
                        localErrorMessage = "Mật khẩu phải dài ít nhất 6 ký tự"
                        return@Button
                    }
                    if (!agreedToTerms) {
                        localErrorMessage = "Bạn phải đồng ý với Điều khoản Dịch vụ"
                        return@Button
                    }
                    
                    viewModel.register(email, password, name)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainerReg,
                    contentColor = OnPrimaryReg,
                    disabledContainerColor = PrimaryContainerReg.copy(alpha = 0.5f)
                ),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = OnPrimaryReg, strokeWidth = 2.dp)
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Đăng ký", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = "Forward")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariantReg)
                Text("HOẶC", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = TextSecondaryReg, modifier = Modifier.padding(horizontal = 16.dp))
                HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariantReg)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Back to Login Link
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Đã có tài khoản? ", fontSize = 14.sp, color = TextSecondaryReg)
                Text("Đăng nhập", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryReg, modifier = Modifier.clickable {
                    navController.popBackStack()
                })
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Decorative image
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAzm-w9vP-3n73e3J8i_qBpxv7M4H7pC9b3iXgC44o3b4wF8y1j4D5D-P0x-H_9mQ2kR7133O_8O1u3V8Bf0tGzXn5mK_9pC5s1gT2hI3Vq0iN2Xz-E1mUaQ6l9WjP2pXyq_Y6H_6V9b5yIu9H6e2S4GfP2X0-R0L6QG8W4T0oFw3Vv3I0QxNlC4F9M_sA_Xp6K4X2f_N4J_o3B8T5I0kF_3g",
                contentDescription = "Decoration",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(120.dp)
                    .alpha(0.5f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(rememberNavController())
}