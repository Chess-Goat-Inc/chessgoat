package com.chessgoat.gameservice.database.entity

import com.chessgoat.gameservice.logic.domain.User
import org.springframework.stereotype.Component

@Component
class UserMapper {

    fun toDomain(entity: UserEntity): User {

        return User(
            id = entity.id,
            username = entity.username,
            rating = entity.rating
        )
    }

    fun toEntity(domain: User): UserEntity {

        return UserEntity(
            id = domain.id,
            username = domain.username,
            rating = domain.rating
        )
    }
}