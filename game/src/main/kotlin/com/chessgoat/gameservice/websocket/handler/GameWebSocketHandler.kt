package com.chessgoat.gameservice.websocket.handler

import com.auth0.jwt.exceptions.JWTVerificationException
import com.chessgoat.gameservice.auth.JwtService
import com.chessgoat.gameservice.logic.domain.GameFinishStatus
import com.chessgoat.gameservice.logic.domain.PlayerColor
import com.chessgoat.gameservice.logic.service.GameApplicationService
import com.chessgoat.gameservice.websocket.model.ConnectionAcceptMessage
import com.chessgoat.gameservice.websocket.model.FinishGameMessage
import com.chessgoat.gameservice.websocket.model.MoveMessage
import com.chessgoat.gameservice.websocket.model.InitialMessage
import com.chessgoat.gameservice.websocket.model.StartGameMessage
import com.chessgoat.gameservice.websocket.model.MoveAcceptMessage
import com.chessgoat.gameservice.websocket.model.MoveRejectMessage
import com.chessgoat.gameservice.websocket.model.OpponentMoveMessage
import com.chessgoat.gameservice.websocket.model.ConnectionRejectMessage
import com.chessgoat.gameservice.websocket.session.GameRoomManager
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import tools.jackson.databind.ObjectMapper
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Component
class GameWebSocketHandler(
    private val objectMapper: ObjectMapper,
    private val roomManager: GameRoomManager,
    private val gameService: GameApplicationService,
    private val jwtService: JwtService
) : TextWebSocketHandler() {

    companion object{
        private const val AUTHENTICATED = "authenticated"
    }

    private val scheduler = Executors.newScheduledThreadPool(1)
    private val authTimeoutTasks = ConcurrentHashMap<String, ScheduledFuture<*>>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        println("Socket connected: ${session.id}")

        val task = scheduler.schedule({
            if (session.isOpen && !session.attributes.containsKey(AUTHENTICATED)) {
                try {
                    sendConnectionRejectMessage(session, "Authentication timeout")
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
        try {
            val initialMessage =
                objectMapper.readValue(
                    payload,
                    InitialMessage::class.java
                )

            val gameId =
                session
                    .uri!!
                    .query!!
                    .split("=")[1].toInt()

            val payload = jwtService.verifyToken(initialMessage.token)
            val playerColor = gameService.authenticatePlayer(
                gameId,
                payload.userId
            )

            if (playerColor == null) {
                sendConnectionRejectMessage(
                    session,
                    "Game does not exist or player does not belong to the game"
                )
                return
            }

            authTimeoutTasks
                .remove(session.id)
                ?.cancel(false)

            val room = roomManager.getOrCreateRoom(gameId)

            session.attributes["authenticated"] = true
            session.attributes["playerColor"] = playerColor.name
            session.attributes["gameId"] = gameId

            room.addSession(playerColor, session)
            sendConnectionAcceptMessage(
                session,
                playerColor.name
            )

            if (room.isReady()) {
                val initialBoardFen = gameService.getBoardFen(gameId)
                if (initialBoardFen == null) {
                    println("${javaClass.simpleName} handleInitialMessage(): Could not get initial board fen for game id=$gameId")
                    return
                }

                val startSuccess = gameService.startGame(gameId)
                if (!startSuccess) {
                    println("${javaClass.simpleName} handleInitialMessage(): Something went wrong in startGame()")
                }

                room.sessions.values.forEach {
                    sendStartGameMessage(
                        it,
                        initialBoardFen
                    )
                }
            }
        } catch (e: JWTVerificationException) {
            sendConnectionRejectMessage(
                session,
                e.message ?: "JWT token is invalid"
            )
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

        val gameId = session.attributes["gameId"] as Int
        val playerColor = PlayerColor.valueOf(
            session.attributes["playerColor"] as String
        )

        val result = gameService.makeMove(gameId, playerColor, moveMessage.move)

        if (!result.success || result.gameState == null) {
            sendMoveRejectMessage(
                session = session,
                move = moveMessage.move,
                reason = result.error ?: "Move failed"
            )
        } else {
            val room = roomManager.getOrCreateRoom(gameId)

            if (result.finishState != null
                && result.finishState.whiteRating != null
                && result.finishState.blackRating != null
                ) {
                room.sessions.values.forEach {
                    sendFinishGameMessage(
                        session = it,
                        fen = result.gameState.fen,
                        winner = result.finishState.finishStatus,
                        rating =
                            if (playerColor == PlayerColor.WHITE) {
                                result.finishState.whiteRating
                            } else {
                                result.finishState.blackRating
                            }
                    )
                }
            } else {
                sendMoveAcceptMessage(
                    session = session,
                    fen = result.gameState.fen,
                    move = moveMessage.move
                )

                val opponent = room.getOpponentSession(playerColor)
                opponent?.let {
                    sendOpponentMoveMessage(
                        opponentSession = opponent,
                        fen = result.gameState.fen,
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

        val gameId = session.attributes["gameId"] as Int
        val playerColor = session.attributes["playerColor"] as PlayerColor
        val result = gameService.handleDisconnect(
            gameId,
            playerColor
        )

        if (!result.success || result.gameState == null) {
            println("${javaClass.simpleName} afterConnectionClosed(): Something went wrong in handleDisconnect()")
            return
        }

        val room = roomManager.getOrCreateRoom(gameId)

        val opponentSession = room.getOpponentSession(playerColor)

        if (opponentSession != null && opponentSession.isOpen) {
            if (result.finishState != null
                && result.finishState.whiteRating != null
                && result.finishState.blackRating != null
                ) {
                sendFinishGameMessage(
                    session = opponentSession,
                    fen = result.gameState.fen,
                    winner = result.finishState.finishStatus,
                    rating =
                        if (playerColor == PlayerColor.WHITE) {
                            result.finishState.blackRating
                        } else {
                            result.finishState.whiteRating
                        }
                )
            }
        }

        authTimeoutTasks
            .remove(session.id)
            ?.cancel(false)
    }

    private fun sendFinishGameMessage(
        session: WebSocketSession,
        fen: String,
        winner: GameFinishStatus,
        rating: Int
    ) {
        val finish = FinishGameMessage(
            state = fen,
            winner = winner.name,
            rating = rating.toString()
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

    private fun sendConnectionRejectMessage(
        session: WebSocketSession,
        reason: String
    ) {
        val connectionReject = ConnectionRejectMessage(reason = reason)
        val rejectJson = objectMapper.writeValueAsString(connectionReject)
        session.sendMessage(TextMessage(rejectJson))
    }

    private fun sendConnectionAcceptMessage(
        session: WebSocketSession,
        color: String
    ) {
        val connectionAccept = ConnectionAcceptMessage(color)
        val acceptJson = objectMapper.writeValueAsString(connectionAccept)
        session.sendMessage(TextMessage(acceptJson))
    }

    private fun sendStartGameMessage(
        session: WebSocketSession,
        initialBoardFen: String
    ) {
        val gameStart = StartGameMessage(state = initialBoardFen)
        val startJson = objectMapper.writeValueAsString(gameStart)
        session.sendMessage(TextMessage(startJson))
    }
}