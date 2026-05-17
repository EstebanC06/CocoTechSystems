/**
 * Tipos TypeScript que reflejan los DTOs del backend Spring Boot.
 * Mantén estos tipos sincronizados con los DTOs de Java.
 */

/** Rol del usuario. Ahora con 3 roles. */
export type Rol = "ROLE_CLIENTE" | "ROLE_EMPLEADO" | "ROLE_ADMIN";

/** Respuesta del login devuelta por el backend en /auth/login. */
export interface LoginResponse {
  token: string;
  tipo: string;
  correo: string;
  rol: Rol;
  id: number;
}

/**
 * Payload de login.
 * IMPORTANTE: los nombres de campos deben coincidir con LoginRequest del back
 * (correo, contrasena). Si el form usa nombres como "username"/"password",
 * traduce al payload antes de mandar.
 */
export interface LoginPayload {
  correo: string;
  contrasena: string;
}

/** DTO para Cliente */
export interface ClienteDTO {
  id?: number;
  nombres: string;
  apellidos: string;
  correo: string;
  contrasena?: string;
  codigoVerificacion?: string;
  telefono: string;
  calle: string;
  barrio: string;
  ciudad: string;
}

/** DTO para Empleado */
export interface EmpleadoDTO {
  id?: number;
  nombres: string;
  apellidos: string;
  correo: string;
  contrasena?: string;
  codigoVerificacion?: string;
  cargo: string;
  salario: number;
  idSucursal: number;
}

// ─── ENUMS ESPEJO DEL BACK ──────────────────────────────────────

/**
 * Conjunto cerrado de nombres de sucursal aceptados por el back.
 * Refleja el enum Sucursal.NombreSucursal del backend.
 */
export type NombreSucursal =
  | "FONTIBON"
  | "USAQUEN"
  | "CHAPINERO"
  | "SUBA"
  | "ENGATIVA";

/** Lista ordenada de los valores válidos del enum NombreSucursal. */
export const NOMBRES_SUCURSAL: readonly NombreSucursal[] = [
  "FONTIBON",
  "USAQUEN",
  "CHAPINERO",
  "SUBA",
  "ENGATIVA",
] as const;

/**
 * Etiquetas legibles para mostrar al usuario en lugar del valor crudo del
 * enum (ej. "FONTIBON" → "Fontibón").
 */
export const ETIQUETAS_SUCURSAL: Record<NombreSucursal, string> = {
  FONTIBON: "Fontibón",
  USAQUEN: "Usaquén",
  CHAPINERO: "Chapinero",
  SUBA: "Suba",
  ENGATIVA: "Engativá",
};

/**
 * Conjunto cerrado de nombres de proveedor aceptados por el back.
 * Refleja el enum Proveedor.NombreProveedor del backend.
 */
export type NombreProveedor =
  | "P_AND_G"
  | "ORGANICS_COLOMBIA_SAS"
  | "ALQUERIA"
  | "COCACOLA_FEMSA"
  | "MCCAIN_FOODS"
  | "BIMBO_COLOMBIA"
  | "NESTLE_COLOMBIA"
  | "NOEL_NUTRESA"
  | "COLOMBINA_SA"
  | "BAVARIA"
  | "SAMSUNG_ELECTRONICS"
  | "UNILEVER_COLOMBIA"
  | "LG_ELECTRONICS"
  | "STUDIO_F_CO"
  | "ARTURO_CALLE"
  | "OFFCORSS"
  | "JOHNSON_AND_JOHNSON";

/** Lista ordenada de los valores válidos del enum NombreProveedor. */
export const NOMBRES_PROVEEDOR: readonly NombreProveedor[] = [
  "P_AND_G",
  "ORGANICS_COLOMBIA_SAS",
  "ALQUERIA",
  "COCACOLA_FEMSA",
  "MCCAIN_FOODS",
  "BIMBO_COLOMBIA",
  "NESTLE_COLOMBIA",
  "NOEL_NUTRESA",
  "COLOMBINA_SA",
  "BAVARIA",
  "SAMSUNG_ELECTRONICS",
  "UNILEVER_COLOMBIA",
  "LG_ELECTRONICS",
  "STUDIO_F_CO",
  "ARTURO_CALLE",
  "OFFCORSS",
  "JOHNSON_AND_JOHNSON",
] as const;

