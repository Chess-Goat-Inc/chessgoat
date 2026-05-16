package com.chessgoat.gameservice.logic.engine

import com.chessgoat.gameservice.logic.domain.GameState
import com.chessgoat.gameservice.logic.domain.GameStatus
import com.chessgoat.gameservice.logic.result.MoveResult
import com.chessgoat.gameservice.logic.domain.PlayerColor
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveGenerator

class ChessLibAdapter : ChessEngine {

    override fun createInitialState(): GameState {
        val board = Board()
        return GameState(
            fen = board.fen,
            turn = PlayerColor.WHITE
        )
    }

    override fun applyMove(
        state: GameState,
        move: String
    ): MoveResult {
        return try {
            val board = Board()
            board.loadFromFen(state.fen)
            val chessMove = Move(
                move,
                board.sideToMove
            )

            if (!board.isMoveLegal(chessMove, true)) {
                return MoveResult(
                    success = false,
                    state = null,
                    error = "Illegal move"
                )
            }

            board.doMove(chessMove)

            val newState = GameState(
                fen = board.fen,
                turn =
                    if (board.sideToMove == Side.WHITE)
                        PlayerColor.WHITE
                    else
                        PlayerColor.BLACK
            )

            MoveResult(
                success = true,
                state = newState,
                error = null
            )

        } catch (e: Exception) {
            MoveResult(
                success = false,
                state = null,
                error = e.message
            )
        }
    }

    override fun getLegalMoves(state: GameState): List<String> {
        val board = Board()
        board.loadFromFen(state.fen)

        return MoveGenerator
            .generateLegalMoves(board)
            .map { it.toString() }
    }
}