import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', () => {
  const access_token = ref(null)

  function refresh() {
    
  }

  return {
    access_token,
    refresh,
  }
})
