<script setup lang="ts">
import { provide } from 'vue';
import ScoreTable from '../ScoreTable.vue';
import { fetch_players, test_ws } from '@/fetches';

const players = await fetch_players();

provide('players', players);

</script>


<template>
  <main>
    <ScoreTable/>
  </main>
</template>


<style scoped>
main {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>
