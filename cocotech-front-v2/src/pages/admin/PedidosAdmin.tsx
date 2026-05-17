/**
 * Gestión global de pedidos online — vista de admin.
 *
 * Cambios respecto a la versión anterior:
 *  - El combobox "Sucursal" muestra etiquetas legibles del enum
 *    NombreSucursal (Fontibón, Usaquén, ...).
 *  - El nombre de sucursal en la tarjeta del pedido y en el dialog
 *    también se muestra legible.
 *  - El badge de estado usa ETIQUETAS_ESTADO_PEDIDO de types.
 *
 * Diferencia con la vista del empleado: ve TODOS los pedidos del sistema,
 * de todas las sucursales, y puede filtrar por estado/sucursal.
 *
 * IMPORTANTE: al cambiar un pedido a ENTREGADO, el back automáticamente
 * crea la Venta + Factura en MySQL y proyecta el documento Factura a
 * MongoDB. No hay que hacer nada extra desde aquí.
 */
import { useEffect, useMemo, useState } from "react";
import {
  Box,
  Typography,
  Chip,
  Button,
  CircularProgress,
  TextField,
  MenuItem,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  Divider,
  Grid,
} from "@mui/material";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faReceipt,
  faTruckFast,
  faStore,
  faBoxOpen,
} from "@fortawesome/free-solid-svg-icons";
import LayoutAdmin from "../../components/layout/LayoutAdmin";
import {
  obtenerTodosPedidos,
  cambiarEstadoPedido,
} from "../../services/pedido.service";
import { obtenerSucursales } from "../../services/sucursal.service";
import {
  ESTADOS_PEDIDO,
  ETIQUETAS_ESTADO_PEDIDO,
  type PedidoDTO,
  type EstadoPedido,
  type SucursalDTO,
} from "../../types";
import { etiquetaSucursal } from "../../utils/etiquetas";

const colores: Record<EstadoPedido, { bg: string; fg: string }> = {
  RECIBIDO: { bg: "var(--coco-info-fill)", fg: "var(--coco-info)" },
  PREPARANDO: { bg: "var(--coco-warning-fill)", fg: "var(--coco-warning)" },
  LISTO_PARA_ENTREGA: { bg: "var(--coco-success-fill)", fg: "var(--coco-success)" },
  EN_CAMINO: { bg: "var(--coco-warning-fill)", fg: "var(--coco-warning)" },
  ENTREGADO: { bg: "var(--coco-success-fill)", fg: "var(--coco-success)" },
  CANCELADO: { bg: "var(--coco-danger-fill)", fg: "var(--coco-danger)" },
};

