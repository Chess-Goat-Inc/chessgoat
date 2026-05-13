<script setup>
import Input from './basic/Input.vue';
import Button from './basic/Button.vue';


</script>

<template>
  <div class="register-dialog">
    <h1>Register in Chess Goat ♟️</h1>
    <Input ref="username" width="100%" placeholder="username"></Input>
    <Input ref="password" width="100%" variant="password" placeholder="password"></Input>
    <Input ref="repeat_password" width="100%" variant="password" placeholder="repeat password"></Input>
    <Button ref="submit" width="100%" variant="green">register</Button>
    <div class="prompt">
      <p>already registered?</p>
      <RouterLink to="/auth/login">login</RouterLink>
    </div>
  </div>
</template>

<style scoped>
.register-dialog {
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
