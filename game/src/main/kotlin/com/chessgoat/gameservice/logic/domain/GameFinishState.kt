package com.chessgoat.gameservice.logic.domain

data class GameFinishState(
    val finishStatus: GameFinishStatus,
    val whiteRating: Int?,
    val blackRating: Int?
)