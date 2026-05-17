/**
 * Servicio de empleados.
 * Conecta con EmpleadoController del backend (/empleado/*).
 */
import api from "./api";
import type { EmpleadoDTO } from "../types";

export const obtenerEmpleados = async (): Promise<EmpleadoDTO[]> => {
  const response = await api.get<EmpleadoDTO[]>("/empleado/mostrarTodos");
  return response.data;
};

export const obtenerEmpleadoPorId = async (
  id: number
): Promise<EmpleadoDTO> => {
  const response = await api.get<EmpleadoDTO>(`/empleado/obtenerPorId/${id}`);
  return response.data;
};

export const crearEmpleado = async (empleado: EmpleadoDTO) => {
  const response = await api.post("/empleado/crear", empleado);
  return response.data;
};

export const actualizarEmpleado = async (
  id: number,
  empleado: EmpleadoDTO
) => {
  const response = await api.put(`/empleado/actualizar?id=${id}`, empleado);
  return response.data;
};

/**
 * Cambia la contraseña del empleado.
 * El back hashea con BCrypt antes de guardar.
 */
export const actualizarContrasenaEmpleado = async (
  id: number,
  nuevaContrasena: string
): Promise<void> => {
  await api.put(
    `/empleado/actualizarContrasena?id=${id}&nuevaContrasena=${encodeURIComponent(
      nuevaContrasena
    )}`
  );
};

export const eliminarEmpleado = async (id: number) => {
  const response = await api.delete(`/empleado/eliminar/${id}`);
  return response.data;
};

export const contarEmpleados = async (): Promise<number> => {
  const response = await api.get<number>("/empleado/contar");
  return response.data;
};
