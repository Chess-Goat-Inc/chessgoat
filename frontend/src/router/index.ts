import { createRouter, createWebHistory } from 'vue-router'
import LobbyView from '@/components/views/LobbyView.vue'
import LoginView from '@/components/views/LoginView.vue'
import RegisterView from '@/components/views/RegisterView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/lobby', component: LobbyView },
    { path: '/auth/login', component: LoginView },
    { path: '/auth/register', component: RegisterView },
    { path: '/auth', redirect: '/auth/login' },
  ],
})

export default router
