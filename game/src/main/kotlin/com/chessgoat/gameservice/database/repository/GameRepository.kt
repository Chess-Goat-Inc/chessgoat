package com.chessgoat.gameservice.database.repository

import com.chessgoat.gameservice.database.entity.GameEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface GameRepository : JpaRepository<GameEntity, UUID>