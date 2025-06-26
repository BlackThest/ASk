<template>
  <div>
    <h2>Αιτήσεις Υιοθεσίας</h2>
    <router-link v-if="hasRole('ADMIN') || hasRole('SHELTER')" to="/animals/add" class="btn">Add New Pet</router-link>
    <table v-if="requests.length > 0" class="table">
      <thead>
        <tr>
          <th>Όνομα</th>
          <th>Είδος</th>
          <th>Φύλο</th>
          <th>Ηλικία</th>
          <th>Ενέργειες</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in requests" :key="r.id">
          <td>{{ r.name }}</td>
          <td>{{ r.type }}</td>
          <td>{{ r.gender }}</td>
          <td>{{ r.age }}</td>
          <td>
            <router-link :to="`/api/requests/${r.id}`" class="btn">Λεπτομέρειες</router-link>
            <button v-if="hasRole('ADMIN')" @click="adminApprove(r.id)">Admin Approve</button>
            <button v-if="hasRole('DOCTOR') || hasRole('ADMIN')" @click="doctorApprove(r.id)">Doctor Approve</button>
            <button @click="deleteRequest(r.id)">Διαγραφή</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-else>Καμία αίτηση βρέθηκε.</div>
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
  data() {
    return { requests: [] }
  },
  mounted() {
    fetch('http://localhost:8080/api/requests', {
      headers: { Authorization: `Bearer ${localStorage.getItem('jwt_token')}` }
    })
      .then(r => r.json())
      .then(data => (this.requests = data))
      .catch(() => alert('Σφάλμα ανάκτησης αιτήσεων'))
  },
  methods: {
    adminApprove(id) {
      fetch(`http://localhost:8080/api/requests/Approve/${id}`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${localStorage.getItem('jwt_token')}` }
      })
        .then(() => this.reload())
        .catch(() => alert('Σφάλμα admin έγκρισης'))
    },
    doctorApprove(id) {
      fetch(`http://localhost:8080/api/requests/ApproveD/${id}`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${localStorage.getItem('jwt_token')}` }
      })
        .then(() => this.reload())
        .catch(() => alert('Σφάλμα doctor έγκρισης'))
    },
    deleteRequest(id) {
      fetch(`http://localhost:8080/requests/${id}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${localStorage.getItem('jwt_token')}` }
      })
        .then(() => this.reload())
        .catch(() => alert('Σφάλμα διαγραφής'))
    },
    reload() {
      this.mounted()
    },
    hasRole(role) {
      const token = localStorage.getItem('jwt_token');
      const payload = parseJwt(token);
      const roles = payload.roles ? payload.roles.map(r => r.name ? r.name.toLowerCase() : r.toLowerCase()) : [];
      return roles.includes(role.toLowerCase());
    }
  }
}
</script>
