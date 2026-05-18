import { ref, type Ref } from 'vue'
import { defineStore } from 'pinia'
import { fetch_login, fetch_me, fetch_refresh, fetch_register } from '@/fetches'
import type { LoginData, Player } from '@/models'
import { useAuthStore } from './auth'


export const useProfileStore = defineStore('profile', () => {
  const auth = useAuthStore();

  const username: Ref<string | undefined> = ref(undefined);
  const place: Ref<number | undefined> = ref(undefined);
  const score: Ref<number | undefined> = ref(undefined);

  async function get_me() {
    if (auth.access_token) {
      const me = await fetch_me(auth.access_token);
      username.value = me.username
      place.value = me.place
      score.value = me.score
    } else {
      throw Error('auth.access_token is undefined')
    }
  }

  return {
    username, place, score,
    get_me
  }
})
