/**
 * Cliente axios centralizado para todas las llamadas al backend CocoTech.
 *
 * - Configura la URL base del backend Spring Boot.
 * - Inyecta automáticamente el token JWT en cada request (si existe en
 *   localStorage) en el header Authorization.
 * - Si el backend responde 401 (no autenticado) o 403 (no autorizado),
 *   limpia la sesión y redirige al login. Esto evita el caso típico de
 *   tener un JWT viejo o de otro rol después de reiniciar el backend,
 *   donde todos los endpoints siguen fallando con códigos repetidos sin
 *   que el frontend se entere.
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
 * Endpoints en los que NO queremos cerrar sesión aunque devuelvan
 * 401/403. Por ejemplo, en el login es esperable recibir un 401 si las
 * credenciales son incorrectas — eso no debe cerrar la sesión (porque
 * todavía no hay) ni redirigir.
 */
const RUTAS_SIN_AUTO_LOGOUT = ["/auth/login", "/auth/verificarCodigo",
  "/auth/solicitarCodigoRecuperacion", "/auth/recuperarContrasenaCliente",
  "/cliente/crear"];

/**
 * Indica si la URL de la petición coincide con alguna ruta donde
 * NO debemos disparar el logout automático.
 */
const esRutaSinAutoLogout = (url: string | undefined): boolean => {
  if (!url) return false;
  return RUTAS_SIN_AUTO_LOGOUT.some((ruta) => url.includes(ruta));
};

/**
 * Bandera global para evitar redirects en cascada cuando varias
 * peticiones simultáneas fallan con 401/403 al mismo tiempo.
 */
let redirigiendo = false;

/**
 * Interceptor de request: inyecta el JWT en Authorization si hay sesión.
 */
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const sesion = localStorage.getItem("cocotech_session");
    if (sesion) {
      try {
        const { token } = JSON.parse(sesion);
        if (token && config.headers) {
          config.headers.Authorization = `Bearer ${token}`;
        }
      } catch {
        // Sesión corrupta — limpiar.
        localStorage.removeItem("cocotech_session");
      }
    }
    return config;
  },
  (error: AxiosError) => Promise.reject(error),
);

/**
 * Interceptor de response: si el backend responde 401 (token expirado/
 * inválido) o 403 (no autorizado para el endpoint), limpia la sesión y
 * redirige al login.
 *
 * Excepciones:
 *  - Si la petición fallida fue al propio login u otros endpoints de
 *    auth, NO redirigimos: el usuario está intentando autenticarse y
 *    el error es legítimo de credenciales.
 *  - Si ya estamos redirigiendo por otra petición simultánea, no
 *    disparamos otro redirect (evita bucles).
 */
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    const status = error.response?.status;
    const url = error.config?.url;

    if ((status === 401 || status === 403) && !esRutaSinAutoLogout(url)) {
      if (!redirigiendo) {
        redirigiendo = true;
        localStorage.removeItem("cocotech_session");
        // Pequeño delay para que cualquier render en curso termine.
        setTimeout(() => {
          if (!window.location.pathname.includes("/login")) {
            window.location.href = "/login";
          }
          redirigiendo = false;
        }, 50);
      }
    }
    return Promise.reject(error);
  },
);

export default api;