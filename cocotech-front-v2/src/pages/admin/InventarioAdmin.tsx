/**
 * Inventario consolidado — vista admin con alertas y filtros por estado.
 *
 * Cambios respecto a la versión anterior:
 *  - Umbral de "stock bajo": ahora es < 25 (antes era < 10).
 *  - "Valor del stock" se calcula sobre los productos visibles tras
 *    aplicar búsqueda y filtro, no sobre el total. Así se actualiza
 *    al filtrar (por ejemplo: "valor de productos agotados", aunque
 *    siempre dará 0 — pero "valor de stock bajo" sí es útil).
 *  - Las categorías muestran etiquetas legibles del enum.
 *  - Los filtros usan el mismo umbral 25 para "BAJO".
 */
import { useEffect, useMemo, useState } from "react";
import {
  Box,
  Typography,
  TextField,
  InputAdornment,
  CircularProgress,
  Chip,
  MenuItem,
  Grid,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
} from "@mui/material";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faMagnifyingGlass,
  faTriangleExclamation,
  faBoxOpen,
  faBan,
  faCheckDouble,
} from "@fortawesome/free-solid-svg-icons";
import LayoutAdmin from "../../components/layout/LayoutAdmin";
import { obtenerProductos } from "../../services/producto.service";
import { obtenerCategorias } from "../../services/categoria.service";
import type { ProductoDTO, CategoriaDTO } from "../../types";
import { etiquetaCategoria } from "../../utils/etiquetas";

type Estado = "TODOS" | "AGOTADO" | "BAJO" | "DISPONIBLE";

const UMBRAL_STOCK_BAJO = 25;

