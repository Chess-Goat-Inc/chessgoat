package com.chessgoat.gameservice.websocket.handler

import com.chessgoat.gameservice.logic.domain.GameFinishStatus
import com.chessgoat.gameservice.logic.domain.PlayerColor
import com.chessgoat.gameservice.logic.service.GameApplicationService
import com.chessgoat.gameservice.logic.service.GameService
import com.chessgoat.gameservice.websocket.model.AcceptMessage
import com.chessgoat.gameservice.websocket.model.FinishGameMessage
import com.chessgoat.gameservice.websocket.model.MoveMessage
import com.chessgoat.gameservice.websocket.model.InitialMessage
import com.chessgoat.gameservice.websocket.model.StartGameMessage
import com.chessgoat.gameservice.websocket.model.MoveAcceptMessage
import com.chessgoat.gameservice.websocket.model.MoveRejectMessage
import com.chessgoat.gameservice.websocket.model.OpponentMoveMessage
import com.chessgoat.gameservice.websocket.model.RejectMessage
import com.chessgoat.gameservice.websocket.session.GameRoomManager
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import tools.jackson.databind.ObjectMapper
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Component
class GameWebSocketHandler(
    private val objectMapper: ObjectMapper,
    private val roomManager: GameRoomManager,
    private val gameService: GameApplicationService
) : TextWebSocketHandler() {

    companion object{
        private const val AUTHENTICATED = "authenticated"
    }

    private val scheduler = Executors.newScheduledThreadPool(1)
    private val authTimeoutTasks = ConcurrentHashMap<String, ScheduledFuture<*>>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        println("Socket connected: ${session.id}")

        val task = scheduler.schedule({
            if (session.isOpen && !session.attributes.containsKey("authenticated")) {
                try {
                    val reject = RejectMessage(reason = "Authentication timeout")

                    session.sendMessage(
                        TextMessage(objectMapper.writeValueAsString(reject))
                    )

                    session.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }, 30, TimeUnit.SECONDS)

        authTimeoutTasks[session.id] = task
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage
    ) {
        val payload = message.payload

        // First message - JWT token
        if (!session.attributes.containsKey(AUTHENTICATED)) {
            handleInitialMessage(session, payload)
            return
        }

        // Following messages - moves
        handleMoveMessage(session, payload)
    }

    private fun handleInitialMessage(
        session: WebSocketSession,
        payload: String
    ) {
        val initialMessage =
            objectMapper.readValue(
                payload,
                InitialMessage::class.java
            )

        /*
         * TODO:
         * verify JWT
         * load game
         * verify player belongs to game
         */
        authTimeoutTasks
            .remove(session.id)
            ?.cancel(false)
        val playerId = UUID.randomUUID() //TODO player id is received from JWT token

        val gameId = UUID.fromString(
                session
                    .uri!!
                    .query!!
                    .split("=")[1]
            )

        val room = roomManager.getOrCreateRoom(gameId)
        val playerColor = gameService.getPlayerColor(gameId, playerId)

        session.attributes["authenticated"] = true
        session.attributes["playerColor"] = playerColor!!.name
        session.attributes["gameId"] = gameId

        room.addSession(playerColor, session)
        val accept = AcceptMessage(playerColor.name)

        session.sendMessage(
            TextMessage(objectMapper.writeValueAsString(accept))
        )

        if (room.isReady()) {
            val initialBoardFen = gameService.getBoardFen(gameId)
            val start = StartGameMessage(state = initialBoardFen)
            val json = objectMapper.writeValueAsString(start)

            room.sessions.values.forEach {
                it.sendMessage(TextMessage(json))
            }
        }
    }

    private fun handleMoveMessage(
        session: WebSocketSession,
        payload: String
    ) {
        val moveMessage = objectMapper.readValue(
                payload,
                MoveMessage::class.java
        )

        val gameId = session.attributes["gameId"] as UUID
        val playerColor = PlayerColor.valueOf(
            session.attributes["playerColor"] as String
        )

        val result = gameService.makeMove(gameId, playerColor, moveMessage.move)

        if (!result.success || result.game == null) {
            sendMoveRejectMessage(
                session = session,
                move = moveMessage.move,
                reason = result.error ?: "Move failed"
            )
        } else {
            val room = roomManager.getOrCreateRoom(gameId)

            if (result.finishStatus != null) {
                room.sessions.values.forEach {
                    sendFinishGameMessage(
                        session = it,
                        fen = result.game.state.fen,
                        winner = result.finishStatus
                    )
                }
            } else {
                sendMoveAcceptMessage(
                    session = session,
                    fen = result.game.state.fen,
                    move = moveMessage.move
                )

                val opponent = room.getOpponentSession(playerColor)
                opponent?.let {
                    sendOpponentMoveMessage(
                        opponentSession = opponent,
                        fen = result.game.state.fen,
                        move = moveMessage.move
                    )
                }
            }
        }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus
    ) {
        println("Socket disconnected: ${session.id}")

        val gameId = session.attributes["gameId"] as UUID
        val playerColor = session.attributes["playerColor"] as PlayerColor
        val result = gameService.handleDisconnect(
            gameId,
            playerColor
        )

        if (!result.success || result.game == null) {
            return
        }

        val room = roomManager.getOrCreateRoom(gameId)

        val opponentSession = room.getOpponentSession(playerColor)

        if (opponentSession != null && opponentSession.isOpen) {
            sendFinishGameMessage(
                session = opponentSession,
                fen = result.game.state.fen,
                winner = result.finishStatus!!
            )
        }

        authTimeoutTasks
            .remove(session.id)
            ?.cancel(false)
    }

    private fun sendFinishGameMessage(
        session: WebSocketSession,
        fen: String,
        winner: GameFinishStatus
    ) {
        val finish = FinishGameMessage(
            state = fen,
            winner = winner.name
        )
        val finishJson = objectMapper.writeValueAsString(finish)
        session.sendMessage(TextMessage(finishJson))
    }

    private fun sendMoveAcceptMessage(
        session: WebSocketSession,
        fen: String,
        move: String
    ) {
        val moveAccept = MoveAcceptMessage(
            move = move,
            state = fen
        )
        val acceptJson = objectMapper.writeValueAsString(moveAccept)
        session.sendMessage(TextMessage(acceptJson))
    }

    private fun sendOpponentMoveMessage(
        opponentSession: WebSocketSession,
        fen: String,
        move: String
    ) {
        val opponentMove = OpponentMoveMessage(
            move = move,
            state = fen
        )
        val opponentMoveJson = objectMapper.writeValueAsString(opponentMove)
        opponentSession.sendMessage(TextMessage(opponentMoveJson))
    }

    private fun sendMoveRejectMessage(
        session: WebSocketSession,
        move: String,
        reason: String
    ) {
        val moveReject = MoveRejectMessage(
            move = move,
            reason = reason
        )
        val rejectJson = objectMapper.writeValueAsString(moveReject)
        session.sendMessage(TextMessage(rejectJson))
    }
}