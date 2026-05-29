package com.example.android_app.feature.assistant.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.android_app.domain.model.Meal
import com.example.android_app.ui.theme.*

@Composable
fun RecipeContent(
    meal: Meal,
    modifier: Modifier = Modifier,
    scrollState: androidx.compose.foundation.ScrollState = rememberScrollState()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Header Image
        val imageModel = meal.imageUrl?.takeIf { it.isNotBlank() } ?: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=600&auto=format&fit=crop"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(240.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            AsyncImage(
                model = imageModel,
                contentDescription = meal.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title & Info
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(meal.title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
            
            if (!meal.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(meal.description, fontSize = 14.sp, color = TextSecondary, lineHeight = 20.sp)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RecipeInfoBadge(icon = Icons.Default.Timer, text = "${meal.prepTime ?: 15} phút", modifier = Modifier.weight(1f))
                RecipeInfoBadge(icon = Icons.Default.LocalFireDepartment, text = "${meal.calories ?: 350} kcal", modifier = Modifier.weight(1f))
                RecipeInfoBadge(icon = Icons.Default.Person, text = "2 người", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Ingredients
            Text("Nguyên liệu sử dụng", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
            Spacer(modifier = Modifier.height(16.dp))
            
            meal.ingredients.forEach { ing ->
                val formattedQty = if (ing.quantity != null) {
                    if (ing.quantity % 1.0 == 0.0) ing.quantity.toInt().toString() else ing.quantity.toString()
                } else {
                    ""
                }
                IngredientItem(ing.name, "$formattedQty ${ing.unit ?: ""}".trim(), true)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Steps
            Text("Các bước thực hiện", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
            Spacer(modifier = Modifier.height(16.dp))
            
            meal.instructions.forEachIndexed { index, step ->
                StepItem(index + 1, "Bước ${index + 1}", step)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RecipeInfoBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(SurfaceContainerLowest, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextOnSurface)
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
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, fontSize = 15.sp, color = TextOnSurface, modifier = Modifier.weight(1f))
        Text(amount, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Primary)
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
                .size(28.dp)
                .background(PrimaryContainer.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(step.toString(), color = Primary, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 14.sp, color = TextSecondary, lineHeight = 20.sp)
        }
    }
}
