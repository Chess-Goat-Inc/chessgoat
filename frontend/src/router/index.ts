import { createRouter, createWebHistory } from 'vue-router'
import LobbyView from '@/components/views/LobbyView.vue'
import ProfileView from '@/components/views/ProfileView.vue'
import LoginView from '@/components/views/LoginView.vue'
import RegisterView from '@/components/views/RegisterView.vue'
import GameView from '@/components/views/GameView.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/lobby'},
    { path: '/lobby', component: LobbyView },
    { path: '/user/:username', component: ProfileView },
    { path: '/game', name: 'game', component: GameView },
    { path: '/auth/login', component: LoginView, meta: { headerTitle: 'Authentication' } },
    { path: '/auth/register', component: RegisterView, meta: { headerTitle: 'Authentication' } },
    { path: '/auth/', redirect: '/auth/login' },
  ],
})

router.beforeEach(async (to, from) => {
  if (to.fullPath.startsWith('/auth'))
    return true;
  const auth = useAuthStore();
  if (auth.access_token) return true;
  try {
    await auth.refresh();
  } catch  {
    router.push('/auth/login')
    return false
  }
})

export default router
