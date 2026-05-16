package com.chessgoat.gameservice.logic.result

import com.chessgoat.gameservice.logic.domain.Game

data class GameMoveResult(
    val success: Boolean,
    val game: Game?,
    val error: String?
)
