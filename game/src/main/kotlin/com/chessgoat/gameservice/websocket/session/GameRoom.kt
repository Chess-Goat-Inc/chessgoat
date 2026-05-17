package com.chessgoat.gameservice.websocket.session

import com.chessgoat.gameservice.logic.domain.PlayerColor
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

class GameRoom(val gameId: Int) {
    val sessions = ConcurrentHashMap<PlayerColor, WebSocketSession>()

    fun addSession(playerColor: PlayerColor, session: WebSocketSession) {
        sessions[playerColor] = session
    }

    fun getOpponentSession(playerColor: PlayerColor): WebSocketSession? {
        return sessions.entries
            .firstOrNull {
                it.key != playerColor
            }
            ?.value
    }

    fun isReady(): Boolean {
        return sessions.size == 2
    }
}