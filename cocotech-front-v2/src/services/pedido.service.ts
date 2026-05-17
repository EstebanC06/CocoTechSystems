/**
 * Servicio de pedidos (e-commerce).
 * Conecta con /pedido/* del backend.
 */
import api from "./api";
import type { PedidoDTO, EstadoPedido } from "../types";

/** Cliente crea un pedido tras el checkout. */
export const crearPedido = async (pedido: PedidoDTO): Promise<PedidoDTO> => {
  const r = await api.post<PedidoDTO>("/pedido/crear", pedido);
  return r.data;
};

/** Admin: todos los pedidos del sistema. */
export const obtenerTodosPedidos = async (): Promise<PedidoDTO[]> => {
  const r = await api.get<PedidoDTO[]>("/pedido/mostrarTodos");
  return r.data;
};

/** Detalle de un pedido. */
export const obtenerPedidoPorId = async (id: number): Promise<PedidoDTO> => {
  const r = await api.get<PedidoDTO>(`/pedido/obtenerPorId/${id}`);
  return r.data;
};

/** Pedidos de un cliente — el cliente ve los suyos. */
export const obtenerPedidosCliente = async (idCliente: number): Promise<PedidoDTO[]> => {
  const r = await api.get<PedidoDTO[]>(`/pedido/cliente/${idCliente}`);
  return r.data;
};

/** Pedidos asignados a una sucursal — el empleado ve los de su sucursal. */
export const obtenerPedidosSucursal = async (idSucursal: number): Promise<PedidoDTO[]> => {
  const r = await api.get<PedidoDTO[]>(`/pedido/sucursal/${idSucursal}`);
  return r.data;
};

/** Pedidos filtrados por estado. */
export const obtenerPedidosPorEstado = async (estado: EstadoPedido): Promise<PedidoDTO[]> => {
  const r = await api.get<PedidoDTO[]>(`/pedido/porEstado/${estado}`);
  return r.data;
};

/** Empleado/admin cambia el estado de un pedido. */
export const cambiarEstadoPedido = async (id: number, nuevoEstado: EstadoPedido) => {
  const r = await api.put(`/pedido/cambiarEstado`, null, {
    params: { id, nuevoEstado },
  });
  return r.data;
};

/** Cliente o admin cancela un pedido (si está en RECIBIDO). */
export const cancelarPedido = async (id: number) => {
  const r = await api.delete(`/pedido/cancelar/${id}`);
  return r.data;
};
