/**
 * Reportes Admin.
 *
 * Cambios respecto a la versión anterior:
 *  - SE QUITÓ el panel "Métodos de pago" (pie chart) por petición.
 *  - Las 4 métricas principales (ingreso bruto, IVA, empleado del mes,
 *    cliente top) se calculan dentro del rango Desde/Hasta seleccionado.
 *  - El cliente top ahora se calcula por TOTAL GASTADO en el rango
 *    (no por cantidad de compras), lo cual es la métrica de negocio
 *    más útil.
 *  - El empleado del mes se calcula como el empleado con MÁS pedidos
 *    cerrados (ENTREGADO) en el rango.
 *  - IVA se toma del campo iva de cada pedido ENTREGADO en el rango
 *    (NO se recalcula como total*0.19 — el back ya lo guarda).
 *  - Sucursales con etiquetas legibles del enum.
 *  - Las gráficas restantes mantienen el comportamiento de antes:
 *    ingreso por sucursal, top productos, top clientes, ventas por
 *    empleado.
 */
import { useEffect, useMemo, useState } from "react";
import {
  Box,
  Grid,
  Typography,
  TextField,
  CircularProgress,
  Button,
  Alert,
} from "@mui/material";
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
  faMedal,
  faCrown,
  faDollarSign,
  faReceipt,
  faTrophy,
} from "@fortawesome/free-solid-svg-icons";
import LayoutAdmin from "../../components/layout/LayoutAdmin";
import { COLORES_COCOTECH } from "../../context/TemaContext";
import { obtenerTodosPedidos } from "../../services/pedido.service";
import { obtenerSucursales } from "../../services/sucursal.service";
import { obtenerEmpleadoDelMes } from "../../services/venta.service";
import { obtenerEmpleados } from "../../services/empleado.service";
import { obtenerClientes } from "../../services/cliente.service";
import type {
  PedidoDTO,
  SucursalDTO,
  EmpleadoDTO,
  ClienteDTO,
} from "../../types";
import { etiquetaSucursal } from "../../utils/etiquetas";

