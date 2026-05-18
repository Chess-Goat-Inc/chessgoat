package com.chessgoat.gameservice.logic.service

import com.chessgoat.gameservice.database.entity.GameEntity
import com.chessgoat.gameservice.logic.domain.PlayerColor
import com.chessgoat.gameservice.database.entity.GameMapper
import com.chessgoat.gameservice.database.entity.UserMapper
import com.chessgoat.gameservice.database.redis.GameStateRepository
import com.chessgoat.gameservice.database.repository.GameRepository
import com.chessgoat.gameservice.database.repository.UserRepository
import com.chessgoat.gameservice.logic.domain.GameFinishState
import com.chessgoat.gameservice.logic.domain.GameFinishStatus
import com.chessgoat.gameservice.logic.domain.GameState
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
    private val gameStateRepository: GameStateRepository,
    private val userMapper: UserMapper,
    private val ratingService: RatingService
) {

    @Transactional
    fun makeMove(
        gameId: Int,
        playerColor: PlayerColor,
        move: String
    ): GameMoveResult {
        try {
            val fen = gameStateRepository.getFen(gameId)
            val status = gameStateRepository.getStatus(gameId)
            if (fen == null || status == null) {
                throw IllegalArgumentException("Could not retrieve game state for id=$gameId")
            }
            val gameState = GameState(fen, GameStatus.valueOf(status.uppercase()))

            val result = gameService.makeMove(gameState, playerColor, move)
            if (!result.success || result.gameState == null) {
                return result
            }

            if (result.finishState != null) {
                val gameEntity = gameRepository.findById(gameId)
                    .orElseThrow {
                        IllegalArgumentException("Game with id=$gameId not found")
                    }

                val (whiteRating, blackRating) = updateRatings(
                    whitePlayerId = gameEntity.whitePlayerId,
                    blackPlayerId = gameEntity.blackPlayerId,
                    finishStatus = result.finishState.finishStatus
                )

                val updatedEntity = GameEntity(
                    id = gameEntity.id,
                    status = result.finishState.finishStatus.name.lowercase(),
                    whitePlayerId = gameEntity.whitePlayerId,
                    blackPlayerId = gameEntity.blackPlayerId,
                    winnerId = when (result.finishState.finishStatus) {
                        GameFinishStatus.WHITE_WIN -> gameEntity.whitePlayerId
                        GameFinishStatus.BLACK_WIN -> gameEntity.blackPlayerId
                        else -> null
                    }
                )
                gameRepository.save(updatedEntity)

                gameStateRepository.saveFen(gameId, result.gameState.fen)
                gameStateRepository.saveStatus(gameId, result.gameState.status.name.lowercase())

                return GameMoveResult(
                    success = true,
                    gameState = result.gameState,
                    error = null,
                    finishState = result.finishState.copy(
                        whiteRating = whiteRating,
                        blackRating = blackRating
                    )
                )
            }

            gameStateRepository.saveFen(gameId, result.gameState.fen)
            gameStateRepository.saveStatus(gameId, result.gameState.status.name.lowercase())

            return GameMoveResult(
                success = true,
                gameState = result.gameState,
                error = null,
                finishState = result.finishState
            )
        } catch(e: Exception) {
            println("${javaClass.simpleName} makeMove(): ${e.message}")

            return GameMoveResult(
                success = false,
                gameState = null,
                error = e.message,
                finishState = null
            )
        }
    }

    // Yeah, that's dumb and counter-intuitive that this function returns GameMoveResult. Live with that
    @Transactional
    fun handleDisconnect(gameId: Int, disconnectedPlayer: PlayerColor): GameMoveResult {
        try {
            val gameEntity = gameRepository.findById(gameId)
                .orElseThrow {
                    IllegalArgumentException("Game with id=$gameId not found")
                }

            val fen = gameStateRepository.getStatus(gameId)
            val status = gameStateRepository.getStatus(gameId)
            if (fen == null || status == null) {
                throw IllegalArgumentException("Could not retrieve game state for id=$gameId")
            }
            val gameStatus = GameStatus.valueOf(status.uppercase())

            if (gameStatus != GameStatus.STARTED) {
                throw IllegalArgumentException("Game with id=$gameId has status=$gameStatus, while should be ${GameStatus.STARTED}")
            }

            val finishStatus =
                if (disconnectedPlayer == PlayerColor.WHITE) {
                    GameFinishStatus.BLACK_WIN
                } else {
                    GameFinishStatus.WHITE_WIN
                }

            val updatedGameState = GameState(
                fen = fen,
                status = GameStatus.FINISHED,
            )
            val (whiteRating, blackRating) = updateRatings(
                gameEntity.whitePlayerId,
                gameEntity.blackPlayerId,
                finishStatus
            )

            val updatedEntity = GameEntity(
                id = gameEntity.id,
                status = GameStatus.FINISHED.name.lowercase(),
                whitePlayerId = gameEntity.whitePlayerId,
                blackPlayerId = gameEntity.blackPlayerId,
                winnerId =
                    when (finishStatus) {
                        GameFinishStatus.WHITE_WIN -> gameEntity.whitePlayerId
                        GameFinishStatus.BLACK_WIN -> gameEntity.blackPlayerId
                        else -> null
                    }
            )
            gameRepository.save(updatedEntity)

            gameStateRepository.saveStatus(gameId, GameStatus.FINISHED.name.lowercase())

            return GameMoveResult(
                success = true,
                gameState = updatedGameState,
                error = null,
                finishState = GameFinishState(
                    finishStatus = finishStatus,
                    whiteRating = whiteRating,
                    blackRating = blackRating
                )
            )
        } catch(e: Exception) {
            println("${javaClass.simpleName} handleDisconnect(): ${e.message}")

            return GameMoveResult(
                success = false,
                gameState = null,
                error = e.message,
                finishState = null
            )
        }
    }

    @Transactional
    fun getPlayerColor(gameId: Int, playerId: Int): PlayerColor? {
        try {
            val entity = gameRepository.findById(gameId)
                .orElseThrow {
                    IllegalArgumentException("Game with id=$gameId not found")
                }

            val game = gameMapper.toDomain(entity)
            val playerColor =
                when (playerId) {
                    game.whitePlayerId -> PlayerColor.WHITE
                    game.blackPlayerId -> PlayerColor.BLACK
                    else -> null
                }

            return playerColor
        } catch(e: Exception) {
            println("${javaClass.simpleName} getPlayerColor(): ${e.message}")

            return null
        }
    }

    @Transactional
    fun getBoardFen(gameId: Int): String? {
        try {
            val fen = gameStateRepository.getFen(gameId)
                ?: throw java.lang.IllegalArgumentException("Could not retrieve fen for id=$gameId")

            return fen
        } catch(e: Exception) {
            println("${javaClass.simpleName} getBoardFen(): ${e.message}")
            return null
        }
    }

    @Transactional
    fun authenticatePlayer(gameId: Int, userId: Int): PlayerColor? {
        try {
            val gameEntity = gameRepository.findById(gameId)
                .orElseThrow {
                    IllegalArgumentException("Game with id=$gameId not found")
                }

            return when (userId) {
                gameEntity.whitePlayerId -> PlayerColor.WHITE
                gameEntity.blackPlayerId -> PlayerColor.BLACK
                else                     -> null
            }
        } catch(e: Exception) {
            println("${javaClass.simpleName} authenticatePlayer(): ${e.message}")

            return null
        }
    }

    fun startGame(gameId: Int): Boolean {
        try {
            val gameEntity = gameRepository.findById(gameId)
                .orElseThrow {
                    IllegalArgumentException("Game with id=$gameId not found")
                }
            val status = gameStateRepository.getStatus(gameId)
                ?: throw IllegalArgumentException("Could not retrieve status for id=$gameId")
            if (GameStatus.valueOf(status.uppercase()) != GameStatus.READY) {
                throw IllegalArgumentException("Game with id=$gameId has already started")
            }

            val updatedEntity = GameEntity(
                id = gameEntity.id,
                status = GameStatus.STARTED.name.lowercase(),
                whitePlayerId = gameEntity.whitePlayerId,
                blackPlayerId = gameEntity.blackPlayerId,
                winnerId = null
            )
            gameRepository.save(updatedEntity)
            gameStateRepository.saveStatus(gameId, GameStatus.STARTED.name.lowercase())

            return true
        } catch(e: Exception) {
            println("${javaClass.simpleName} startGame(): ${e.message}")

            return false
        }
    }

    private fun updateRatings(
        whitePlayerId: Int,
        blackPlayerId: Int,
        finishStatus: GameFinishStatus
    ): Pair<Int, Int> {
        val whiteUserEntity = userRepository.findById(whitePlayerId)
            .orElseThrow {
                IllegalArgumentException("User with id=$whitePlayerId not found")
            }
        val blackUserEntity = userRepository.findById(blackPlayerId)
            .orElseThrow {
                IllegalArgumentException("User with id=$blackPlayerId not found")
            }

        val whiteUser = userMapper.toDomain(whiteUserEntity)
        val blackUser = userMapper.toDomain(blackUserEntity)

        val ratingResult =
            ratingService.calculateRatings(
                whiteRating = whiteUser.rating,
                blackRating = blackUser.rating,
                finishStatus = finishStatus
            )

        val updatedWhiteUser = whiteUser.copy(rating = ratingResult.whiteRating)
        val updatedBlackUser = blackUser.copy(rating = ratingResult.blackRating)

        userRepository.save(userMapper.toEntity(updatedWhiteUser))
        userRepository.save(userMapper.toEntity(updatedBlackUser))

        return Pair(ratingResult.whiteRating, ratingResult.blackRating)
    }
}