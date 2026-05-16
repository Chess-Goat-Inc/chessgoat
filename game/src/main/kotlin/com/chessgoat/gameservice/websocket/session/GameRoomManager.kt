package com.chessgoat.gameservice.websocket.session

import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Component
class GameRoomManager {

    private val rooms = ConcurrentHashMap<UUID, GameRoom>()

    fun getOrCreateRoom(gameId: UUID): GameRoom {
        return rooms.computeIfAbsent(gameId) {
            GameRoom(it)
        }
    }
}