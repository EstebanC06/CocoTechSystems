/**
 * Página de gestión de pedidos para el empleado.
 *
 * Solo muestra los pedidos de SU sucursal y permite avanzar el estado
 * a través del flujo:
 *   RECIBIDO → PREPARANDO → LISTO_PARA_ENTREGA → EN_CAMINO → ENTREGADO
 * (o CANCELADO)
 */
import { useEffect, useState } from "react";
import {
  Box, Typography, Chip, Button, CircularProgress, TextField, MenuItem,
  Dialog, DialogTitle, DialogContent, DialogActions, Alert, Divider,
} from "@mui/material";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faReceipt, faTruckFast, faStore, faBoxOpen,
} from "@fortawesome/free-solid-svg-icons";
import LayoutEmpleado from "../../components/layout/LayoutEmpleado";
import {
  obtenerPedidosSucursal, cambiarEstadoPedido,
} from "../../services/pedido.service";
import { obtenerEmpleadoPorId } from "../../services/empleado.service";
import { useAuth } from "../../context/AuthContext";
import type { PedidoDTO, EstadoPedido } from "../../types";

const colores: Record<EstadoPedido, { bg: string; fg: string; label: string }> = {
  RECIBIDO: { bg: "var(--coco-info-fill)", fg: "var(--coco-info)", label: "Recibido" },
  PREPARANDO: { bg: "var(--coco-warning-fill)", fg: "var(--coco-warning)", label: "Preparando" },
  LISTO_PARA_ENTREGA: { bg: "var(--coco-success-fill)", fg: "var(--coco-success)", label: "Listo" },
  EN_CAMINO: { bg: "var(--coco-warning-fill)", fg: "var(--coco-warning)", label: "En camino" },
  ENTREGADO: { bg: "var(--coco-success-fill)", fg: "var(--coco-success)", label: "Entregado" },
  CANCELADO: { bg: "var(--coco-danger-fill)", fg: "var(--coco-danger)", label: "Cancelado" },
};

/** Devuelve el siguiente estado según el flujo y tipo de entrega. */
const siguienteEstado = (
  estado: EstadoPedido,
  tipoEntrega: PedidoDTO["tipoEntrega"]
): EstadoPedido | null => {
  if (estado === "RECIBIDO") return "PREPARANDO";
  if (estado === "PREPARANDO") {
    return tipoEntrega === "DOMICILIO" ? "EN_CAMINO" : "LISTO_PARA_ENTREGA";
  }
  if (estado === "EN_CAMINO" || estado === "LISTO_PARA_ENTREGA") return "ENTREGADO";
  return null;
};

