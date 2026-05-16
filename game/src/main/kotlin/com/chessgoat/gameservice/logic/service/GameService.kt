package com.chessgoat.gameservice.logic.service

import com.chessgoat.gameservice.logic.domain.Game
import com.chessgoat.gameservice.logic.domain.GameFinishStatus
import com.chessgoat.gameservice.logic.domain.GameState
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
                error = "Game is not active",
                finishStatus = null
            )
        }

        if (game.state.turn != playerColor) {
            return GameMoveResult(
                success = false,
                game = null,
                error = "It is not $playerColor turn",
                finishStatus = null
            )
        }

        val moveResult = chessEngine.applyMove(game.state, move)

        if (!moveResult.success || moveResult.state == null) {
            return GameMoveResult(
                success = false,
                game = null,
                error = moveResult.error ?: "Move failed",
                finishStatus = null
            )
        }

        val finishStatus = detectFinish(moveResult.state.fen)
        val gameStatus =
            if (finishStatus != null)
                GameStatus.FINISHED
            else
                GameStatus.STARTED
        val winner =
            when (finishStatus) {
                GameFinishStatus.WHITE_WIN -> PlayerColor.WHITE
                GameFinishStatus.BLACK_WIN -> PlayerColor.BLACK
                else                       -> null
            }

        val updatedGame = game.copy(
            state = GameState(
                fen = moveResult.state.fen,
                turn = moveResult.state.turn,
            ),
            status = gameStatus,
            winner = winner
        )
        
        return GameMoveResult(
            success = true,
            game = updatedGame,
            error = null,
            finishStatus = finishStatus,
        )
    }

    private fun detectFinish(fen: String): GameFinishStatus? {
        return when (chessEngine.getGameStatus(fen)) {
            GameStatus.FINISHED -> {
                when (chessEngine.getWinner(fen)) {
                    PlayerColor.WHITE -> GameFinishStatus.WHITE_WIN
                    PlayerColor.BLACK -> GameFinishStatus.BLACK_WIN
                    null              -> GameFinishStatus.DRAW
                }
            }

            else -> null
        }
    }

    @OptIn(ExperimentalTime::class)
    fun resign(game: Game, playerColor: PlayerColor): Game {
        val newStatus = GameStatus.FINISHED
        return game.copy(status = newStatus)
    }

    fun isGameFinished(game: Game): Boolean =
        game.status == GameStatus.FINISHED
}