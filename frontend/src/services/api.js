import axios from 'axios';

// Create axios instance
const api = axios.create({
  baseURL: '/api', // All API calls will be prefixed with /api
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add interceptor to inject auth token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Authentication APIs
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
};

// User Management APIs
export const userAPI = {
  acceptUser: (username) => api.post('/users/accept-user', { username }),
  updateUser: (userData) => api.post('/users/update-user', userData),
  deleteUser: (username) => api.post('/users/delete-user', { username }),
  addRole: (username, role) => api.post('/users/add-role', { username, role }),
  removeRole: (username, role) => api.post('/users/remove-role', { username, role }),
  searchUsers: (username) => api.post('/users/search-users', { username }),
  searchUsersAdmin: (username) => api.post('/users/search-users-admin', { username }),
  editScore: (username, integer) => api.post('/users/edit-score', { username, integer }),
};

export default api;
