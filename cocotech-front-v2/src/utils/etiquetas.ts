/**
 * Utilidades para mostrar valores de enum como etiquetas legibles.
 *
 * Resuelve el problema de mostrar al usuario un valor crudo del enum
 * (ej. "FRUTAS_VERDURAS") como una etiqueta amigable
 * (ej. "Frutas y verduras"), tomando los diccionarios definidos en
 * `types/index.ts` como fuente de verdad.
 *
 * Si el valor no está en el diccionario, se hace un mejor esfuerzo:
 * se reemplazan guiones bajos por espacios y se capitalizan palabras.
 * Eso garantiza que aunque el back agregue un valor nuevo al enum sin
 * etiqueta correspondiente, la UI no muestre cosas como "VALOR_X" sino
 * "Valor X".
 */
import {
  ETIQUETAS_SUCURSAL,
  ETIQUETAS_PROVEEDOR,
  ETIQUETAS_CATEGORIA,
  ETIQUETAS_ESTADO_PEDIDO,
  ETIQUETAS_ESTADO_CAJA,
  type NombreSucursal,
  type NombreProveedor,
  type NombreCategoria,
  type EstadoPedido,
  type EstadoCaja,
} from "../types";

/**
 * Convierte cualquier identificador SCREAMING_SNAKE_CASE en un texto
 * legible "Capitalizado". Sirve como fallback cuando no hay etiqueta
 * declarada en el diccionario correspondiente.
 *
 * Ejemplo: `"FRUTAS_VERDURAS"` → `"Frutas verduras"`.
 */
export const formatearEnum = (valor: string | null | undefined): string => {
  if (!valor) return "";
  return valor
    .toLowerCase()
    .replace(/_/g, " ")
    .replace(/\b\w/g, (c) => c.toUpperCase());
};

/** Etiqueta legible de una sucursal. */
export const etiquetaSucursal = (n: NombreSucursal | string | null | undefined): string => {
  if (!n) return "";
  return ETIQUETAS_SUCURSAL[n as NombreSucursal] ?? formatearEnum(n);
};

/** Etiqueta legible de un proveedor. */
export const etiquetaProveedor = (n: NombreProveedor | string | null | undefined): string => {
  if (!n) return "";
  return ETIQUETAS_PROVEEDOR[n as NombreProveedor] ?? formatearEnum(n);
};

/** Etiqueta legible de una categoría. */
export const etiquetaCategoria = (n: NombreCategoria | string | null | undefined): string => {
  if (!n) return "";
  return ETIQUETAS_CATEGORIA[n as NombreCategoria] ?? formatearEnum(n);
};

/** Etiqueta legible de un estado de pedido. */
export const etiquetaEstadoPedido = (e: EstadoPedido | string | null | undefined): string => {
  if (!e) return "";
  return ETIQUETAS_ESTADO_PEDIDO[e as EstadoPedido] ?? formatearEnum(e);
};

/** Etiqueta legible de un estado de caja. */
export const etiquetaEstadoCaja = (e: EstadoCaja | string | null | undefined): string => {
  if (!e) return "";
  return ETIQUETAS_ESTADO_CAJA[e as EstadoCaja] ?? formatearEnum(e);
};