/**
 * Etiquetas legibles para mostrar al usuario en lugar del valor crudo del
 * enum (ej. "P_AND_G" → "Procter & Gamble").
 */
export const ETIQUETAS_PROVEEDOR: Record<NombreProveedor, string> = {
  P_AND_G: "Procter & Gamble",
  ORGANICS_COLOMBIA_SAS: "Organics Colombia S.A.S",
  ALQUERIA: "Alquería",
  COCACOLA_FEMSA: "Coca-Cola FEMSA",
  MCCAIN_FOODS: "McCain Foods",
  BIMBO_COLOMBIA: "Bimbo Colombia",
  NESTLE_COLOMBIA: "Nestlé Colombia",
  NOEL_NUTRESA: "Noel (Grupo Nutresa)",
  COLOMBINA_SA: "Colombina S.A.",
  BAVARIA: "Bavaria",
  SAMSUNG_ELECTRONICS: "Samsung Electronics",
  UNILEVER_COLOMBIA: "Unilever Colombia",
  LG_ELECTRONICS: "LG Electronics",
  STUDIO_F_CO: "Studio F Colombia",
  ARTURO_CALLE: "Arturo Calle",
  OFFCORSS: "OFFCORSS",
  JOHNSON_AND_JOHNSON: "Johnson & Johnson",
};

/**
 * Conjunto cerrado de nombres de categoría aceptados por el back.
 * Refleja el enum Categoria.NombreCategoria del backend.
 */
export type NombreCategoria =
  | "ASEO"
  | "FRUTAS_VERDURAS"
  | "DERIVADOS_DE_ANIMALES"
  | "BEBIDAS_NO_ALCOHOLICAS"
  | "CONGELADOS"
  | "PANADERIA_REPOSTERIA"
  | "DESPENSA"
  | "PAQUETES_GALLETAS"
  | "DULCES"
  | "BEBIDAS_ALCOHOLICAS"
  | "TECNOLOGIA"
  | "CUIDADO_PERSONAL"
  | "ELECTRODOMESTICOS"
  | "ROPA_MUJER"
  | "ROPA_HOMBRE"
  | "ROPA_NINOS"
  | "PRODUCTOS_BEBES";

/** Lista ordenada de los valores válidos del enum NombreCategoria. */
export const NOMBRES_CATEGORIA: readonly NombreCategoria[] = [
  "ASEO",
  "FRUTAS_VERDURAS",
  "DERIVADOS_DE_ANIMALES",
  "BEBIDAS_NO_ALCOHOLICAS",
  "CONGELADOS",
  "PANADERIA_REPOSTERIA",
  "DESPENSA",
  "PAQUETES_GALLETAS",
  "DULCES",
  "BEBIDAS_ALCOHOLICAS",
  "TECNOLOGIA",
  "CUIDADO_PERSONAL",
  "ELECTRODOMESTICOS",
  "ROPA_MUJER",
  "ROPA_HOMBRE",
  "ROPA_NINOS",
  "PRODUCTOS_BEBES",
] as const;

/**
 * Etiquetas legibles para mostrar al usuario en lugar del valor crudo del
 * enum (ej. "FRUTAS_VERDURAS" → "Frutas y verduras").
 */
export const ETIQUETAS_CATEGORIA: Record<NombreCategoria, string> = {
  ASEO: "Aseo del hogar",
  FRUTAS_VERDURAS: "Frutas y verduras",
  DERIVADOS_DE_ANIMALES: "Derivados de animales",
  BEBIDAS_NO_ALCOHOLICAS: "Bebidas no alcohólicas",
  CONGELADOS: "Congelados",
  PANADERIA_REPOSTERIA: "Panadería y repostería",
  DESPENSA: "Despensa",
  PAQUETES_GALLETAS: "Paquetes y galletas",
  DULCES: "Dulces",
  BEBIDAS_ALCOHOLICAS: "Bebidas alcohólicas",
  TECNOLOGIA: "Tecnología",
  CUIDADO_PERSONAL: "Cuidado personal",
  ELECTRODOMESTICOS: "Electrodomésticos",
  ROPA_MUJER: "Ropa de mujer",
  ROPA_HOMBRE: "Ropa de hombre",
  ROPA_NINOS: "Ropa de niños",
  PRODUCTOS_BEBES: "Productos para bebés",
};

