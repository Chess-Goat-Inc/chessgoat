package com.chessgoat.gameservice.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "games")
class GameEntity(

    @Id
    @Column(name = "game_id")
    val id: Int,

    @Column(name = "state")
    var status: String,

    @Column(name = "white_id")
    var whitePlayerId: Int,

    @Column(name = "black_id")
    var blackPlayerId: Int,

    @Column(name = "winner_id")
    var winnerId: Int?
)