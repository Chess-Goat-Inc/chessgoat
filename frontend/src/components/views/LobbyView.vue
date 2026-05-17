<script setup lang="ts">
import { onBeforeMount, onMounted, ref, watch } from 'vue';
import type { Ref } from 'vue';
import type { Player } from '@/models';
import { computed, provide } from 'vue';
import ScoreTable from '../ScoreTable.vue';
import { fetch_me, fetch_players, test_ws } from '@/fetches';
import { useProfileStore } from '@/stores/profile';
import ChallengeMessage from '../ChallengeMessage.vue';

const profile = await useProfileStore();
const players: Ref<Player[]> = ref([]);

async function get_players_and_me() {
  players.value = await fetch_players();
  players.value.forEach(player => { player.is_me = (player.username === profile.username) });
}

watch(profile, ()=>{
  get_players_and_me();
})

provide('players', players);

onMounted(async () => {
  await get_players_and_me()
})

</script>


<template>
  <main>
    <ScoreTable/>
    <ChallengeMessage />
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
