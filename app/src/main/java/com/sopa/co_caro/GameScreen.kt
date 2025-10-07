package com.sopa.co_caro

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    modifier: Modifier = Modifier
) {
    var gameState by remember { mutableStateOf(GameLogic.resetGame(Difficulty.EASY)) }
    var showModeSelector by remember { mutableStateOf(true) }
    var showDifficultySelector by remember { mutableStateOf(false) }
    var showBluetoothConnection by remember { mutableStateOf(false) }
    var showLanguageSelector by remember { mutableStateOf(false) }
    
    // Xử lý nước đi của AI (chỉ khi chế độ AI)
    LaunchedEffect(gameState.currentPlayer, gameState.gameStatus, gameState.gameMode) {
        if (gameState.currentPlayer == Player.AI && 
            gameState.gameStatus == GameStatus.PLAYING && 
            gameState.gameMode == GameMode.AI) {
            delay(1000) // Tăng delay để giảm computation load
            val aiMove = GameLogic.getAIMove(gameState)
            gameState = GameLogic.makeMove(gameState, aiMove.first, aiMove.second)
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tiêu đề game
        Text(
            text = "🎮 Cờ Caro",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        AnimatedContent(
            targetState = when {
                showLanguageSelector -> "language"
                showBluetoothConnection -> "bluetooth"
                showDifficultySelector -> "difficulty"
                showModeSelector -> "mode"
                else -> "game"
            },
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            },
            label = "screenTransition"
        ) { screen ->
            when (screen) {
                "language" -> {
                    // Màn hình chọn ngôn ngữ
                    LanguageSelector(
                        onLanguageSelected = { language ->
                            showLanguageSelector = false
                        }
                    )
                }
                "bluetooth" -> {
                    // Màn hình kết nối Bluetooth
                    BluetoothConnectionScreen(
                        onBack = {
                            showBluetoothConnection = false
                            showModeSelector = true
                        },
                        onGameStart = { newGameState ->
                            gameState = newGameState
                            showBluetoothConnection = false
                            showModeSelector = false
                        }
                    )
                }
                "difficulty" -> {
                    Column {
                        // Màn hình chọn độ khó cho AI
                        DifficultySelector(
                            selectedDifficulty = gameState.difficulty,
                            onDifficultySelected = { difficulty ->
                                gameState = GameLogic.resetGame(difficulty, GameMode.AI)
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = {
                                gameState = GameLogic.resetGame(gameState.difficulty, GameMode.AI)
                                showDifficultySelector = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = LocalizedString("start_playing_ai"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedButton(
                            onClick = {
                                showDifficultySelector = false
                                showModeSelector = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(LocalizedString("back"))
                        }
                    }
                }
                "mode" -> {
                    Column {
                        // Màn hình chọn chế độ chơi
                        GameModeSelector(
                            onAISelected = {
                                showModeSelector = false
                                showDifficultySelector = true
                            },
                            onMultiplayerSelected = {
                                showBluetoothConnection = true
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Language selector button
                        OutlinedButton(
                            onClick = {
                                showLanguageSelector = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🌐 ${LocalizedString("language")}")
                        }
                    }
                }
                "game" -> {
                    Column {
                        // Màn hình game chính
                        GameBoard(
                            gameState = gameState,
                            onCellClick = { row, col ->
                                if (gameState.currentPlayer == Player.HUMAN && gameState.gameStatus == GameStatus.PLAYING) {
                                    gameState = GameLogic.makeMove(gameState, row, col)
                                }
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Nút chơi lại
                        Button(
                            onClick = {
                                gameState = GameLogic.resetGame(gameState.difficulty)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = LocalizedString("restart"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                               // Nút thay đổi chế độ
                               OutlinedButton(
                                   onClick = {
                                       if (gameState.gameMode == GameMode.AI) {
                                           showDifficultySelector = true
                                       } else {
                                           showModeSelector = true
                                       }
                                   },
                                   modifier = Modifier.fillMaxWidth()
                               ) {
                                   Text(
                                       text = if (gameState.gameMode == GameMode.AI) LocalizedString("change_difficulty") else LocalizedString("change_mode"),
                                       fontSize = 16.sp
                                   )
                               }
                               
                               Spacer(modifier = Modifier.height(8.dp))
                               
                               // Test language button
                               OutlinedButton(
                                   onClick = {
                                       showLanguageSelector = true
                                   },
                                   modifier = Modifier.fillMaxWidth()
                               ) {
                                   Text("🌐 ${LocalizedString("language")}")
                               }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
               // Hiển thị thông tin game hiện tại
               if (!showModeSelector && !showBluetoothConnection && !showDifficultySelector && !showLanguageSelector) {
                   val winRule = if (gameState.difficulty == Difficulty.EASY) LocalizedString("win_3_in_row") else LocalizedString("win_5_in_row")
                   val modeText = if (gameState.gameMode == GameMode.AI) LocalizedString("vs_ai") else LocalizedString("multiplayer")
                   Text(
                       text = LocalizedString("win_rule_format", modeText, getDifficultyDisplayName(gameState.difficulty), gameState.boardSize, gameState.boardSize, winRule),
                       fontSize = 12.sp,
                       color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                       textAlign = TextAlign.Center
                   )
                   
                   // Debug: Show current language
                   Text(
                       text = "Debug: ${LocalizedString("your_turn")} | ${LocalizedString("easy")}",
                       fontSize = 10.sp,
                       color = Color.Red,
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
