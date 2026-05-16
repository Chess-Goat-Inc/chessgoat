package com.chessgoat.gameservice.websocket.model

data class MoveRejectMessage(val move: String, val reason: String)