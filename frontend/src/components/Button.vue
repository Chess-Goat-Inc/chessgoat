<script setup>
import { computed } from 'vue';

const VARIANTS = [
  'gray', 'green', 'red',
];

const props = defineProps({
  variant: {type: String, default: 'gray'}
});

const validVariant = computed(() => {
  if (VARIANTS.includes(props.variant)) {
    return props.variant;
  }
  console.warn(`Invalid variant "${props.variant}", using "gray"`);
  return 'gray';
});

</script>

<template>
  <button v-bind="$attrs" :class="validVariant">
    <slot></slot>
  </button>
</template>


<style scoped>
button {
  font-size: 16px;
  border-style: solid;
  border-width: 1px;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 4px;
  padding-left: 6px;
  padding-right: 6px;
  gap: 8px;
  cursor: pointer;
}

button:disabled {
  color: #CACACA;
  background-color: #EEEEEE;
  border-style: dashed;
  border-color: #CACACA;
  cursor: default;
}

.gray {
  color: black;
  background-color: #DFDFDF;
  border-color: #A0A0A0;
}

.gray:hover {
  background-color: #d0d0d0;
}

.gray:active {
  background-color: #c2c2c2;
}

.green {
  color: #0B6B0B;
  background-color: #B9FBB3;
  border-color: #67B960;
}

.green:hover {
  background-color: #aceda6;
}

.green:active {
  background-color: #9cd598;
}

.red {
  color: #7F0000;
  background-color: #FFC7C7;
  border-color: #7F0000;
}

.red:hover {
  background-color: #f0b9b9;
}

.red:active {
  background-color: #e89898;
}
</style>


