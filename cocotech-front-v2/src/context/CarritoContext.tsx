/**
 * Contexto del carrito de compras (con scoping por usuario).
 *
 * Características:
 *  - Carrito separado por usuario: cada usuario logueado tiene su propio
 *    carrito persistido bajo la clave `cocotech_carrito_<id>`.
 *  - Carrito de invitado: visitantes anónimos usan `cocotech_carrito_guest`.
 *  - Merge al login: si el invitado tenía productos y luego inicia sesión,
 *    sus items se fusionan con el carrito existente del usuario (sumando
 *    cantidades y respetando stock); luego se borra el carrito de invitado.
 *  - Reset visual al logout: el carrito en pantalla pasa a mostrar el de
 *    invitado (típicamente vacío). El carrito del usuario que cerró sesión
 *    permanece guardado bajo su clave para cuando vuelva a entrar.
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
  useRef,
  type ReactNode,
} from "react";
import type { ProductoDTO, ItemCarrito } from "../types";
import { useAuth } from "./AuthContext";

const IVA = 0.19;
const STORAGE_PREFIX = "cocotech_carrito";
const GUEST_KEY = `${STORAGE_PREFIX}_guest`;

/**
 * Construye la clave de localStorage según el usuario actual.
 * Sin sesión → invitado.
 */
const claveCarrito = (idUsuario: number | undefined | null): string =>
  idUsuario != null ? `${STORAGE_PREFIX}_${idUsuario}` : GUEST_KEY;

/**
 * Lee de forma segura un carrito de localStorage.
 */
const leerCarrito = (clave: string): ItemCarrito[] => {
  try {
    const raw = localStorage.getItem(clave);
    return raw ? (JSON.parse(raw) as ItemCarrito[]) : [];
  } catch {
    return [];
  }
};

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
  const { sesion } = useAuth();
  const idActual = sesion?.id ?? null;

  // Estado inicial: cargado desde la clave correspondiente (guest si no hay
  // sesión todavía).
  const [items, setItems] = useState<ItemCarrito[]>(() =>
    leerCarrito(claveCarrito(idActual))
  );

  // Ref para detectar cambios de usuario y disparar swap/merge una sola vez
  // por transición (login, logout, cambio de cuenta).
  const idAnterior = useRef<number | null>(idActual);

  // Reaccionar a cambios de sesión:
  //  · null  → id:    LOGIN  → merge del carrito de invitado con el del user
  //  · id    → null:  LOGOUT → cambiar a carrito de invitado
  //  · id1   → id2:   SWITCH → cambiar al carrito del nuevo usuario
  useEffect(() => {
    if (idAnterior.current === idActual) return;

    const previo = idAnterior.current;
    idAnterior.current = idActual;

    // LOGIN: previo era invitado (null), ahora hay id.
    if (previo == null && idActual != null) {
      const itemsGuest = leerCarrito(GUEST_KEY);
      const itemsUsuario = leerCarrito(claveCarrito(idActual));

      if (itemsGuest.length === 0) {
        // Nada que mergear: simplemente cargar el carrito del usuario.
        setItems(itemsUsuario);
      } else {
        // Mergear: sumar cantidades de productos repetidos sin pasarse de stock.
        // Items sin idProducto se descartan por seguridad (no deberían existir).
        const mapa = new Map<number, ItemCarrito>();
        for (const it of itemsUsuario) {
          if (it.producto.idProducto == null) continue;
          mapa.set(it.producto.idProducto, { ...it });
        }
        for (const it of itemsGuest) {
          if (it.producto.idProducto == null) continue;
          const existente = mapa.get(it.producto.idProducto);
          if (existente) {
            const sumada = existente.cantidad + it.cantidad;
            existente.cantidad = Math.min(sumada, existente.producto.stock);
          } else {
            mapa.set(it.producto.idProducto, { ...it });
          }
        }
        const merged = Array.from(mapa.values());
        setItems(merged);
        // Limpiar el carrito de invitado tras el merge.
        localStorage.removeItem(GUEST_KEY);
      }
      return;
    }

    // LOGOUT: previo tenía id, ahora es null.
    if (previo != null && idActual == null) {
      // El carrito del usuario ya está persistido bajo su clave por el
      // useEffect de persistencia. Cargamos el carrito de invitado en pantalla
      // (típicamente vacío).
      setItems(leerCarrito(GUEST_KEY));
      return;
    }

    // SWITCH: cambio directo de un usuario a otro (raro, pero posible).
    if (previo != null && idActual != null && previo !== idActual) {
      setItems(leerCarrito(claveCarrito(idActual)));
      return;
    }
  }, [idActual]);

  // Persistir cada cambio bajo la clave del usuario actual (o guest).
  useEffect(() => {
    localStorage.setItem(claveCarrito(idActual), JSON.stringify(items));
  }, [items, idActual]);

  const precioConDescuento = (p: ProductoDTO): number => {
    const d = p.descuentoPorcentaje ?? 0;
    return d > 0 ? p.precio * (1 - d / 100) : p.precio;
  };

  const agregar = (producto: ProductoDTO, cantidad: number = 1) => {
    setItems((prev) => {
      const existente = prev.find(
        (it) => it.producto.idProducto === producto.idProducto
      );
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
    setItems((prev) =>
      prev.filter((it) => it.producto.idProducto !== idProducto)
    );
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
