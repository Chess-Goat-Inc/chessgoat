<script setup lang="ts">
import { useGameStore } from '@/stores/game';
import { useProfileStore } from '@/stores/profile';
import { useAuthStore } from '@/stores/auth';
import ChessBoard from '../board/ChessBoard.vue';
import { ref, watch } from 'vue';

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

</script>


<template>
  <main>
    <ChessBoard/>
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