/**
 * Categorías que SÍ manejan fecha de vencimiento.
 * El formulario de creación/edición de Producto muestra el campo
 * fechaVencimiento solo cuando la categoría seleccionada está en este
 * conjunto. Las demás se guardan con fechaVencimiento = null.
 */
export const CATEGORIAS_CON_VENCIMIENTO: ReadonlySet<NombreCategoria> = new Set([
  "FRUTAS_VERDURAS",
  "DERIVADOS_DE_ANIMALES",
  "BEBIDAS_NO_ALCOHOLICAS",
  "CONGELADOS",
  "PANADERIA_REPOSTERIA",
  "DESPENSA",
  "PAQUETES_GALLETAS",
  "DULCES",
  "BEBIDAS_ALCOHOLICAS",
  "CUIDADO_PERSONAL",
]);

/**
 * Estado operativo de una caja registradora.
 * Refleja el enum CajaRegistradora.Estado del backend (¡atención al
 * valor EN_MANTENIMIENTO, NO "MANTENIMIENTO"!).
 */
export type EstadoCaja = "ACTIVA" | "INACTIVA" | "EN_MANTENIMIENTO";

/** Lista ordenada de estados válidos de caja. */
export const ESTADOS_CAJA: readonly EstadoCaja[] = [
  "ACTIVA",
  "INACTIVA",
  "EN_MANTENIMIENTO",
] as const;

/** Etiquetas legibles para los estados de caja. */
export const ETIQUETAS_ESTADO_CAJA: Record<EstadoCaja, string> = {
  ACTIVA: "Activa",
  INACTIVA: "Inactiva",
  EN_MANTENIMIENTO: "En mantenimiento",
};

// ─── DTOs ───────────────────────────────────────────────────────

/** DTO para Sucursal */
export interface SucursalDTO {
  idSucursal?: number;
  nombre: NombreSucursal;
  telefonoContacto: string;
  ciudad: string;
  barrio: string;
  direccion: string;
}

/**
 * DTO para Producto.
 *
 * Campos NUEVOS que el back debe agregar:
 *  - imagenUrl: URL pública de la imagen del producto.
 *  - descripcion: texto largo descriptivo.
 *  - descuentoPorcentaje: 0-100 (default 0).
 *  - destacado: boolean para mostrarse en Home.
 *  - activo: boolean para baja lógica.
 */
export interface ProductoDTO {
  idProducto?: number;
  nombre: string;
  precio: number;
  stock: number;
  fechaVencimiento?: string | null;
  idCategoria: number;
  idProveedor: number;
  imagenUrl?: string;
  descripcion?: string;
  descuentoPorcentaje?: number;
  destacado?: boolean;
  activo?: boolean;
}

/**
 * DTO para Categoria.
 * El campo nombre es ahora un valor restringido del enum NombreCategoria.
 */
export interface CategoriaDTO {
  idCategoria?: number;
  nombre: NombreCategoria;
  descripcion: string;
  imagenUrl?: string;
  icono?: string;
}

/**
 * DTO para Proveedor.
 * El campo nombre es ahora un valor restringido del enum NombreProveedor.
 * El campo "calle" del back se renombró a "direccion".
 */
export interface ProveedorDTO {
  idProveedor?: number;
  nombre: NombreProveedor;
  telefono: string;
  direccion: string;
  barrio: string;
  ciudad: string;
}

/**
 * DTO para Caja Registradora.
 * El estado usa el enum EstadoCaja (ojo: "EN_MANTENIMIENTO", no "MANTENIMIENTO").
 */
export interface CajaRegistradoraDTO {
  idCaja?: number;
  numero: number;
  estado: EstadoCaja;
  idEmpleado: number;
  idSucursal: number;
}

/** Venta (POS físico) */
export interface VentaDTO {
  idVenta?: number;
  fecha?: string;
  total?: number;
  idEmpleado: number;
  idCliente: number;
}

export interface DetalleVentaDTO {
  idDetalle?: number;
  cantidadProductos: number;
  precioUnitario: number;
  subtotal: number;
  metodoPago: string;
  promocion: boolean;
  porcentajeDescuento?: number;
  precioOriginal?: number;
  precioNuevo?: number;
  idVenta: number;
  idProducto: number;
}

export interface FacturaDTO {
  idFactura?: number;
  fecha?: string;
  precioTotal: number;
  precioImpuestos: number;
  idVenta: number;
}

// ─── ENTIDADES NUEVAS (e-commerce) ──────────────────────────────

