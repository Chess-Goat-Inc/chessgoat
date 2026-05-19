<script setup lang="ts">
import { onMounted, reactive } from 'vue';
import BoardCell from './BoardCell.vue';
import FloatingFigure from './FloatingFigure.vue';
import { useGameStore } from '@/stores/game';

const game = useGameStore();

const boardStyle = reactive({
  cursor: 'grab'
})

const floatingClass = reactive({
  hidden: true,
  floatingFigure: true
})

onMounted(()=>{
  window.addEventListener('mouseup', () => {
    boardStyle.cursor = 'grab';
    floatingClass.hidden = true;
  })
})

function onCellPress(idx: number, figure: string, event: Event) {
  console.log('press', idx);
  if (figure !== 'empty') {
    boardStyle.cursor = 'grabbing';
    floatingClass.hidden = false;
    game.grabFigure(idx);
  }
}

function onCellRelease(idx: number, figure: string, event: Event) {
  console.log('release', idx);
  boardStyle.cursor = 'grab';
  floatingClass.hidden = true;

  if (game.grabbing && game.canPut(idx)) {
    if (game.ws !== null) {
      console.log('ws', game.ws)
      if (game.ws.readyState === game.ws.CLOSED) {
        if (game.gameId) {
          game.start_game_websocket(game.gameId);
        }
      }
      console.log('trying to send', game.ws)
      game.ws.send(JSON.stringify({
        type: 'move',
        from: game.grabbedFrom,
        to: idx,
      }))
      console.log('trying to send', game.ws)
    }
    game.putFigure(idx);
  }
}
</script>


<template>
  <div :style="boardStyle" class="board">
    <div class="row" v-for="i in 8" :key="i">
      <BoardCell v-for="j in 8" :type="(j%2+i%2)%2" :key="j" :idx="(i-1)*8+(j-1)"
                 @press="onCellPress"
                 @release="onCellRelease"
      />
    </div>
  </div>
  <FloatingFigure :class="floatingClass" />
</template>


<style scoped>
.board {
  display: flex;
  flex-direction: column;
  border: 1px solid #A0A0A0;
  width: fit-content;
  height: fit-content;

  cursor: grab;
}

.board .row {
  display: flex;
  flex-direction: row;
  width: fit-content;
  height: fit-content;
}
</style>
