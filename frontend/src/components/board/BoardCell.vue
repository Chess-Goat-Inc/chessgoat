<script setup lang="ts">
import { useGameStore } from '@/stores/game';
import { computed, reactive, watch } from 'vue';

const props = defineProps({
  type: { type: Number, required: true },
  x: { type: Number, required: true },
  y: { type: Number, required: true },
});

const x = props.x;
const y = props.y;

const game = useGameStore();

const figure = computed(() => game.board?.[x]?.[y] || 'empty')
const empty = computed(() => (figure.value === 'empty'));
const figureSrc = computed(() => `figures/${figure.value}.png`)

const cellClass = reactive({
  cell0: !props.type,
  cell1: props.type
})

</script>

<template>
  <div :class="cellClass">
    <img v-if="!empty" class="figure" :src="figureSrc" :alt="figure">
  </div>
</template>

<style scoped>
.cell0, .cell1 {
  width: 80px;
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
  user-select: none;
  pointer-events: none;
  -webkit-user-drag: none;
}
.cell0 { background-color: #B9FBB3; }
.cell1 { background-color: #FFFDDC; }

.figure {
  width: 53px;
  user-select: none;
  -webkit-user-drag: none;
  pointer-events: none;
}

</style>
