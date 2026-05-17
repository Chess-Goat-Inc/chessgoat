<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { fetch_player } from '@/fetches';
import { useAuthStore } from '@/stores/auth';
import Button from '../basic/Button.vue';


const route = useRoute();
const auth = useAuthStore();

const ownUsername: string = auth.username;

const username = computed(() => {
  if (route.params.username !== undefined) {
    if (typeof(route.params.username) == 'string')
      return route.params.username;
    else
      return 'unknown';
  } else if (route.fullPath === '/profile') {
    return ownUsername;
  } else {
    return 'unknown';
  }
});

const player = await fetch_player(username.value);


</script>

<template>
  <main>
    <div class="profile">
      <h1 class="username">{{ player.username }}</h1>
      <div class="rating">
        <div class="place">Top: {{ player.place }}</div>
        <div class="score">{{ player.score }}</div>
      </div>
      <div class="actions">
        <Button></Button>
      </div>
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

