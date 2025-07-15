import axios from 'axios';

// Base instance with interceptor for token
const API = axios.create({
  baseURL: 'http://localhost:7070',
});

// Attach token to every request automatically
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// POST: Book appointment
export const bookAppointment = (payload: any, token: string) =>
  API.post('/api/appointment/create', payload, {
    headers: { Authorization: `Bearer ${token}` },
  });

// DELETE: Cancel appointment
export const cancelAppointment = (id: number, token: string) =>
  API.delete(`/api/appointment/delete/${id}`, {
    headers: { Authorization: `Bearer ${token}` },
  });

export default API;
