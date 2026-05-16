package com.chessgoat.gameservice.logic.result

import com.chessgoat.gameservice.logic.domain.GameState

data class MoveResult(
    val success: Boolean,
    val state: GameState?,
    val error: String?
)