/**
 * Dashboard del administrador.
 *
 * Cambios respecto a la versión anterior:
 *  - KPI "Ventas" ahora cuenta pedidos en estado ENTREGADO (no todas las
 *    ventas crudas), alineado con la definición de "venta completada".
 *  - KPI "Ingreso total" suma el total de los pedidos ENTREGADOS del
 *    último mes (en lugar de todas las facturas).
 *  - Gráfica "Ingreso por sucursal" tiene un combobox que permite
 *    seleccionar 1 sucursal y ver su desglose DIARIO del último mes,
 *    en lugar de mostrar siempre todas las sucursales agregadas.
 *  - "Top productos" ahora se renderiza como BarChart horizontal en vez
 *    de PieChart (más legible cuando hay 5+ productos).
 *  - Sucursales mostradas con etiquetas legibles del enum.
 *  - Se quitaron las tendencias hardcodeadas ("+5 esta semana", etc.).
 */
import { useEffect, useMemo, useState } from "react";
import {
  Box,
  Grid,
  Typography,
  CircularProgress,
  TextField,
  MenuItem,
} from "@mui/material";
import { motion } from "framer-motion";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from "recharts";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faBoxesStacked,
  faUsers,
  faFileInvoiceDollar,
  faReceipt,
} from "@fortawesome/free-solid-svg-icons";
import LayoutAdmin from "../../components/layout/LayoutAdmin";
import { COLORES_COCOTECH } from "../../context/TemaContext";
import { contarProductos } from "../../services/producto.service";
import { contarEmpleados } from "../../services/empleado.service";
import { obtenerTodosPedidos } from "../../services/pedido.service";
import { obtenerSucursales } from "../../services/sucursal.service";
import type { PedidoDTO, SucursalDTO } from "../../types";
import { etiquetaSucursal } from "../../utils/etiquetas";

interface Metrica {
  label: string;
  valor: string | number;
  icono: typeof faBoxesStacked;
  color: string;
}

