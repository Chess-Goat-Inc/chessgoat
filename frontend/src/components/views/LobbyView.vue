<script setup lang="ts">
import { provide } from 'vue';
import ScoreTable from '../ScoreTable.vue';
import type { Player } from '@/models';


const players: Player[] = [
  { place: 1, score: 200000,
    username: 'PavMash',
    status: 'online', is_me: false },
  { place: 2, score: 100000,
    username: 'Stepan_Tap',
    status: 'online', is_me: true },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
  { place: 3, score: 5000,
    username: 'al.blinov',
    status: 'offline', is_me: false },
]

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
