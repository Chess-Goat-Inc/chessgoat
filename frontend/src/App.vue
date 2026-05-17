<script setup lang="ts">
import Header from './components/Header.vue'
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import { useGameStore } from './stores/game'
import { fetch_login } from './fetches'
import { useAuthStore } from './stores/auth'
import { useProfileStore } from './stores/profile'
import type { LoginData } from './models'

const route = useRoute()

const auth = useAuthStore();
const profile = useProfileStore();
const gameStore = useGameStore()

const data: LoginData = {
  'username': 'upco',
  'password': '123'
}
// auth.refresh().then(()=>{
//   console.log(auth.access_token);
//   profile.get_me().then(()=>{
//     console.log(profile.me)
//   })
// })
auth.login(data).then(()=>{
  console.log(auth.access_token);
  profile.get_me().then(()=>{
    console.log(profile.me)
  })
})

const headerTitle = computed(() => {
  const metaTitle = route.meta.headerTitle as string | undefined
  if (metaTitle) return metaTitle
  if (route.name === 'game') {
    return gameStore.opponent ? `Game with ${gameStore.opponent}` : 'Game'
  }
  return undefined
})

</script>

<template>
  <Header>
    <template #default>
      <span v-if="headerTitle">{{ headerTitle }}</span>
      <span v-else>
        <RouterLink to="/lobby">Lobby</RouterLink>
        <RouterLink to="/profile">Profile</RouterLink>
      </span>
    </template>
  </Header>
  <Suspense>
    <RouterView />
  </Suspense>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

html,
body,
#app {
  font-family: 'Inter', sans-serif;
  margin: 0;
  padding: 0;
}
a {
  color: black;
  text-decoration: none;
}
a:hover {
  text-decoration: underline;
}
</style>

<style scoped>
.buttons {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 10em;
}

header a {
  color: gray;
  text-decoration: none;
}

header .router-link-active {
  color: black;
}

header a:hover {
  text-decoration: underline;
  text-decoration-color: black;
  -moz-text-decoration-color: black;
}
</style>
