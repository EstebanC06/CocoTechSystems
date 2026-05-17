/**
 * Home pública del e-commerce.
 *
 * Sections:
 *  1. Hero / banner principal con CTA.
 *  2. Carrusel/grid de categorías (chips clickeables).
 *  3. Productos destacados.
 *  4. Productos con descuento (ofertas).
 */
import { useEffect, useState } from "react";
import { Box, Grid, Typography, Button, Chip, CircularProgress, Container } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faTruckFast,
  faShieldHalved,
  faTags,
  faArrowRight,
  faStore,
} from "@fortawesome/free-solid-svg-icons";
import LayoutEcommerce from "../../components/layout/LayoutEcommerce";
import TarjetaProducto from "../../components/producto/TarjetaProducto";
import {
  obtenerCategoriasPublico,
  obtenerProductosDestacados,
  obtenerProductosPublico,
} from "../../services/publico.service";
import type { ProductoDTO, CategoriaDTO } from "../../types";

const Home = () => {
  const navigate = useNavigate();
  const [destacados, setDestacados] = useState<ProductoDTO[]>([]);
  const [ofertas, setOfertas] = useState<ProductoDTO[]>([]);
  const [categorias, setCategorias] = useState<CategoriaDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const cargar = async () => {
      try {
        const [dest, todos, cats] = await Promise.all([
          obtenerProductosDestacados(),
          obtenerProductosPublico(),
          obtenerCategoriasPublico(),
        ]);
        setDestacados(dest.slice(0, 8));
        setOfertas(
          todos
            .filter((p) => (p.descuentoPorcentaje ?? 0) > 0)
            .slice(0, 8)
        );
        setCategorias(cats);
      } catch (e) {
        console.error("Error cargando home", e);
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, []);

  return (
    <LayoutEcommerce fluid>
      {/* Hero */}
      <Box
        sx={{
          background: "linear-gradient(135deg, var(--coco-primary) 0%, var(--coco-primary-dark) 100%)",
          color: "#FFFFFF",
          paddingY: { xs: 5, md: 8 },
          paddingX: 2,
          marginBottom: 4,
        }}
      >
        <Container maxWidth="lg">
          <Grid container spacing={4} alignItems="center">
            <Grid size={{ xs: 12, md: 7 }}>
              <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}>
                <Typography sx={{ fontSize: { xs: 32, md: 44 }, fontWeight: 700, marginBottom: 2, lineHeight: 1.2 }}>
                  Tu supermercado <br /> ahora a un click
                </Typography>
                <Typography sx={{ fontSize: 17, color: "var(--coco-accent)", marginBottom: 3, maxWidth: 480 }}>
                  Compra tus productos favoritos y recíbelos en casa, o recógelos en la sucursal que prefieras.
                </Typography>
                <Box sx={{ display: "flex", gap: 2, flexWrap: "wrap" }}>
                  <Button
                    variant="contained"
                    color="secondary"
                    size="large"
                    endIcon={<FontAwesomeIcon icon={faArrowRight} />}
                    onClick={() => navigate("/productos")}
                  >
                    Empezar a comprar
                  </Button>
                  <Button
                    variant="outlined"
                    size="large"
                    onClick={() => navigate("/productos?promo=true")}
                    sx={{
                      color: "#FFFFFF",
                      borderColor: "rgba(255,255,255,0.5)",
                      "&:hover": { borderColor: "#FFFFFF", backgroundColor: "rgba(255,255,255,0.1)" },
                    }}
                  >
                    Ver ofertas
                  </Button>
                </Box>
              </motion.div>
            </Grid>

            <Grid size={{ xs: 12, md: 5 }}>
              <motion.div initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} transition={{ duration: 0.5, delay: 0.2 }}>
                <Box
                  sx={{
                    backgroundColor: "rgba(255,255,255,0.1)",
                    borderRadius: 3,
                    padding: 3,
                    backdropFilter: "blur(10px)",
                  }}
                >
                  <Grid container spacing={2}>
                    {[
                      { icon: faTruckFast, t: "Domicilio rápido", d: "Entregas en menos de 1 hora" },
                      { icon: faStore, t: "Recoger en tienda", d: "Sin costos de envío" },
                      { icon: faTags, t: "Mejores precios", d: "Ofertas todos los días" },
                      { icon: faShieldHalved, t: "Pago seguro", d: "Múltiples métodos disponibles" },
                    ].map((it, i) => (
                      <Grid size={6} key={i}>
                        <Box sx={{ textAlign: "center", padding: 1 }}>
                          <FontAwesomeIcon icon={it.icon} style={{ fontSize: 24, color: "var(--coco-accent)", marginBottom: 8 }} />
                          <Typography sx={{ fontSize: 13, fontWeight: 600 }}>{it.t}</Typography>
                          <Typography sx={{ fontSize: 11, opacity: 0.8 }}>{it.d}</Typography>
                        </Box>
                      </Grid>
                    ))}
                  </Grid>
                </Box>
              </motion.div>
            </Grid>
          </Grid>
        </Container>
      </Box>

      <Container maxWidth="lg" sx={{ paddingBottom: 5 }}>
        {/* Categorías */}
        <Box sx={{ marginBottom: 5 }}>
          <Typography sx={{ fontSize: 22, fontWeight: 600, marginBottom: 2 }}>
            Compra por categoría
          </Typography>
          {loading ? (
            <Box sx={{ textAlign: "center", paddingY: 4 }}><CircularProgress /></Box>
          ) : categorias.length === 0 ? (
            <Typography sx={{ color: "var(--coco-text-secondary)" }}>No hay categorías disponibles.</Typography>
          ) : (
            <Box
              sx={{
                display: "flex",
                gap: 1.5,
                overflowX: "auto",
                paddingY: 1,
                "&::-webkit-scrollbar": { height: 6 },
              }}
            >
              {categorias.map((c) => (
                <Chip
                  key={c.idCategoria}
                  label={c.nombre}
                  clickable
                  onClick={() => navigate(`/productos?categoria=${c.idCategoria}`)}
                  sx={{
                    paddingX: 1,
                    paddingY: 2.5,
                    backgroundColor: "var(--coco-success-fill)",
                    color: "var(--coco-primary)",
                    fontWeight: 500,
                    fontSize: 13,
                    "&:hover": {
                      backgroundColor: "var(--coco-primary)",
                      color: "#FFFFFF",
                    },
                  }}
                />
              ))}
            </Box>
          )}
        </Box>

        {/* Destacados */}
        {destacados.length > 0 && (
          <Box sx={{ marginBottom: 5 }}>
            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 2 }}>
              <Typography sx={{ fontSize: 22, fontWeight: 600 }}>Productos destacados</Typography>
              <Button
                size="small"
                endIcon={<FontAwesomeIcon icon={faArrowRight} />}
                onClick={() => navigate("/productos?destacados=true")}
              >
                Ver todos
              </Button>
            </Box>
            <Grid container spacing={2}>
              {destacados.map((p) => (
                <Grid size={{ xs: 6, sm: 4, md: 3 }} key={p.idProducto}>
                  <TarjetaProducto producto={p} />
                </Grid>
              ))}
            </Grid>
          </Box>
        )}

        {/* Ofertas */}
        {ofertas.length > 0 && (
          <Box sx={{ marginBottom: 5 }}>
            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 2 }}>
              <Box>
                <Typography sx={{ fontSize: 22, fontWeight: 600 }}>
                  <FontAwesomeIcon icon={faTags} style={{ color: "var(--coco-secondary)", marginRight: 10 }} />
                  Ofertas del día
                </Typography>
                <Typography sx={{ fontSize: 13, color: "var(--coco-text-secondary)" }}>
                  Aprovecha los descuentos especiales
                </Typography>
              </Box>
              <Button
                size="small"
                endIcon={<FontAwesomeIcon icon={faArrowRight} />}
                onClick={() => navigate("/productos?promo=true")}
              >
                Ver todas
              </Button>
            </Box>
            <Grid container spacing={2}>
              {ofertas.map((p) => (
                <Grid size={{ xs: 6, sm: 4, md: 3 }} key={p.idProducto}>
                  <TarjetaProducto producto={p} />
                </Grid>
              ))}
            </Grid>
          </Box>
        )}

        {/* Fallback si todo está vacío */}
        {!loading && destacados.length === 0 && ofertas.length === 0 && (
          <Box className="coco-card" sx={{ textAlign: "center", padding: 6 }}>
            <FontAwesomeIcon icon={faStore} style={{ fontSize: 48, color: "var(--coco-text-muted)" }} />
            <Typography sx={{ marginTop: 2, fontSize: 16 }}>
              Aún no hay productos publicados
            </Typography>
            <Typography sx={{ color: "var(--coco-text-secondary)", fontSize: 13 }}>
              Cuando el administrador agregue productos, aparecerán aquí.
            </Typography>
            <Button variant="contained" color="secondary" sx={{ marginTop: 2 }} onClick={() => navigate("/productos")}>
              Ver catálogo completo
            </Button>
          </Box>
        )}
      </Container>
    </LayoutEcommerce>
  );
};

export default Home;
