/**
 * Detalle de un pedido del cliente con timeline visual del estado.
 */
import { useEffect, useState } from "react";
import {
  Box, Grid, Typography, Button, Chip, CircularProgress, Divider, Stepper, Step, StepLabel,
} from "@mui/material";
import { useParams, useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faArrowLeft, faReceipt, faTruckFast, faStore, faBoxOpen,
} from "@fortawesome/free-solid-svg-icons";
import LayoutEcommerce from "../../components/layout/LayoutEcommerce";
import { obtenerPedidoPorId, cancelarPedido } from "../../services/pedido.service";
import type { PedidoDTO, EstadoPedido } from "../../types";

const pasosTimeline: EstadoPedido[] = [
  "RECIBIDO", "PREPARANDO", "LISTO_PARA_ENTREGA", "EN_CAMINO", "ENTREGADO",
];
const labels: Record<EstadoPedido, string> = {
  RECIBIDO: "Recibido", PREPARANDO: "Preparando",
  LISTO_PARA_ENTREGA: "Listo", EN_CAMINO: "En camino",
  ENTREGADO: "Entregado", CANCELADO: "Cancelado",
};

const DetallePedidoCliente = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [pedido, setPedido] = useState<PedidoDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [cancelando, setCancelando] = useState(false);

  const cargar = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const data = await obtenerPedidoPorId(parseInt(id));
      setPedido(data);
    } catch {
      setPedido(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { cargar(); }, [id]);

  const handleCancelar = async () => {
    if (!pedido?.idPedido) return;
    if (!confirm("¿Seguro que quieres cancelar este pedido?")) return;
    setCancelando(true);
    try {
      await cancelarPedido(pedido.idPedido);
      cargar();
    } catch (e: any) {
      alert(e?.response?.data?.message ?? "No se pudo cancelar el pedido.");
    } finally {
      setCancelando(false);
    }
  };

  if (loading) {
    return <LayoutEcommerce><Box sx={{ textAlign: "center", padding: 8 }}><CircularProgress /></Box></LayoutEcommerce>;
  }

  if (!pedido) {
    return (
      <LayoutEcommerce>
        <Box sx={{ textAlign: "center", padding: 8 }}>
          <Typography>Pedido no encontrado.</Typography>
          <Button sx={{ marginTop: 2 }} onClick={() => navigate("/cliente/pedidos")}>Volver</Button>
        </Box>
      </LayoutEcommerce>
    );
  }

  const cancelado = pedido.estado === "CANCELADO";
  const pasoActual = cancelado ? -1 : pasosTimeline.indexOf(pedido.estado);
  const puedeCancelar = pedido.estado === "RECIBIDO";

  return (
    <LayoutEcommerce>
      <Button
        startIcon={<FontAwesomeIcon icon={faArrowLeft} />}
        onClick={() => navigate("/cliente/pedidos")}
        sx={{ marginBottom: 2, color: "var(--coco-text-secondary)" }}
      >
        Mis pedidos
      </Button>

      <Box className="coco-card" sx={{ padding: 0, overflow: "hidden", marginBottom: 3 }}>
        {/* Cabecera */}
        <Box
          sx={{
            background: cancelado
              ? "linear-gradient(135deg, var(--coco-danger) 0%, #6E1F1F 100%)"
              : "linear-gradient(135deg, var(--coco-primary) 0%, var(--coco-primary-dark) 100%)",
            color: "#FFFFFF",
            padding: 3,
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            flexWrap: "wrap",
            gap: 2,
          }}
        >
          <Box>
            <Typography sx={{ fontSize: 11, opacity: 0.8, textTransform: "uppercase", letterSpacing: 1 }}>
              Pedido
            </Typography>
            <Typography variant="h4" sx={{ fontWeight: 700, marginTop: 0.5 }}>#{pedido.idPedido}</Typography>
            <Typography sx={{ fontSize: 13, opacity: 0.9, marginTop: 0.5 }}>
              {pedido.fechaCreacion && new Date(pedido.fechaCreacion).toLocaleString("es-CO")}
            </Typography>
          </Box>
          <Box sx={{ textAlign: "right" }}>
            <Typography sx={{ fontSize: 11, opacity: 0.8 }}>Total</Typography>
            <Typography sx={{ fontSize: 26, fontWeight: 700 }}>
              ${Math.round(pedido.total).toLocaleString("es-CO")}
            </Typography>
          </Box>
        </Box>

        {/* Timeline */}
        <Box sx={{ padding: 3, borderBottom: "1px solid var(--coco-border)" }}>
          {cancelado ? (
            <Chip
              label="Pedido cancelado"
              sx={{ backgroundColor: "var(--coco-danger-fill)", color: "var(--coco-danger)", fontWeight: 600 }}
            />
          ) : (
            <Stepper activeStep={pasoActual} alternativeLabel>
              {pasosTimeline.map((p) => <Step key={p}><StepLabel>{labels[p]}</StepLabel></Step>)}
            </Stepper>
          )}
        </Box>

        {/* Info entrega + pago */}
        <Box sx={{ padding: 3, display: "grid", gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" }, gap: 3, borderBottom: "1px solid var(--coco-border)" }}>
          <Box>
            <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)", textTransform: "uppercase", letterSpacing: 0.5, marginBottom: 0.5 }}>
              Entrega
            </Typography>
            <Box sx={{ display: "flex", alignItems: "center", gap: 1, fontWeight: 600 }}>
              <FontAwesomeIcon icon={pedido.tipoEntrega === "DOMICILIO" ? faTruckFast : faStore} style={{ color: "var(--coco-primary)" }} />
              {pedido.tipoEntrega === "DOMICILIO" ? "Domicilio" : "Recoger en sucursal"}
            </Box>
            {pedido.tipoEntrega === "DOMICILIO" && pedido.direccionEnvio && (
              <Typography sx={{ fontSize: 13, color: "var(--coco-text-secondary)", marginTop: 0.5 }}>
                {pedido.direccionEnvio}, {pedido.barrioEnvio}, {pedido.ciudadEnvio}
              </Typography>
            )}
            {pedido.nombreSucursal && (
              <Typography sx={{ fontSize: 13, color: "var(--coco-text-secondary)", marginTop: 0.5 }}>
                {pedido.nombreSucursal}
              </Typography>
            )}
          </Box>
          <Box>
            <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)", textTransform: "uppercase", letterSpacing: 0.5, marginBottom: 0.5 }}>
              Pago
            </Typography>
            <Typography sx={{ fontWeight: 600 }}>
              {pedido.metodoPago === "EFECTIVO_CONTRA_ENTREGA" && "Efectivo contra entrega"}
              {pedido.metodoPago === "TARJETA_SIMULADA" && "Tarjeta"}
              {pedido.metodoPago === "PSE_SIMULADO" && "PSE"}
            </Typography>
          </Box>
        </Box>

        {/* Productos */}
        <Box sx={{ padding: 3 }}>
          <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)", textTransform: "uppercase", letterSpacing: 0.5, marginBottom: 1.5 }}>
            Productos ({pedido.detalles?.length ?? 0})
          </Typography>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5 }}>
            {pedido.detalles?.map((d) => (
              <Box key={d.idProducto} sx={{ display: "flex", gap: 1.5, alignItems: "center" }}>
                <Box sx={{
                  width: 50, height: 50, borderRadius: 1, backgroundColor: "var(--coco-success-fill)",
                  display: "flex", alignItems: "center", justifyContent: "center", color: "var(--coco-primary)",
                  flexShrink: 0,
                }}>
                  {d.imagenUrl ? (
                    <img src={d.imagenUrl} alt={d.nombreProducto} style={{ width: "100%", height: "100%", objectFit: "cover", borderRadius: 4 }} />
                  ) : (
                    <FontAwesomeIcon icon={faBoxOpen} />
                  )}
                </Box>
                <Box sx={{ flex: 1 }}>
                  <Typography sx={{ fontSize: 13, fontWeight: 500 }}>
                    {d.nombreProducto}
                    {d.promocion && (
                      <Chip label={`-${d.porcentajeDescuento}%`} size="small" sx={{
                        marginLeft: 1, height: 16, fontSize: 9,
                        backgroundColor: "var(--coco-warning-fill)", color: "var(--coco-warning)",
                      }} />
                    )}
                  </Typography>
                  <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)" }}>
                    {d.cantidad} × ${d.precioUnitario.toLocaleString("es-CO")}
                  </Typography>
                </Box>
                <Typography sx={{ fontWeight: 600, fontSize: 14 }}>
                  ${Math.round(d.subtotal).toLocaleString("es-CO")}
                </Typography>
              </Box>
            ))}
          </Box>

          <Divider sx={{ marginY: 2 }} />

          <Box sx={{ marginLeft: "auto", maxWidth: 280 }}>
            <Box sx={{ display: "flex", justifyContent: "space-between", fontSize: 13, padding: "3px 0" }}>
              <span style={{ color: "var(--coco-text-secondary)" }}>Subtotal</span>
              <span>${Math.round(pedido.subtotal).toLocaleString("es-CO")}</span>
            </Box>
            <Box sx={{ display: "flex", justifyContent: "space-between", fontSize: 13, padding: "3px 0" }}>
              <span style={{ color: "var(--coco-text-secondary)" }}>IVA</span>
              <span>${Math.round(pedido.iva).toLocaleString("es-CO")}</span>
            </Box>
            <Box sx={{ display: "flex", justifyContent: "space-between", fontSize: 13, padding: "3px 0" }}>
              <span style={{ color: "var(--coco-text-secondary)" }}>Envío</span>
              <span>{pedido.costoEnvio > 0 ? `$${pedido.costoEnvio.toLocaleString("es-CO")}` : "Gratis"}</span>
            </Box>
            <Divider sx={{ marginY: 1 }} />
            <Box sx={{ display: "flex", justifyContent: "space-between", fontWeight: 700 }}>
              <span>Total</span>
              <span style={{ color: "var(--coco-primary)" }}>
                ${Math.round(pedido.total).toLocaleString("es-CO")}
              </span>
            </Box>
          </Box>

          {puedeCancelar && (
            <Box sx={{ marginTop: 3, textAlign: "right" }}>
              <Button variant="outlined" color="error" disabled={cancelando} onClick={handleCancelar}>
                {cancelando ? "Cancelando..." : "Cancelar pedido"}
              </Button>
            </Box>
          )}
        </Box>
      </Box>
    </LayoutEcommerce>
  );
};

export default DetallePedidoCliente;
