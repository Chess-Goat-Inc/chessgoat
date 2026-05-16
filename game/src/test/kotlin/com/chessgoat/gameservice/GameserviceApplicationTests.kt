package com.chessgoat.gameservice

import com.chessgoat.gameservice.logic.domain.PlayerColor
import com.chessgoat.gameservice.logic.engine.ChessLibAdapter
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
class GameserviceApplicationTests {

	@Test
	fun contextLoads() {
	}

    @Test
    fun `initial position created correctly`() {
        val engine = ChessLibAdapter()
        val state = engine.createInitialState()

        assertEquals(
            PlayerColor.WHITE,
            state.turn
        )
    }

    @Test
    fun `e2e4 should be legal`() {
        val engine = ChessLibAdapter()
        val state = engine.createInitialState()
        val result = engine.applyMove(
            state,
            "e2e4"
        )

        assertTrue(result.success)
    }

    @Test
    fun `illegal move rejected`() {
        val engine = ChessLibAdapter()
        val state = engine.createInitialState()
        val result = engine.applyMove(
            state,
            "e2e5"
        )

        assertFalse(result.success)
    }
}
