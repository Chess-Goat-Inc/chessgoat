<script setup lang="ts">
import Header from './components/Header.vue'
import type { Ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { computed, onBeforeMount, ref, watch } from 'vue'
import { useGameStore } from './stores/game'
import { useAuthStore } from './stores/auth'
import { useProfileStore } from './stores/profile'

const route = useRoute()
const router = useRouter()

const auth = useAuthStore();
const profile = useProfileStore();
const gameStore = useGameStore()


const headerTitle = computed(() => {
  const metaTitle = route.meta.headerTitle as string | undefined
  if (metaTitle) return metaTitle
  if (route.name === 'game') {
    return gameStore.opponent ? `Game with ${gameStore.opponent}` : 'Game'
  }
  return undefined
})

watch(auth, async () => {
  await profile.get_me();
  myUsername.value = profile.username;
})

const myUsername: Ref<string | undefined> = ref(undefined);

onBeforeMount(async () => {
  try { await auth.refresh(); }
  catch (error) {
    console.log(error);
    router.push('/auth/login');
  }
})

</script>

<template>
  <Header>
    <template #default>
      <span v-if="headerTitle">{{ headerTitle }}</span>
      <span v-else>
        <RouterLink to="/lobby">Lobby</RouterLink>
        <RouterLink :to="`/user/${myUsername}`">Profile</RouterLink>
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
