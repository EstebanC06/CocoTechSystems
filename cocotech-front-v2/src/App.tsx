/**
 * App.tsx — punto de entrada con todas las rutas.
 *
 * Estructura de rutas:
 *  · Públicas: catálogo, detalle producto, carrito (sin login)
 *  · Auth: login, register, verificar, recuperar
 *  · Cliente (ROLE_CLIENTE): checkout, pedidos, perfil, direcciones
 *  · Empleado (ROLE_EMPLEADO): dashboard, pedidos, productos, perfil
 *  · Admin (ROLE_ADMIN): dashboard + todos los CRUDs y reportes
 */
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
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
 * Protege una ruta verificando autenticación + rol(es) permitido(s).
 * Si no está autenticado, manda a login (preservando intención).
 * Si está autenticado con otro rol, manda a su área correspondiente.
 */
const RutaProtegida = ({
  children,
  roles,
}: {
  children: JSX.Element;
  roles: Rol[];
}) => {
  const { sesion, estaAutenticado } = useAuth();
  if (!estaAutenticado) {
    return (
      <Navigate
        to={`/login?redirect=${encodeURIComponent(window.location.pathname)}`}
        replace
      />
    );
  }
  if (!roles.includes(sesion!.rol)) {
    if (sesion!.rol === "ROLE_ADMIN") return <Navigate to="/admin" replace />;
    if (sesion!.rol === "ROLE_EMPLEADO")
      return <Navigate to="/empleado" replace />;
    return <Navigate to="/" replace />;
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

    {/* ─── Auth ─── */}
    <Route path="/login" element={<Login />} />
    <Route path="/register" element={<Register />} />
    <Route path="/verificar" element={<VerificarCodigo />} />
    <Route path="/recuperar" element={<RecuperarContrasena />} />

    {/* ─── Cliente (requieren ROLE_CLIENTE) ─── */}
    <Route
      path="/checkout"
      element={
        <RutaProtegida roles={["ROLE_CLIENTE"]}>
          <Checkout />
        </RutaProtegida>
      }
    />
    <Route
      path="/cliente/pedidos"
      element={
        <RutaProtegida roles={["ROLE_CLIENTE"]}>
          <MisPedidos />
        </RutaProtegida>
      }
    />
    <Route
      path="/cliente/pedido/:id"
      element={
        <RutaProtegida roles={["ROLE_CLIENTE"]}>
          <DetallePedidoCliente />
        </RutaProtegida>
      }
    />
    <Route
      path="/cliente/perfil"
      element={
        <RutaProtegida roles={["ROLE_CLIENTE"]}>
          <PerfilCliente />
        </RutaProtegida>
      }
    />
    <Route
      path="/cliente/direcciones"
      element={
        <RutaProtegida roles={["ROLE_CLIENTE"]}>
          <MisDirecciones />
        </RutaProtegida>
      }
    />

    {/* ─── Empleado (requieren ROLE_EMPLEADO) ─── */}
    <Route
      path="/empleado"
      element={
        <RutaProtegida roles={["ROLE_EMPLEADO"]}>
          <DashboardEmpleado />
        </RutaProtegida>
      }
    />
    <Route
      path="/empleado/pedidos"
      element={
        <RutaProtegida roles={["ROLE_EMPLEADO"]}>
          <PedidosEmpleado />
        </RutaProtegida>
      }
    />
    <Route
      path="/empleado/productos"
      element={
        <RutaProtegida roles={["ROLE_EMPLEADO"]}>
          <ProductosEmpleado />
        </RutaProtegida>
      }
    />
    <Route
      path="/empleado/perfil"
      element={
        <RutaProtegida roles={["ROLE_EMPLEADO"]}>
          <PerfilEmpleado />
        </RutaProtegida>
      }
    />

    {/* ─── Admin (requieren ROLE_ADMIN) ─── */}
    <Route
      path="/admin"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <DashboardAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/pedidos"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <PedidosAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/clientes"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <ClientesAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/productos"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <ProductosAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/inventario"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <InventarioAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/categorias"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <CategoriasAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/proveedores"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <ProveedoresAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/empleados"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <EmpleadosAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/sucursales"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <SucursalesAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/cajas"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <CajasAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/ventas"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <VentasAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/facturas"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
          <FacturasAdmin />
        </RutaProtegida>
      }
    />
    <Route
      path="/admin/reportes"
      element={
        <RutaProtegida roles={["ROLE_ADMIN"]}>
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
