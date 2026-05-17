/**
 * Vista de productos en modo lectura para empleados.
 * Pueden consultar stock pero NO crear/editar/eliminar.
 *
 * El combobox de categoría muestra los nombres legibles del enum
 * NombreCategoria (ej. "Frutas y verduras" en lugar de "FRUTAS_VERDURAS").
 */
import { useEffect, useMemo, useState } from "react";
import {
  Box,
  Grid,
  Typography,
  TextField,
  MenuItem,
  CircularProgress,
  Chip,
  InputAdornment,
} from "@mui/material";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faMagnifyingGlass,
  faBoxOpen,
} from "@fortawesome/free-solid-svg-icons";
import LayoutEmpleado from "../../components/layout/LayoutEmpleado";
import { obtenerProductos } from "../../services/producto.service";
import { obtenerCategorias } from "../../services/categoria.service";
import { ETIQUETAS_CATEGORIA } from "../../types";
import type { ProductoDTO, CategoriaDTO } from "../../types";

const ProductosEmpleado = () => {
  const [productos, setProductos] = useState<ProductoDTO[]>([]);
  const [categorias, setCategorias] = useState<CategoriaDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [busqueda, setBusqueda] = useState("");
  const [filtroCat, setFiltroCat] = useState<number | "TODAS">("TODAS");

  useEffect(() => {
    const cargar = async () => {
      try {
        const [prods, cats] = await Promise.all([
          obtenerProductos(),
          obtenerCategorias(),
        ]);
        setProductos(prods);
        setCategorias(cats);
      } catch {
        // silencioso
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, []);

  const filtrados = useMemo(() => {
    let r = [...productos];
    if (filtroCat !== "TODAS") r = r.filter((p) => p.idCategoria === filtroCat);
    if (busqueda) {
      const t = busqueda.toLowerCase();
      r = r.filter((p) => p.nombre.toLowerCase().includes(t));
    }
    return r;
  }, [productos, busqueda, filtroCat]);

  const stockColor = (s: number) => {
    if (s <= 0)
      return { bg: "var(--coco-danger-fill)", fg: "var(--coco-danger)" };
    if (s < 10)
      return { bg: "var(--coco-warning-fill)", fg: "var(--coco-warning)" };
    return { bg: "var(--coco-success-fill)", fg: "var(--coco-success)" };
  };

  return (
    <LayoutEmpleado>
      <Typography sx={{ fontSize: 26, fontWeight: 600 }}>Productos</Typography>
      <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 3 }}>
        Consulta el inventario de productos
      </Typography>

      <Box sx={{ display: "flex", gap: 2, marginBottom: 3, flexWrap: "wrap" }}>
        <TextField
          size="small"
          placeholder="Buscar producto..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          sx={{ flex: 1, minWidth: 240 }}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <FontAwesomeIcon icon={faMagnifyingGlass} />
              </InputAdornment>
            ),
          }}
        />
        <TextField
          select
          size="small"
          label="Categoría"
          value={filtroCat}
          onChange={(e) =>
            setFiltroCat(
              e.target.value === "TODAS" ? "TODAS" : parseInt(e.target.value),
            )
          }
          sx={{ minWidth: 220 }}
        >
          <MenuItem value="TODAS">Todas</MenuItem>
          {categorias.map((c) => (
            <MenuItem key={c.idCategoria} value={c.idCategoria}>
              {ETIQUETAS_CATEGORIA[c.nombre] ?? c.nombre}
            </MenuItem>
          ))}
        </TextField>
      </Box>

      {loading ? (
        <Box sx={{ textAlign: "center", padding: 6 }}>
          <CircularProgress />
        </Box>
      ) : (
        <Grid container spacing={2}>
          {filtrados.map((p) => {
            const sc = stockColor(p.stock);
            return (
              <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }} key={p.idProducto}>
                <Box className="coco-card">
                  <Box
                    sx={{
                      height: 100,
                      backgroundColor: "var(--coco-success-fill)",
                      borderRadius: 1.5,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      marginBottom: 1,
                    }}
                  >
                    {p.imagenUrl ? (
                      <img
                        src={p.imagenUrl}
                        alt={p.nombre}
                        style={{
                          width: "100%",
                          height: "100%",
                          objectFit: "cover",
                          borderRadius: 6,
                        }}
                      />
                    ) : (
                      <FontAwesomeIcon
                        icon={faBoxOpen}
                        style={{ fontSize: 36, color: "var(--coco-primary)" }}
                      />
                    )}
                  </Box>
                  <Typography
                    sx={{
                      fontSize: 13,
                      fontWeight: 500,
                      minHeight: 36,
                      marginBottom: 1,
                    }}
                  >
                    {p.nombre}
                  </Typography>
                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                    }}
                  >
                    <Typography
                      sx={{ fontWeight: 700, color: "var(--coco-primary)" }}
                    >
                      ${p.precio.toLocaleString("es-CO")}
                    </Typography>
                    <Chip
                      label={p.stock <= 0 ? "Agotado" : `Stock: ${p.stock}`}
                      size="small"
                      sx={{
                        backgroundColor: sc.bg,
                        color: sc.fg,
                        fontSize: 10,
                      }}
                    />
                  </Box>
                </Box>
              </Grid>
            );
          })}
        </Grid>
      )}
    </LayoutEmpleado>
  );
};

export default ProductosEmpleado;