const DashboardAdmin = () => {
  const [loading, setLoading] = useState(true);
  const [pedidos, setPedidos] = useState<PedidoDTO[]>([]);
  const [sucursales, setSucursales] = useState<SucursalDTO[]>([]);
  const [totalProductos, setTotalProductos] = useState(0);
  const [totalEmpleados, setTotalEmpleados] = useState(0);
  const [sucursalElegida, setSucursalElegida] = useState<number | "TODAS">("TODAS");

  // Cargar todo en paralelo
  useEffect(() => {
    (async () => {
      try {
        const [prods, emps, peds, sucs] = await Promise.allSettled([
          contarProductos(),
          contarEmpleados(),
          obtenerTodosPedidos(),
          obtenerSucursales(),
        ]);
        setTotalProductos(prods.status === "fulfilled" ? prods.value : 0);
        setTotalEmpleados(emps.status === "fulfilled" ? emps.value : 0);
        setPedidos(peds.status === "fulfilled" ? peds.value : []);
        setSucursales(sucs.status === "fulfilled" ? sucs.value : []);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  // Rango del último mes
  const inicioRango = useMemo(() => {
    const d = new Date();
    d.setMonth(d.getMonth() - 1);
    d.setHours(0, 0, 0, 0);
    return d;
  }, []);

  // Pedidos ENTREGADOS del último mes (fuente de verdad para ventas+ingreso)
  const entregadosUltimoMes = useMemo(
    () =>
      pedidos.filter((p) => {
        if (p.estado !== "ENTREGADO") return false;
        const f = new Date(p.fechaActualizacion ?? p.fechaCreacion ?? 0);
        return f >= inicioRango;
      }),
    [pedidos, inicioRango]
  );

  const metricas: Metrica[] = useMemo(() => {
    const ventas = entregadosUltimoMes.length;
    const ingreso = entregadosUltimoMes.reduce((acc, p) => acc + (p.total ?? 0), 0);
    return [
      {
        label: "Productos",
        valor: totalProductos,
        icono: faBoxesStacked,
        color: COLORES_COCOTECH.primary,
      },
      {
        label: "Empleados",
        valor: totalEmpleados,
        icono: faUsers,
        color: COLORES_COCOTECH.secondary,
      },
      {
        label: "Ventas (mes)",
        valor: ventas,
        icono: faReceipt,
        color: COLORES_COCOTECH.blue,
      },
      {
        label: "Ingreso (mes)",
        valor:
          ingreso >= 1_000_000
            ? `$${(ingreso / 1_000_000).toFixed(1)}M`
            : `$${Math.round(ingreso).toLocaleString("es-CO")}`,
        icono: faFileInvoiceDollar,
        color: COLORES_COCOTECH.amber,
      },
    ];
  }, [totalProductos, totalEmpleados, entregadosUltimoMes]);

  // Gráfica de ingreso: si "TODAS", agregar por sucursal;
  // si una sucursal específica, desglose diario del último mes.
  const datosIngreso = useMemo(() => {
    if (sucursalElegida === "TODAS") {
      const acumulador = new Map<number, number>();
      entregadosUltimoMes.forEach((p) => {
        acumulador.set(
          p.idSucursalDespacho,
          (acumulador.get(p.idSucursalDespacho) ?? 0) + (p.total ?? 0)
        );
      });
      return sucursales.map((s) => ({
        etiqueta: etiquetaSucursal(s.nombre),
        ingreso: Math.round(acumulador.get(s.idSucursal!) ?? 0),
      }));
    }
    // Sucursal específica: agregar por día
    const filtrados = entregadosUltimoMes.filter(
      (p) => p.idSucursalDespacho === sucursalElegida
    );
    const acumulador = new Map<string, number>();
    filtrados.forEach((p) => {
      const fecha = new Date(p.fechaActualizacion ?? p.fechaCreacion ?? 0);
      const clave = fecha.toISOString().slice(0, 10);
      acumulador.set(clave, (acumulador.get(clave) ?? 0) + (p.total ?? 0));
    });
    const dias = [...acumulador.keys()].sort();
    return dias.map((d) => ({
      etiqueta: d.slice(5),
      ingreso: Math.round(acumulador.get(d) ?? 0),
    }));
  }, [sucursalElegida, entregadosUltimoMes, sucursales]);

  // Top productos: agregar cantidades vendidas en pedidos ENTREGADOS
  const topProductos = useMemo(() => {
    const acumulador = new Map<number, { nombre: string; cantidad: number }>();
    entregadosUltimoMes.forEach((p) => {
      p.detalles?.forEach((d) => {
        const prev = acumulador.get(d.idProducto) ?? {
          nombre: d.nombreProducto ?? `Producto #${d.idProducto}`,
          cantidad: 0,
        };
        prev.cantidad += d.cantidad ?? 0;
        prev.nombre = d.nombreProducto ?? prev.nombre;
        acumulador.set(d.idProducto, prev);
      });
    });
    return [...acumulador.values()]
      .sort((a, b) => b.cantidad - a.cantidad)
      .slice(0, 8);
  }, [entregadosUltimoMes]);

  return (
    <LayoutAdmin titulo="Dashboard" subtitulo="Resumen general del supermercado">
      {loading ? (
        <Box sx={{ textAlign: "center", padding: 8 }}>
          <CircularProgress />
        </Box>
      ) : (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.4 }}
        >
          {/* Métricas */}
          <Grid container spacing={2} sx={{ marginBottom: 3 }}>
            {metricas.map((m, i) => (
              <Grid size={{ xs: 6, md: 3 }} key={i}>
                <Box className="coco-card" sx={{ padding: 2 }}>
                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "flex-start",
                    }}
                  >
                    <Box>
                      <Typography
                        sx={{
                          fontSize: 12,
                          color: "var(--coco-text-secondary)",
                          textTransform: "uppercase",
                          letterSpacing: 0.5,
                        }}
                      >
                        {m.label}
                      </Typography>
                      <Typography sx={{ fontSize: 24, fontWeight: 700, marginTop: 0.5 }}>
                        {m.valor}
                      </Typography>
                    </Box>
                    <Box
                      sx={{
                        width: 40,
                        height: 40,
                        borderRadius: 2,
                        backgroundColor: `${m.color}20`,
                        color: m.color,
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        fontSize: 18,
                      }}
                    >
                      <FontAwesomeIcon icon={m.icono} />
                    </Box>
                  </Box>
                </Box>
              </Grid>
            ))}
          </Grid>

          {/* Gráficas */}
          <Grid container spacing={2}>
            <Grid size={{ xs: 12, lg: 7 }}>
              <Box className="coco-card">
                <Box
                  sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    flexWrap: "wrap",
                    gap: 1,
                    marginBottom: 2,
                  }}
                >
                  <Typography sx={{ fontWeight: 600 }}>
                    Ingreso por sucursal (último mes)
                  </Typography>
                  <TextField
                    size="small"
                    select
                    label="Sucursal"
                    value={sucursalElegida}
                    onChange={(e) =>
                      setSucursalElegida(
                        e.target.value === "TODAS"
                          ? "TODAS"
                          : parseInt(e.target.value)
                      )
                    }
                    sx={{ minWidth: 200 }}
                  >
                    <MenuItem value="TODAS">Todas (agregado)</MenuItem>
                    {sucursales.map((s) => (
                      <MenuItem key={s.idSucursal} value={s.idSucursal}>
                        {etiquetaSucursal(s.nombre)}
                      </MenuItem>
                    ))}
                  </TextField>
                </Box>
                <ResponsiveContainer width="100%" height={280}>
                  <BarChart data={datosIngreso}>
                    <CartesianGrid strokeDasharray="3 3" stroke="var(--coco-border)" />
                    <XAxis dataKey="etiqueta" tick={{ fontSize: 11 }} />
                    <YAxis tick={{ fontSize: 11 }} />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: "var(--coco-surface)",
                        border: "1px solid var(--coco-border)",
                        borderRadius: 8,
                      }}
                      formatter={(v: number) => `$${v.toLocaleString("es-CO")}`}
                    />
                    <Bar
                      dataKey="ingreso"
                      fill={COLORES_COCOTECH.primary}
                      radius={[6, 6, 0, 0]}
                    />
                  </BarChart>
                </ResponsiveContainer>
              </Box>
            </Grid>

            <Grid size={{ xs: 12, lg: 5 }}>
              <Box className="coco-card">
                <Typography sx={{ fontWeight: 600, marginBottom: 2 }}>
                  Top productos vendidos (mes)
                </Typography>
                {topProductos.length === 0 ? (
                  <Typography
                    sx={{
                      color: "var(--coco-text-muted)",
                      textAlign: "center",
                      padding: 4,
                    }}
                  >
                    Sin pedidos entregados en el último mes.
                  </Typography>
                ) : (
                  <ResponsiveContainer width="100%" height={280}>
                    <BarChart data={topProductos} layout="vertical">
                      <CartesianGrid
                        strokeDasharray="3 3"
                        stroke="var(--coco-border)"
                      />
                      <XAxis type="number" tick={{ fontSize: 11 }} />
                      <YAxis
                        type="category"
                        dataKey="nombre"
                        tick={{ fontSize: 11 }}
                        width={120}
                      />
                      <Tooltip
                        contentStyle={{
                          backgroundColor: "var(--coco-surface)",
                          border: "1px solid var(--coco-border)",
                          borderRadius: 8,
                        }}
                      />
                      <Bar
                        dataKey="cantidad"
                        fill={COLORES_COCOTECH.secondary}
                        radius={[0, 6, 6, 0]}
                      />
                    </BarChart>
                  </ResponsiveContainer>
                )}
              </Box>
            </Grid>
          </Grid>
        </motion.div>
      )}
    </LayoutAdmin>
  );
};

export default DashboardAdmin;
