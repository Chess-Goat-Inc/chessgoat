<script setup lang="ts">
import Button from './components/Button.vue';
import Input from './components/Input.vue';
import EyeIcon from './components/icons/EyeIcon.vue';
</script>

<template>
  <div class="buttons">
    <Button variant="green">challenge ⚔️</Button>
    <Button variant="red">capitulate 🏳️</Button>
    <Button disabled>offline 💤</Button>
    <Input variant="password" placeholder="password"></Input>
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
