<script setup lang="ts">
import { computed, ref, watch, type Ref } from 'vue';
import { useRoute } from 'vue-router';
import { fetch_player } from '@/fetches';
import { useAuthStore } from '@/stores/auth';
import Button from '../basic/Button.vue';
import { useProfileStore } from '@/stores/profile';
import { EMPTY_PLAYER, type Player } from '@/models';


const route = useRoute();
const auth = useAuthStore();
const profile = useProfileStore();

const player: Ref<Player | undefined> = ref(undefined);
fetch_player(route.params.username).then((pl) => {
  player.value = pl;
})

watch(route, () => {
  fetch_player(route.params.username).then((pl) => {
    player.value = pl;
  })
})

</script>

<template>
  <main>
    <div class="profile" v-if="player">
      <h1 class="username">{{ player.username }}</h1>
      <div class="rating">
        <div class="place">Top: {{ player.place }}</div>
        <div class="score">{{ player.score }}</div>
      </div>
      <div class="actions">
        <Button></Button>
      </div>
    </div>
    <div class="loading" v-if="!player">
      Loading...
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

.profile {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 50%;
}

.rating {
  display: flex;
  flex-direction: row;
  gap: 8px;
  align-items: center;
}

.score {
  min-width: 4em;
  color: green;
  border: 1px solid #A0A0A0;
  background-color: white;
  padding: 2px;
  padding-left: 4px;
  padding-right: 4px;
}
</style>

