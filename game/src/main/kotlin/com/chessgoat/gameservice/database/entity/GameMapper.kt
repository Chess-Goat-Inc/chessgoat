package com.chessgoat.gameservice.database.entity

import com.chessgoat.gameservice.logic.domain.Game
import com.chessgoat.gameservice.logic.domain.GameState
import com.chessgoat.gameservice.logic.domain.GameStatus
import com.chessgoat.gameservice.logic.domain.PlayerColor
import org.springframework.stereotype.Component

@Component
class GameMapper {

    fun toDomain(entity: GameEntity): Game {

        return Game(
            id = entity.id,
            state = GameState(
                fen = entity.boardFen,
                turn = extractTurnFromFen(entity.boardFen)
            ),
            status = GameStatus.valueOf(entity.status.uppercase()),
            whitePlayerId = entity.whitePlayerId,
            blackPlayerId = entity.blackPlayerId
        )
    }

    fun toEntity(game: Game): GameEntity {
        return GameEntity(
            id = game.id,
            boardFen = game.state.fen,
            status = game.status.name.lowercase(),
            whitePlayerId = game.whitePlayerId,
            blackPlayerId = game.blackPlayerId
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