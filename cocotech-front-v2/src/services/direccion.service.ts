/**
 * Servicio de direcciones del cliente.
 * Conecta con /direccion/* del backend.
 */
import api from "./api";
import type { DireccionClienteDTO } from "../types";

export const crearDireccion = async (dir: DireccionClienteDTO) => {
  const r = await api.post("/direccion/crear", dir);
  return r.data;
};

export const obtenerDireccionesCliente = async (
  idCliente: number
): Promise<DireccionClienteDTO[]> => {
  const r = await api.get<DireccionClienteDTO[]>(`/direccion/cliente/${idCliente}`);
  return r.data;
};

export const actualizarDireccion = async (id: number, dir: DireccionClienteDTO) => {
  const r = await api.put(`/direccion/actualizar?id=${id}`, dir);
  return r.data;
};

export const eliminarDireccion = async (id: number) => {
  const r = await api.delete(`/direccion/eliminar/${id}`);
  return r.data;
};

export const marcarPredeterminada = async (id: number) => {
  const r = await api.put(`/direccion/marcarPredeterminada/${id}`);
  return r.data;
};
