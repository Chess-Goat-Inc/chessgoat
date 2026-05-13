<script setup lang="ts">
import LoginDialog from './components/LoginDialog.vue';
import RegisterDialog from './components/RegisterDialog.vue';
</script>

<template>
  <LoginDialog />
  <RegisterDialog />
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

html,
body,
#app {
  font-family: 'Inter', sans-serif;
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
