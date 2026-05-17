<script setup lang="ts">
import { useGameStore } from '@/stores/game';
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';

const props = defineProps({
  x: { type: Number, required: true },
  y: { type: Number, required: true },
});

const game = useGameStore();

const x = props.x;
const y = props.y;

const figure = computed(() => game.board?.[x]?.[y] || 'empty');
const figureSrc = computed(() => `figures/${figure.value}.png`);


const position = ref({ x: 0, y: 0 });

const updatePosition = (event: MouseEvent) => {
  position.value = {
    x: event.clientX,
    y: event.clientY,
  };
};

const pressStart = () => {
  document.body.style.cursor = 'grabbing';
};

const pressEnd = () => {
  document.body.style.cursor = '';
};

const setPressed = (pressed: boolean) => {
  if (pressed) {
    pressStart();
    return;
  }

  pressEnd();
};

onMounted(() => {
  window.addEventListener('mousemove', updatePosition);
  window.addEventListener('pointerdown', pressStart);
  window.addEventListener('pointerup', pressEnd);
  window.addEventListener('pointercancel', pressEnd);
  window.addEventListener('blur', pressEnd);
});

onBeforeUnmount(() => {
  window.removeEventListener('mousemove', updatePosition);
  window.removeEventListener('pointerdown', pressStart);
  window.removeEventListener('pointerup', pressEnd);
  window.removeEventListener('pointercancel', pressEnd);
  window.removeEventListener('blur', pressEnd);
  setPressed(false);
});

</script>

<template>
  <img
    class="floating-figure"
    draggable="false"
    :src="figureSrc"
    :alt="figure"
    :style="{
      left: `${position.x}px`,
      top: `${position.y}px`,
    }"
  >
</template>

<style scoped>
.floating-figure {
  width: 70px;
  height: 70px;
  position: fixed;
  /* Ensures the object doesn't block other page interactions */
  pointer-events: none;
  /* Centers the object on the cursor tip */
  transform: translate(-50%, -50%);
  z-index: 9999;
  user-select: none;
  -webkit-user-drag: none;
}

.floating-figure.hidden {
  display: none;
}

</style>