/** Estado del pedido — se moverá a través del flujo. */
export type EstadoPedido =
  | "RECIBIDO"
  | "PREPARANDO"
  | "LISTO_PARA_ENTREGA"
  | "EN_CAMINO"
  | "ENTREGADO"
  | "CANCELADO";

/** Lista ordenada de estados válidos de pedido. */
export const ESTADOS_PEDIDO: readonly EstadoPedido[] = [
  "RECIBIDO",
  "PREPARANDO",
  "LISTO_PARA_ENTREGA",
  "EN_CAMINO",
  "ENTREGADO",
  "CANCELADO",
] as const;

/** Etiquetas legibles para los estados de pedido. */
export const ETIQUETAS_ESTADO_PEDIDO: Record<EstadoPedido, string> = {
  RECIBIDO: "Recibido",
  PREPARANDO: "Preparando",
  LISTO_PARA_ENTREGA: "Listo para entrega",
  EN_CAMINO: "En camino",
  ENTREGADO: "Entregado",
  CANCELADO: "Cancelado",
};

/** Tipo de entrega elegido por el cliente. */
export type TipoEntrega = "DOMICILIO" | "RECOGER_EN_SUCURSAL";

/** Método de pago simulado (proyecto académico). */
export type MetodoPago =
  | "EFECTIVO_CONTRA_ENTREGA"
  | "TARJETA_SIMULADA"
  | "PSE_SIMULADO";

/** Direccion guardada del cliente. */
export interface DireccionClienteDTO {
  idDireccion?: number;
  idCliente: number;
  alias: string;
  calle: string;
  barrio: string;
  ciudad: string;
  referencia?: string;
  predeterminada: boolean;
}

/** Detalle de un pedido (producto + cantidad dentro de un pedido). */
export interface DetallePedidoDTO {
  idDetallePedido?: number;
  idPedido?: number;
  idProducto: number;
  nombreProducto?: string;
  imagenUrl?: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  promocion: boolean;
  porcentajeDescuento?: number;
}

/**
 * DTO de Pedido (e-commerce).
 *
 * Flujo:
 *  1. Cliente termina checkout → POST /pedido/crear (estado RECIBIDO).
 *  2. Empleado/admin cambia estado: PREPARANDO → EN_CAMINO/LISTO → ENTREGADO.
 *  3. Al marcarse ENTREGADO el back crea Venta + Factura automáticamente.
 */
export interface PedidoDTO {
  idPedido?: number;
  fechaCreacion?: string;
  fechaActualizacion?: string;
  estado: EstadoPedido;
  tipoEntrega: TipoEntrega;
  metodoPago: MetodoPago;
  subtotal: number;
  iva: number;
  costoEnvio: number;
  total: number;
  idCliente: number;
  idSucursalDespacho: number;
  direccionEnvio?: string;
  barrioEnvio?: string;
  ciudadEnvio?: string;
  referenciaEnvio?: string;
  notasCliente?: string;
  detalles: DetallePedidoDTO[];
  /** Datos hidratados (si el back los envía). */
  nombreCliente?: string;
  nombreSucursal?: string;
}

/** Item del carrito (solo en cliente, no se persiste en back hasta el checkout). */
export interface ItemCarrito {
  producto: ProductoDTO;
  cantidad: number;
}

// ─── MONGO ─────────────────────────────────────────────────────

export interface FacturaDocumento {
  id?: string;
  idFacturaMySQL: number;
  idVenta: number;
  fecha: string;
  precioTotal: number;
  precioImpuestos: number;
  cliente?: {
    idCliente: number;
    nombres: string;
    apellidos: string;
    correo: string;
    ciudad: string;
  };
  empleado?: {
    idEmpleado: number;
    nombres: string;
    apellidos: string;
    cargo: string;
  };
  sucursal?: {
    idSucursal: number;
    nombre: string;
    ciudad: string;
  };
  detalles?: Array<{
    idProducto: number;
    nombreProducto: string;
    categoria: string;
    cantidad: number;
    precioUnitario: number;
    subtotal: number;
    metodoPago: string;
    promocion: boolean;
    porcentajeDescuento?: number;
  }>;
}

// ─── SESIÓN ────────────────────────────────────────────────────

export interface SesionUsuario {
  token: string;
  correo: string;
  rol: Rol;
  id: number;
  nombres?: string;
  apellidos?: string;
}
