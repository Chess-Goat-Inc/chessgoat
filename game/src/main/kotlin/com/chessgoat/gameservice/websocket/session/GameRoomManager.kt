package com.chessgoat.gameservice.websocket.session

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class GameRoomManager {

    private val rooms = ConcurrentHashMap<Int, GameRoom>()

    fun getOrCreateRoom(gameId: Int): GameRoom {
        return rooms.computeIfAbsent(gameId) {
            GameRoom(it)
        }
    }
}