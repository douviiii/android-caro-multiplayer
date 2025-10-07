package com.sopa.co_caro

import kotlin.random.Random

object GameLogic {
    
    fun makeMove(gameState: GameState, row: Int, col: Int): GameState {
        if (gameState.board[row][col] != null || gameState.gameStatus != GameStatus.PLAYING) {
            return gameState
        }
        
        val newBoard = gameState.board.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                if (r == row && c == col) gameState.currentPlayer else cell
            }
        }
        
        val newGameStatus = checkWinner(newBoard)
        val nextPlayer = if (newGameStatus == GameStatus.PLAYING) {
            if (gameState.currentPlayer == Player.HUMAN) Player.AI else Player.HUMAN
        } else gameState.currentPlayer
        
        return gameState.copy(
            board = newBoard,
            currentPlayer = nextPlayer,
            gameStatus = newGameStatus
        )
    }
    
    fun getAIMove(gameState: GameState): Pair<Int, Int> {
        return when (gameState.difficulty) {
            Difficulty.EASY -> getEasyMove(gameState.board)
            Difficulty.MEDIUM -> getMediumMove(gameState.board)
            Difficulty.HARD -> getHardMove(gameState.board)
        }
    }
    
    private fun getEasyMove(board: List<List<Player?>>): Pair<Int, Int> {
        // AI dễ: chọn ngẫu nhiên
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == null) {
                    emptyCells.add(Pair(i, j))
                }
            }
        }
        return emptyCells.random()
    }
    
    private fun getMediumMove(board: List<List<Player?>>): Pair<Int, Int> {
        // AI trung bình: ưu tiên thắng, sau đó chặn người chơi
        val emptyCells = getEmptyCells(board)
        
        // Kiểm tra xem AI có thể thắng không
        for (cell in emptyCells) {
            val testBoard = board.map { it.toMutableList() }.map { it.toMutableList() }
            testBoard[cell.first][cell.second] = Player.AI
            if (checkWinner(testBoard) == GameStatus.AI_WIN) {
                return cell
            }
        }
        
        // Kiểm tra xem có cần chặn người chơi không
        for (cell in emptyCells) {
            val testBoard = board.map { it.toMutableList() }.map { it.toMutableList() }
            testBoard[cell.first][cell.second] = Player.HUMAN
            if (checkWinner(testBoard) == GameStatus.HUMAN_WIN) {
                return cell
            }
        }
        
        // Nếu không có gì đặc biệt, chọn ngẫu nhiên
        return emptyCells.random()
    }
    
    private fun getHardMove(board: List<List<Player?>>): Pair<Int, Int> {
        // AI khó: sử dụng minimax algorithm với depth limit để tránh ANR
        val emptyCells = getEmptyCells(board)
        
        // Kiểm tra xem AI có thể thắng không
        for (cell in emptyCells) {
            val testBoard = board.map { it.toMutableList() }.map { it.toMutableList() }
            testBoard[cell.first][cell.second] = Player.AI
            if (checkWinner(testBoard) == GameStatus.AI_WIN) {
                return cell
            }
        }
        
        // Kiểm tra xem có cần chặn người chơi không
        for (cell in emptyCells) {
            val testBoard = board.map { it.toMutableList() }.map { it.toMutableList() }
            testBoard[cell.first][cell.second] = Player.HUMAN
            if (checkWinner(testBoard) == GameStatus.HUMAN_WIN) {
                return cell
            }
        }
        
        // Sử dụng minimax với depth limit để tránh ANR
        var bestScore = Int.MIN_VALUE
        var bestMove = emptyCells.random()
        
        for (cell in emptyCells) {
            val testBoard = board.map { it.toMutableList() }.map { it.toMutableList() }
            testBoard[cell.first][cell.second] = Player.AI
            val score = minimax(testBoard, 0, false, 2) // Giới hạn depth = 2 cho 9x9
            if (score > bestScore) {
                bestScore = score
                bestMove = cell
            }
        }
        
        return bestMove
    }
    
    private fun minimax(board: List<List<Player?>>, depth: Int, isMaximizing: Boolean, maxDepth: Int = 2): Int {
        // Giới hạn depth để tránh ANR
        if (depth >= maxDepth) {
            return 0 // Hòa nếu quá sâu
        }
        
        val result = checkWinner(board)
        when (result) {
            GameStatus.AI_WIN -> return 10 - depth
            GameStatus.HUMAN_WIN -> return depth - 10
            GameStatus.DRAW -> return 0
            GameStatus.PLAYING -> {
                val emptyCells = getEmptyCells(board)
                if (isMaximizing) {
                    var maxScore = Int.MIN_VALUE
                    for (cell in emptyCells) {
                        val testBoard = board.map { it.toMutableList() }.map { it.toMutableList() }
                        testBoard[cell.first][cell.second] = Player.AI
                        val score = minimax(testBoard, depth + 1, false, maxDepth)
                        maxScore = maxOf(maxScore, score)
                    }
                    return maxScore
                } else {
                    var minScore = Int.MAX_VALUE
                    for (cell in emptyCells) {
                        val testBoard = board.map { it.toMutableList() }.map { it.toMutableList() }
                        testBoard[cell.first][cell.second] = Player.HUMAN
                        val score = minimax(testBoard, depth + 1, true, maxDepth)
                        minScore = minOf(minScore, score)
                    }
                    return minScore
                }
            }
        }
    }
    
    private fun getEmptyCells(board: List<List<Player?>>): List<Pair<Int, Int>> {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        val size = board.size
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (board[i][j] == null) {
                    emptyCells.add(Pair(i, j))
                }
            }
        }
        return emptyCells
    }
    
    fun checkWinner(board: List<List<Player?>>): GameStatus {
        val size = board.size
        val winLength = if (size == 3) 3 else 5 // 3x3 cần 3 ô liên tiếp, 6x6 và 9x9 cần 5 ô liên tiếp
        
        // Kiểm tra hàng ngang
        for (row in 0 until size) {
            for (col in 0..size - winLength) {
                val humanWin = (0 until winLength).all { board[row][col + it] == Player.HUMAN }
                val aiWin = (0 until winLength).all { board[row][col + it] == Player.AI }
                if (humanWin) return GameStatus.HUMAN_WIN
                if (aiWin) return GameStatus.AI_WIN
            }
        }
        
        // Kiểm tra cột dọc
        for (col in 0 until size) {
            for (row in 0..size - winLength) {
                val humanWin = (0 until winLength).all { board[row + it][col] == Player.HUMAN }
                val aiWin = (0 until winLength).all { board[row + it][col] == Player.AI }
                if (humanWin) return GameStatus.HUMAN_WIN
                if (aiWin) return GameStatus.AI_WIN
            }
        }
        
        // Kiểm tra đường chéo chính (từ trái trên xuống phải dưới)
        for (row in 0..size - winLength) {
            for (col in 0..size - winLength) {
                val humanWin = (0 until winLength).all { board[row + it][col + it] == Player.HUMAN }
                val aiWin = (0 until winLength).all { board[row + it][col + it] == Player.AI }
                if (humanWin) return GameStatus.HUMAN_WIN
                if (aiWin) return GameStatus.AI_WIN
            }
        }
        
        // Kiểm tra đường chéo phụ (từ phải trên xuống trái dưới)
        for (row in 0..size - winLength) {
            for (col in winLength - 1 until size) {
                val humanWin = (0 until winLength).all { board[row + it][col - it] == Player.HUMAN }
                val aiWin = (0 until winLength).all { board[row + it][col - it] == Player.AI }
                if (humanWin) return GameStatus.HUMAN_WIN
                if (aiWin) return GameStatus.AI_WIN
            }
        }
        
        // Kiểm tra hòa
        val isFull = board.all { row -> row.all { it != null } }
        return if (isFull) GameStatus.DRAW else GameStatus.PLAYING
    }
    
    fun resetGame(difficulty: Difficulty = Difficulty.EASY, gameMode: GameMode = GameMode.AI): GameState {
        return GameState(
            board = createEmptyBoard(difficulty),
            difficulty = difficulty,
            gameMode = gameMode
        )
    }
}
