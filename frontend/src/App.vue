<script setup lang="ts">
import Header from './components/Header.vue';
</script>

<template>
  <Header>
    <a href="/lobby">Lobby</a>
    <a href="/profile">Profile</a>
  </Header>
  <RouterView />
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
</style>

<style scoped>
.buttons {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 10em;
}
</style>
