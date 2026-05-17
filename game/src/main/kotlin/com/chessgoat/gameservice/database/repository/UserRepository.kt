package com.chessgoat.gameservice.database.repository

import com.chessgoat.gameservice.database.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID>