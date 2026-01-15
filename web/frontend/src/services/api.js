import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:3001", // Ou a porta do seu backend
});

// INTERCEPTADOR: Antes de cada requisição, ele roda isso
api.interceptors.request.use((config) => {
  // Tenta pegar o token do localStorage
  const token = localStorage.getItem("salesToken");

  // Se tiver token, adiciona no cabeçalho Authorization
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default api;