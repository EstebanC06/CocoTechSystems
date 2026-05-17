/**
 * Servicio de productos.
 * Conecta con ProductoController del backend (/producto/*).
 */
import api from "./api";
import type { ProductoDTO } from "../types";

/** Obtiene todos los productos. */
export const obtenerProductos = async (): Promise<ProductoDTO[]> => {
  const response = await api.get<ProductoDTO[]>("/producto/mostrarTodos");
  return response.data;
};

/** Obtiene un producto por su ID. */
export const obtenerProductoPorId = async (
  id: number
): Promise<ProductoDTO> => {
  const response = await api.get<ProductoDTO>(`/producto/obtenerPorId/${id}`);
  return response.data;
};

/** Crea un nuevo producto. Requiere rol ADMIN. */
export const crearProducto = async (producto: ProductoDTO) => {
  const response = await api.post("/producto/crear", producto);
  return response.data;
};

/** Actualiza un producto existente. Requiere rol ADMIN. */
export const actualizarProducto = async (
  id: number,
  producto: ProductoDTO
) => {
  const response = await api.put(`/producto/actualizar?id=${id}`, producto);
  return response.data;
};

/** Elimina un producto por ID. Requiere rol ADMIN. */
export const eliminarProducto = async (id: number) => {
  const response = await api.delete(`/producto/eliminar/${id}`);
  return response.data;
};

/** Cuenta los productos registrados. */
export const contarProductos = async (): Promise<number> => {
  const response = await api.get<number>("/producto/contar");
  return response.data;
};

/** Reporte: producto más vendido por categoría. */
export const obtenerMasVendidoPorCategoria = async () => {
  const response = await api.get("/producto/reportes/masVendidoPorCategoria");
  return response.data;
};

/** Reporte: ranking de productos por ventas. */
export const obtenerRankingVentas = async () => {
  const response = await api.get("/producto/reportes/rankingVentas");
  return response.data;
};
