<script setup lang="ts">
defineOptions({
  name: 'SiteHeader',
})

defineSlots<{
  default: () => unknown
}>()
</script>

<template>
  <header>
    <RouterLink to="/">Chess Goat ♟️</RouterLink>
    <div class="right-side">
      <slot></slot>
    </div>
  </header>
</template>

<style scoped>
header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding: 16px;
  height: fit-content;
  border-bottom: 1px solid black;
}

header a {
  padding: 0;
  margin: 0;
  font-weight: 600;
  font-size: 16px;
  text-decoration: none;
  color: black;
}

header a:hover {
  animation: shake 0.5s infinite;
}

@keyframes shake {
  0% {
    rotate: 0deg;
  }
  25% {
    rotate: 1deg;
  }
  75% {
    rotate: -1deg;
  }
  100% {
    rotate: 0deg;
  }
}
</style>

<style>
.right-side,
.right-side span {
  display: flex;
  width: fit-content;
  flex-direction: row;
  gap: 8px;
}
</style>
