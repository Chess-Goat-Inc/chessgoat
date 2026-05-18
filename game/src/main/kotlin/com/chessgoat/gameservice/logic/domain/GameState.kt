package com.chessgoat.gameservice.logic.domain

data class GameState(
    val fen: String,
//    val turn: PlayerColor,
    val status: GameStatus,
)
