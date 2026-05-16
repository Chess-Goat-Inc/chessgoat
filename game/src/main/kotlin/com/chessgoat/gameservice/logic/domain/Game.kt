package com.chessgoat.gameservice.logic.domain

import java.util.UUID
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class Game(
    val id: UUID,
    val state: GameState,
    val status: GameStatus,
    val whitePlayerId: UUID,
    val blackPlayerId: UUID
)
