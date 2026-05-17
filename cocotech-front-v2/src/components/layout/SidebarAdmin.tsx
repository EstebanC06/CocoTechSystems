/**
 * Sidebar de navegación para el área administrativa.
 * Despliega todos los módulos del CRUD y reportes.
 */
import { useNavigate, useLocation } from "react-router-dom";
import { Box, Divider, Avatar, Menu, MenuItem } from "@mui/material";
import { useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faChartLine,
  faBoxesStacked,
  faTags,
  faTruck,
  faUsers,
  faStore,
  faCashRegister,
  faReceipt,
  faFileInvoiceDollar,
  faTruckFast,
  faUserGroup,
  faWarehouse,
  faChartPie,
  faRightFromBracket,
} from "@fortawesome/free-solid-svg-icons";
import Logo from "../common/Logo";
import ToggleTema from "../common/ToggleTema";
import { useAuth } from "../../context/AuthContext";

interface ItemMenu {
  ruta: string;
  texto: string;
  icono: typeof faChartLine;
  categoria?: string;
}

const menu: ItemMenu[] = [
  { ruta: "/admin", texto: "Dashboard", icono: faChartLine },
  { ruta: "/admin/pedidos", texto: "Pedidos online", icono: faTruckFast, categoria: "Operación" },
  { ruta: "/admin/clientes", texto: "Clientes", icono: faUserGroup, categoria: "Operación" },
  { ruta: "/admin/productos", texto: "Productos", icono: faBoxesStacked, categoria: "Inventario" },
  { ruta: "/admin/inventario", texto: "Inventario", icono: faWarehouse, categoria: "Inventario" },
  { ruta: "/admin/categorias", texto: "Categorías", icono: faTags, categoria: "Inventario" },
  { ruta: "/admin/proveedores", texto: "Proveedores", icono: faTruck, categoria: "Inventario" },
  { ruta: "/admin/empleados", texto: "Empleados", icono: faUsers, categoria: "Personal" },
  { ruta: "/admin/sucursales", texto: "Sucursales", icono: faStore, categoria: "Personal" },
  { ruta: "/admin/cajas", texto: "Cajas registradoras", icono: faCashRegister, categoria: "Personal" },
  { ruta: "/admin/ventas", texto: "Ventas", icono: faReceipt, categoria: "Finanzas" },
  { ruta: "/admin/facturas", texto: "Facturas", icono: faFileInvoiceDollar, categoria: "Finanzas" },
  { ruta: "/admin/reportes", texto: "Reportes", icono: faChartPie, categoria: "Finanzas" },
];

const SidebarAdmin = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { sesion, cerrarSesion } = useAuth();
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);

  const iniciales = sesion?.correo?.slice(0, 2).toUpperCase() ?? "AD";

  // Agrupar por categoría
  const agrupado: Record<string, ItemMenu[]> = {};
  menu.forEach((item) => {
    const cat = item.categoria ?? "Principal";
    if (!agrupado[cat]) agrupado[cat] = [];
    agrupado[cat].push(item);
  });

  return (
    <Box
      component="aside"
      sx={{
        width: 240,
        minHeight: "100vh",
        backgroundColor: "var(--coco-primary-dark)",
        color: "#FFFFFF",
        display: "flex",
        flexDirection: "column",
        position: "sticky",
        top: 0,
        flexShrink: 0,
      }}
    >
      <Box sx={{ padding: "1.5rem 1.25rem", cursor: "pointer" }} onClick={() => navigate("/admin")}>
        <Logo size="md" color="#FFFFFF" />
        <Box
          sx={{
            fontSize: 11,
            color: "var(--coco-accent)",
            textTransform: "uppercase",
            letterSpacing: 1,
            marginTop: 1,
          }}
        >
          Panel administrador
        </Box>
      </Box>

      <Divider sx={{ borderColor: "rgba(255,255,255,0.1)" }} />

      <Box sx={{ padding: "1rem 0.5rem", flexGrow: 1, overflowY: "auto" }}>
        {Object.entries(agrupado).map(([categoria, items]) => (
          <Box key={categoria} sx={{ marginBottom: 2 }}>
            <Box
              sx={{
                fontSize: 10,
                color: "rgba(255,255,255,0.5)",
                textTransform: "uppercase",
                letterSpacing: 1,
                padding: "8px 16px 4px",
              }}
            >
              {categoria}
            </Box>
            {items.map((item) => {
              const activo = location.pathname === item.ruta;
              return (
                <Box
                  key={item.ruta}
                  onClick={() => navigate(item.ruta)}
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 1.5,
                    padding: "9px 16px",
                    margin: "2px 8px",
                    fontSize: 13,
                    cursor: "pointer",
                    borderRadius: "6px",
                    backgroundColor: activo
                      ? "rgba(255,255,255,0.12)"
                      : "transparent",
                    color: activo ? "#FFFFFF" : "var(--coco-accent)",
                    transition: "all 0.2s ease",
                    "&:hover": {
                      backgroundColor: "rgba(255,255,255,0.08)",
                      color: "#FFFFFF",
                    },
                  }}
                >
                  <FontAwesomeIcon icon={item.icono} style={{ fontSize: 14, width: 16 }} />
                  <span>{item.texto}</span>
                </Box>
              );
            })}
          </Box>
        ))}
      </Box>

      <Divider sx={{ borderColor: "rgba(255,255,255,0.1)" }} />

      <Box
        sx={{
          padding: "12px 16px",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
        }}
      >
        <Box
          sx={{ display: "flex", alignItems: "center", gap: 1.5, cursor: "pointer", flexGrow: 1 }}
          onClick={(e) => setAnchorEl(e.currentTarget)}
        >
          <Avatar sx={{ bgcolor: "var(--coco-secondary)", width: 32, height: 32, fontSize: 12 }}>
            {iniciales}
          </Avatar>
          <Box sx={{ fontSize: 12 }}>
            <Box sx={{ color: "#FFFFFF", fontWeight: 500 }}>{sesion?.correo?.split("@")[0]}</Box>
            <Box sx={{ color: "var(--coco-accent)", fontSize: 10 }}>Administrador</Box>
          </Box>
        </Box>
        <ToggleTema inline />
      </Box>

      <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={() => setAnchorEl(null)}>
        <MenuItem
          onClick={() => {
            cerrarSesion();
            navigate("/");
          }}
          sx={{ color: "var(--coco-danger)", gap: 1 }}
        >
          <FontAwesomeIcon icon={faRightFromBracket} />
          Cerrar sesión
        </MenuItem>
      </Menu>
    </Box>
  );
};

export default SidebarAdmin;
