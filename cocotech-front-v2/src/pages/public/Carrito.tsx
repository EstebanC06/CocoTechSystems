/**
 * Página del carrito de compras.
 * Lee del CarritoContext, permite modificar cantidades, eliminar y
 * dirigirse al checkout.
 */
import { Box, Grid, Typography, Button, IconButton, Divider } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCartShopping, faTrash, faMinus, faPlus, faBoxOpen, faArrowRight, faLock,
} from "@fortawesome/free-solid-svg-icons";
import LayoutEcommerce from "../../components/layout/LayoutEcommerce";
import { useCarrito } from "../../context/CarritoContext";
import { useAuth } from "../../context/AuthContext";

const Carrito = () => {
  const navigate = useNavigate();
  const { estaAutenticado, esCliente } = useAuth();
  const {
    items, cantidadItems, subtotal, iva, total,
    modificarCantidad, quitar, precioConDescuento,
  } = useCarrito();

  const irAlCheckout = () => {
    if (estaAutenticado && esCliente) {
      navigate("/checkout");
    } else {
      // Guardar intención y enviar al login.
      navigate("/login?redirect=/checkout");
    }
  };

  if (items.length === 0) {
    return (
      <LayoutEcommerce>
        <Box className="coco-card" sx={{ textAlign: "center", padding: 6 }}>
          <FontAwesomeIcon icon={faCartShopping} style={{ fontSize: 64, color: "var(--coco-text-muted)" }} />
          <Typography sx={{ marginTop: 2, fontSize: 20, fontWeight: 600 }}>
            Tu carrito está vacío
          </Typography>
          <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 3 }}>
            Agrega productos para empezar a comprar.
          </Typography>
          <Button variant="contained" color="secondary" size="large" onClick={() => navigate("/productos")}>
            Ir al catálogo
          </Button>
        </Box>
      </LayoutEcommerce>
    );
  }

  return (
    <LayoutEcommerce>
      <Typography sx={{ fontSize: 26, fontWeight: 600, marginBottom: 0.5 }}>
        Mi carrito
      </Typography>
      <Typography sx={{ fontSize: 13, color: "var(--coco-text-secondary)", marginBottom: 3 }}>
        {cantidadItems} {cantidadItems === 1 ? "producto" : "productos"}
      </Typography>

      <Grid container spacing={3}>
        {/* Lista de items */}
        <Grid size={{ xs: 12, md: 8 }}>
          <Box className="coco-card" sx={{ padding: 0, overflow: "hidden" }}>
            <AnimatePresence>
              {items.map((it, idx) => {
                const precio = precioConDescuento(it.producto);
                const sub = precio * it.cantidad;
                const tieneDesc = (it.producto.descuentoPorcentaje ?? 0) > 0;
                return (
                  <motion.div
                    key={it.producto.idProducto}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: 20 }}
                    transition={{ duration: 0.2 }}
                  >
                    <Box
                      sx={{
                        display: "flex",
                        gap: 2,
                        padding: 2,
                        borderTop: idx > 0 ? "1px solid var(--coco-border)" : "none",
                      }}
                    >
                      {/* Imagen */}
                      <Box
                        sx={{
                          width: 80,
                          height: 80,
                          flexShrink: 0,
                          backgroundColor: "var(--coco-success-fill)",
                          borderRadius: 2,
                          display: "flex",
                          alignItems: "center",
                          justifyContent: "center",
                          overflow: "hidden",
                          cursor: "pointer",
                        }}
                        onClick={() => navigate(`/producto/${it.producto.idProducto}`)}
                      >
                        {it.producto.imagenUrl ? (
                          <img
                            src={it.producto.imagenUrl}
                            alt={it.producto.nombre}
                            style={{ width: "100%", height: "100%", objectFit: "cover" }}
                          />
                        ) : (
                          <FontAwesomeIcon icon={faBoxOpen} style={{ fontSize: 28, color: "var(--coco-primary)" }} />
                        )}
                      </Box>

                      {/* Info */}
                      <Box sx={{ flex: 1, minWidth: 0 }}>
                        <Typography
                          sx={{ fontSize: 14, fontWeight: 500, cursor: "pointer", "&:hover": { color: "var(--coco-primary)" } }}
                          onClick={() => navigate(`/producto/${it.producto.idProducto}`)}
                        >
                          {it.producto.nombre}
                        </Typography>
                        <Box sx={{ display: "flex", alignItems: "baseline", gap: 1, marginTop: 0.5 }}>
                          {tieneDesc && (
                            <Typography sx={{ fontSize: 11, color: "var(--coco-text-muted)", textDecoration: "line-through" }}>
                              ${it.producto.precio.toLocaleString("es-CO")}
                            </Typography>
                          )}
                          <Typography sx={{ fontSize: 13, fontWeight: 600, color: "var(--coco-primary)" }}>
                            ${Math.round(precio).toLocaleString("es-CO")} c/u
                          </Typography>
                        </Box>

                        {/* Cantidad */}
                        <Box sx={{ display: "flex", alignItems: "center", gap: 2, marginTop: 1.5 }}>
                          <Box sx={{ display: "inline-flex", alignItems: "center", border: "1px solid var(--coco-border-strong)", borderRadius: 1 }}>
                            <IconButton size="small" onClick={() => modificarCantidad(it.producto.idProducto!, it.cantidad - 1)}>
                              <FontAwesomeIcon icon={faMinus} style={{ fontSize: 10 }} />
                            </IconButton>
                            <Typography sx={{ minWidth: 30, textAlign: "center", fontSize: 13, fontWeight: 600 }}>
                              {it.cantidad}
                            </Typography>
                            <IconButton size="small" onClick={() => modificarCantidad(it.producto.idProducto!, it.cantidad + 1)}>
                              <FontAwesomeIcon icon={faPlus} style={{ fontSize: 10 }} />
                            </IconButton>
                          </Box>
                          <IconButton size="small" onClick={() => quitar(it.producto.idProducto!)} sx={{ color: "var(--coco-danger)" }}>
                            <FontAwesomeIcon icon={faTrash} style={{ fontSize: 13 }} />
                          </IconButton>
                        </Box>
                      </Box>

                      {/* Subtotal */}
                      <Box sx={{ minWidth: 100, textAlign: "right" }}>
                        <Typography sx={{ fontSize: 15, fontWeight: 700, color: "var(--coco-primary)" }}>
                          ${Math.round(sub).toLocaleString("es-CO")}
                        </Typography>
                      </Box>
                    </Box>
                  </motion.div>
                );
              })}
            </AnimatePresence>
          </Box>
        </Grid>

        {/* Resumen */}
        <Grid size={{ xs: 12, md: 4 }}>
          <Box className="coco-card" sx={{ padding: 2.5, position: "sticky", top: 100 }}>
            <Typography sx={{ fontWeight: 600, fontSize: 16, marginBottom: 2 }}>
              Resumen del pedido
            </Typography>

            <Box sx={{ display: "flex", flexDirection: "column", gap: 1, marginBottom: 2 }}>
              <Box sx={{ display: "flex", justifyContent: "space-between", fontSize: 14 }}>
                <span style={{ color: "var(--coco-text-secondary)" }}>Subtotal</span>
                <span>${Math.round(subtotal).toLocaleString("es-CO")}</span>
              </Box>
              <Box sx={{ display: "flex", justifyContent: "space-between", fontSize: 14 }}>
                <span style={{ color: "var(--coco-text-secondary)" }}>IVA (19%)</span>
                <span>${Math.round(iva).toLocaleString("es-CO")}</span>
              </Box>
              <Box sx={{ display: "flex", justifyContent: "space-between", fontSize: 13, color: "var(--coco-text-muted)" }}>
                <span>Envío</span>
                <span>Se calcula en checkout</span>
              </Box>
            </Box>

            <Divider sx={{ marginY: 2 }} />

            <Box sx={{ display: "flex", justifyContent: "space-between", marginBottom: 2 }}>
              <Typography sx={{ fontWeight: 600 }}>Total</Typography>
              <Typography sx={{ fontSize: 22, fontWeight: 700, color: "var(--coco-primary)" }}>
                ${Math.round(total).toLocaleString("es-CO")}
              </Typography>
            </Box>

            <Button
              fullWidth
              variant="contained"
              color="secondary"
              size="large"
              endIcon={<FontAwesomeIcon icon={faArrowRight} />}
              onClick={irAlCheckout}
            >
              Ir a pagar
            </Button>

            {!estaAutenticado && (
              <Typography sx={{ fontSize: 11, color: "var(--coco-text-muted)", textAlign: "center", marginTop: 1.5 }}>
                <FontAwesomeIcon icon={faLock} style={{ marginRight: 4 }} />
                Te pediremos iniciar sesión antes de pagar
              </Typography>
            )}

            <Button
              fullWidth
              size="small"
              sx={{ marginTop: 1, color: "var(--coco-text-secondary)" }}
              onClick={() => navigate("/productos")}
            >
              Seguir comprando
            </Button>
          </Box>
        </Grid>
      </Grid>
    </LayoutEcommerce>
  );
};

export default Carrito;
