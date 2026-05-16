package com.chessgoat.gameservice.websocket.model

data class FinishGameMessage(val state: String, val winner: String)