<script lang="ts">
import { defineComponent, onMounted } from 'vue';
import { useGameStore } from '@/stores/game';
import { useAuthStore } from '@/stores/auth';
import Button from './basic/Button.vue';
import router from '@/router';
const LOBBY_HOST_URL = 'http://localhost:8000'
export default defineComponent({
    props: {
        opponent: {
            type: String,
            required: true
        },
        time: {
            type: Number,
            default: 45
        }
    },
    emits: ['timeout'],
    components: {
        Button
    },
    setup(props, { emit }) {
        onMounted(() => {
            setTimeout(() => {
                emit('timeout');
            }, props.time * 1000);
        });
        const gameStore = useGameStore()
        const auth = useAuthStore()
        return {gameStore, auth};
    },
    methods: {
        async declineChallenge(){

        },
        async acceptChallenge(opponent: string){
                const access_token = this.auth.access_token
                const headers = new Headers({
                        'Authorization': `Bearer ${access_token}`
                    })
                const response = await fetch(
                    `${LOBBY_HOST_URL}/challenge/accept/${opponent}`,
                    { method: 'POST', headers: headers}
                )
                if (!response.ok) {
                    console.log('Fetch error:', response)
                    return;
                }
                const body = await response.json();
                const gameId = body.game_id;
                this.gameStore.gameId = gameId;
                console.log(this.gameStore.gameId)
                router.push("/game");
        }
    }
})
</script>


<template>
    <div class="challenge-body">
        <div class="timer-container">
            <div class="timer-bar">

            </div>
        </div>
        <div class="challenge-text">{{ opponent }} challenges you ⚔️</div>
        <div class="button-container">
            <Button class="acc-button" variant="green" @click="acceptChallenge(opponent)">accept ⚔️</Button>
            <Button class="dec-button" variant="red" @click="declineChallenge()">decline 🤡</Button>
        </div>
    </div>

</template>

<style scoped>
.challenge-body {
    position:absolute;
    right: 16px;
    top: 30%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    width: 230px;
    height: 95px;
    border: 1px #A0A0A0 solid;
    padding: 10px;
    background-color: white;
}

.button-container {
    display: flex;
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    gap: 8px;
    padding: 4px 0;
}

.timer-container {
  width: 100%;
  height: 2px;
  background-color: #e0e0e0;
  border-radius: 6px;
  overflow: hidden;
}

.timer-bar {
  height: 100%;
  width: 100%;
  background: linear-gradient(90deg, #4caf50, #8bc34a);
  border-radius: 6px;
  animation: countdown 45s linear forwards;
}

@keyframes countdown {
  0% {
    width: 100%;
  }
  100% {
    width: 0%;
  }
}

.acc-button {
    width: 100px;
    height: 25px;
}

.dec-button {
    width: 100px;
    height: 17px;
}
</style>
