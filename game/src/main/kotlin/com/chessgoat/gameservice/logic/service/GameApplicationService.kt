package com.chessgoat.gameservice.logic.service

import com.chessgoat.gameservice.logic.domain.PlayerColor
import com.chessgoat.gameservice.database.entity.GameMapper
import com.chessgoat.gameservice.database.repository.GameRepository
import com.chessgoat.gameservice.logic.domain.Game
import com.chessgoat.gameservice.logic.domain.GameFinishStatus
import com.chessgoat.gameservice.logic.domain.GameStatus
import com.chessgoat.gameservice.logic.result.GameMoveResult
import com.chessgoat.gameservice.websocket.model.FinishGameMessage
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

        val updatedEntity = mapper.toEntity(result.game)

        repository.save(updatedEntity)

        return GameMoveResult(
            success = true,
            game = game,
            error = null,
            finishStatus = result.finishStatus
        )
    }

    // Yeah, that's dumb and counter-intuitive that this function returns GameMoveResult. Live with that
    @Transactional
    fun handleDisconnect(gameId: UUID, disconnectedPlayer: PlayerColor): GameMoveResult {
        val entity = repository.findById(gameId)
                .orElseThrow()

        val game = mapper.toDomain(entity)

        if (game.status != GameStatus.STARTED) {
            return GameMoveResult(
                success = false,
                game = null,
                error = "Game is not active",
                finishStatus = null
            )
        }

        val winner =
            if (disconnectedPlayer == PlayerColor.WHITE) {
                PlayerColor.BLACK
            } else {
                PlayerColor.WHITE
            }

        val updatedGame = game.copy(
                status = GameStatus.FINISHED,
                winner = winner
        )

        repository.save(mapper.toEntity(updatedGame))

        return GameMoveResult(
            success = true,
            game = updatedGame,
            error = null,
            finishStatus =
                if (winner == PlayerColor.WHITE) {
                    GameFinishStatus.WHITE_WIN
                } else {
                    GameFinishStatus.BLACK_WIN
                }
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