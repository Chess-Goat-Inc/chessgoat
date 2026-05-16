package com.chessgoat.gameservice.logic.result

import com.chessgoat.gameservice.logic.domain.Game
import com.chessgoat.gameservice.logic.domain.GameFinishStatus

data class GameMoveResult(
    val success: Boolean,
    val game: Game?,
    val error: String?,
    val finishStatus: GameFinishStatus?
)
