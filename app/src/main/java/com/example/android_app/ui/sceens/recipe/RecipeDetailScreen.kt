package com.example.android_app.ui.sceens.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Timer
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.android_app.ui.sceens.login.RegisterScreen
import com.example.android_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(navController: NavController, recipeId: String? = "1") {
    val scrollState = rememberScrollState()
    
    // Giả lập data dựa theo id
    val recipeName = "Salad Bơ Trứng"

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextOnSurface)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = TextOnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Surface(
                color = SurfaceContainerLowest,
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainer,
                            contentColor = OnPrimaryContainer
                        )
                    ) {
                        Text("Bắt đầu nấu", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Header Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(240.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDqhCzRCVj--6_NDrYvm4zb68Vt3EbOQZjSNReTWxubKbDn7VUurXn5wXQ7nwtJNy-9w6yAvzuVN2ACdPmQ7ZhWcNhwD3aEXoCsj5ZvLm6n21dxLQkaCID5CAfa9KRTF23Y403rawnSDJlEv6JoxBVmpnTQCcHpSh9CjmVlMOdW1wd48LqOnvgNfYDZgv4b1bnUjriYLmGau3OgoP73jlPkzZnCY2wSGcLJAgIt1RCDVY0t5rHW0JHfqjxg-vlCmNHOvWbGNUqj90r8",
                    contentDescription = recipeName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title & Info
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(recipeName, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RecipeInfoBadge(icon = Icons.Default.Timer, text = "15 phút")
                    RecipeInfoBadge(icon = Icons.Default.LocalFireDepartment, text = "320 kcal")
                    RecipeInfoBadge(icon = Icons.Default.Person, text = "2 người")
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Ingredients
                Text("Nguyên liệu", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                Spacer(modifier = Modifier.height(16.dp))
                
                IngredientItem("Bơ sáp", "1 quả", true)
                IngredientItem("Trứng gà", "2 quả", true)
                IngredientItem("Xà lách", "100g", false)
                IngredientItem("Cà chua bi", "50g", false)
                IngredientItem("Sốt Mayonnaise", "2 muỗng", false)

                Spacer(modifier = Modifier.height(32.dp))

                // Steps
                Text("Các bước thực hiện", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                Spacer(modifier = Modifier.height(16.dp))
                
                StepItem(1, "Sơ chế", "Luộc trứng chín, bóc vỏ và cắt múi cau. Bơ lột vỏ, bỏ hạt, thái miếng vừa ăn. Rau xà lách rửa sạch, cắt nhỏ.")
                StepItem(2, "Trộn salad", "Cho xà lách, bơ, cà chua bi vào tô. Thêm sốt Mayonnaise, một chút muối và tiêu, trộn đều nhẹ tay.")
                StepItem(3, "Hoàn thành", "Bày ra đĩa, xếp trứng lên trên và thưởng thức ngay khi còn tươi ngon.")

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun RecipeInfoBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .background(SurfaceContainerLowest, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextOnSurface)
    }
}

@Composable
fun IngredientItem(name: String, amount: String, inFridge: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (inFridge) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (inFridge) Primary else TextSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, fontSize = 16.sp, color = TextOnSurface, modifier = Modifier.weight(1f))
        Text(amount, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
    }
}

@Composable
fun StepItem(step: Int, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(PrimaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(step.toString(), color = OnPrimaryContainer, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 14.sp, color = TextSecondary, lineHeight = 20.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecipeDetailScreenPreview() {
    RecipeDetailScreen(rememberNavController())
}