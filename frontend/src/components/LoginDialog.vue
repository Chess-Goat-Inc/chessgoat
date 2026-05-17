<script setup lang="ts">
import Input from './basic/Input.vue';
import Button from './basic/Button.vue';
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useProfileStore } from '@/stores/profile';

const router = useRouter();

const auth = useAuthStore();
const profile = useProfileStore();

const username = ref('');
const password = ref('');

async function on_submit() {
  const data = {
    username: username.value,
    password: password.value
  }
  try {
    await auth.login(data);
    await profile.get_me();
    router.push('/lobby');
  } catch(error) {
    console.log(error);
    alert(error);
  }
}
</script>

<template>
  <div class="login-dialog">
    <h1>Login to Chess Goat ♟️</h1>
    <Input width="100%" placeholder="username" v-model="username"></Input>
    <Input width="100%" variant="password" placeholder="password" v-model="password"></Input>
    <Button width="100%" variant="green" @click="on_submit">login</Button>
    <div class="prompt">
      <p>not yet registered?</p>
      <RouterLink to="/auth/register">register</RouterLink>
    </div>
  </div>
</template>

<style scoped>
.login-dialog {
  padding: 16px;
  border: 1px solid black;
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 16em;
}

h1 {
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  margin: 0;
  margin-bottom: 0.5em;
}
.prompt {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  font-size: 14px;
  margin-top: 0.5em;
}
.prompt p {
  margin: 0;
  padding: 0;
  color: #A0A0A0;
}
</style>
