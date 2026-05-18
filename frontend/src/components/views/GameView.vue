<script setup>
import { useGameStore } from '@/stores/game';
import ChessBoard from '../board/ChessBoard.vue';

const game = useGameStore()

game.setFigure({x:0,y:0}, 'b_rook');
game.setFigure({x:1,y:0}, 'b_bishop');
game.setFigure({x:2,y:0}, 'b_knight');
game.setFigure({x:3,y:0}, 'b_king');
game.setFigure({x:4,y:0}, 'b_queen');
game.setFigure({x:5,y:0}, 'b_knight');
game.setFigure({x:6,y:0}, 'b_bishop');
game.setFigure({x:7,y:0}, 'b_rook');
game.setFigure({x:0,y:1}, 'b_pawn');
game.setFigure({x:1,y:1}, 'b_pawn');
game.setFigure({x:2,y:1}, 'b_pawn');
game.setFigure({x:3,y:1}, 'b_pawn');
game.setFigure({x:4,y:1}, 'b_pawn');
game.setFigure({x:5,y:1}, 'b_pawn');
game.setFigure({x:6,y:1}, 'b_pawn');
game.setFigure({x:7,y:1}, 'b_pawn');

game.setFigure({x:0,y:6}, 'w_pawn');
game.setFigure({x:1,y:6}, 'w_pawn');
game.setFigure({x:2,y:6}, 'w_pawn');
game.setFigure({x:3,y:6}, 'w_pawn');
game.setFigure({x:4,y:6}, 'w_pawn');
game.setFigure({x:5,y:6}, 'w_pawn');
game.setFigure({x:6,y:6}, 'w_pawn');
game.setFigure({x:7,y:6}, 'w_pawn');
game.setFigure({x:0,y:7}, 'w_rook');
game.setFigure({x:1,y:7}, 'w_bishop');
game.setFigure({x:2,y:7}, 'w_knight');
game.setFigure({x:3,y:7}, 'w_queen');
game.setFigure({x:4,y:7}, 'w_king');
game.setFigure({x:5,y:7}, 'w_knight');
game.setFigure({x:6,y:7}, 'w_bishop');
game.setFigure({x:7,y:7}, 'w_rook');

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
