<script setup lang="ts">
import { useGameStore } from '@/stores/game';
import { useProfileStore } from '@/stores/profile';
import { useAuthStore } from '@/stores/auth';
import ChessBoard from '../board/ChessBoard.vue';
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue';
import GameStatus from '../GameStatus.vue';
import { useRoute, useRouter } from 'vue-router';

const game = useGameStore()

game.initBoard();

if (game.gameId) {
  game.start_game_websocket(game.gameId);
}

// watch(
//   () => game.gameId,
//   (id) => {
//     if (id && !gameStarted.value) {
//       gameStarted.value = true;
//       start_game_websocket(id);
//     }
//   }
// )

const router = useRouter()

const gameRunning = computed(()=>(game.gameState == 'running'))

function onKeyDown(event: Event) {
  console.log("KEYDOWN")
  if (event.key === 'w' || event.key === 'W') {
    console.log("W")
    if (game.ws != null) {
      game.gameState = 'Victory 🎉'
      game.ws.send(JSON.stringify({type: 'win'}))
      setTimeout(()=>{ router.push('/lobby') }, 2000)
    }
  }
}

onMounted(() => {
  window.addEventListener('keydown', onKeyDown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', onKeyDown)
})

</script>


<template>
  <main>
    <ChessBoard/>
    <GameStatus :class="gameRunning ? 'disabled' : ''"/>
  </main>
</template>


<style scoped>
main {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 32px;
}
</style>
