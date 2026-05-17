/**
 * Servicio de cajas registradoras.
 * Conecta con CajaRegistradoraController del backend (/caja/*).
 */
import api from "./api";
import type { CajaRegistradoraDTO } from "../types";

export const obtenerCajas = async (): Promise<CajaRegistradoraDTO[]> => {
  const response = await api.get<CajaRegistradoraDTO[]>("/caja/mostrarTodas");
  return response.data;
};

export const obtenerCajaPorId = async (
  id: number
): Promise<CajaRegistradoraDTO> => {
  const response = await api.get<CajaRegistradoraDTO>(
    `/caja/obtenerPorId/${id}`
  );
  return response.data;
};

export const crearCaja = async (caja: CajaRegistradoraDTO) => {
  const response = await api.post("/caja/crear", caja);
  return response.data;
};

export const actualizarCaja = async (
  id: number,
  caja: CajaRegistradoraDTO
) => {
  const response = await api.put(`/caja/actualizar?id=${id}`, caja);
  return response.data;
};

export const eliminarCaja = async (id: number) => {
  const response = await api.delete(`/caja/eliminar/${id}`);
  return response.data;
};
