package com.chessgoat.gameservice.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(

    @Id
    @Column(name = "user_id")
    val id: Int,

    @Column(name = "username")
    val username: String,

    @Column(name = "score")
    var rating: Int
)