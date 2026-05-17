import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', () => {
  const access_token = ref(null)
  const username = ref('unknown')

  function refresh() {

  }

  return {
    access_token,
    username,
    refresh,
  }
})
