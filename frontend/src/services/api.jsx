import axios from 'axios';

const API_URL = '/api';

// Create axios instance
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add interceptor to add auth token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Auth API functions
export const login = (usernameOrEmail, password) => {
  return api.post('/auth/login', { usernameOrEmail, password });
};

export const register = (username, name, email, password, passwordConfirm) => {
  return api.post('/auth/register', {
    username,
    name,
    email,
    password,
    passwordConfirm
  });
};

export default api;
