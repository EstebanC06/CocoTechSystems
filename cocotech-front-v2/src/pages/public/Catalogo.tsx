/**
 * Catálogo público de productos.
 *
 * Acepta query params:
 *  - ?q=texto       → búsqueda
 *  - ?categoria=N   → filtro por categoría
 *  - ?promo=true    → solo con descuento
 *  - ?destacados=true → solo destacados
 */
import { useEffect, useMemo, useState } from "react";
import {
  Box,
  Grid,
  Typography,
  TextField,
  MenuItem,
  InputAdornment,
  CircularProgress,
  Chip,
} from "@mui/material";
import { useSearchParams } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMagnifyingGlass, faBoxOpen } from "@fortawesome/free-solid-svg-icons";
import LayoutEcommerce from "../../components/layout/LayoutEcommerce";
import TarjetaProducto from "../../components/producto/TarjetaProducto";
import {
  obtenerProductosPublico,
  obtenerCategoriasPublico,
} from "../../services/publico.service";
import type { ProductoDTO, CategoriaDTO } from "../../types";
import { etiquetaCategoria } from "../../utils/etiquetas";

type Orden = "relevancia" | "precio_asc" | "precio_desc" | "nombre";

const Catalogo = () => {
  const [params, setParams] = useSearchParams();
  const [productos, setProductos] = useState<ProductoDTO[]>([]);
  const [categorias, setCategorias] = useState<CategoriaDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const q = params.get("q") ?? "";
  const idCategoria = params.get("categoria");
  const soloPromo = params.get("promo") === "true";
  const soloDestacados = params.get("destacados") === "true";

  const [busqueda, setBusqueda] = useState(q);
  const [orden, setOrden] = useState<Orden>("relevancia");

  useEffect(() => {
    setBusqueda(q);
  }, [q]);

  useEffect(() => {
    const cargar = async () => {
      try {
        const [prods, cats] = await Promise.all([
          obtenerProductosPublico(),
          obtenerCategoriasPublico(),
        ]);
        setProductos(prods);
        setCategorias(cats);
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, []);

  const filtrados = useMemo(() => {
    let r = [...productos];
    if (busqueda) {
      const t = busqueda.toLowerCase();
      r = r.filter((p) => p.nombre.toLowerCase().includes(t));
    }
    if (idCategoria) {
      r = r.filter((p) => p.idCategoria === parseInt(idCategoria));
    }
    if (soloPromo) {
      r = r.filter((p) => (p.descuentoPorcentaje ?? 0) > 0);
    }
    if (soloDestacados) {
      r = r.filter((p) => p.destacado);
    }
    switch (orden) {
      case "precio_asc": r.sort((a, b) => a.precio - b.precio); break;
      case "precio_desc": r.sort((a, b) => b.precio - a.precio); break;
      case "nombre": r.sort((a, b) => a.nombre.localeCompare(b.nombre)); break;
    }
    return r;
  }, [productos, busqueda, idCategoria, soloPromo, soloDestacados, orden]);

  const tituloPagina = soloPromo
    ? "Ofertas del día"
    : soloDestacados
    ? "Productos destacados"
    : idCategoria
    ? categorias.find((c) => c.idCategoria === parseInt(idCategoria))?.nombre ?? "Categoría"
    : q
    ? `Resultados para "${q}"`
    : "Catálogo completo";

  const limpiarFiltros = (filtro: string) => {
    const np = new URLSearchParams(params);
    np.delete(filtro);
    setParams(np);
  };

  return (
    <LayoutEcommerce>
      <Typography sx={{ fontSize: 26, fontWeight: 600, marginBottom: 0.5 }}>
        {tituloPagina}
      </Typography>
      <Typography sx={{ fontSize: 13, color: "var(--coco-text-secondary)", marginBottom: 2 }}>
        {filtrados.length} productos encontrados
      </Typography>

      {/* Chips de filtros activos */}
      <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap", marginBottom: 2 }}>
        {q && (
          <Chip
            label={`Búsqueda: "${q}"`}
            size="small"
            onDelete={() => {
              setBusqueda("");
              limpiarFiltros("q");
            }}
          />
        )}
        {idCategoria && (
          <Chip
            label={`Categoría: ${categorias.find((c) => c.idCategoria === parseInt(idCategoria))?.nombre ?? idCategoria}`}
            size="small"
            onDelete={() => limpiarFiltros("categoria")}
          />
        )}
        {soloPromo && <Chip label="Solo ofertas" size="small" color="secondary" onDelete={() => limpiarFiltros("promo")} />}
        {soloDestacados && <Chip label="Solo destacados" size="small" onDelete={() => limpiarFiltros("destacados")} />}
      </Box>

      {/* Barra de búsqueda y ordenamiento */}
      <Box sx={{ display: "flex", gap: 2, marginBottom: 3, flexWrap: "wrap" }}>
        <TextField
          placeholder="Buscar dentro de los resultados..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          size="small"
          sx={{ flex: 1, minWidth: 240 }}
          InputProps={{
            startAdornment: <InputAdornment position="start"><FontAwesomeIcon icon={faMagnifyingGlass} /></InputAdornment>,
          }}
        />
        <TextField
          select
          size="small"
          label="Ordenar por"
          value={orden}
          onChange={(e) => setOrden(e.target.value as Orden)}
          sx={{ minWidth: 180 }}
        >
          <MenuItem value="relevancia">Relevancia</MenuItem>
          <MenuItem value="precio_asc">Precio: menor a mayor</MenuItem>
          <MenuItem value="precio_desc">Precio: mayor a menor</MenuItem>
          <MenuItem value="nombre">Nombre A-Z</MenuItem>
        </TextField>
      </Box>

      <Grid container spacing={3}>
        {/* Sidebar filtros */}
        <Grid size={{ xs: 12, md: 3 }}>
          <Box className="coco-card" sx={{ padding: 2 }}>
            <Typography sx={{ fontSize: 13, fontWeight: 600, marginBottom: 1.5, textTransform: "uppercase", letterSpacing: 0.5, color: "var(--coco-text-secondary)" }}>
              Categorías
            </Typography>
            <Box sx={{ display: "flex", flexDirection: "column", gap: 0.5 }}>
              <Box
                onClick={() => limpiarFiltros("categoria")}
                sx={{
                  padding: "6px 10px",
                  fontSize: 13,
                  cursor: "pointer",
                  borderRadius: 1,
                  backgroundColor: !idCategoria ? "var(--coco-success-fill)" : "transparent",
                  color: !idCategoria ? "var(--coco-primary)" : "var(--coco-text)",
                  fontWeight: !idCategoria ? 600 : 400,
                  "&:hover": { backgroundColor: "var(--coco-surface-2)" },
                }}
              >
                Todas
              </Box>
              {categorias.map((c) => (
                <Box
                  key={c.idCategoria}
                  onClick={() => {
                    const np = new URLSearchParams(params);
                    np.set("categoria", String(c.idCategoria));
                    setParams(np);
                  }}
                  sx={{
                    padding: "6px 10px",
                    fontSize: 13,
                    cursor: "pointer",
                    borderRadius: 1,
                    backgroundColor: idCategoria === String(c.idCategoria) ? "var(--coco-success-fill)" : "transparent",
                    color: idCategoria === String(c.idCategoria) ? "var(--coco-primary)" : "var(--coco-text)",
                    fontWeight: idCategoria === String(c.idCategoria) ? 600 : 400,
                    "&:hover": { backgroundColor: "var(--coco-surface-2)" },
                  }}
                >
                  {etiquetaCategoria(c.nombre)}
                </Box>
              ))}
            </Box>
          </Box>
        </Grid>

        {/* Grid productos */}
        <Grid size={{ xs: 12, md: 9 }}>
          {loading ? (
            <Box sx={{ textAlign: "center", padding: 6 }}><CircularProgress /></Box>
          ) : filtrados.length === 0 ? (
            <Box className="coco-card" sx={{ textAlign: "center", padding: 6 }}>
              <FontAwesomeIcon icon={faBoxOpen} style={{ fontSize: 48, color: "var(--coco-text-muted)" }} />
              <Typography sx={{ marginTop: 2 }}>No se encontraron productos.</Typography>
            </Box>
          ) : (
            <Grid container spacing={2}>
              {filtrados.map((p) => (
                <Grid size={{ xs: 6, sm: 4, md: 4, lg: 3 }} key={p.idProducto}>
                  <TarjetaProducto producto={p} />
                </Grid>
              ))}
            </Grid>
          )}
        </Grid>
      </Grid>
    </LayoutEcommerce>
  );
};

export default Catalogo;
