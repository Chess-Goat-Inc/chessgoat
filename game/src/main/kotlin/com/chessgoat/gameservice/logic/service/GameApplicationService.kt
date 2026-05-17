package com.chessgoat.gameservice.logic.service

import com.chessgoat.gameservice.logic.domain.PlayerColor
import com.chessgoat.gameservice.database.entity.GameMapper
import com.chessgoat.gameservice.database.entity.UserMapper
import com.chessgoat.gameservice.database.repository.GameRepository
import com.chessgoat.gameservice.database.repository.UserRepository
import com.chessgoat.gameservice.logic.domain.GameFinishState
import com.chessgoat.gameservice.logic.domain.GameFinishStatus
import com.chessgoat.gameservice.logic.domain.GameStatus
import com.chessgoat.gameservice.logic.result.GameMoveResult
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class GameApplicationService(
    private val gameRepository: GameRepository,
    private val gameMapper: GameMapper,
    private val gameService: GameService,
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val ratingService: RatingService
) {

    @Transactional
    fun makeMove(
        gameId: Int,
        playerColor: PlayerColor,
        move: String
    ): GameMoveResult {
        val entity = gameRepository.findById(gameId)
                .orElseThrow {
                    IllegalArgumentException("Game not found")
                }

        val game = gameMapper.toDomain(entity)

        val result = gameService.makeMove(game, playerColor, move)

        if (!result.success || result.game == null) {
            return result
        }

        if (result.finishState != null) {
            val (whiteRating, blackRating) = updateRatings(result)

            return GameMoveResult(
                success = true,
                game = result.game,
                error = null,
                finishState = result.finishState.copy(
                    whiteRating = whiteRating,
                    blackRating = blackRating
                )
            )
        }

        val updatedEntity = gameMapper.toEntity(result.game)
        gameRepository.save(updatedEntity)

        return GameMoveResult(
            success = true,
            game = result.game,
            error = null,
            finishState = result.finishState
        )
    }

    // Yeah, that's dumb and counter-intuitive that this function returns GameMoveResult. Live with that
    @Transactional
    fun handleDisconnect(gameId: Int, disconnectedPlayer: PlayerColor): GameMoveResult {
        val entity = gameRepository.findById(gameId)
                .orElseThrow()

        val game = gameMapper.toDomain(entity)

        if (game.status != GameStatus.STARTED) {
            return GameMoveResult(
                success = false,
                game = null,
                error = "Game is not active",
                finishState = null
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
        val result = GameMoveResult(
            success = true,
            game = updatedGame,
            error = null,
            finishState = GameFinishState(
                finishStatus =
                    if (winner == PlayerColor.WHITE) {
                        GameFinishStatus.WHITE_WIN
                    } else {
                        GameFinishStatus.BLACK_WIN
                    },
                whiteRating = null,
                blackRating = null
            )
        )

        val (whiteRating, blackRating) = updateRatings(result)

        gameRepository.save(gameMapper.toEntity(updatedGame))

        return result.copy(
            finishState = result.finishState!!.copy(
                whiteRating = whiteRating,
                blackRating = blackRating
            )
        )
    }

    @Transactional
    fun getPlayerColor(gameId: Int, playerId: Int): PlayerColor? {
        val entity = gameRepository.findById(gameId)
            .orElseThrow {
                IllegalArgumentException("Game not found")
            }

        val game = gameMapper.toDomain(entity)
        val playerColor =
            when(playerId) {
                game.whitePlayerId -> PlayerColor.WHITE
                game.blackPlayerId -> PlayerColor.BLACK
                else               -> null
            }

        return playerColor
    }

    @Transactional
    fun getBoardFen(gameId: Int): String {
        val entity = gameRepository.findById(gameId)
            .orElseThrow {
                IllegalArgumentException("Game not found")
            }

        val game = gameMapper.toDomain(entity)
        return game.state.fen
    }

    @Transactional
    fun authenticatePlayer(gameId: Int, userId: Int): PlayerColor? {
        val gameEntity = gameRepository.findById(gameId)
                .orElse(null)
                ?: return null
        val game = gameMapper.toDomain(gameEntity)

        return when (userId) {
            game.whitePlayerId -> PlayerColor.WHITE
            game.blackPlayerId -> PlayerColor.BLACK
            else               -> null
        }
    }

    private fun updateRatings(moveResult: GameMoveResult): Pair<Int, Int> {
        val whiteUserEntity = userRepository.findById(moveResult.game!!.whitePlayerId)
            .orElseThrow()
        val blackUserEntity = userRepository.findById(moveResult.game.blackPlayerId)
            .orElseThrow()

        val whiteUser = userMapper.toDomain(whiteUserEntity)
        val blackUser = userMapper.toDomain(blackUserEntity)

        val ratingResult =
            ratingService.calculateRatings(
                whiteRating = whiteUser.rating,
                blackRating = blackUser.rating,
                finishStatus = moveResult.finishState!!.finishStatus
            )

        val updatedWhiteUser = whiteUser.copy(rating = ratingResult.whiteRating)
        val updatedBlackUser = blackUser.copy(rating = ratingResult.blackRating)

        userRepository.save(userMapper.toEntity(updatedWhiteUser))
        userRepository.save(userMapper.toEntity(updatedBlackUser))

        return Pair(ratingResult.whiteRating, ratingResult.blackRating)
    }
}