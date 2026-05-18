/**
 * Cliente axios centralizado para todas las llamadas al backend CocoTech.
 *
 * - Configura la URL base del backend Spring Boot.
 * - Inyecta automáticamente el token JWT en cada request (si existe en
 *   localStorage) en el header Authorization.
 * - Maneja errores 401 (token expirado) cerrando sesión y redirigiendo
 *   al login.
 */
import axios, { type InternalAxiosRequestConfig, type AxiosError } from "axios";

/**
 * URL base del backend. Si configuraste el context-path /api en el
 * application.properties, cámbiala a "http://localhost:8080/api".
 */
export const BASE_URL = "http://localhost:9999/api";

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Interceptor de request: inyecta el JWT en Authorization si hay sesión.
 */
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const sesion = sessionStorage.getItem("cocotech_session");
    if (sesion) {
      try {
        const { token } = JSON.parse(sesion);
        if (token && config.headers) {
          config.headers.Authorization = `Bearer ${token}`;
        }
      } catch {
        // Sesión corrupta — limpiar.
        sessionStorage.removeItem("cocotech_session");
      }
    }
    return config;
  },
  (error: AxiosError) => Promise.reject(error)
);

/**
 * Interceptor de response: si el backend responde 401, asumimos que el
 * token expiró y cerramos sesión.
 */
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      sessionStorage.removeItem("cocotech_session");
      // Redirigir solo si no estamos ya en login para evitar bucles.
      if (!window.location.pathname.includes("/login")) {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

export default api;
