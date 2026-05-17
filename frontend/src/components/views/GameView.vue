<script setup>
import { useGameStore } from '@/stores/game';
import ChessBoard from '../board/ChessBoard.vue';

const game = useGameStore()

game.setFigure({x:1,y:1}, 'b_rook');

console.log(game.board);

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
