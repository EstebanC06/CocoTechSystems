/**
 * Servicio de autenticación.
 *
 * Conecta con el AuthController del backend (/auth/*).
 */
import api from "./api";
import type {
  LoginPayload,
  LoginResponse,
  ClienteDTO,
  SesionUsuario,
} from "../types";

/**
 * Inicia sesión enviando credenciales al backend.
 *
 * @param payload Correo (username) y contraseña.
 * @returns Respuesta con token JWT, rol e ID del usuario.
 */
export const login = async (payload: LoginPayload): Promise<LoginResponse> => {
  const response = await api.post<LoginResponse>("/auth/login", payload);
  return response.data;
};

/**
 * Registra un nuevo cliente. Internamente el back crea un Cliente con
 * ROLE_CLIENTE.
 *
 * @param cliente Datos del cliente a registrar.
 */
export const registrarCliente = async (cliente: ClienteDTO) => {
  const response = await api.post("/cliente/crear", cliente);
  return response.data;
};

/**
 * Solicita recuperación de contraseña para un cliente. El back genera un
 * código de verificación que se envía por correo.
 *
 * @param correo Correo del cliente.
 */
export const recuperarContrasenaCliente = async (correo: string) => {
  const response = await api.put("/auth/recuperarContrasenaCliente", { correo });
  return response.data;
};

/**
 * Guarda la sesión del usuario en localStorage tras un login exitoso.
 */
export const guardarSesion = (sesion: SesionUsuario): void => {
  localStorage.setItem("cocotech_session", JSON.stringify(sesion));
};

/**
 * Obtiene la sesión actual desde localStorage, o null si no hay sesión.
 */
export const obtenerSesion = (): SesionUsuario | null => {
  const raw = localStorage.getItem("cocotech_session");
  if (!raw) return null;
  try {
    return JSON.parse(raw) as SesionUsuario;
  } catch {
    return null;
  }
};

/**
 * Cierra la sesión del usuario.
 */
export const cerrarSesion = (): void => {
  localStorage.removeItem("cocotech_session");
};

/**
 * Indica si hay una sesión activa.
 */
export const estaAutenticado = (): boolean => obtenerSesion() !== null;

/**
 * Indica si el usuario actual es administrador.
 */
export const esAdmin = (): boolean => obtenerSesion()?.rol === "ROLE_ADMIN";

/**
 * Indica si el usuario actual es cliente.
 */
export const esCliente = (): boolean => obtenerSesion()?.rol === "ROLE_CLIENTE";
