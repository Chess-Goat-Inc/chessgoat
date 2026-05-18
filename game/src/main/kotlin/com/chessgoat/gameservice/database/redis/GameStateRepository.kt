package com.chessgoat.gameservice.database.redis

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class GameStateRepository(private val redisTemplate: StringRedisTemplate) {

    fun saveFen(gameId: Int, fen: String) {
        redisTemplate.opsForHash<String, String>()
            .put(
                key(gameId),
                "state",
                fen
            )
    }

    fun getFen(gameId: Int): String? {
        return redisTemplate
            .opsForHash<String, String>()
            .get(
                key(gameId),
                "state"
            )
    }

    fun saveStatus(gameId: Int, status: String) {
        redisTemplate.opsForHash<String, String>()
            .put(
                key(gameId),
                "status",
                status
            )
    }

    fun getStatus(gameId: Int): String? {
        return redisTemplate
            .opsForHash<String, String>()
            .get(
                key(gameId),
                "status"
            )
    }

    private fun key(gameId: Int): String {
        return "game:$gameId"
    }
}