package com.sopa.co_caro

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameBoard(
    gameState: GameState,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hiển thị trạng thái game
        GameStatusText(gameState = gameState)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bàn cờ với scroll cho 9x9
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .verticalScroll(rememberScrollState())
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp)
            ) {
                for (row in 0 until gameState.boardSize) {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (col in 0 until gameState.boardSize) {
                            GameCell(
                                player = gameState.board[row][col],
                                onClick = { onCellClick(row, col) },
                                isClickable = gameState.board[row][col] == null && 
                                            gameState.gameStatus == GameStatus.PLAYING &&
                                            gameState.currentPlayer == Player.HUMAN,
                                boardSize = gameState.boardSize
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameCell(
    player: Player?,
    onClick: () -> Unit,
    isClickable: Boolean,
    boardSize: Int,
    modifier: Modifier = Modifier
) {
    // Animation states
    val cellScale by animateFloatAsState(
        targetValue = if (player != null) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "cellScale"
    )
    
    val cellAlpha by animateFloatAsState(
        targetValue = if (isClickable) 1f else 0.7f,
        animationSpec = tween(300),
        label = "cellAlpha"
    )
    // Kích thước ô rất nhỏ cho bàn cờ 9x9
    val cellSize = when (boardSize) {
        3 -> 60.dp
        6 -> 40.dp
        9 -> 30.dp
        else -> 40.dp
    }
    
    val fontSize = when (boardSize) {
        3 -> 30.sp
        6 -> 20.sp
        9 -> 15.sp
        else -> 20.sp
    }
    
    Box(
        modifier = modifier
            .size(cellSize)
            .scale(cellScale)
            .alpha(cellAlpha)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(enabled = isClickable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when (player) {
            Player.HUMAN -> {
                Text(
                    text = "X",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue,
                    textAlign = TextAlign.Center
                )
            }
            Player.AI -> {
                Text(
                    text = "O",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
            null -> {
                // Ô trống
            }
        }
    }
}

@Composable
fun GameStatusText(
    gameState: GameState,
    modifier: Modifier = Modifier
) {
    // Animation states
    val statusScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "statusScale"
    )
    
    val statusAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500),
        label = "statusAlpha"
    )
    val statusText = when (gameState.gameStatus) {
        GameStatus.PLAYING -> {
            if (gameState.currentPlayer == Player.HUMAN) {
                "${LocalizedString("your_turn")} (X)"
            } else {
                "${LocalizedString("ai_turn")} (O)"
            }
        }
        GameStatus.HUMAN_WIN -> "🎉 ${LocalizedString("you_win")}"
        GameStatus.AI_WIN -> "😢 ${LocalizedString("ai_wins")}"
        GameStatus.DRAW -> "🤝 ${LocalizedString("draw")}"
    }
    
    val statusColor = when (gameState.gameStatus) {
        GameStatus.PLAYING -> MaterialTheme.colorScheme.primary
        GameStatus.HUMAN_WIN -> Color.Green
        GameStatus.AI_WIN -> Color.Red
        GameStatus.DRAW -> Color(0xFFFF9800) // Orange color
    }
    
    Text(
        text = statusText,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = statusColor,
        textAlign = TextAlign.Center,
        modifier = modifier
            .scale(statusScale)
            .alpha(statusAlpha)
    )
}
