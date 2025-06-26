<template>
  <div>
    <h2>Καλώς ήρθατε στην Πλατφόρμα Υιοθεσίας Κατοικίδιων</h2>
    <div v-if="role === 'ADMIN'">
      <router-link to="/admin">Μετάβαση στο Admin Dashboard</router-link>
    </div>
    <div v-else-if="role === 'SHELTER'">
      <router-link to="/shelter">Μετάβαση στο Shelter Dashboard</router-link>
    </div>
    <div v-else-if="role === 'DOCTOR'">
      <router-link to="/doctor">Μετάβαση στο Doctor Dashboard</router-link>
    </div>
    <div v-else>
      <router-link to="/citizen">Μετάβαση στο Citizen Dashboard</router-link>
    </div>
  </div>
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
  computed: {
    roles() {
      const token = localStorage.getItem('jwt_token');
      const payload = parseJwt(token);
      return payload.roles ? payload.roles.map(r => r.name || r) : [];
    },
    role() {
      if (this.roles.includes('ADMIN')) return 'ADMIN';
      if (this.roles.includes('SHELTER')) return 'SHELTER';
      if (this.roles.includes('DOCTOR')) return 'DOCTOR';
      return 'CITIZEN';
    }
  }
}
</script>


