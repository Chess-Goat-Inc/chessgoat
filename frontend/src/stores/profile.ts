import { ref, type Ref } from 'vue'
import { defineStore } from 'pinia'
import { fetch_login, fetch_me, fetch_refresh, fetch_register } from '@/fetches'
import type { LoginData, Player } from '@/models'
import { useAuthStore } from './auth'


export const useProfileStore = defineStore('profile', () => {
  const auth = useAuthStore();

  const me: Ref<Player | undefined> = ref(undefined);

  async function get_me() {
    if (auth.access_token) {
      me.value = await fetch_me(auth.access_token);
    } else {
      throw Error('auth.access_token is undefined')
    }
  }

  return {
    me,
    get_me
  }
})
