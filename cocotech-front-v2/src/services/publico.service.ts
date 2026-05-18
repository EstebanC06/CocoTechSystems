/**
 * Servicio público — endpoints accesibles sin autenticación.
 *
 * Conecta con /publico/* del backend.
 *
 * IMPORTANTE: si tu backend NO tiene estos endpoints aún, este service
 * tiene un fallback que llama a los endpoints autenticados normales.
 * Cuando agregues los /publico/* en Spring Boot, los fallbacks se
 * pueden eliminar.
 */
import axios from "axios";
import api, { BASE_URL } from "./api";
import type { ProductoDTO, CategoriaDTO, SucursalDTO } from "../types";

/**
 * Cliente axios sin interceptor de JWT, para llamadas públicas.
 * Si el backend aún no tiene endpoints /publico/*, se usa el cliente
 * normal con token.
 */
const apiPublico = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json" },
});

/** Trae el catálogo completo de productos activos. */
export const obtenerProductosPublico = async (): Promise<ProductoDTO[]> => {
  try {
    const r = await apiPublico.get<ProductoDTO[]>("/publico/producto/mostrarTodos");
    return r.data;
  } catch {
    // Fallback al endpoint autenticado existente.
    const r = await api.get<ProductoDTO[]>("/producto/mostrarTodos");
    return r.data;
  }
};

/** Trae un producto por ID. */
export const obtenerProductoPublicoPorId = async (id: number): Promise<ProductoDTO> => {
  try {
    const r = await apiPublico.get<ProductoDTO>(`/publico/producto/obtenerPorId/${id}`);
    return r.data;
  } catch {
    const r = await api.get<ProductoDTO>(`/producto/obtenerPorId/${id}`);
    return r.data;
  }
};

/** Busca productos por término. */
export const buscarProductosPublico = async (q: string): Promise<ProductoDTO[]> => {
  try {
    const r = await apiPublico.get<ProductoDTO[]>("/publico/producto/buscar", { params: { q } });
    return r.data;
  } catch {
    // Fallback: trae todos y filtra en cliente.
    const r = await api.get<ProductoDTO[]>("/producto/mostrarTodos");
    return r.data.filter((p) =>
      p.nombre.toLowerCase().includes(q.toLowerCase())
    );
  }
};

/** Productos destacados (para Home). */
export const obtenerProductosDestacados = async (): Promise<ProductoDTO[]> => {
  try {
    const r = await apiPublico.get<ProductoDTO[]>("/publico/producto/destacados");
    return r.data;
  } catch {
    const r = await api.get<ProductoDTO[]>("/producto/mostrarTodos");
    return r.data.filter((p) => p.destacado).slice(0, 8);
  }
};

/** Productos de una categoría específica. */
export const obtenerProductosPorCategoria = async (idCategoria: number): Promise<ProductoDTO[]> => {
  try {
    const r = await apiPublico.get<ProductoDTO[]>(`/publico/producto/porCategoria/${idCategoria}`);
    return r.data;
  } catch {
    const r = await api.get<ProductoDTO[]>("/producto/mostrarTodos");
    return r.data.filter((p) => p.idCategoria === idCategoria);
  }
};

/** Categorías visibles públicamente. */
export const obtenerCategoriasPublico = async (): Promise<CategoriaDTO[]> => {
  try {
    const r = await apiPublico.get<CategoriaDTO[]>("/publico/categoria/mostrarTodas");
    return r.data;
  } catch {
    const r = await api.get<CategoriaDTO[]>("/categoria/mostrarTodas");
    return r.data;
  }
};

/** Sucursales disponibles para checkout (sin requerir login). */
export const obtenerSucursalesPublico = async (): Promise<SucursalDTO[]> => {
  try {
    const r = await apiPublico.get<SucursalDTO[]>("/publico/sucursal/mostrarTodas");
    return r.data;
  } catch {
    const r = await api.get<SucursalDTO[]>("/sucursal/mostrarTodas");
    return r.data;
  }
};
