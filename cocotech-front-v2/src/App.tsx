/**
 * App.tsx — punto de entrada con todas las rutas.
 *
 * Estructura de rutas:
 *  · Públicas: catálogo, detalle producto, carrito (sin login)
 *  · Auth: login, register, verificar, recuperar
 *  · Cliente (ROLE_CLIENTE): checkout, pedidos, perfil, direcciones
 *  · Empleado (ROLE_EMPLEADO): dashboard, pedidos, productos, perfil
 *  · Admin (ROLE_ADMIN): dashboard + todos los CRUDs y reportes
 *
 * Protección de rutas:
 *  · <RutaProtegida> envuelve las páginas privadas
 *  · Si no hay sesión → redirige a /login conservando la URL de origen
 *  · Si el rol no coincide → redirige al dashboard correspondiente
 */
import { BrowserRouter, Routes, Route, Navigate, useLocation } from "react-router-dom";
import type { JSX } from "react";
import { TemaProvider } from "./context/TemaContext";
import { AuthProvider, useAuth } from "./context/AuthContext";
import { CarritoProvider } from "./context/CarritoContext";
import type { Rol } from "./types";

// Públicas
import Home from "./pages/public/Home";
import Catalogo from "./pages/public/Catalogo";
import DetalleProductoPublico from "./pages/public/DetalleProductoPublico";
import Carrito from "./pages/public/Carrito";
import Checkout from "./pages/public/Checkout";

// Auth
import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";
import VerificarCodigo from "./pages/auth/VerificarCodigo";
import RecuperarContrasena from "./pages/auth/RecuperarContrasena";

// Cliente
import MisPedidos from "./pages/cliente/MisPedidos";
import DetallePedidoCliente from "./pages/cliente/DetallePedidoCliente";
import PerfilCliente from "./pages/cliente/PerfilCliente";
import MisDirecciones from "./pages/cliente/MisDirecciones";

// Empleado
import DashboardEmpleado from "./pages/empleado/DashboardEmpleado";
import PedidosEmpleado from "./pages/empleado/PedidosEmpleado";
import ProductosEmpleado from "./pages/empleado/ProductosEmpleado";
import PerfilEmpleado from "./pages/empleado/PerfilEmpleado";

// Admin
import DashboardAdmin from "./pages/admin/DashboardAdmin";
import PedidosAdmin from "./pages/admin/PedidosAdmin";
import ClientesAdmin from "./pages/admin/ClientesAdmin";
import ProductosAdmin from "./pages/admin/ProductosAdmin";
import InventarioAdmin from "./pages/admin/InventarioAdmin";
import CategoriasAdmin from "./pages/admin/CategoriasAdmin";
import ProveedoresAdmin from "./pages/admin/ProveedoresAdmin";
import EmpleadosAdmin from "./pages/admin/EmpleadosAdmin";
import SucursalesAdmin from "./pages/admin/SucursalesAdmin";
import CajasAdmin from "./pages/admin/CajasAdmin";
import VentasAdmin from "./pages/admin/VentasAdmin";
import FacturasAdmin from "./pages/admin/FacturasAdmin";
import ReportesAdmin from "./pages/admin/ReportesAdmin";

/**
 * Dashboard por defecto según el rol. Se usa cuando un usuario autenticado
 * intenta entrar a una ruta de otro rol: lo redirigimos a su área natural
 * en lugar de mostrarle un error.
 */
const dashboardPorRol = (rol: Rol): string => {
  switch (rol) {
    case "ROLE_ADMIN":
      return "/admin";
    case "ROLE_EMPLEADO":
      return "/empleado";
    case "ROLE_CLIENTE":
      return "/";
    default:
      return "/";
  }
};

/**
 * Protege una ruta verificando autenticación + rol(es) permitido(s).
 *
 *  · Sin sesión → redirige a /login conservando la URL solicitada en `state.from`
 *    para que después del login se pueda volver a ella.
 *  · Sesión con rol no permitido → redirige al dashboard del rol del usuario.
 *  · Sesión con rol permitido → renderiza los hijos.
 */
interface RutaProtegidaProps {
  rolesPermitidos: Rol[];
  children: JSX.Element;
}