const PedidosEmpleado = () => {
  const { sesion } = useAuth();
  const [pedidos, setPedidos] = useState<PedidoDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [filtro, setFiltro] = useState<EstadoPedido | "TODOS">("TODOS");
  const [seleccionado, setSeleccionado] = useState<PedidoDTO | null>(null);
  const [actualizando, setActualizando] = useState(false);
  const [error, setError] = useState("");

  const cargar = async () => {
    if (!sesion) return;
    setLoading(true);
    try {
      const emp = await obtenerEmpleadoPorId(sesion.id);
      const data = await obtenerPedidosSucursal(emp.idSucursal);
      setPedidos(data ?? []);
    } catch {
      setPedidos([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { cargar(); }, [sesion]);

  const visibles = filtro === "TODOS" ? pedidos : pedidos.filter((p) => p.estado === filtro);

  const avanzar = async (nuevo: EstadoPedido) => {
    if (!seleccionado?.idPedido) return;
    setActualizando(true);
    setError("");
    try {
      await cambiarEstadoPedido(seleccionado.idPedido, nuevo);
      setSeleccionado(null);
      cargar();
    } catch (e: any) {
      setError(e?.response?.data?.message ?? "Error al actualizar.");
    } finally {
      setActualizando(false);
    }
  };

  return (
    <LayoutEmpleado>
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 3, flexWrap: "wrap", gap: 2 }}>
        <Box>
          <Typography sx={{ fontSize: 26, fontWeight: 600 }}>Pedidos online</Typography>
          <Typography sx={{ color: "var(--coco-text-secondary)" }}>
            Gestiona los pedidos asignados a tu sucursal
          </Typography>
        </Box>
        <TextField
          select size="small" label="Filtrar"
          value={filtro} onChange={(e) => setFiltro(e.target.value as any)}
          sx={{ minWidth: 180 }}
        >
          <MenuItem value="TODOS">Todos</MenuItem>
          <MenuItem value="RECIBIDO">Recibidos</MenuItem>
          <MenuItem value="PREPARANDO">Preparando</MenuItem>
          <MenuItem value="LISTO_PARA_ENTREGA">Listos</MenuItem>
          <MenuItem value="EN_CAMINO">En camino</MenuItem>
          <MenuItem value="ENTREGADO">Entregados</MenuItem>
          <MenuItem value="CANCELADO">Cancelados</MenuItem>
        </TextField>
      </Box>

      {loading ? (
        <Box sx={{ textAlign: "center", padding: 6 }}><CircularProgress /></Box>
      ) : visibles.length === 0 ? (
        <Box className="coco-card" sx={{ textAlign: "center", padding: 6 }}>
          <FontAwesomeIcon icon={faReceipt} style={{ fontSize: 48, color: "var(--coco-text-muted)" }} />
          <Typography sx={{ marginTop: 2 }}>No hay pedidos en este filtro.</Typography>
        </Box>
      ) : (
        <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5 }}>
          {visibles.map((p) => {
            const c = colores[p.estado];
            return (
              <Box
                key={p.idPedido}
                className="coco-card"
                sx={{ cursor: "pointer", "&:hover": { borderColor: "var(--coco-primary)" } }}
                onClick={() => setSeleccionado(p)}
              >
                <Box sx={{ display: "flex", alignItems: "center", gap: 2, flexWrap: "wrap" }}>
                  <Box sx={{
                    width: 44, height: 44, borderRadius: 1.5,
                    backgroundColor: "var(--coco-success-fill)", color: "var(--coco-primary)",
                    display: "flex", alignItems: "center", justifyContent: "center",
                  }}>
                    <FontAwesomeIcon icon={p.tipoEntrega === "DOMICILIO" ? faTruckFast : faStore} />
                  </Box>
                  <Box sx={{ flex: 1, minWidth: 180 }}>
                    <Typography sx={{ fontWeight: 600, fontSize: 14 }}>Pedido #{p.idPedido}</Typography>
                    <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)" }}>
                      {p.fechaCreacion && new Date(p.fechaCreacion).toLocaleString("es-CO")} · {p.detalles?.length ?? 0} productos
                    </Typography>
                  </Box>
                  <Chip label={c.label} size="small" sx={{ backgroundColor: c.bg, color: c.fg, fontWeight: 600 }} />
                  <Typography sx={{ fontWeight: 700, color: "var(--coco-primary)" }}>
                    ${Math.round(p.total).toLocaleString("es-CO")}
                  </Typography>
                </Box>
              </Box>
            );
          })}
        </Box>
      )}

      {/* Modal detalle + cambio de estado */}
      <Dialog open={!!seleccionado} onClose={() => setSeleccionado(null)} maxWidth="sm" fullWidth>
        {seleccionado && (
          <>
            <DialogTitle>Pedido #{seleccionado.idPedido}</DialogTitle>
            <DialogContent dividers>
              {error && <Alert severity="error" sx={{ marginBottom: 2 }}>{error}</Alert>}

              <Box sx={{ display: "flex", justifyContent: "space-between", marginBottom: 2 }}>
                <Box>
                  <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)" }}>ESTADO</Typography>
                  <Chip
                    label={colores[seleccionado.estado].label}
                    sx={{
                      backgroundColor: colores[seleccionado.estado].bg,
                      color: colores[seleccionado.estado].fg,
                      fontWeight: 600, marginTop: 0.5,
                    }}
                  />
                </Box>
                <Box sx={{ textAlign: "right" }}>
                  <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)" }}>TOTAL</Typography>
                  <Typography sx={{ fontWeight: 700, fontSize: 18, color: "var(--coco-primary)" }}>
                    ${Math.round(seleccionado.total).toLocaleString("es-CO")}
                  </Typography>
                </Box>
              </Box>

              <Divider sx={{ marginBottom: 2 }} />

              <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)", marginBottom: 0.5 }}>
                ENTREGA
              </Typography>
              <Typography sx={{ fontSize: 14, fontWeight: 500, marginBottom: 2 }}>
                {seleccionado.tipoEntrega === "DOMICILIO" ? "Domicilio" : "Recoger en sucursal"}
                {seleccionado.direccionEnvio && (
                  <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)", marginTop: 0.5 }}>
                    {seleccionado.direccionEnvio}, {seleccionado.barrioEnvio}, {seleccionado.ciudadEnvio}
                  </Typography>
                )}
              </Typography>

              {seleccionado.notasCliente && (
                <>
                  <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)", marginBottom: 0.5 }}>
                    NOTAS DEL CLIENTE
                  </Typography>
                  <Typography sx={{ fontSize: 13, fontStyle: "italic", marginBottom: 2 }}>
                    {seleccionado.notasCliente}
                  </Typography>
                </>
              )}

              <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)", marginBottom: 1 }}>
                PRODUCTOS
              </Typography>
              <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
                {seleccionado.detalles?.map((d) => (
                  <Box key={d.idProducto} sx={{ display: "flex", justifyContent: "space-between", fontSize: 13 }}>
                    <span>
                      <FontAwesomeIcon icon={faBoxOpen} style={{ marginRight: 8, fontSize: 11, color: "var(--coco-text-muted)" }} />
                      {d.cantidad}× {d.nombreProducto ?? `Producto #${d.idProducto}`}
                    </span>
                    <span style={{ fontWeight: 600 }}>${Math.round(d.subtotal).toLocaleString("es-CO")}</span>
                  </Box>
                ))}
              </Box>
            </DialogContent>
            <DialogActions sx={{ padding: 2, gap: 1, flexWrap: "wrap" }}>
              <Button onClick={() => setSeleccionado(null)}>Cerrar</Button>
              {seleccionado.estado !== "CANCELADO" && seleccionado.estado !== "ENTREGADO" && (
                <>
                  {seleccionado.estado === "RECIBIDO" && (
                    <Button
                      variant="outlined" color="error" disabled={actualizando}
                      onClick={() => avanzar("CANCELADO")}
                    >
                      Cancelar
                    </Button>
                  )}
                  {siguienteEstado(seleccionado.estado, seleccionado.tipoEntrega) && (
                    <Button
                      variant="contained" color="secondary" disabled={actualizando}
                      onClick={() => avanzar(siguienteEstado(seleccionado.estado, seleccionado.tipoEntrega)!)}
                    >
                      {actualizando ? <CircularProgress size={20} color="inherit" /> : `Avanzar a ${colores[siguienteEstado(seleccionado.estado, seleccionado.tipoEntrega)!].label}`}
                    </Button>
                  )}
                </>
              )}
            </DialogActions>
          </>
        )}
      </Dialog>
    </LayoutEmpleado>
  );
};

export default PedidosEmpleado;
