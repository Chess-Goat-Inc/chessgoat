package com.chessgoat.gameservice.logic.service

import com.chessgoat.gameservice.logic.domain.GameFinishStatus
import com.chessgoat.gameservice.logic.result.RatingResult
import org.springframework.stereotype.Service
import kotlin.math.pow
import kotlin.math.roundToInt

@Service
class RatingService {

    fun calculateRatings(
        whiteRating: Int,
        blackRating: Int,
        finishStatus: GameFinishStatus
    ): RatingResult {

        val whiteExpected = expectedScore(
                playerRating = whiteRating,
                opponentRating = blackRating
        )
        val blackExpected = expectedScore(
                playerRating = blackRating,
                opponentRating = whiteRating
        )

        val whiteActual =
            when (finishStatus) {
                GameFinishStatus.WHITE_WIN -> 1.0
                GameFinishStatus.BLACK_WIN -> 0.0
                GameFinishStatus.DRAW -> 0.5
            }
        val blackActual = 1.0 - whiteActual

        val whiteKFactor = getKFactor(whiteRating)
        val blackKFactor = getKFactor(blackRating)

        val newWhiteRating = whiteRating + whiteKFactor * (whiteActual - whiteExpected)
        val newBlackRating = blackRating + blackKFactor * (blackActual - blackExpected)

        return RatingResult(
            whiteRating = newWhiteRating.roundToInt(),
            blackRating = newBlackRating.roundToInt()
        )
    }

    private fun expectedScore(
        playerRating: Int,
        opponentRating: Int
    ): Double {
        return 1.0 / (1.0 + 10.0.pow((opponentRating - playerRating) / 400.0))
    }

    private fun getKFactor(rating: Int): Int {
        return when {
            rating < 1000 -> 40
            rating < 2400 -> 20
            else          -> 10
        }
    }
}