const RutaProtegida = ({ rolesPermitidos, children }: RutaProtegidaProps): JSX.Element => {
  const { sesion } = useAuth();
  const location = useLocation();

  if (!sesion) {
    return <Navigate to="/login" replace state={{ from: location.pathname + location.search }} />;
  }
  if (!rolesPermitidos.includes(sesion.rol)) {
    return <Navigate to={dashboardPorRol(sesion.rol)} replace />;
  }
  return children;
};

/**
 * Bloquea las páginas de auth (login/register/verificar/recuperar) cuando ya
 * hay sesión activa, para evitar que un usuario logueado vuelva a iniciar
 * sesión sin cerrar la actual. Lo manda a su dashboard.
 */
const SoloInvitado = ({ children }: { children: JSX.Element }): JSX.Element => {
  const { sesion } = useAuth();
  if (sesion) {
    return <Navigate to={dashboardPorRol(sesion.rol)} replace />;
  }
  return children;
};

const AppRoutes = () => (
  <Routes>
    {/* ─── Públicas (sin login) ─── */}
    <Route path="/" element={<Home />} />
    <Route path="/productos" element={<Catalogo />} />
    <Route path="/buscar" element={<Catalogo />} />
    <Route path="/producto/:id" element={<DetalleProductoPublico />} />
    <Route path="/categoria/:id" element={<Catalogo />} />
    <Route path="/carrito" element={<Carrito />} />

    {/* ─── Auth (solo si NO hay sesión) ─── */}
    <Route path="/login" element={<SoloInvitado><Login /></SoloInvitado>} />
    <Route path="/register" element={<SoloInvitado><Register /></SoloInvitado>} />
    <Route path="/verificar" element={<SoloInvitado><VerificarCodigo /></SoloInvitado>} />
    <Route path="/recuperar" element={<SoloInvitado><RecuperarContrasena /></SoloInvitado>} />

    {/* ─── Cliente (requieren ROLE_CLIENTE) ─── */}
    <Route
      path="/checkout"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_CLIENTE"]}>
          <Checkout />
        </RutaProtegida>
      }
    />
    <Route
      path="/cliente/pedidos"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_CLIENTE"]}>
          <MisPedidos />
        </RutaProtegida>
      }
    />
    <Route
      path="/cliente/pedido/:id"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_CLIENTE"]}>
          <DetallePedidoCliente />
        </RutaProtegida>
      }
    />
    <Route
      path="/cliente/perfil"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_CLIENTE"]}>
          <PerfilCliente />
        </RutaProtegida>
      }
    />
    <Route
      path="/cliente/direcciones"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_CLIENTE"]}>
          <MisDirecciones />
        </RutaProtegida>
      }
    />

    {/* ─── Empleado (requieren ROLE_EMPLEADO; admin también puede acceder) ─── */}
    <Route
      path="/empleado"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_EMPLEADO", "ROLE_ADMIN"]}>
          <DashboardEmpleado />
        </RutaProtegida>
      }
    />
    <Route
      path="/empleado/pedidos"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_EMPLEADO", "ROLE_ADMIN"]}>
          <PedidosEmpleado />
        </RutaProtegida>
      }
    />
    <Route
      path="/empleado/productos"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_EMPLEADO", "ROLE_ADMIN"]}>
          <ProductosEmpleado />
        </RutaProtegida>
      }
    />
    <Route
      path="/empleado/perfil"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_EMPLEADO", "ROLE_ADMIN"]}>
          <PerfilEmpleado />
        </RutaProtegida>
      }
    />

    {/* ─── Admin (requieren ROLE_ADMIN) ─── */}
    <Route
      path="/admin"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <DashboardAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/pedidos"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <PedidosAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/clientes"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <ClientesAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/productos"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <ProductosAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/inventario"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <InventarioAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/categorias"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <CategoriasAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/proveedores"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <ProveedoresAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/empleados"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <EmpleadosAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/sucursales"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <SucursalesAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/cajas"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <CajasAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/ventas"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <VentasAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/facturas"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <FacturasAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/reportes"
      element={
        <RutaProtegida rolesPermitidos={["ROLE_ADMIN"]}>
          <ReportesAdmin />
        </RutaProtegida>
      }
    />

    {/* Fallback */}
    <Route path="*" element={<Navigate to="/" replace />} />
  </Routes>
);

function App() {
  return (
    <TemaProvider>
      <AuthProvider>
        <CarritoProvider>
          <BrowserRouter>
            <AppRoutes />
          </BrowserRouter>
        </CarritoProvider>
      </AuthProvider>
    </TemaProvider>
  );
}

export default App;