<script setup lang="ts">
import Button from './components/Button.vue';
</script>

<template>
  <div class="buttons">
    <Button variant="green">challenge ⚔️</Button>
    <Button variant="red">capitulate 🏳️</Button>
    <Button disabled>offline 💤</Button>
  </div>
</template>

<style scoped>
.buttons {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 10em;
}
</style>
