<script setup lang="ts">
import { onBeforeMount, onMounted, ref, watch } from 'vue';
import type { Ref } from 'vue';
import type { Player, RequestMessage, AcceptMessage} from '@/models';
import { computed, provide } from 'vue';
import ScoreTable from '../ScoreTable.vue';
import { fetch_me, fetch_players } from '@/fetches';
import { useProfileStore} from '@/stores/profile';
import { useGameStore } from '@/stores/game';
import { useAuthStore } from '@/stores/auth';
import ChallengeMessage from '../ChallengeMessage.vue';
import router from '@/router';

const profile = useProfileStore();
const players: Ref<Player[]> = ref([]);
const challengeMessages = ref<{opponent: string, time?: number, id: number}[]>([]);
let challengeId = 0;
const gameStore = useGameStore();

async function get_players_and_me() {
  players.value = await fetch_players();
  players.value.forEach(player => { player.is_me = (player.username === profile.username) });
}

function test_ws() {
  const URI = 'ws://localhost:8000/lobby'
  const ws = new WebSocket(URI);
  const profile = useProfileStore()
  const auth = useAuthStore()

  ws.addEventListener('open', () => {
    console.log('CONNECTED');
    const token = auth.access_token
    ws.send(`${token}`)
  });
  ws.addEventListener('message', (e)=> {
    const message = JSON.parse(e.data) as RequestMessage|AcceptMessage;
    console.log(`Recevied ${message.type}, ${message.opponent?.username}`)
    if (message.type === 'challenge_request' && message.opponent?.username) {
      challengeMessages.value.push({ opponent: message.opponent.username, time: 45, id: challengeId++ });
    }
    else if (message.type === "challenge_accept") {
      let accMessage = message as AcceptMessage
      gameStore.gameId = accMessage.gameId
      console.log(gameStore.gameId)
      router.push("/game")
    }
    else if (message.type === "challenge_decline") {
      alert(`${message.opponent.username} does not want to play with you(`)
    }
  })
  ws.addEventListener('error', (event) => {
    console.log('WebSocket error:', event)
  })
  ws.addEventListener('close', (event) => {
    console.log('WebSocket closed:', event.code, event.reason);
  });
}

watch(profile, ()=>{
  get_players_and_me();
})

provide('players', players);

onMounted(async () => {
  await get_players_and_me()
  test_ws()
})

</script>



<template>
  <main>
    <ScoreTable/>
    <div style="position: fixed; top: 16px; right: 16px; z-index: 1000; display: flex; flex-direction: column; gap: 8px;">
      <ChallengeMessage
        v-for="msg in challengeMessages"
        :key="msg.id"
        :opponent="msg.opponent"
        :time="msg.time"
        @timeout="challengeMessages.splice(challengeMessages.findIndex(m => m.id === msg.id), 1)"
      />
    </div>
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
