<script setup lang="ts">
import Button from './basic/Button.vue';
const props = defineProps({
  place: { type: Number, required: true },
  score: { type: Number, required: true },
  username: { type: String, required: true },
  status: { type: String, required: true },
  is_me: { type: Boolean, required: true },
})
const realStatus = props.is_me ? '' : props.status;
</script>


<template>
  <div :class="props.is_me ? 'row me' : 'row'">
    <div class="row-info">
        <div class="row-place">{{ props.place }}</div>
        <div class="row-score">{{ props.score }}</div>
        <div class="row-username">
          <RouterLink :to="`/user/${$props.username}`" >
            {{ props.username }}
          </RouterLink>
        </div>
    </div>
    <div class="row-actions">
        <Button variant="green" v-if="realStatus === 'online'">challenge ⚔️</Button>
      <Button disabled v-if="realStatus === 'offline'">offline 💤</Button>
      <Button disabled v-if="realStatus === 'in-game'">in game ⏳</Button>
    </div>
  </div>
</template>


<style scoped>
  .row {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    background-color: #FFFFFF;
    border-bottom: 1px solid #A0A0A0;
    height: 42px;
    padding: 4px;
    padding-left: 8px;
    padding-right: 8px;
    transition: background-color 1s;
  }
  .row:hover {
    background-color: #fafafa;
  }
  .row-info {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: left;
    width: fit-content;
    gap: 16px;
    padding: 8px;
  }
  .row-place { min-width: 1.5em; }
  .row-score {
    min-width: 4em;
    color: green;
    border: 1px solid #A0A0A0;
    background-color: white;
    padding: 2px;
    padding-left: 4px;
    padding-right: 4px;
  }
  .row-username { min-width: 8em; }
  .row-actions {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: right;
    width: fit-content;
  }
  .row.me {
    background-color: #FFFDDC;
  }
</style>
