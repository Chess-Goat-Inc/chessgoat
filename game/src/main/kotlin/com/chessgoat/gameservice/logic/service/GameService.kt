package com.chessgoat.gameservice.logic.service

import com.chessgoat.gameservice.logic.domain.Game
import com.chessgoat.gameservice.logic.domain.GameStatus
import com.chessgoat.gameservice.logic.domain.PlayerColor
import com.chessgoat.gameservice.logic.engine.ChessEngine
import com.chessgoat.gameservice.logic.result.GameMoveResult
import kotlin.time.ExperimentalTime

class GameService(
    private val chessEngine: ChessEngine
) {
    @OptIn(ExperimentalTime::class)
    fun makeMove(
        game: Game,
        playerColor: PlayerColor,
        move: String
    ): GameMoveResult {
        if (game.status != GameStatus.STARTED) {
            return GameMoveResult(
                success = false,
                game = null,
                error = "Game is not active"
            )
        }

        if (game.state.turn != playerColor) {
            return GameMoveResult(
                success = false,
                game = null,
                error = "It is not $playerColor turn"
            )
        }

        val moveResult = chessEngine.applyMove(game.state, move)

        if (!moveResult.success || moveResult.state == null) {
            return GameMoveResult(
                success = false,
                game = null,
                error = moveResult.error ?: "Move failed"
            )
        }

        val updatedGame = game.copy(state = moveResult.state)

        return GameMoveResult(
            success = true,
            game = updatedGame,
            error = null
        )
    }

    @OptIn(ExperimentalTime::class)
    fun resign(game: Game, playerColor: PlayerColor): Game {
        val newStatus = GameStatus.FINISHED
        return game.copy(status = newStatus)
    }

    fun isGameFinished(game: Game): Boolean =
        game.status == GameStatus.FINISHED
}