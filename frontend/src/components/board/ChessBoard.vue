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

function onCellPress(x: number, y: number, event: Event) {
  console.log('press', x, y);
  // game.board.value[x][y] = 'empty'
  boardStyle.cursor = 'grabbing';
  floatingClass.hidden = false;
  game.grabFigure(y,x);
}

function onCellRelease(x: number, y: number, event: Event) {
  console.log('release', x, y);
  boardStyle.cursor = 'grab';
  floatingClass.hidden = true;
  game.putFigure(y,x);
}
</script>


<template>
  <div :style="boardStyle" class="board">
    <div class="row" v-for="i in 8" :key="i">
      <BoardCell v-for="j in 8" :type="(j%2+i%2)%2" :key="j" :x="i-1" :y="j-1"
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
