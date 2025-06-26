<template>
  <div class="login">
    <h2>Login</h2>
    <form @submit.prevent="login">
      <input v-model="username" placeholder="Username" required />
      <input v-model="password" type="password" placeholder="Password" required />
      <button type="submit">Login</button>
    </form>
    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script>
import api from '../api';

export default {
  data() {
    return {
      username: '',
      password: '',
      error: ''
    };
  },
  methods: {
    async login() {
      this.error = '';
      try {
        const res = await api.post('/api/auth/login', {
          username: this.username,
          password: this.password
        });
        const token = res.data.token;
        localStorage.setItem('jwt_token', token);
        this.$root.$emit('login', token); // Notify app of login
        this.$router.push('/');
      } catch (e) {
        this.error = 'Invalid username or password';
      }
    }
  }
};
</script>

<style scoped>
.login {
  max-width: 400px;
  margin: 2rem auto;
  padding: 2rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #fafafa;
}
.error {
  color: #b00;
  margin-top: 1rem;
}
</style> 