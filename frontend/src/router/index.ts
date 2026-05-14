import { createRouter, createWebHistory } from 'vue-router'
import LobbyView from '@/components/views/LobbyView.vue'
import ProfileView from '@/components/views/ProfileView.vue'
import LoginView from '@/components/views/LoginView.vue'
import RegisterView from '@/components/views/RegisterView.vue'
import GameView from '@/components/views/GameView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/lobby'},
    { path: '/lobby', component: LobbyView },
    { path: '/profile', component: ProfileView },
    { path: '/game', name: 'game', component: GameView },
    { path: '/auth/login', component: LoginView, meta: { headerTitle: 'Authentication' } },
    { path: '/auth/register', component: RegisterView, meta: { headerTitle: 'Authentication' } },
    { path: '/auth/', redirect: '/auth/login' },
  ],
})

export default router
