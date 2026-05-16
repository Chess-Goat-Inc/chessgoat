package com.chessgoat.gameservice.logic.engine

import com.chessgoat.gameservice.logic.domain.GameState
import com.chessgoat.gameservice.logic.domain.GameStatus
import com.chessgoat.gameservice.logic.domain.PlayerColor
import com.chessgoat.gameservice.logic.result.MoveResult

interface ChessEngine {

    fun createInitialState(): GameState

    fun applyMove(state: GameState, move: String): MoveResult

    fun getLegalMoves(state: GameState): List<String>

    fun getGameStatus(fen: String): GameStatus

    fun getWinner(fen: String): PlayerColor?
}