package com.chessgoat.gameservice.auth

import java.util.UUID

data class JwtPayload(
    val userId: Int,
    val username: String
)