package com.sopa.co_caro

data class GameState(
    val board: List<List<Player?>> = createEmptyBoard(Difficulty.EASY),
    val currentPlayer: Player = Player.HUMAN,
    val gameStatus: GameStatus = GameStatus.PLAYING,
    val difficulty: Difficulty = Difficulty.EASY,
    val gameMode: GameMode = GameMode.AI,
    val isConnected: Boolean = false,
    val isHost: Boolean = false
) {
    val boardSize: Int
        get() = board.size
}

enum class Player {
    HUMAN, AI
}

enum class GameStatus {
    PLAYING, HUMAN_WIN, AI_WIN, DRAW
}

enum class Difficulty(val size: Int) {
    EASY(3), MEDIUM(6), HARD(9)
}

enum class GameMode {
    AI, MULTIPLAYER
}

fun createEmptyBoard(difficulty: Difficulty): List<List<Player?>> {
    return List(difficulty.size) { List(difficulty.size) { null } }
}
