/**
 * Servicio de categorías.
 * Conecta con CategoriaController del backend (/categoria/*).
 */
import api from "./api";
import type { CategoriaDTO } from "../types";

export const obtenerCategorias = async (): Promise<CategoriaDTO[]> => {
  const response = await api.get<CategoriaDTO[]>("/categoria/mostrarTodas");
  return response.data;
};

export const obtenerCategoriaPorId = async (
  id: number
): Promise<CategoriaDTO> => {
  const response = await api.get<CategoriaDTO>(`/categoria/obtenerPorId/${id}`);
  return response.data;
};

export const crearCategoria = async (categoria: CategoriaDTO) => {
  const response = await api.post("/categoria/crear", categoria);
  return response.data;
};

export const actualizarCategoria = async (
  id: number,
  categoria: CategoriaDTO
) => {
  const response = await api.put(`/categoria/actualizar?id=${id}`, categoria);
  return response.data;
};

export const eliminarCategoria = async (id: number) => {
  const response = await api.delete(`/categoria/eliminar/${id}`);
  return response.data;
};
