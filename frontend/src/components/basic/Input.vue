<script setup>
import { computed, ref } from 'vue';
import EyeIcon from '../icons/EyeIcon.vue';

const VARIANTS = [
  'text', 'password'
];

const passwordHidden = ref(true);

const props = defineProps({
  variant: {type: String, default: 'text'},
  width: {type: String, default: '10em'}
});

const validVariant = computed(() => {
  if (VARIANTS.includes(props.variant)) {
    return props.variant;
  }
  console.warn(`Invalid variant "${props.variant}", using "text"`);
  return 'text';
});
</script>


<template>
  <div class="input-wrapper" :style="{ width: props.width }">
    <input :class="validVariant" :type="passwordHidden ? validVariant : 'text'" v-bind="$attrs" />
    <button
      v-if="validVariant == 'password'"
      class="eye-button"
      type="button"
      @click="passwordHidden = !passwordHidden"
    >
      <EyeIcon :closed="!passwordHidden" style="width: 1.8em; height: 1.8em;" color="#A0A0A0" :strokeWidth="0.8"/>
    </button>
  </div>
</template>


<style scoped>
.input-wrapper {
  position: relative;
  height: fit-content;
}

input {
  color: #A0A0A0;
  box-sizing: border-box;
  border: 1px solid #A0A0A0;
  background-color: #FFFFFF;
  font-size: 14px;
  padding: 4px;
  padding-left: 6px;
  padding-right: 6px;
  width: 100%;
}

input.password {
  padding-right: 2.25em;
}

input:focus {
  color: black;
  outline: none;
}

.input-wrapper:focus-within input {
  color: black;
  outline: none;
}

.eye-button {
  position: absolute;
  top: 50%;
  right: 0.5em;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
}

.eye-button img{
  height: 1.5em;
}
</style>
