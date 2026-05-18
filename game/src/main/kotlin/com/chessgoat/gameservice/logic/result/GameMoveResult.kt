package com.chessgoat.gameservice.logic.result

import com.chessgoat.gameservice.logic.domain.GameFinishState
import com.chessgoat.gameservice.logic.domain.GameState

data class GameMoveResult(
    val success: Boolean,
    val gameState: GameState?,
    val error: String?,
    val finishState: GameFinishState?
)
