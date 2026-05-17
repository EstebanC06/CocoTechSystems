/**
 * Detalle de producto público — accesible sin login.
 */
import { useEffect, useState } from "react";
import {
  Box, Grid, Typography, Button, Chip, CircularProgress, IconButton, Snackbar, Alert,
} from "@mui/material";
import { useParams, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faArrowLeft, faBoxOpen, faCartPlus, faMinus, faPlus, faTruckFast, faShieldHalved,
} from "@fortawesome/free-solid-svg-icons";
import LayoutEcommerce from "../../components/layout/LayoutEcommerce";
import { obtenerProductoPublicoPorId } from "../../services/publico.service";
import { useCarrito } from "../../context/CarritoContext";
import type { ProductoDTO } from "../../types";

const DetalleProductoPublico = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { agregar, precioConDescuento } = useCarrito();
  const [producto, setProducto] = useState<ProductoDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [cantidad, setCantidad] = useState(1);
  const [imgError, setImgError] = useState(false);
  const [snackbar, setSnackbar] = useState(false);

  useEffect(() => {
    const cargar = async () => {
      if (!id) return;
      try {
        const data = await obtenerProductoPublicoPorId(parseInt(id));
        setProducto(data);
      } catch {
        setProducto(null);
      } finally {
        setLoading(false);
      }
    };
    cargar();
    setCantidad(1);
    setImgError(false);
  }, [id]);

  if (loading) {
    return (
      <LayoutEcommerce>
        <Box sx={{ textAlign: "center", padding: 8 }}><CircularProgress /></Box>
      </LayoutEcommerce>
    );
  }

  if (!producto) {
    return (
      <LayoutEcommerce>
        <Box sx={{ textAlign: "center", padding: 8 }}>
          <Typography>Producto no encontrado.</Typography>
          <Button sx={{ marginTop: 2 }} onClick={() => navigate("/productos")}>Volver al catálogo</Button>
        </Box>
      </LayoutEcommerce>
    );
  }

  const tieneDescuento = (producto.descuentoPorcentaje ?? 0) > 0;
  const precioFinal = precioConDescuento(producto);
  const agotado = producto.stock <= 0;
  const ahorro = tieneDescuento ? producto.precio - precioFinal : 0;

  const handleAgregar = () => {
    agregar(producto, cantidad);
    setSnackbar(true);
  };

  return (
    <LayoutEcommerce>
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
        <Button
          startIcon={<FontAwesomeIcon icon={faArrowLeft} />}
          onClick={() => navigate(-1)}
          sx={{ marginBottom: 2, color: "var(--coco-text-secondary)" }}
        >
          Volver
        </Button>

        <Box className="coco-card" sx={{ padding: 3 }}>
          <Grid container spacing={4}>
            {/* Imagen */}
            <Grid size={{ xs: 12, md: 5 }}>
              <Box
                sx={{
                  backgroundColor: "var(--coco-success-fill)",
                  borderRadius: 3,
                  height: 400,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  overflow: "hidden",
                  position: "relative",
                }}
              >
                {tieneDescuento && (
                  <Chip
                    label={`-${producto.descuentoPorcentaje}% OFF`}
                    sx={{
                      position: "absolute",
                      top: 16,
                      left: 16,
                      backgroundColor: "var(--coco-secondary)",
                      color: "#FFFFFF",
                      fontWeight: 700,
                      fontSize: 14,
                    }}
                  />
                )}
                {producto.imagenUrl && !imgError ? (
                  <img
                    src={producto.imagenUrl}
                    alt={producto.nombre}
                    onError={() => setImgError(true)}
                    style={{ width: "100%", height: "100%", objectFit: "contain" }}
                  />
                ) : (
                  <FontAwesomeIcon icon={faBoxOpen} style={{ fontSize: 140, color: "var(--coco-primary)" }} />
                )}
              </Box>
            </Grid>

            {/* Info */}
            <Grid size={{ xs: 12, md: 7 }}>
              <Chip
                label={agotado ? "Agotado" : producto.stock < 10 ? `Solo ${producto.stock} disponibles` : "En stock"}
                size="small"
                sx={{
                  backgroundColor: agotado ? "var(--coco-danger-fill)" : producto.stock < 10 ? "var(--coco-warning-fill)" : "var(--coco-success-fill)",
                  color: agotado ? "var(--coco-danger)" : producto.stock < 10 ? "var(--coco-warning)" : "var(--coco-success)",
                  marginBottom: 2,
                }}
              />

              <Typography sx={{ fontSize: 28, fontWeight: 700, marginBottom: 2, lineHeight: 1.2 }}>
                {producto.nombre}
              </Typography>

              {producto.descripcion && (
                <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 3, lineHeight: 1.6 }}>
                  {producto.descripcion}
                </Typography>
              )}

              {/* Precio */}
              <Box sx={{ marginBottom: 3 }}>
                {tieneDescuento && (
                  <Typography sx={{ fontSize: 16, color: "var(--coco-text-muted)", textDecoration: "line-through" }}>
                    ${producto.precio.toLocaleString("es-CO")}
                  </Typography>
                )}
                <Typography sx={{ fontSize: 36, fontWeight: 700, color: "var(--coco-primary)", lineHeight: 1 }}>
                  ${Math.round(precioFinal).toLocaleString("es-CO")}
                </Typography>
                {tieneDescuento && (
                  <Typography sx={{ fontSize: 13, color: "var(--coco-secondary)", fontWeight: 600, marginTop: 0.5 }}>
                    ¡Ahorras ${Math.round(ahorro).toLocaleString("es-CO")}!
                  </Typography>
                )}
              </Box>

              {/* Selector cantidad */}
              {!agotado && (
                <Box sx={{ marginBottom: 3 }}>
                  <Typography sx={{ fontSize: 13, fontWeight: 600, marginBottom: 1, color: "var(--coco-text-secondary)" }}>
                    Cantidad
                  </Typography>
                  <Box sx={{ display: "inline-flex", alignItems: "center", border: "1px solid var(--coco-border-strong)", borderRadius: 2 }}>
                    <IconButton size="small" onClick={() => setCantidad(Math.max(1, cantidad - 1))}>
                      <FontAwesomeIcon icon={faMinus} style={{ fontSize: 11 }} />
                    </IconButton>
                    <Typography sx={{ minWidth: 40, textAlign: "center", fontWeight: 600 }}>{cantidad}</Typography>
                    <IconButton size="small" onClick={() => setCantidad(Math.min(producto.stock, cantidad + 1))}>
                      <FontAwesomeIcon icon={faPlus} style={{ fontSize: 11 }} />
                    </IconButton>
                  </Box>
                </Box>
              )}

              {/* Botones */}
              <Box sx={{ display: "flex", gap: 1.5, flexWrap: "wrap" }}>
                <Button
                  variant="contained"
                  color="secondary"
                  size="large"
                  startIcon={<FontAwesomeIcon icon={faCartPlus} />}
                  disabled={agotado}
                  onClick={handleAgregar}
                  sx={{ paddingX: 4 }}
                >
                  Agregar al carrito
                </Button>
                <Button
                  variant="outlined"
                  color="primary"
                  size="large"
                  disabled={agotado}
                  onClick={() => {
                    agregar(producto, cantidad);
                    navigate("/carrito");
                  }}
                >
                  Comprar ahora
                </Button>
              </Box>

              {/* Beneficios */}
              <Box sx={{ marginTop: 4, paddingTop: 3, borderTop: "1px solid var(--coco-border)" }}>
                <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5 }}>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, fontSize: 13 }}>
                    <FontAwesomeIcon icon={faTruckFast} style={{ color: "var(--coco-primary)" }} />
                    <span>Envío a domicilio disponible</span>
                  </Box>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, fontSize: 13 }}>
                    <FontAwesomeIcon icon={faShieldHalved} style={{ color: "var(--coco-primary)" }} />
                    <span>Garantía de calidad CocoTech</span>
                  </Box>
                </Box>
              </Box>
            </Grid>
          </Grid>
        </Box>
      </motion.div>

      <Snackbar
        open={snackbar}
        autoHideDuration={2500}
        onClose={() => setSnackbar(false)}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert severity="success" onClose={() => setSnackbar(false)}>
          ¡Agregado al carrito!
        </Alert>
      </Snackbar>
    </LayoutEcommerce>
  );
};

export default DetalleProductoPublico;
