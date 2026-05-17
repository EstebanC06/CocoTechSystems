/**
 * Servicio de sucursales.
 * Conecta con SucursalController del backend (/sucursal/*).
 */
import api from "./api";
import type { SucursalDTO } from "../types";

export const obtenerSucursales = async (): Promise<SucursalDTO[]> => {
  const response = await api.get<SucursalDTO[]>("/sucursal/mostrarTodas");
  return response.data;
};

export const obtenerSucursalPorId = async (
  id: number
): Promise<SucursalDTO> => {
  const response = await api.get<SucursalDTO>(`/sucursal/obtenerPorId/${id}`);
  return response.data;
};

export const crearSucursal = async (sucursal: SucursalDTO) => {
  const response = await api.post("/sucursal/crear", sucursal);
  return response.data;
};

export const actualizarSucursal = async (
  id: number,
  sucursal: SucursalDTO
) => {
  const response = await api.put(`/sucursal/actualizar?id=${id}`, sucursal);
  return response.data;
};

export const eliminarSucursal = async (id: number) => {
  const response = await api.delete(`/sucursal/eliminar/${id}`);
  return response.data;
};
