<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';


const route = useRoute();

const username = computed(() => ((route.fullPath === '/profile' && 'lol') || route.params.username));


</script>

<template>
  <main>
    <h1>{{ username }}</h1>
  </main>
</template>

<style scoped>
main {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>