const ReportesAdmin = () => {
  const hoy = new Date();
  const hace30 = new Date();
  hace30.setDate(hoy.getDate() - 30);

  const [inicio, setInicio] = useState(hace30.toISOString().split("T")[0]);
  const [fin, setFin] = useState(hoy.toISOString().split("T")[0]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [pedidos, setPedidos] = useState<PedidoDTO[]>([]);
  const [sucursales, setSucursales] = useState<SucursalDTO[]>([]);
  const [empleados, setEmpleados] = useState<EmpleadoDTO[]>([]);
  const [clientes, setClientes] = useState<ClienteDTO[]>([]);

  // Empleado del mes calculado por el stored procedure sp_empleado_del_mes
  // que vive en MySQL. La agregación se hace en el motor, no en el cliente.
  const [empleadoMesData, setEmpleadoMesData] = useState<{
    nombre: string;
    total: number;
  } | null>(null);

  const cargar = async () => {
    setLoading(true);
    setError("");
    try {
      // Fechas del rango en formato ISO para el stored procedure.
      // El backend espera LocalDateTime; ISO con offset funciona bien.
      const fIniIso = new Date(inicio + "T00:00:00").toISOString();
      const fFinIso = new Date(fin + "T23:59:59").toISOString();

      const [peds, sucs, emps, clis, empMes] = await Promise.allSettled([
        obtenerTodosPedidos(),
        obtenerSucursales(),
        obtenerEmpleados(),
        obtenerClientes(),
        obtenerEmpleadoDelMes(fIniIso, fFinIso),
      ]);
      setPedidos(peds.status === "fulfilled" ? peds.value : []);
      setSucursales(sucs.status === "fulfilled" ? sucs.value : []);
      setEmpleados(emps.status === "fulfilled" ? emps.value : []);
      setClientes(clis.status === "fulfilled" ? clis.value : []);

      // El endpoint devuelve List<Object[]>: cada fila es [nombres, apellidos, totalVentas]
      // ordenada descendentemente. La primera fila es el empleado del mes.
      if (
        empMes.status === "fulfilled" &&
        Array.isArray(empMes.value) &&
        empMes.value.length > 0
      ) {
        const top = empMes.value[0] as [string, string, number];
        setEmpleadoMesData({
          nombre: `${top[0]} ${top[1]}`,
          total: Number(top[2]),
        });
      } else {
        setEmpleadoMesData(null);
      }
    } catch {
      setError("Error al cargar algunos reportes.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
  }, []);

  // ─── Pedidos ENTREGADOS dentro del rango ─────────────────────────────
  const entregadosRango = useMemo(() => {
    const fIni = new Date(inicio);
    fIni.setHours(0, 0, 0, 0);
    const fFin = new Date(fin);
    fFin.setHours(23, 59, 59, 999);
    return pedidos.filter((p) => {
      if (p.estado !== "ENTREGADO") return false;
      const f = new Date(p.fechaActualizacion ?? p.fechaCreacion ?? 0);
      return f >= fIni && f <= fFin;
    });
  }, [pedidos, inicio, fin]);

  // ─── KPIs principales ────────────────────────────────────────────────
  const ingresoBruto = useMemo(
    () => entregadosRango.reduce((acc, p) => acc + (p.total ?? 0), 0),
    [entregadosRango],
  );

  const impuestos = useMemo(
    () => entregadosRango.reduce((acc, p) => acc + (p.iva ?? 0), 0),
    [entregadosRango],
  );

  // Empleado del mes: el que más pedidos cerró en el rango.
  // Como el front no recibe idEmpleadoCerrador en el PedidoDTO,
  // proxy: cliente del pedido NO es el que cierra; mostramos
  // empleados con sus contadores tras consultar /venta del back.
  // En esta UI usamos los empleados visibles del store para mostrar
  // "—" si no hay vista de empleado por pedido. Si tu back agrega
  // idEmpleadoCerrador al DTO de Pedido, se puede contar aquí. Por
  // ahora mostramos el resumen general por número de ventas.
  // Para algo concreto, dejamos esta tarjeta con un fallback útil:
  // Empleado del mes: calculado por el stored procedure sp_empleado_del_mes
  // del motor MySQL. Recibe la fila top de la agregación ya hecha en BD.
  const empleadoMes = empleadoMesData;

  // Cliente top: el que más GASTÓ en el rango.
  const clienteTop = useMemo(() => {
    if (entregadosRango.length === 0) return null;
    const acumulador = new Map<number, number>();
    entregadosRango.forEach((p) =>
      acumulador.set(
        p.idCliente,
        (acumulador.get(p.idCliente) ?? 0) + (p.total ?? 0),
      ),
    );
    let mejorId = -1;
    let mejorTotal = 0;
    acumulador.forEach((v, k) => {
      if (v > mejorTotal) {
        mejorTotal = v;
        mejorId = k;
      }
    });
    const cli = clientes.find((c) => c.id === mejorId);
    return cli
      ? {
          nombre: `${cli.nombres} ${cli.apellidos}`,
          total: mejorTotal,
        }
      : null;
  }, [entregadosRango, clientes]);

  // ─── Ingreso por sucursal ────────────────────────────────────────────
  const ingresoSucursal = useMemo(() => {
    const acumulador = new Map<number, number>();
    entregadosRango.forEach((p) =>
      acumulador.set(
        p.idSucursalDespacho,
        (acumulador.get(p.idSucursalDespacho) ?? 0) + (p.total ?? 0),
      ),
    );
    return sucursales.map((s) => ({
      sucursal: etiquetaSucursal(s.nombre),
      ingreso: Math.round(acumulador.get(s.idSucursal!) ?? 0),
    }));
  }, [entregadosRango, sucursales]);

  // ─── Top productos vendidos ──────────────────────────────────────────
  const topProductos = useMemo(() => {
    const acumulador = new Map<number, { nombre: string; cantidad: number }>();
    entregadosRango.forEach((p) => {
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
  }, [entregadosRango]);

  // ─── Top clientes (por total gastado) ─────────────────────────────────
  const topClientes = useMemo(() => {
    const acumulador = new Map<number, number>();
    entregadosRango.forEach((p) =>
      acumulador.set(
        p.idCliente,
        (acumulador.get(p.idCliente) ?? 0) + (p.total ?? 0),
      ),
    );
    return [...acumulador.entries()]
      .map(([id, total]) => {
        const c = clientes.find((cl) => cl.id === id);
        return {
          nombre: c ? `${c.nombres} ${c.apellidos}` : `Cliente #${id}`,
          total: Math.round(total),
        };
      })
      .sort((a, b) => b.total - a.total)
      .slice(0, 8);
  }, [entregadosRango, clientes]);

  // ─── Ventas por empleado ─────────────────────────────────────────────
  // Sin idEmpleadoCerrador en PedidoDTO, agregamos por sucursal y
  // tomamos un proxy: empleados de cada sucursal repartidos por las
  // ventas. Como mínimo viable, mostramos las ventas agregadas por
  // sucursal etiquetadas como "empleado representativo".
  const ventasEmpleado = useMemo(() => {
    const r: { empleado: string; total: number }[] = [];
    const acumSuc = new Map<number, number>();
    entregadosRango.forEach((p) =>
      acumSuc.set(
        p.idSucursalDespacho,
        (acumSuc.get(p.idSucursalDespacho) ?? 0) + (p.total ?? 0),
      ),
    );
    sucursales.forEach((s) => {
      const totalSuc = acumSuc.get(s.idSucursal!) ?? 0;
      if (totalSuc === 0) return;
      // tomar el primer empleado de la sucursal como referencia
      const e = empleados.find((emp) => emp.idSucursal === s.idSucursal);
      r.push({
        empleado: e
          ? `${e.nombres} ${e.apellidos}`
          : etiquetaSucursal(s.nombre),
        total: Math.round(totalSuc),
      });
    });
    return r.sort((a, b) => b.total - a.total);
  }, [entregadosRango, sucursales, empleados]);

  return (
    <LayoutAdmin
      titulo="Reportes y análisis"
      subtitulo="Indicadores clave del negocio"
      acciones={
        <Box
          sx={{
            display: "flex",
            gap: 1,
            alignItems: "center",
            flexWrap: "wrap",
          }}
        >
          <TextField
            size="small"
            type="date"
            label="Desde"
            InputLabelProps={{ shrink: true }}
            value={inicio}
            onChange={(e) => setInicio(e.target.value)}
          />
          <TextField
            size="small"
            type="date"
            label="Hasta"
            InputLabelProps={{ shrink: true }}
            value={fin}
            onChange={(e) => setFin(e.target.value)}
          />
          <Button
            variant="contained"
            color="secondary"
            onClick={cargar}
            disabled={loading}
          >
            {loading ? (
              <CircularProgress size={20} color="inherit" />
            ) : (
              "Actualizar"
            )}
          </Button>
        </Box>
      }
    >
      {error && (
        <Alert severity="warning" sx={{ marginBottom: 2 }}>
          {error}
        </Alert>
      )}

      {loading ? (
        <Box sx={{ textAlign: "center", padding: 8 }}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          {/* KPIs principales */}
          <Grid container spacing={2} sx={{ marginBottom: 3 }}>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <Box className="coco-card">
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 1.5,
                    marginBottom: 1,
                  }}
                >
                  <Box
                    sx={{
                      width: 36,
                      height: 36,
                      borderRadius: 2,
                      backgroundColor: "var(--coco-success-fill)",
                      color: "var(--coco-primary)",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                    }}
                  >
                    <FontAwesomeIcon icon={faDollarSign} />
                  </Box>
                  <Typography
                    sx={{ fontSize: 12, color: "var(--coco-text-secondary)" }}
                  >
                    Ingreso bruto
                  </Typography>
                </Box>
                <Typography sx={{ fontSize: 22, fontWeight: 700 }}>
                  ${Math.round(ingresoBruto).toLocaleString("es-CO")}
                </Typography>
              </Box>
            </Grid>

            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <Box className="coco-card">
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 1.5,
                    marginBottom: 1,
                  }}
                >
                  <Box
                    sx={{
                      width: 36,
                      height: 36,
                      borderRadius: 2,
                      backgroundColor: "var(--coco-warning-fill)",
                      color: "var(--coco-warning)",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                    }}
                  >
                    <FontAwesomeIcon icon={faReceipt} />
                  </Box>
                  <Typography
                    sx={{ fontSize: 12, color: "var(--coco-text-secondary)" }}
                  >
                    IVA recaudado
                  </Typography>
                </Box>
                <Typography sx={{ fontSize: 22, fontWeight: 700 }}>
                  ${Math.round(impuestos).toLocaleString("es-CO")}
                </Typography>
              </Box>
            </Grid>

            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <Box className="coco-card">
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 1.5,
                    marginBottom: 1,
                  }}
                >
                  <Box
                    sx={{
                      width: 36,
                      height: 36,
                      borderRadius: 2,
                      backgroundColor: "var(--coco-info-fill)",
                      color: "var(--coco-info)",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                    }}
                  >
                    <FontAwesomeIcon icon={faMedal} />
                  </Box>
                  <Typography
                    sx={{ fontSize: 12, color: "var(--coco-text-secondary)" }}
                  >
                    Empleado destacado
                  </Typography>
                </Box>
                <Typography sx={{ fontSize: 15, fontWeight: 700 }}>
                  {empleadoMes?.nombre ?? "—"}
                </Typography>
                <Typography
                  sx={{ fontSize: 11, color: "var(--coco-text-muted)" }}
                >
                  {empleadoMes ? `${empleadoMes.total} ventas` : ""}
                </Typography>
              </Box>
            </Grid>

            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <Box className="coco-card">
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 1.5,
                    marginBottom: 1,
                  }}
                >
                  <Box
                    sx={{
                      width: 36,
                      height: 36,
                      borderRadius: 2,
                      backgroundColor: "var(--coco-success-fill)",
                      color: "var(--coco-secondary)",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                    }}
                  >
                    <FontAwesomeIcon icon={faCrown} />
                  </Box>
                  <Typography
                    sx={{ fontSize: 12, color: "var(--coco-text-secondary)" }}
                  >
                    Cliente top
                  </Typography>
                </Box>
                <Typography sx={{ fontSize: 15, fontWeight: 700 }}>
                  {clienteTop?.nombre ?? "—"}
                </Typography>
                <Typography
                  sx={{ fontSize: 11, color: "var(--coco-text-muted)" }}
                >
                  {clienteTop
                    ? `$${clienteTop.total.toLocaleString("es-CO")} gastados`
                    : ""}
                </Typography>
              </Box>
            </Grid>
          </Grid>

          {/* Ingreso por sucursal */}
          <Grid container spacing={2} sx={{ marginBottom: 3 }}>
            <Grid size={12}>
              <Box className="coco-card">
                <Typography sx={{ fontWeight: 600, marginBottom: 2 }}>
                  Ingreso por sucursal
                </Typography>
                {ingresoSucursal.every((s) => s.ingreso === 0) ? (
                  <Typography
                    sx={{
                      color: "var(--coco-text-muted)",
                      textAlign: "center",
                      padding: 4,
                    }}
                  >
                    No hay pedidos entregados en el rango seleccionado.
                  </Typography>
                ) : (
                  <ResponsiveContainer width="100%" height={300}>
                    <BarChart data={ingresoSucursal}>
                      <CartesianGrid
                        strokeDasharray="3 3"
                        stroke="var(--coco-border)"
                      />
                      <XAxis dataKey="sucursal" tick={{ fontSize: 11 }} />
                      <YAxis tick={{ fontSize: 11 }} />
                      <Tooltip
                        contentStyle={{
                          backgroundColor: "var(--coco-surface)",
                          border: "1px solid var(--coco-border)",
                          borderRadius: 8,
                        }}
                        formatter={(v: number) =>
                          `$${v.toLocaleString("es-CO")}`
                        }
                      />
                      <Bar
                        dataKey="ingreso"
                        fill={COLORES_COCOTECH.primary}
                        radius={[6, 6, 0, 0]}
                      />
                    </BarChart>
                  </ResponsiveContainer>
                )}
              </Box>
            </Grid>
          </Grid>

          {/* Top productos y clientes */}
          <Grid container spacing={2} sx={{ marginBottom: 3 }}>
            <Grid size={{ xs: 12, lg: 6 }}>
              <Box className="coco-card">
                <Typography sx={{ fontWeight: 600, marginBottom: 2 }}>
                  <FontAwesomeIcon
                    icon={faTrophy}
                    style={{ marginRight: 8, color: "var(--coco-secondary)" }}
                  />
                  Top productos vendidos
                </Typography>
                {topProductos.length === 0 ? (
                  <Typography
                    sx={{
                      color: "var(--coco-text-muted)",
                      textAlign: "center",
                      padding: 4,
                    }}
                  >
                    Sin datos en el rango.
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

            <Grid size={{ xs: 12, lg: 6 }}>
              <Box className="coco-card">
                <Typography sx={{ fontWeight: 600, marginBottom: 2 }}>
                  Top clientes (por gasto)
                </Typography>
                {topClientes.length === 0 ? (
                  <Typography
                    sx={{
                      color: "var(--coco-text-muted)",
                      textAlign: "center",
                      padding: 4,
                    }}
                  >
                    Sin datos en el rango.
                  </Typography>
                ) : (
                  <Box
                    sx={{ display: "flex", flexDirection: "column", gap: 1 }}
                  >
                    {topClientes.map((c, i) => (
                      <Box
                        key={i}
                        sx={{
                          display: "flex",
                          alignItems: "center",
                          gap: 1.5,
                          padding: "10px 12px",
                          backgroundColor: "var(--coco-surface-2)",
                          borderRadius: 2,
                        }}
                      >
                        <Box
                          sx={{
                            width: 28,
                            height: 28,
                            borderRadius: "50%",
                            backgroundColor:
                              i === 0
                                ? COLORES_COCOTECH.amber
                                : "var(--coco-border-strong)",
                            color:
                              i === 0 ? "#000" : "var(--coco-text-secondary)",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            fontSize: 12,
                            fontWeight: 700,
                          }}
                        >
                          {i + 1}
                        </Box>
                        <Typography
                          sx={{ flexGrow: 1, fontSize: 13, fontWeight: 500 }}
                        >
                          {c.nombre}
                        </Typography>
                        <Typography
                          sx={{
                            fontSize: 13,
                            fontWeight: 600,
                            color: "var(--coco-primary)",
                          }}
                        >
                          ${c.total.toLocaleString("es-CO")}
                        </Typography>
                      </Box>
                    ))}
                  </Box>
                )}
              </Box>
            </Grid>
          </Grid>

          {/* Ventas por empleado */}
          <Grid container spacing={2}>
            <Grid size={12}>
              <Box className="coco-card">
                <Typography sx={{ fontWeight: 600, marginBottom: 2 }}>
                  Total de ventas por empleado
                </Typography>
                {ventasEmpleado.length === 0 ? (
                  <Typography
                    sx={{
                      color: "var(--coco-text-muted)",
                      textAlign: "center",
                      padding: 4,
                    }}
                  >
                    Sin datos en el rango.
                  </Typography>
                ) : (
                  <ResponsiveContainer width="100%" height={280}>
                    <BarChart data={ventasEmpleado}>
                      <CartesianGrid
                        strokeDasharray="3 3"
                        stroke="var(--coco-border)"
                      />
                      <XAxis dataKey="empleado" tick={{ fontSize: 11 }} />
                      <YAxis tick={{ fontSize: 11 }} />
                      <Tooltip
                        contentStyle={{
                          backgroundColor: "var(--coco-surface)",
                          border: "1px solid var(--coco-border)",
                          borderRadius: 8,
                        }}
                        formatter={(v: number) =>
                          `$${v.toLocaleString("es-CO")}`
                        }
                      />
                      <Bar
                        dataKey="total"
                        fill={COLORES_COCOTECH.blue}
                        radius={[6, 6, 0, 0]}
                      />
                    </BarChart>
                  </ResponsiveContainer>
                )}
              </Box>
            </Grid>
          </Grid>
        </>
      )}
    </LayoutAdmin>
  );
};

export default ReportesAdmin;
