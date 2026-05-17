/**
 * Servicio de proveedores.
 * Conecta con ProveedorController del backend (/proveedor/*).
 */
import api from "./api";
import type { ProveedorDTO } from "../types";

export const obtenerProveedores = async (): Promise<ProveedorDTO[]> => {
  const response = await api.get<ProveedorDTO[]>("/proveedor/mostrarTodos");
  return response.data;
};

export const obtenerProveedorPorId = async (
  id: number
): Promise<ProveedorDTO> => {
  const response = await api.get<ProveedorDTO>(`/proveedor/obtenerPorId/${id}`);
  return response.data;
};

export const crearProveedor = async (proveedor: ProveedorDTO) => {
  const response = await api.post("/proveedor/crear", proveedor);
  return response.data;
};

export const actualizarProveedor = async (
  id: number,
  proveedor: ProveedorDTO
) => {
  const response = await api.put(`/proveedor/actualizar?id=${id}`, proveedor);
  return response.data;
};

export const eliminarProveedor = async (id: number) => {
  const response = await api.delete(`/proveedor/eliminar/${id}`);
  return response.data;
};
