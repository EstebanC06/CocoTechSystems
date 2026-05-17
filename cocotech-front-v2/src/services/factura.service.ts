/**
 * Servicio de facturas.
 *
 * Cubre dos grupos de endpoints:
 *  - /factura/*       → CRUD transaccional en MySQL.
 *  - /factura/mongo/* → consultas de lectura intensiva en MongoDB.
 */
import api from "./api";
import type { FacturaDTO, FacturaDocumento } from "../types";

// ─── Endpoints MySQL ──────────────────────────────────────────

export const obtenerFacturas = async (): Promise<FacturaDTO[]> => {
  const response = await api.get<FacturaDTO[]>("/factura/mostrarTodas");
  return response.data;
};

export const obtenerFacturaPorId = async (id: number): Promise<FacturaDTO> => {
  const response = await api.get<FacturaDTO>(`/factura/obtenerPorId/${id}`);
  return response.data;
};

export const obtenerFacturaPorVenta = async (
  idVenta: number
): Promise<FacturaDTO> => {
  const response = await api.get<FacturaDTO>(
    `/factura/obtenerPorVenta/${idVenta}`
  );
  return response.data;
};

export const crearFactura = async (factura: FacturaDTO) => {
  const response = await api.post("/factura/crear", factura);
  return response.data;
};

export const actualizarFactura = async (id: number, factura: FacturaDTO) => {
  const response = await api.put(`/factura/actualizar?id=${id}`, factura);
  return response.data;
};

export const eliminarFactura = async (id: number) => {
  const response = await api.delete(`/factura/eliminar/${id}`);
  return response.data;
};

/** Ingreso bruto en un rango de fechas. */
export const obtenerIngresoBruto = async (inicio: string, fin: string) => {
  const response = await api.get("/factura/reportes/ingresoBruto", {
    params: { inicio, fin },
  });
  return response.data;
};

/** Impuestos recaudados en un rango de fechas. */
export const obtenerImpuestosRecaudados = async (
  inicio: string,
  fin: string
) => {
  const response = await api.get("/factura/reportes/impuestosRecaudados", {
    params: { inicio, fin },
  });
  return response.data;
};

// ─── Endpoints MongoDB (lectura intensiva) ────────────────────

/** Obtiene la factura embebida desde Mongo por ID de MySQL. */
export const obtenerFacturaMongoPorIdMySQL = async (
  idFacturaMySQL: number
): Promise<FacturaDocumento> => {
  const response = await api.get<FacturaDocumento>(
    `/factura/mongo/obtenerPorIdMySQL/${idFacturaMySQL}`
  );
  return response.data;
};

/** Obtiene factura embebida por ID de venta. */
export const obtenerFacturaMongoPorVenta = async (
  idVenta: number
): Promise<FacturaDocumento> => {
  const response = await api.get<FacturaDocumento>(
    `/factura/mongo/obtenerPorVenta/${idVenta}`
  );
  return response.data;
};

/** Historial completo de facturas de un cliente (sin JOINs). */
export const obtenerHistorialCliente = async (
  idCliente: number
): Promise<FacturaDocumento[]> => {
  const response = await api.get<FacturaDocumento[]>(
    `/factura/mongo/historialCliente/${idCliente}`
  );
  return response.data;
};

/** Facturas en un rango de fechas (Mongo). */
export const obtenerFacturasPorPeriodo = async (
  inicio: string,
  fin: string
): Promise<FacturaDocumento[]> => {
  const response = await api.get<FacturaDocumento[]>(
    "/factura/mongo/porPeriodo",
    { params: { inicio, fin } }
  );
  return response.data;
};

/** Reporte agregado: ingreso por sucursal. */
export const obtenerIngresoPorSucursal = async (
  inicio: string,
  fin: string
) => {
  const response = await api.get(
    "/factura/mongo/reportes/ingresoPorSucursal",
    { params: { inicio, fin } }
  );
  return response.data;
};

/** Top 10 productos más vendidos. */
export const obtenerTopProductos = async (inicio: string, fin: string) => {
  const response = await api.get("/factura/mongo/reportes/topProductos", {
    params: { inicio, fin },
  });
  return response.data;
};

/** Top 10 clientes por monto gastado. */
export const obtenerTopClientes = async (inicio: string, fin: string) => {
  const response = await api.get("/factura/mongo/reportes/topClientes", {
    params: { inicio, fin },
  });
  return response.data;
};

/** Sincroniza facturas MySQL → MongoDB (uso administrativo). */
export const sincronizarFacturas = async () => {
  const response = await api.post("/factura/mongo/sincronizar");
  return response.data;
};
