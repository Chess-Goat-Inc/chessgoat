<script setup lang="ts">
import { useGameStore } from '@/stores/game';
import { useProfileStore } from '@/stores/profile';
import { useAuthStore } from '@/stores/auth';
import ChessBoard from '../board/ChessBoard.vue';
import { ref, watch } from 'vue';

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

function start_game_websocket(game_id: number) {
  const URI = `ws://localhost:6767/game?id=${game_id}`
  const ws = new WebSocket(URI);

  const profile = useProfileStore()
  const auth = useAuthStore()
  const game = useGameStore()

  ws.addEventListener('open', () => {
    console.log('CONNECTED');
    const token = auth.access_token
    ws.send(`${token}`)
    game.ws = ws;
  });
  ws.addEventListener('message', (e)=> {
    const message = JSON.parse(e.data)
    switch (message.type) {
      case 'move': {
        const m = message;
        game.board[m.x1][m.y1] = game.board[m.x0][m.y0]
        game.board[m.x0][m.y0] = 'empty'
      }
    }
    console.log(e.data)
  })
  ws.addEventListener('error', (event) => {
    console.log('WebSocket error:', event)
  })
  ws.addEventListener('close', (event) => {
    console.log('WebSocket closed:', event.code, event.reason);
  });
}

const gameStarted = ref(false);

start_game_websocket(game.gameId);

// watch(
//   () => game.gameId,
//   (id) => {
//     if (id && !gameStarted.value) {
//       gameStarted.value = true;
//       start_game_websocket(id);
//     }
//   }
// )

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
