<template>
  <header class="navbar">
    <h1>🐾 Pet Adoption App</h1>
    <nav class="nav-links">
      <template v-if="isAuthenticated">
        <span class="user-info">
          Είστε συνδεδεμένος ως <b>{{ username }}</b> (<b>{{ mainRole }}</b>)
        </span>
        <router-link to="/">Αρχική</router-link>
        <router-link to="/animals">Ζώα</router-link>
        <router-link to="/requests" v-if="hasRole('ADMIN') || hasRole('USER') || hasRole('DOCTOR') || hasRole('SHELTER')">Αιτήσεις</router-link>
        <router-link to="/users" v-if="hasRole('ADMIN')">Χρήστες</router-link>
        <router-link to="/admin" v-if="hasRole('ADMIN')">Admin</router-link>
        <router-link to="/doctor" v-if="hasRole('DOCTOR')">Doctor</router-link>
        <router-link to="/shelter" v-if="hasRole('SHELTER')">Shelter</router-link>
        <button @click="logout">Logout</button>
      </template>
      <template v-else>
        <router-link to="/register">Register</router-link>
        <router-link to="/login">Login</router-link>
      </template>
    </nav>
  </header>
</template>
<script>
function parseJwt(token) {
  if (!token) return {};
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
  } catch (e) {
    return {};
  }
}

export default {
  data() {
    return {
      token: localStorage.getItem('jwt_token') || '',
      payload: parseJwt(localStorage.getItem('jwt_token'))
    };
  },
  computed: {
    isAuthenticated() {
      return !!this.token;
    },
    roles() {
      return this.payload.roles ? this.payload.roles.map(r => r.name || r) : [];
    },
    username() {
      return this.payload.sub || this.payload.username || this.payload.email || 'Χρήστης';
    },
    mainRole() {
      const priority = ['ADMIN', 'DOCTOR', 'SHELTER', 'USER'];
      for (const p of priority) {
        if (this.roles.map(r => r.toUpperCase()).includes(p)) return p;
      }
      return this.roles[0] || '-';
    }
  },
  methods: {
    logout() {
      localStorage.removeItem('jwt_token');
      this.token = '';
      this.payload = {};
      this.$router.push('/login');
    },
    hasRole(role) {
      return this.roles.map(r => r.toUpperCase()).includes(role.toUpperCase());
    }
  },
  created() {
    this.$root.$on('login', token => {
      this.token = token;
      this.payload = parseJwt(token);
    });
  }
}
</script> 