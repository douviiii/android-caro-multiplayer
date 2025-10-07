package com.sopa.co_caro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DifficultySelector(
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states
    val titleScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "titleScale"
    )
    
    val chip1Scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, delayMillis = 100),
        label = "chip1Scale"
    )
    
    val chip2Scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, delayMillis = 200),
        label = "chip2Scale"
    )
    
    val chip3Scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, delayMillis = 300),
        label = "chip3Scale"
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = LocalizedString("select_difficulty"),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(titleScale)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Difficulty.values().forEachIndexed { index, difficulty ->
                val scale = when (index) {
                    0 -> chip1Scale
                    1 -> chip2Scale
                    2 -> chip3Scale
                    else -> 1f
                }
                
                DifficultyButton(
                    difficulty = difficulty,
                    isSelected = difficulty == selectedDifficulty,
                    onClick = { onDifficultySelected(difficulty) },
                    modifier = Modifier.scale(scale)
                )
            }
        }
    }
}

@Composable
fun DifficultyButton(
    difficulty: Difficulty,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    val textColor = if (isSelected) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getDifficultyDisplayName(difficulty),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = getDifficultyDescription(difficulty),
                fontSize = 10.sp,
                color = textColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun getDifficultyDisplayName(difficulty: Difficulty): String {
    return when (difficulty) {
        Difficulty.EASY -> LocalizedString("easy")
        Difficulty.MEDIUM -> LocalizedString("medium")
        Difficulty.HARD -> LocalizedString("hard")
    }
}

@Composable
private fun getDifficultyDescription(difficulty: Difficulty): String {
    return when (difficulty) {
        Difficulty.EASY -> "${LocalizedString("easy_3x3")} - ${LocalizedString("ai_random")}"
        Difficulty.MEDIUM -> "${LocalizedString("medium_6x6")} - ${LocalizedString("ai_smart")}"
        Difficulty.HARD -> "${LocalizedString("hard_9x9")} - ${LocalizedString("ai_expert")}"
    }
}
