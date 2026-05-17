/**
 * Navbar principal del e-commerce.
 *
 * Se muestra en TODAS las páginas que no son admin/empleado:
 *  - Cliente autenticado (compra desde casa).
 *  - Visitante anónimo (puede ver catálogo libre, login al checkout).
 *
 * Contiene: logo, buscador, carrito con badge, menú usuario o login.
 */
import { useState } from "react";
import {
  AppBar,
  Toolbar,
  Box,
  TextField,
  IconButton,
  Badge,
  Button,
  Avatar,
  Menu,
  MenuItem,
  Divider,
  InputAdornment,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faMagnifyingGlass,
  faCartShopping,
  faUser,
  faBars,
  faRightFromBracket,
  faBoxOpen,
  faMapLocationDot,
} from "@fortawesome/free-solid-svg-icons";
import Logo from "../common/Logo";
import ToggleTema from "../common/ToggleTema";
import { useAuth } from "../../context/AuthContext";
import { useCarrito } from "../../context/CarritoContext";

const NavbarEcommerce = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const theme = useTheme();
  const esMovil = useMediaQuery(theme.breakpoints.down("md"));
  const { sesion, cerrarSesion, esCliente } = useAuth();
  const { cantidadItems } = useCarrito();
  const [busqueda, setBusqueda] = useState("");
  const [anchorUsuario, setAnchorUsuario] = useState<HTMLElement | null>(null);
  const [anchorMenu, setAnchorMenu] = useState<HTMLElement | null>(null);

  const handleBuscar = (e: React.FormEvent) => {
    e.preventDefault();
    if (busqueda.trim()) {
      navigate(`/buscar?q=${encodeURIComponent(busqueda.trim())}`);
    }
  };

  const handleCerrarSesion = () => {
    cerrarSesion();
    setAnchorUsuario(null);
    navigate("/");
  };

  const iniciales = sesion?.correo?.slice(0, 2).toUpperCase() ?? "??";

  return (
    <AppBar
      position="sticky"
      elevation={0}
      sx={{
        backgroundColor: "var(--coco-surface)",
        borderBottom: "1px solid var(--coco-border)",
        color: "var(--coco-text)",
      }}
    >
      <Toolbar sx={{ gap: 2, paddingY: 1 }}>
        {/* Menú móvil */}
        {esMovil && (
          <IconButton onClick={(e) => setAnchorMenu(e.currentTarget)} sx={{ color: "var(--coco-text)" }}>
            <FontAwesomeIcon icon={faBars} />
          </IconButton>
        )}

        {/* Logo */}
        <Box sx={{ cursor: "pointer", flexShrink: 0 }} onClick={() => navigate("/")}>
          <Logo size="md" />
        </Box>

        {/* Buscador (oculto en móvil pequeño) */}
        <Box
          component="form"
          onSubmit={handleBuscar}
          sx={{
            flex: 1,
            maxWidth: 560,
            display: { xs: "none", sm: "block" },
          }}
        >
          <TextField
            fullWidth
            size="small"
            placeholder="Buscar productos, marcas, categorías..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <FontAwesomeIcon icon={faMagnifyingGlass} style={{ color: "var(--coco-text-muted)" }} />
                </InputAdornment>
              ),
              sx: {
                backgroundColor: "var(--coco-surface-2)",
                borderRadius: 2,
              },
            }}
          />
        </Box>

        {/* Lado derecho */}
        <Box sx={{ display: "flex", alignItems: "center", gap: 1, marginLeft: "auto" }}>
          <ToggleTema />

          {/* Carrito */}
          <IconButton
            onClick={() => navigate("/carrito")}
            sx={{
              color: location.pathname === "/carrito" ? "var(--coco-primary)" : "var(--coco-text)",
            }}
          >
            <Badge badgeContent={cantidadItems} color="secondary">
              <FontAwesomeIcon icon={faCartShopping} />
            </Badge>
          </IconButton>

          {/* Usuario o Login */}
          {sesion && esCliente ? (
            <>
              <IconButton onClick={(e) => setAnchorUsuario(e.currentTarget)}>
                <Avatar sx={{ bgcolor: "var(--coco-primary)", width: 32, height: 32, fontSize: 12 }}>
                  {iniciales}
                </Avatar>
              </IconButton>
              <Menu
                anchorEl={anchorUsuario}
                open={Boolean(anchorUsuario)}
                onClose={() => setAnchorUsuario(null)}
                PaperProps={{ sx: { minWidth: 200 } }}
              >
                <Box sx={{ paddingX: 2, paddingY: 1 }}>
                  <Box sx={{ fontSize: 13, fontWeight: 600 }}>{sesion.correo}</Box>
                  <Box sx={{ fontSize: 11, color: "var(--coco-text-muted)" }}>Cliente</Box>
                </Box>
                <Divider />
                <MenuItem onClick={() => { setAnchorUsuario(null); navigate("/cliente/pedidos"); }}>
                  <FontAwesomeIcon icon={faBoxOpen} style={{ marginRight: 10, width: 14 }} />
                  Mis pedidos
                </MenuItem>
                <MenuItem onClick={() => { setAnchorUsuario(null); navigate("/cliente/perfil"); }}>
                  <FontAwesomeIcon icon={faUser} style={{ marginRight: 10, width: 14 }} />
                  Mi perfil
                </MenuItem>
                <MenuItem onClick={() => { setAnchorUsuario(null); navigate("/cliente/direcciones"); }}>
                  <FontAwesomeIcon icon={faMapLocationDot} style={{ marginRight: 10, width: 14 }} />
                  Mis direcciones
                </MenuItem>
                <Divider />
                <MenuItem onClick={handleCerrarSesion} sx={{ color: "var(--coco-danger)" }}>
                  <FontAwesomeIcon icon={faRightFromBracket} style={{ marginRight: 10, width: 14 }} />
                  Cerrar sesión
                </MenuItem>
              </Menu>
            </>
          ) : (
            <Button
              variant="contained"
              color="secondary"
              size="small"
              onClick={() => navigate("/login")}
              sx={{ display: { xs: "none", sm: "inline-flex" } }}
            >
              Ingresar
            </Button>
          )}
        </Box>
      </Toolbar>

      {/* Barra secundaria con categorías rápidas */}
      <Box
        sx={{
          display: { xs: "none", md: "flex" },
          gap: 2.5,
          paddingX: 3,
          paddingY: 1,
          backgroundColor: "var(--coco-primary)",
          color: "#FFFFFF",
          fontSize: 13,
        }}
      >
        <Box sx={{ cursor: "pointer", "&:hover": { opacity: 0.8 } }} onClick={() => navigate("/productos")}>
          Catálogo completo
        </Box>
        <Box sx={{ cursor: "pointer", "&:hover": { opacity: 0.8 } }} onClick={() => navigate("/productos?promo=true")}>
          Ofertas
        </Box>
        <Box sx={{ cursor: "pointer", "&:hover": { opacity: 0.8 } }} onClick={() => navigate("/productos?destacados=true")}>
          Destacados
        </Box>
      </Box>

      {/* Menú móvil desplegable */}
      <Menu anchorEl={anchorMenu} open={Boolean(anchorMenu)} onClose={() => setAnchorMenu(null)}>
        <MenuItem onClick={() => { setAnchorMenu(null); navigate("/productos"); }}>Catálogo</MenuItem>
        <MenuItem onClick={() => { setAnchorMenu(null); navigate("/productos?promo=true"); }}>Ofertas</MenuItem>
        {!sesion && (
          <MenuItem onClick={() => { setAnchorMenu(null); navigate("/login"); }}>Ingresar</MenuItem>
        )}
      </Menu>
    </AppBar>
  );
};

export default NavbarEcommerce;
