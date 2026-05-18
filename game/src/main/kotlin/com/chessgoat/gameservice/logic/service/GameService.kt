package com.chessgoat.gameservice.logic.service

import com.chessgoat.gameservice.logic.domain.Game
import com.chessgoat.gameservice.logic.domain.GameFinishState
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
        gameState: GameState,
        playerColor: PlayerColor,
        move: String
    ): GameMoveResult {
        if (gameState.status != GameStatus.STARTED) {
            return GameMoveResult(
                success = false,
                gameState = null,
                error = "Game is not active",
                finishState = null
            )
        }

        val moveResult = chessEngine.applyMove(gameState, move)

        if (!moveResult.success || moveResult.state == null) {
            return GameMoveResult(
                success = false,
                gameState = null,
                error = moveResult.error ?: "Move failed",
                finishState = null
            )
        }

        val finishStatus = detectFinish(moveResult.state.fen)
        val gameStatus =
            if (finishStatus != null)
                GameStatus.FINISHED
            else
                GameStatus.STARTED
//        val winner =
//            when (finishStatus) {
//                GameFinishStatus.WHITE_WIN -> PlayerColor.WHITE
//                GameFinishStatus.BLACK_WIN -> PlayerColor.BLACK
//                else                       -> null
//            }

        val updatedGameState = gameState.copy(
            fen = moveResult.state.fen,
            status = gameStatus
        )
        
        return GameMoveResult(
            success = true,
            gameState = updatedGameState,
            error = null,
            finishState =
                if (finishStatus != null) {
                    GameFinishState(finishStatus, null, null)
                } else {
                    null
                }
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
}