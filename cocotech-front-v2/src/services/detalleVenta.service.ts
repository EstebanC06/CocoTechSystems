/**
 * Servicio de detalles de venta.
 * Conecta con DetalleVentaController del backend (/detalleVenta/*).
 */
import api from "./api";
import type { DetalleVentaDTO } from "../types";

export const obtenerDetallesVenta = async (): Promise<DetalleVentaDTO[]> => {
  const response = await api.get<DetalleVentaDTO[]>(
    "/detalleVenta/mostrarTodos"
  );
  return response.data;
};

export const obtenerDetalleVentaPorId = async (
  id: number
): Promise<DetalleVentaDTO> => {
  const response = await api.get<DetalleVentaDTO>(
    `/detalleVenta/obtenerPorId/${id}`
  );
  return response.data;
};

export const crearDetalleVenta = async (detalle: DetalleVentaDTO) => {
  const response = await api.post("/detalleVenta/crear", detalle);
  return response.data;
};

export const actualizarDetalleVenta = async (
  id: number,
  detalle: DetalleVentaDTO
) => {
  const response = await api.put(
    `/detalleVenta/actualizar?id=${id}`,
    detalle
  );
  return response.data;
};

export const eliminarDetalleVenta = async (id: number) => {
  const response = await api.delete(`/detalleVenta/eliminar/${id}`);
  return response.data;
};

/** Reporte: resumen por método de pago. */
export const obtenerResumenPorMetodoPago = async () => {
  const response = await api.get("/detalleVenta/reportes/resumenPorMetodoPago");
  return response.data;
};

/** Reporte: detalles con promoción aplicada. */
export const obtenerDetallesConPromocion = async () => {
  const response = await api.get("/detalleVenta/reportes/conPromocion");
  return response.data;
};
