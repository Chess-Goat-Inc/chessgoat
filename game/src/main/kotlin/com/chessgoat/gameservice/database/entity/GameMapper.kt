package com.chessgoat.gameservice.database.entity

import com.chessgoat.gameservice.logic.domain.Game
import com.chessgoat.gameservice.logic.domain.GameState
import com.chessgoat.gameservice.logic.domain.GameStatus
import com.chessgoat.gameservice.logic.domain.PlayerColor
import com.chessgoat.gameservice.websocket.model.FinishGameMessage
import org.springframework.stereotype.Component

@Component
class GameMapper {

    fun toDomain(entity: GameEntity): Game {

        return Game(
            id = entity.id,
            whitePlayerId = entity.whitePlayerId,
            blackPlayerId = entity.blackPlayerId,
            winner = entity.winnerId?.let {
                if (it == entity.whitePlayerId)
                    PlayerColor.WHITE
                else
                    PlayerColor.BLACK
            }
        )
    }

    fun toEntity(game: Game): GameEntity {
        return GameEntity(
            id = game.id,
            status = game.state?.let {
                game.state.status.name.lowercase()
            } ?: GameStatus.STARTED.name.lowercase(),
            whitePlayerId = game.whitePlayerId,
            blackPlayerId = game.blackPlayerId,
            winnerId = when(game.winner) {
                PlayerColor.WHITE -> game.whitePlayerId
                PlayerColor.BLACK -> game.blackPlayerId
                else              -> null
            }
        )
    }

    private fun extractTurnFromFen(fen: String): PlayerColor {
        val parts = fen.split(" ")

        return when (parts[1]) {
            "w"  -> PlayerColor.WHITE
            "b"  -> PlayerColor.BLACK
            else -> throw IllegalArgumentException("Invalid FEN: $fen")
        }
    }
}