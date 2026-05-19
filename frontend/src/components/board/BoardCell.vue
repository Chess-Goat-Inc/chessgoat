<script setup lang="ts">
import { useGameStore } from '@/stores/game';
import { computed, reactive, watch } from 'vue';

const props = defineProps({
  type: { type: Number, required: true },
  idx: { type: Number, required: true },
});

const idx = props.idx;

const game = useGameStore();

const figure = computed(() => game.board[idx] || 'empty')
const empty = computed(() => (figure.value === 'empty'));
const figureSrc = computed(() => `figures/${figure.value}.png`)

const cellClass = reactive({
  cell0: !props.type,
  cell1: props.type
})

</script>

<template>
  <div :class="cellClass"
    @pointerdown="$emit('press', idx, figure, $event)"
    @pointerup="$emit('release', idx, figure, $event)"
  >
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
  /* pointer-events: none; */
  -webkit-user-drag: none;
}
.cell0 { background-color: #FFFDDC; }
.cell1 { background-color: #B9FBB3; }

.figure {
  width: 53px;
  user-select: none;
  -webkit-user-drag: none;
  pointer-events: none;
}

</style>
