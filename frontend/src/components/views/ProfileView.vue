<script setup lang="ts">
import { computed, ref, watch, type Ref } from 'vue';
import { useRoute } from 'vue-router';
import { fetch_player } from '@/fetches';
import { useAuthStore } from '@/stores/auth';
import Button from '../basic/Button.vue';
import { useProfileStore } from '@/stores/profile';
import { EMPTY_PLAYER, type Player } from '@/models';
import UserIcon from '../icons/UserIcon.vue';


const route = useRoute();
const auth = useAuthStore();
const profile = useProfileStore();

const player: Ref<Player | undefined> = ref(undefined);


function update_contents() {
  fetch_player(route.params.username).then((pl) => {
    pl.is_me = (pl.username == profile.username);
    player.value = pl;
  })
}


update_contents();
watch(route, update_contents);
</script>

<template>
  <main>
    <div class="profile" v-if="player">
      <!-- <p>behold the profile of:</p> -->
      <UserIcon class="user-icon" color="#000000"></UserIcon>
      <h1 class="username">{{ player.username }}</h1>
      <div class="rating">
        <div class="place">Top: {{ player.place }}</div>
        <div class="score">{{ player.score }}</div>
      </div>
      <div class="actions" v-if="!player.is_me">
        <Button variant="green" v-if="player.status === 'online'">challenge ⚔️</Button>
        <Button disabled v-if="player.status === 'offline'">offline 💤</Button>
        <Button disabled v-if="player.status === 'in-game'">in game ⏳</Button>
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

.user-icon {
  width: 128px;
  height: 128px;
}

.profile {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 32px;
  width: 50%;
}

.profile h1 {
  margin: 0;
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

.actions {
  padding: 16px;
  border: 1px solid #A0A0A0
}
</style>

