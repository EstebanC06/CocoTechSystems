/**
 * Contexto del carrito de compras.
 *
 * Características:
 *  - Persiste en localStorage para sobrevivir refresh y cerrar navegador.
 *  - Permite navegación libre sin login; solo en checkout se exige sesión.
 *  - Valida stock al agregar/incrementar.
 *  - Calcula totales (subtotal, IVA 19%, total) en tiempo real.
 */
import {
  createContext,
  useContext,
  useState,
  useEffect,
  useMemo,
  type ReactNode,
} from "react";
import type { ProductoDTO, ItemCarrito } from "../types";

const IVA = 0.19;
const STORAGE_KEY = "cocotech_carrito";

interface CarritoContextType {
  items: ItemCarrito[];
  cantidadItems: number;
  subtotal: number;
  iva: number;
  total: number;
  agregar: (p: ProductoDTO, cantidad?: number) => void;
  modificarCantidad: (idProducto: number, nuevaCantidad: number) => void;
  quitar: (idProducto: number) => void;
  vaciar: () => void;
  /** Calcula precio final de un producto considerando su descuento. */
  precioConDescuento: (p: ProductoDTO) => number;
}

const CarritoContext = createContext<CarritoContextType | undefined>(undefined);

export const CarritoProvider = ({ children }: { children: ReactNode }) => {
  const [items, setItems] = useState<ItemCarrito[]>(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  });

  // Persistir cada cambio.
  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
  }, [items]);

  const precioConDescuento = (p: ProductoDTO): number => {
    const d = p.descuentoPorcentaje ?? 0;
    return d > 0 ? p.precio * (1 - d / 100) : p.precio;
  };

  const agregar = (producto: ProductoDTO, cantidad: number = 1) => {
    setItems((prev) => {
      const existente = prev.find((it) => it.producto.idProducto === producto.idProducto);
      if (existente) {
        const nuevaCantidad = existente.cantidad + cantidad;
        // No exceder stock disponible.
        const limitada = Math.min(nuevaCantidad, producto.stock);
        return prev.map((it) =>
          it.producto.idProducto === producto.idProducto
            ? { ...it, cantidad: limitada }
            : it
        );
      }
      const inicial = Math.min(cantidad, producto.stock);
      if (inicial <= 0) return prev;
      return [...prev, { producto, cantidad: inicial }];
    });
  };

  const modificarCantidad = (idProducto: number, nuevaCantidad: number) => {
    setItems((prev) => {
      if (nuevaCantidad <= 0) {
        return prev.filter((it) => it.producto.idProducto !== idProducto);
      }
      return prev.map((it) => {
        if (it.producto.idProducto !== idProducto) return it;
        const limitada = Math.min(nuevaCantidad, it.producto.stock);
        return { ...it, cantidad: limitada };
      });
    });
  };

  const quitar = (idProducto: number) => {
    setItems((prev) => prev.filter((it) => it.producto.idProducto !== idProducto));
  };

  const vaciar = () => setItems([]);

  const cantidadItems = useMemo(
    () => items.reduce((acc, it) => acc + it.cantidad, 0),
    [items]
  );

  const subtotal = useMemo(
    () =>
      items.reduce(
        (acc, it) => acc + precioConDescuento(it.producto) * it.cantidad,
        0
      ),
    [items]
  );

  const iva = useMemo(() => subtotal * IVA, [subtotal]);
  const total = useMemo(() => subtotal + iva, [subtotal, iva]);

  return (
    <CarritoContext.Provider
      value={{
        items,
        cantidadItems,
        subtotal,
        iva,
        total,
        agregar,
        modificarCantidad,
        quitar,
        vaciar,
        precioConDescuento,
      }}
    >
      {children}
    </CarritoContext.Provider>
  );
};

export const useCarrito = () => {
  const ctx = useContext(CarritoContext);
  if (!ctx) throw new Error("useCarrito debe usarse dentro de CarritoProvider");
  return ctx;
};
