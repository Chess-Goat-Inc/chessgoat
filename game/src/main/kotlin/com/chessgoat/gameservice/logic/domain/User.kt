package com.chessgoat.gameservice.logic.domain

import jakarta.persistence.Column
import java.util.UUID

data class User(
    val id: UUID,
    val username: String,
    val rating: Int
)