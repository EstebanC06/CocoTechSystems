/**
 * Servicio de clientes.
 * Conecta con ClienteController del backend (/cliente/*).
 */
import api from "./api";
import type { ClienteDTO } from "../types";

export const obtenerClientes = async (): Promise<ClienteDTO[]> => {
  const response = await api.get<ClienteDTO[]>("/cliente/mostrarTodos");
  return response.data;
};

export const obtenerClientePorId = async (id: number): Promise<ClienteDTO> => {
  const response = await api.get<ClienteDTO>(`/cliente/obtenerPorId/${id}`);
  return response.data;
};

export const actualizarCliente = async (id: number, cliente: ClienteDTO) => {
  const response = await api.put(`/cliente/actualizar?id=${id}`, cliente);
  return response.data;
};

export const actualizarContrasenaCliente = async (
  id: number,
  nuevaContrasena: string
) => {
  const response = await api.put(`/cliente/actualizarContrasena?id=${id}`, {
    contrasena: nuevaContrasena,
  });
  return response.data;
};

export const eliminarCliente = async (id: number) => {
  const response = await api.delete(`/cliente/eliminar/${id}`);
  return response.data;
};
