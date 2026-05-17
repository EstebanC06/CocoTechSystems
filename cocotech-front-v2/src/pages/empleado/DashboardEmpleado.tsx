/**
 * Dashboard del empleado.
 * Muestra KPIs operativos: pedidos pendientes en su sucursal, stock bajo, etc.
 *
 * Hace polling cada 30 segundos para mantener los KPIs actualizados
 * sin que el usuario tenga que recargar manualmente.
 */
import { useEffect, useState } from "react";
import { Box, Grid, Typography, CircularProgress, Chip, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faTruckFast, faBoxOpen, faClock, faCheckDouble, faArrowRight,
} from "@fortawesome/free-solid-svg-icons";
import LayoutEmpleado from "../../components/layout/LayoutEmpleado";
import { obtenerEmpleadoPorId } from "../../services/empleado.service";
import { obtenerPedidosSucursal } from "../../services/pedido.service";
import { obtenerProductos } from "../../services/producto.service";
import { useAuth } from "../../context/AuthContext";
import type { PedidoDTO, ProductoDTO } from "../../types";

const DashboardEmpleado = () => {
  const navigate = useNavigate();
  const { sesion } = useAuth();
  const [loading, setLoading] = useState(true);
  const [pedidos, setPedidos] = useState<PedidoDTO[]>([]);
  const [stockBajo, setStockBajo] = useState<ProductoDTO[]>([]);
  const [nombreSucursal, setNombreSucursal] = useState("");

  useEffect(() => {
    const cargar = async () => {
      if (!sesion) return;
      try {
        const empleado = await obtenerEmpleadoPorId(sesion.id);
        const [peds, prods] = await Promise.all([
          obtenerPedidosSucursal(empleado.idSucursal).catch(() => []),
          obtenerProductos().catch(() => []),
        ]);
        setPedidos(peds);
        setStockBajo(prods.filter((p) => p.stock < 10).slice(0, 5));
        setNombreSucursal(`Sucursal #${empleado.idSucursal}`);
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };

    // Carga inicial + polling cada 30 segundos.
    cargar();
    const id = setInterval(cargar, 30_000);
    return () => clearInterval(id);
  }, [sesion]);

  if (loading) {
    return <LayoutEmpleado><Box sx={{ textAlign: "center", padding: 8 }}><CircularProgress /></Box></LayoutEmpleado>;
  }

  const recibidos = pedidos.filter((p) => p.estado === "RECIBIDO").length;
  const preparando = pedidos.filter((p) => p.estado === "PREPARANDO").length;
  const enCamino = pedidos.filter((p) => p.estado === "EN_CAMINO" || p.estado === "LISTO_PARA_ENTREGA").length;
  const entregadosHoy = pedidos.filter((p) => {
    if (p.estado !== "ENTREGADO" || !p.fechaActualizacion) return false;
    const hoy = new Date().toDateString();
    return new Date(p.fechaActualizacion).toDateString() === hoy;
  }).length;

  const cards = [
    { t: "Recibidos", v: recibidos, i: faClock, c: "var(--coco-info)", bg: "var(--coco-info-fill)" },
    { t: "Preparando", v: preparando, i: faBoxOpen, c: "var(--coco-warning)", bg: "var(--coco-warning-fill)" },
    { t: "En camino", v: enCamino, i: faTruckFast, c: "var(--coco-primary)", bg: "var(--coco-success-fill)" },
    { t: "Entregados hoy", v: entregadosHoy, i: faCheckDouble, c: "var(--coco-success)", bg: "var(--coco-success-fill)" },
  ];

  return (
    <LayoutEmpleado>
      <Typography sx={{ fontSize: 26, fontWeight: 600 }}>Dashboard</Typography>
      <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 3 }}>
        Operación de {nombreSucursal} — {new Date().toLocaleDateString("es-CO", { dateStyle: "full" })}
      </Typography>

      <Grid container spacing={2} sx={{ marginBottom: 3 }}>
        {cards.map((c) => (
          <Grid size={{ xs: 6, md: 3 }} key={c.t}>
            <Box className="coco-card" sx={{ padding: 2 }}>
              <Box sx={{
                width: 40, height: 40, borderRadius: 1.5,
                backgroundColor: c.bg, color: c.c,
                display: "flex", alignItems: "center", justifyContent: "center",
                marginBottom: 1.5,
              }}>
                <FontAwesomeIcon icon={c.i} />
              </Box>
              <Typography sx={{ fontSize: 11, color: "var(--coco-text-secondary)", textTransform: "uppercase", letterSpacing: 0.5 }}>
                {c.t}
              </Typography>
              <Typography sx={{ fontSize: 28, fontWeight: 700 }}>{c.v}</Typography>
            </Box>
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={2}>
        <Grid size={{ xs: 12, md: 8 }}>
          <Box className="coco-card">
            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 2 }}>
              <Typography sx={{ fontWeight: 600 }}>Pedidos por atender</Typography>
              <Button size="small" endIcon={<FontAwesomeIcon icon={faArrowRight} />} onClick={() => navigate("/empleado/pedidos")}>
                Ver todos
              </Button>
            </Box>
            {pedidos.filter((p) => p.estado === "RECIBIDO" || p.estado === "PREPARANDO").length === 0 ? (
              <Typography sx={{ color: "var(--coco-text-secondary)", fontSize: 13, textAlign: "center", padding: 3 }}>
                No hay pedidos pendientes 🎉
              </Typography>
            ) : (
              <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
                {pedidos
                  .filter((p) => p.estado === "RECIBIDO" || p.estado === "PREPARANDO")
                  .slice(0, 5)
                  .map((p) => (
                    <Box
                      key={p.idPedido}
                      sx={{
                        display: "flex", alignItems: "center", gap: 2, padding: 1.5,
                        backgroundColor: "var(--coco-surface-2)", borderRadius: 1, cursor: "pointer",
                        "&:hover": { backgroundColor: "var(--coco-success-fill)" },
                      }}
                      onClick={() => navigate("/empleado/pedidos")}
                    >
                      <Typography sx={{ fontWeight: 600, fontSize: 14 }}>#{p.idPedido}</Typography>
                      <Box sx={{ flex: 1 }}>
                        <Typography sx={{ fontSize: 13 }}>
                          {p.detalles?.length ?? 0} productos · ${Math.round(p.total).toLocaleString("es-CO")}
                        </Typography>
                      </Box>
                      <Chip label={p.estado === "RECIBIDO" ? "Recibido" : "Preparando"} size="small" />
                    </Box>
                  ))}
              </Box>
            )}
          </Box>
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Box className="coco-card">
            <Typography sx={{ fontWeight: 600, marginBottom: 2 }}>Stock bajo</Typography>
            {stockBajo.length === 0 ? (
              <Typography sx={{ color: "var(--coco-text-secondary)", fontSize: 13 }}>
                Todo el stock está en niveles normales.
              </Typography>
            ) : (
              <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
                {stockBajo.map((p) => (
                  <Box key={p.idProducto} sx={{ display: "flex", justifyContent: "space-between", fontSize: 13 }}>
                    <span>{p.nombre}</span>
                    <Chip label={`${p.stock}`} size="small" sx={{ backgroundColor: "var(--coco-warning-fill)", color: "var(--coco-warning)" }} />
                  </Box>
                ))}
              </Box>
            )}
          </Box>
        </Grid>
      </Grid>
    </LayoutEmpleado>
  );
};

export default DashboardEmpleado;
