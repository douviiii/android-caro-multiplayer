package com.sopa.co_caro

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameModeSelector(
    onAISelected: () -> Unit,
    onMultiplayerSelected: () -> Unit,
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
    
    val card1Scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, delayMillis = 200),
        label = "card1Scale"
    )
    
    val card2Scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, delayMillis = 400),
        label = "card2Scale"
    )
    
    val instructionsAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, delayMillis = 600),
        label = "instructionsAlpha"
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = LocalizedString("select_game_mode"),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(titleScale)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // AI Mode Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .scale(card1Scale),
            onClick = onAISelected,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🤖",
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = LocalizedString("play_with_ai"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = LocalizedString("ai_description"),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Multiplayer Mode Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .scale(card2Scale),
            onClick = onMultiplayerSelected,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "👥",
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = LocalizedString("play_two_players"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = LocalizedString("multiplayer_description"),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Instructions
        Text(
            text = "💡 ${LocalizedString("instructions")}\n${LocalizedString("ai_instructions")}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f * instructionsAlpha),
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            modifier = Modifier.alpha(instructionsAlpha)
        )
    }
}
