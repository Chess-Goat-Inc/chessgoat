package com.chessgoat.gameservice.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "games")
class GameEntity(

    @Id
    @Column(name = "game_id")
    val id: UUID,

    @Column(name = "board_fen")
    var boardFen: String,

    @Column(name = "state")
    var status: String,

    @Column(name = "white_id")
    var whitePlayerId: UUID,

    @Column(name = "black_id")
    var blackPlayerId: UUID,

    @Column(name = "winner_id")
    var winnerId: UUID?
)