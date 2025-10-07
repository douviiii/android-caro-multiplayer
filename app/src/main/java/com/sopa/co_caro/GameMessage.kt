package com.sopa.co_caro

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed class GameMessage {
    @Serializable
    data class Move(val row: Int, val col: Int) : GameMessage()
    
    @Serializable
    data class GameStateUpdate(
        val board: List<List<String?>>,
        val currentPlayer: String,
        val gameStatus: String,
        val difficulty: String
    ) : GameMessage()
    
    @Serializable
    data class GameStart(
        val difficulty: String,
        val isHost: Boolean
    ) : GameMessage()
    
    @Serializable
    data class GameEnd(val winner: String) : GameMessage()
    
    @Serializable
    data class ChatMessage(val message: String) : GameMessage()
    
    @Serializable
    object Ping : GameMessage()
    
    @Serializable
    object Pong : GameMessage()
}

object MessageSerializer {
    private val json = Json { ignoreUnknownKeys = true }
    
    fun serialize(message: GameMessage): String {
        return try {
            json.encodeToString(message)
        } catch (e: Exception) {
            ""
        }
    }
    
    fun deserialize(jsonString: String): GameMessage? {
        return try {
            when {
                jsonString.contains("\"Move\"") -> json.decodeFromString<GameMessage.Move>(jsonString)
                jsonString.contains("\"GameStateUpdate\"") -> json.decodeFromString<GameMessage.GameStateUpdate>(jsonString)
                jsonString.contains("\"GameStart\"") -> json.decodeFromString<GameMessage.GameStart>(jsonString)
                jsonString.contains("\"GameEnd\"") -> json.decodeFromString<GameMessage.GameEnd>(jsonString)
                jsonString.contains("\"ChatMessage\"") -> json.decodeFromString<GameMessage.ChatMessage>(jsonString)
                jsonString.contains("\"Ping\"") -> json.decodeFromString<GameMessage.Ping>(jsonString)
                jsonString.contains("\"Pong\"") -> json.decodeFromString<GameMessage.Pong>(jsonString)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}

// Extension functions để convert giữa GameState và GameMessage
fun GameState.toGameStateUpdate(): GameMessage.GameStateUpdate {
    return GameMessage.GameStateUpdate(
        board = board.map { row -> row.map { player -> player?.name } },
        currentPlayer = currentPlayer.name,
        gameStatus = gameStatus.name,
        difficulty = difficulty.name
    )
}

fun GameMessage.GameStateUpdate.toGameState(): GameState {
    val convertedBoard = board.map { row ->
        row.map { playerName ->
            when (playerName) {
                "HUMAN" -> Player.HUMAN
                "AI" -> Player.AI
                else -> null
            }
        }
    }
    
    return GameState(
        board = convertedBoard,
        currentPlayer = when (currentPlayer) {
            "HUMAN" -> Player.HUMAN
            "AI" -> Player.AI
            else -> Player.HUMAN
        },
        gameStatus = when (gameStatus) {
            "PLAYING" -> GameStatus.PLAYING
            "HUMAN_WIN" -> GameStatus.HUMAN_WIN
            "AI_WIN" -> GameStatus.AI_WIN
            "DRAW" -> GameStatus.DRAW
            else -> GameStatus.PLAYING
        },
        difficulty = when (difficulty) {
            "EASY" -> Difficulty.EASY
            "MEDIUM" -> Difficulty.MEDIUM
            "HARD" -> Difficulty.HARD
            else -> Difficulty.EASY
        },
        gameMode = GameMode.MULTIPLAYER
    )
}
