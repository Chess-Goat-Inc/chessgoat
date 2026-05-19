<script setup lang="ts">
import { useGameStore } from '@/stores/game';

const game = useGameStore()
</script>


<template>
  <div class="game-status">
    {{ game.gameState }}
  </div>
</template>


<style scoped>
.game-status {
  display: flex;
  position: absolute;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 160px;
  background-color: rgba(0, 0, 0, 0.75);
  color: white;
  font-size: 32px;
}

.game-status.disabled {
  display: none;
}
</style>
