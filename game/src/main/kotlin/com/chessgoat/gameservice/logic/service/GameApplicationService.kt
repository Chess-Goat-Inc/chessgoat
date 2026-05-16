package com.chessgoat.gameservice.logic.service

import com.chessgoat.gameservice.logic.domain.PlayerColor
import com.chessgoat.gameservice.database.entity.GameMapper
import com.chessgoat.gameservice.database.repository.GameRepository
import com.chessgoat.gameservice.logic.result.GameMoveResult
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GameApplicationService(
    private val repository: GameRepository,
    private val mapper: GameMapper,
    private val gameService: GameService
) {

    @Transactional
    fun makeMove(
        gameId: UUID,
        playerColor: PlayerColor,
        move: String
    ): GameMoveResult {
        val entity = repository.findById(gameId)
                .orElseThrow {
                    IllegalArgumentException("Game not found")
                }

        val game = mapper.toDomain(entity)

        val result = gameService.makeMove(game, playerColor, move)

        if (!result.success || result.game == null) {
            return result
        }

        val updatedEntity = mapper.toEntity(game)

        repository.save(updatedEntity)

        return GameMoveResult(
            success = true,
            game = game,
            error = null
        )
    }

    @Transactional
    fun getPlayerColor(gameId: UUID, playerId: UUID): PlayerColor? {
        val entity = repository.findById(gameId)
            .orElseThrow {
                IllegalArgumentException("Game not found")
            }

        val game = mapper.toDomain(entity)
        val playerColor =
            when(playerId) {
                game.whitePlayerId -> PlayerColor.WHITE
                game.blackPlayerId -> PlayerColor.BLACK
                else               -> null
            }

        return playerColor
    }

    @Transactional
    fun getBoardFen(gameId: UUID): String {
        val entity = repository.findById(gameId)
            .orElseThrow {
                IllegalArgumentException("Game not found")
            }

        val game = mapper.toDomain(entity)
        return game.state.fen
    }
}