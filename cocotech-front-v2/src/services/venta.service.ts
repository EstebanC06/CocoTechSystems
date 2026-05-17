/**
 * Servicio de ventas.
 * Conecta con VentaController del backend (/venta/*).
 */
import api from "./api";
import type { VentaDTO } from "../types";

export const obtenerVentas = async (): Promise<VentaDTO[]> => {
  const response = await api.get<VentaDTO[]>("/venta/mostrarTodas");
  return response.data;
};

export const obtenerVentaPorId = async (id: number): Promise<VentaDTO> => {
  const response = await api.get<VentaDTO>(`/venta/obtenerPorId/${id}`);
  return response.data;
};

export const crearVenta = async (venta: VentaDTO) => {
  const response = await api.post("/venta/crear", venta);
  return response.data;
};

export const actualizarVenta = async (id: number, venta: VentaDTO) => {
  const response = await api.put(`/venta/actualizar?id=${id}`, venta);
  return response.data;
};

export const eliminarVenta = async (id: number) => {
  const response = await api.delete(`/venta/eliminar/${id}`);
  return response.data;
};

// ─── Reportes analíticos ──────────────────────────────────────

/** Empleado del mes (más ventas en un rango). */
export const obtenerEmpleadoDelMes = async (inicio: string, fin: string) => {
  const response = await api.get("/venta/reportes/empleadoDelMes", {
    params: { inicio, fin },
  });
  return response.data;
};

/** Cliente con más compras realizadas. */
export const obtenerClienteConMasCompras = async () => {
  const response = await api.get("/venta/reportes/clienteConMasCompras");
  return response.data;
};

/** Total de ventas por empleado. */
export const obtenerTotalPorEmpleado = async () => {
  const response = await api.get("/venta/reportes/totalPorEmpleado");
  return response.data;
};