const PedidosAdmin = () => {
  const [pedidos, setPedidos] = useState<PedidoDTO[]>([]);
  const [sucursales, setSucursales] = useState<SucursalDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [filtroEstado, setFiltroEstado] = useState<EstadoPedido | "TODOS">("TODOS");
  const [filtroSucursal, setFiltroSucursal] = useState<number | "TODAS">("TODAS");
  const [sel, setSel] = useState<PedidoDTO | null>(null);
  const [nuevoEstado, setNuevoEstado] = useState<EstadoPedido>("RECIBIDO");
  const [procesando, setProcesando] = useState(false);
  const [error, setError] = useState("");

  // Map id sucursal → etiqueta legible
  const sucursalLabel = useMemo(() => {
    const m = new Map<number, string>();
    sucursales.forEach((s) => m.set(s.idSucursal!, etiquetaSucursal(s.nombre)));
    return m;
  }, [sucursales]);

  const cargar = async () => {
    setLoading(true);
    try {
      const [peds, sucs] = await Promise.all([
        obtenerTodosPedidos().catch(() => []),
        obtenerSucursales().catch(() => []),
      ]);
      setPedidos(peds);
      setSucursales(sucs);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
  }, []);

  const visibles = useMemo(() => {
    let r = [...pedidos];
    if (filtroEstado !== "TODOS") r = r.filter((p) => p.estado === filtroEstado);
    if (filtroSucursal !== "TODAS")
      r = r.filter((p) => p.idSucursalDespacho === filtroSucursal);
    return r;
  }, [pedidos, filtroEstado, filtroSucursal]);

  const counters = useMemo(
    () => ({
      total: pedidos.length,
      activos: pedidos.filter(
        (p) => !["ENTREGADO", "CANCELADO"].includes(p.estado)
      ).length,
      entregados: pedidos.filter((p) => p.estado === "ENTREGADO").length,
      cancelados: pedidos.filter((p) => p.estado === "CANCELADO").length,
    }),
    [pedidos]
  );

  const cambiar = async () => {
    if (!sel?.idPedido) return;
    setProcesando(true);
    setError("");
    try {
      await cambiarEstadoPedido(sel.idPedido, nuevoEstado);
      setSel(null);
      cargar();
    } catch (e: any) {
      setError(e?.response?.data?.message ?? "Error al actualizar.");
    } finally {
      setProcesando(false);
    }
  };

  return (
    <LayoutAdmin>
      <Typography sx={{ fontSize: 26, fontWeight: 600 }}>Pedidos online</Typography>
      <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 3 }}>
        Vista global de todos los pedidos del sistema
      </Typography>

      <Grid container spacing={2} sx={{ marginBottom: 3 }}>
        {[
          { t: "Total", v: counters.total, c: "var(--coco-primary)" },
          { t: "En proceso", v: counters.activos, c: "var(--coco-warning)" },
          { t: "Entregados", v: counters.entregados, c: "var(--coco-success)" },
          { t: "Cancelados", v: counters.cancelados, c: "var(--coco-danger)" },
        ].map((k) => (
          <Grid size={{ xs: 6, md: 3 }} key={k.t}>
            <Box className="coco-card" sx={{ padding: 2 }}>
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
              <Typography sx={{ fontSize: 28, fontWeight: 700, color: k.c }}>
                {k.v}
              </Typography>
            </Box>
          </Grid>
        ))}
      </Grid>

      <Box sx={{ display: "flex", gap: 2, marginBottom: 3, flexWrap: "wrap" }}>
        <TextField
          select
          size="small"
          label="Estado"
          value={filtroEstado}
          onChange={(e) => setFiltroEstado(e.target.value as any)}
          sx={{ minWidth: 200 }}
        >
          <MenuItem value="TODOS">Todos los estados</MenuItem>
          {ESTADOS_PEDIDO.map((k) => (
            <MenuItem key={k} value={k}>
              {ETIQUETAS_ESTADO_PEDIDO[k]}
            </MenuItem>
          ))}
        </TextField>
        <TextField
          select
          size="small"
          label="Sucursal"
          value={filtroSucursal}
          onChange={(e) =>
            setFiltroSucursal(
              e.target.value === "TODAS" ? "TODAS" : parseInt(e.target.value)
            )
          }
          sx={{ minWidth: 200 }}
        >
          <MenuItem value="TODAS">Todas las sucursales</MenuItem>
          {sucursales.map((s) => (
            <MenuItem key={s.idSucursal} value={s.idSucursal}>
              {etiquetaSucursal(s.nombre)}
            </MenuItem>
          ))}
        </TextField>
      </Box>

      {loading ? (
        <Box sx={{ textAlign: "center", padding: 6 }}>
          <CircularProgress />
        </Box>
      ) : visibles.length === 0 ? (
        <Box className="coco-card" sx={{ textAlign: "center", padding: 6 }}>
          <FontAwesomeIcon
            icon={faReceipt}
            style={{ fontSize: 48, color: "var(--coco-text-muted)" }}
          />
          <Typography sx={{ marginTop: 2 }}>No hay pedidos.</Typography>
        </Box>
      ) : (
        <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5 }}>
          {visibles.map((p) => {
            const c = colores[p.estado];
            const sucNombre =
              sucursalLabel.get(p.idSucursalDespacho) ??
              p.nombreSucursal ??
              `Suc. #${p.idSucursalDespacho}`;
            return (
              <Box
                key={p.idPedido}
                className="coco-card"
                sx={{
                  cursor: "pointer",
                  "&:hover": { borderColor: "var(--coco-primary)" },
                }}
                onClick={() => {
                  setSel(p);
                  setNuevoEstado(p.estado);
                }}
              >
                <Box sx={{ display: "flex", alignItems: "center", gap: 2, flexWrap: "wrap" }}>
                  <Box
                    sx={{
                      width: 44,
                      height: 44,
                      borderRadius: 1.5,
                      backgroundColor: "var(--coco-success-fill)",
                      color: "var(--coco-primary)",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                    }}
                  >
                    <FontAwesomeIcon
                      icon={p.tipoEntrega === "DOMICILIO" ? faTruckFast : faStore}
                    />
                  </Box>
                  <Box sx={{ flex: 1, minWidth: 180 }}>
                    <Typography sx={{ fontWeight: 600, fontSize: 14 }}>
                      Pedido #{p.idPedido}
                    </Typography>
                    <Typography
                      sx={{ fontSize: 11, color: "var(--coco-text-secondary)" }}
                    >
                      {p.fechaCreacion &&
                        new Date(p.fechaCreacion).toLocaleString("es-CO")}{" "}
                      · {sucNombre} · {p.detalles?.length ?? 0} productos
                    </Typography>
                  </Box>
                  <Chip
                    label={ETIQUETAS_ESTADO_PEDIDO[p.estado]}
                    size="small"
                    sx={{ backgroundColor: c.bg, color: c.fg, fontWeight: 600 }}
                  />
                  <Typography sx={{ fontWeight: 700, color: "var(--coco-primary)" }}>
                    ${Math.round(p.total).toLocaleString("es-CO")}
                  </Typography>
                </Box>
              </Box>
            );
          })}
        </Box>
      )}

      <Dialog open={!!sel} onClose={() => setSel(null)} maxWidth="sm" fullWidth>
        {sel && (
          <>
            <DialogTitle>Pedido #{sel.idPedido}</DialogTitle>
            <DialogContent dividers>
              {error && (
                <Alert severity="error" sx={{ marginBottom: 2 }}>
                  {error}
                </Alert>
              )}

              <Grid container spacing={2} sx={{ marginBottom: 2 }}>
                <Grid size={6}>
                  <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)" }}>
                    CLIENTE
                  </Typography>
                  <Typography sx={{ fontWeight: 600 }}>
                    {sel.nombreCliente ?? `Cliente #${sel.idCliente}`}
                  </Typography>
                </Grid>
                <Grid size={6}>
                  <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)" }}>
                    SUCURSAL
                  </Typography>
                  <Typography sx={{ fontWeight: 600 }}>
                    {sucursalLabel.get(sel.idSucursalDespacho) ??
                      sel.nombreSucursal ??
                      `#${sel.idSucursalDespacho}`}
                  </Typography>
                </Grid>
                <Grid size={6}>
                  <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)" }}>
                    ENTREGA
                  </Typography>
                  <Typography>
                    {sel.tipoEntrega === "DOMICILIO"
                      ? "Domicilio"
                      : "Recoger en sucursal"}
                  </Typography>
                </Grid>
                <Grid size={6}>
                  <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)" }}>
                    TOTAL
                  </Typography>
                  <Typography sx={{ fontWeight: 700, color: "var(--coco-primary)" }}>
                    ${Math.round(sel.total).toLocaleString("es-CO")}
                  </Typography>
                </Grid>
              </Grid>

              <Divider sx={{ marginBottom: 2 }} />

              <Typography
                sx={{ fontSize: 12, color: "var(--coco-text-secondary)", marginBottom: 1 }}
              >
                PRODUCTOS
              </Typography>
              <Box
                sx={{
                  display: "flex",
                  flexDirection: "column",
                  gap: 0.5,
                  marginBottom: 2,
                }}
              >
                {sel.detalles?.map((d) => (
                  <Box
                    key={d.idProducto}
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      fontSize: 13,
                    }}
                  >
                    <span>
                      <FontAwesomeIcon
                        icon={faBoxOpen}
                        style={{
                          marginRight: 8,
                          fontSize: 11,
                          color: "var(--coco-text-muted)",
                        }}
                      />
                      {d.cantidad}× {d.nombreProducto ?? `Producto #${d.idProducto}`}
                    </span>
                    <span style={{ fontWeight: 600 }}>
                      ${Math.round(d.subtotal).toLocaleString("es-CO")}
                    </span>
                  </Box>
                ))}
              </Box>

              <Divider sx={{ marginBottom: 2 }} />

              <Typography
                sx={{ fontSize: 12, color: "var(--coco-text-secondary)", marginBottom: 1 }}
              >
                CAMBIAR ESTADO
              </Typography>
              <TextField
                select
                fullWidth
                size="small"
                value={nuevoEstado}
                onChange={(e) => setNuevoEstado(e.target.value as EstadoPedido)}
                helperText={
                  nuevoEstado === "ENTREGADO"
                    ? "Al marcar ENTREGADO se generará Venta + Factura automáticamente."
                    : ""
                }
              >
                {ESTADOS_PEDIDO.map((k) => (
                  <MenuItem key={k} value={k}>
                    {ETIQUETAS_ESTADO_PEDIDO[k]}
                  </MenuItem>
                ))}
              </TextField>
            </DialogContent>
            <DialogActions sx={{ padding: 2 }}>
              <Button onClick={() => setSel(null)}>Cerrar</Button>
              <Button
                variant="contained"
                color="secondary"
                disabled={procesando || nuevoEstado === sel.estado}
                onClick={cambiar}
              >
                {procesando ? (
                  <CircularProgress size={20} color="inherit" />
                ) : (
                  "Actualizar estado"
                )}
              </Button>
            </DialogActions>
          </>
        )}
      </Dialog>
    </LayoutAdmin>
  );
};

export default PedidosAdmin;
