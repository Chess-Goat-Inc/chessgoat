<script setup lang="ts">
import type { Player } from '@/models.ts'
import ScoreTableRow from './ScoreTableRow.vue';
import Button from './basic/Button.vue';
import Input from './basic/Input.vue';
import { inject } from 'vue';

const players: Player[] = inject('players', []);

</script>


<template>
  <div class="score-table">
    <div class="score-table-panel">
      <Button variant="green">focus on me</Button>
      <Input class="search-bar" width="80%" placeholder="search by username"/>
    </div>
    <div class="score-table-contents">
      <ScoreTableRow
        v-for="player in players"
        :place="player.place"
        :score="player.score"
        :username="player.username"
        :status="player.status"
        :is_me="player.is_me"
        :key="player.place"
      />
    </div>
  </div>
</template>


<style scoped>
.score-table {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 40%;
  margin: 16px;
  height: fit-content;
}

.score-table-panel {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  gap: 8px;
}
.score-table-contents {
  display: flex;
  scroll-behavior: smooth;
  overflow: scroll;
  flex-direction: column;
  justify-content: top;
  border: 1px solid black;
  height: 80vh;
}
</style>
