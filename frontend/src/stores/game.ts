import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useGameStore = defineStore('game', () => {
  const opponent = ref<string | null>(null)

  function setOpponent(name: string | null) {
    opponent.value = name
  }

  return { opponent, setOpponent }
})
