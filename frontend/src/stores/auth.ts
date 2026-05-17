import { ref, type Ref } from 'vue'
import { defineStore } from 'pinia'
import { fetch_login, fetch_me, fetch_refresh, fetch_register } from '@/fetches'
import type { LoginData } from '@/models'

export const useAuthStore = defineStore('auth', () => {
  const access_token: Ref<string | null> = ref(null)
  const username: Ref<string | undefined> = ref(undefined)

  async function refresh() {
    access_token.value = await fetch_refresh();
  }

  async function register(data: LoginData) {
    access_token.value = await fetch_register(data);
  }

  async function login(data: LoginData) {
    access_token.value = await fetch_login(data);
  }

  return {
    access_token,
    username,
    refresh,
    register,
    login,
  }
})
