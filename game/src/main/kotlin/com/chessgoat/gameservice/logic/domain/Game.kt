package com.chessgoat.gameservice.logic.domain

import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class Game(
    val id: Int,
    val state: GameState? = null,
    val whitePlayerId: Int,
    val blackPlayerId: Int,
    val winner: PlayerColor?
)