const InventarioAdmin = () => {
  const [productos, setProductos] = useState<ProductoDTO[]>([]);
  const [categorias, setCategorias] = useState<CategoriaDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [busqueda, setBusqueda] = useState("");
  const [filtro, setFiltro] = useState<Estado>("TODOS");

  useEffect(() => {
    Promise.all([obtenerProductos(), obtenerCategorias()])
      .then(([p, c]) => {
        setProductos(p);
        setCategorias(c);
      })
      .catch(() => null)
      .finally(() => setLoading(false));
  }, []);

  const categoriaPorId = useMemo(() => {
    const m = new Map<number, string>();
    categorias.forEach((c) => m.set(c.idCategoria!, etiquetaCategoria(c.nombre)));
    return m;
  }, [categorias]);

  // Lista filtrada (búsqueda + filtro de estado)
  const visibles = useMemo(() => {
    let r = [...productos];
    if (busqueda) {
      const t = busqueda.toLowerCase();
      r = r.filter((p) => p.nombre.toLowerCase().includes(t));
    }
    switch (filtro) {
      case "AGOTADO":
        r = r.filter((p) => p.stock <= 0);
        break;
      case "BAJO":
        r = r.filter((p) => p.stock > 0 && p.stock < UMBRAL_STOCK_BAJO);
        break;
      case "DISPONIBLE":
        r = r.filter((p) => p.stock >= UMBRAL_STOCK_BAJO);
        break;
    }
    return r;
  }, [productos, busqueda, filtro]);

  // Stats GLOBALES (no filtradas) para los 3 primeros KPIs
  const statsGlobales = useMemo(
    () => ({
      total: productos.length,
      agotados: productos.filter((p) => p.stock <= 0).length,
      bajos: productos.filter((p) => p.stock > 0 && p.stock < UMBRAL_STOCK_BAJO).length,
    }),
    [productos]
  );

  // Valor del stock calculado SOBRE LOS VISIBLES (responde al filtro).
  const valorStockVisible = useMemo(
    () => visibles.reduce((acc, p) => acc + p.precio * p.stock, 0),
    [visibles]
  );

  const chipStock = (s: number) => {
    if (s <= 0)
      return (
        <Chip
          label="Agotado"
          size="small"
          sx={{
            backgroundColor: "var(--coco-danger-fill)",
            color: "var(--coco-danger)",
            fontSize: 10,
          }}
        />
      );
    if (s < UMBRAL_STOCK_BAJO)
      return (
        <Chip
          label={`Bajo (${s})`}
          size="small"
          sx={{
            backgroundColor: "var(--coco-warning-fill)",
            color: "var(--coco-warning)",
            fontSize: 10,
          }}
        />
      );
    return (
      <Chip
        label={`${s} unidades`}
        size="small"
        sx={{
          backgroundColor: "var(--coco-success-fill)",
          color: "var(--coco-success)",
          fontSize: 10,
        }}
      />
    );
  };

  return (
    <LayoutAdmin>
      <Typography sx={{ fontSize: 26, fontWeight: 600 }}>Inventario</Typography>
      <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 3 }}>
        Vista consolidada de stock y alertas
      </Typography>

      <Grid container spacing={2} sx={{ marginBottom: 3 }}>
        {[
          {
            t: "Total productos",
            v: statsGlobales.total,
            i: faBoxOpen,
            c: "var(--coco-primary)",
            bg: "var(--coco-success-fill)",
          },
          {
            t: "Agotados",
            v: statsGlobales.agotados,
            i: faBan,
            c: "var(--coco-danger)",
            bg: "var(--coco-danger-fill)",
          },
          {
            t: `Stock bajo (<${UMBRAL_STOCK_BAJO})`,
            v: statsGlobales.bajos,
            i: faTriangleExclamation,
            c: "var(--coco-warning)",
            bg: "var(--coco-warning-fill)",
          },
          {
            t: "Valor stock (visible)",
            v: `$${Math.round(valorStockVisible).toLocaleString("es-CO")}`,
            i: faCheckDouble,
            c: "var(--coco-info)",
            bg: "var(--coco-info-fill)",
          },
        ].map((k) => (
          <Grid size={{ xs: 6, md: 3 }} key={k.t}>
            <Box className="coco-card" sx={{ padding: 2 }}>
              <Box
                sx={{
                  width: 40,
                  height: 40,
                  borderRadius: 1.5,
                  backgroundColor: k.bg,
                  color: k.c,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  marginBottom: 1.5,
                }}
              >
                <FontAwesomeIcon icon={k.i} />
              </Box>
              <Typography
                sx={{
                  fontSize: 11,
                  color: "var(--coco-text-secondary)",
                  textTransform: "uppercase",
                  letterSpacing: 0.5,
                }}
              >
                {k.t}
              </Typography>
              <Typography sx={{ fontSize: 22, fontWeight: 700 }}>{k.v}</Typography>
            </Box>
          </Grid>
        ))}
      </Grid>

      <Box sx={{ display: "flex", gap: 2, marginBottom: 2, flexWrap: "wrap" }}>
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
          label="Filtrar"
          value={filtro}
          onChange={(e) => setFiltro(e.target.value as Estado)}
          sx={{ minWidth: 200 }}
        >
          <MenuItem value="TODOS">Todos</MenuItem>
          <MenuItem value="AGOTADO">Agotados</MenuItem>
          <MenuItem value="BAJO">{`Stock bajo (<${UMBRAL_STOCK_BAJO})`}</MenuItem>
          <MenuItem value="DISPONIBLE">{`Disponibles (≥${UMBRAL_STOCK_BAJO})`}</MenuItem>
        </TextField>
      </Box>

      {loading ? (
        <Box sx={{ textAlign: "center", padding: 6 }}>
          <CircularProgress />
        </Box>
      ) : (
        <Box className="coco-card" sx={{ padding: 0, overflow: "auto" }}>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>
                  <strong>Producto</strong>
                </TableCell>
                <TableCell>
                  <strong>Categoría</strong>
                </TableCell>
                <TableCell align="right">
                  <strong>Precio</strong>
                </TableCell>
                <TableCell>
                  <strong>Stock</strong>
                </TableCell>
                <TableCell align="right">
                  <strong>Valor</strong>
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {visibles.map((p) => (
                <TableRow key={p.idProducto} hover>
                  <TableCell>{p.nombre}</TableCell>
                  <TableCell>{categoriaPorId.get(p.idCategoria) ?? "—"}</TableCell>
                  <TableCell align="right">
                    ${p.precio.toLocaleString("es-CO")}
                  </TableCell>
                  <TableCell>{chipStock(p.stock)}</TableCell>
                  <TableCell align="right">
                    ${(p.precio * p.stock).toLocaleString("es-CO")}
                  </TableCell>
                </TableRow>
              ))}
              {visibles.length === 0 && (
                <TableRow>
                  <TableCell colSpan={5} align="center" sx={{ padding: 4 }}>
                    <Typography sx={{ color: "var(--coco-text-secondary)" }}>
                      No hay productos que coincidan con el filtro.
                    </Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </Box>
      )}
    </LayoutAdmin>
  );
};

export default InventarioAdmin;
