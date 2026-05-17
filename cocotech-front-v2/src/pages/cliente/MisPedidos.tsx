/**
 * Lista de pedidos del cliente con tracking de estado.
 */
import { useEffect, useState } from "react";
import { Box, Typography, Chip, Button, CircularProgress } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBoxOpen, faReceipt } from "@fortawesome/free-solid-svg-icons";
import LayoutEcommerce from "../../components/layout/LayoutEcommerce";
import { obtenerPedidosCliente } from "../../services/pedido.service";
import { useAuth } from "../../context/AuthContext";
import type { PedidoDTO, EstadoPedido } from "../../types";

const coloresEstado: Record<EstadoPedido, { bg: string; fg: string; label: string }> = {
  RECIBIDO: { bg: "var(--coco-info-fill)", fg: "var(--coco-info)", label: "Recibido" },
  PREPARANDO: { bg: "var(--coco-warning-fill)", fg: "var(--coco-warning)", label: "Preparando" },
  LISTO_PARA_ENTREGA: { bg: "var(--coco-success-fill)", fg: "var(--coco-success)", label: "Listo" },
  EN_CAMINO: { bg: "var(--coco-warning-fill)", fg: "var(--coco-warning)", label: "En camino" },
  ENTREGADO: { bg: "var(--coco-success-fill)", fg: "var(--coco-success)", label: "Entregado" },
  CANCELADO: { bg: "var(--coco-danger-fill)", fg: "var(--coco-danger)", label: "Cancelado" },
};

const MisPedidos = () => {
  const navigate = useNavigate();
  const { sesion } = useAuth();
  const [pedidos, setPedidos] = useState<PedidoDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const cargar = async () => {
      if (!sesion) return;
      try {
        const data = await obtenerPedidosCliente(sesion.id);
        setPedidos(data ?? []);
      } catch {
        setPedidos([]);
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, [sesion]);

  return (
    <LayoutEcommerce>
      <Typography sx={{ fontSize: 26, fontWeight: 600, marginBottom: 0.5 }}>Mis pedidos</Typography>
      <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 3 }}>
        Historial y estado de tus compras
      </Typography>

      {loading ? (
        <Box sx={{ textAlign: "center", padding: 6 }}><CircularProgress /></Box>
      ) : pedidos.length === 0 ? (
        <Box className="coco-card" sx={{ textAlign: "center", padding: 6 }}>
          <FontAwesomeIcon icon={faBoxOpen} style={{ fontSize: 56, color: "var(--coco-text-muted)" }} />
          <Typography sx={{ marginTop: 2, fontSize: 16, fontWeight: 600 }}>
            Aún no has hecho ningún pedido
          </Typography>
          <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 2, fontSize: 13 }}>
            Cuando hagas tu primera compra, aparecerá aquí.
          </Typography>
          <Button variant="contained" color="secondary" onClick={() => navigate("/productos")}>
            Explorar productos
          </Button>
        </Box>
      ) : (
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          {pedidos.map((p) => {
            const c = coloresEstado[p.estado];
            return (
              <Box
                key={p.idPedido}
                className="coco-card"
                sx={{ cursor: "pointer", "&:hover": { borderColor: "var(--coco-primary)" } }}
                onClick={() => navigate(`/cliente/pedido/${p.idPedido}`)}
              >
                <Box sx={{ display: "flex", alignItems: "center", gap: 2, flexWrap: "wrap" }}>
                  <Box
                    sx={{
                      width: 52, height: 52, borderRadius: 2,
                      backgroundColor: "var(--coco-success-fill)",
                      color: "var(--coco-primary)",
                      display: "flex", alignItems: "center", justifyContent: "center", fontSize: 22,
                    }}
                  >
                    <FontAwesomeIcon icon={faReceipt} />
                  </Box>
                  <Box sx={{ flex: 1, minWidth: 200 }}>
                    <Typography sx={{ fontWeight: 600 }}>Pedido #{p.idPedido}</Typography>
                    <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)" }}>
                      {p.fechaCreacion ? new Date(p.fechaCreacion).toLocaleString("es-CO") : ""} · {p.detalles?.length ?? 0} productos
                    </Typography>
                  </Box>
                  <Chip
                    label={c.label}
                    size="small"
                    sx={{ backgroundColor: c.bg, color: c.fg, fontWeight: 600 }}
                  />
                  <Typography sx={{ fontWeight: 700, fontSize: 16, color: "var(--coco-primary)" }}>
                    ${Math.round(p.total).toLocaleString("es-CO")}
                  </Typography>
                </Box>
              </Box>
            );
          })}
        </Box>
      )}
    </LayoutEcommerce>
  );
};

export default MisPedidos;
