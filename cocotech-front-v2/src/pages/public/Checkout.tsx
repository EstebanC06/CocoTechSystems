/**
 * Checkout en 3 pasos:
 *  1. Tipo de entrega (domicilio / recoger) + dirección o sucursal.
 *  2. Método de pago simulado.
 *  3. Confirmación + crear pedido.
 *
 * Al finalizar: POST /pedido/crear con todos los datos.
 */
import { useState, useEffect } from "react";
import {
  Box, Grid, Typography, Button, Stepper, Step, StepLabel, TextField,
  MenuItem, RadioGroup, FormControlLabel, Radio, CircularProgress, Alert, Divider,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faTruckFast, faStore, faMoneyBill, faCreditCard, faQrcode, faCircleCheck,
} from "@fortawesome/free-solid-svg-icons";
import LayoutEcommerce from "../../components/layout/LayoutEcommerce";
import { useCarrito } from "../../context/CarritoContext";
import { useAuth } from "../../context/AuthContext";
import { obtenerSucursalesPublico } from "../../services/publico.service";
import { obtenerClientePorId } from "../../services/cliente.service";
import { crearPedido } from "../../services/pedido.service";
import type {
  SucursalDTO, TipoEntrega, MetodoPago, PedidoDTO, ClienteDTO,
} from "../../types";

const pasos = ["Entrega", "Pago", "Confirmación"];
const COSTO_DOMICILIO = 8000;

const Checkout = () => {
  const navigate = useNavigate();
  const { sesion } = useAuth();
  const { items, subtotal, iva, total: totalCarrito, vaciar, precioConDescuento } = useCarrito();

  const [paso, setPaso] = useState(0);
  const [sucursales, setSucursales] = useState<SucursalDTO[]>([]);
  const [cliente, setCliente] = useState<ClienteDTO | null>(null);
  const [loading, setLoading] = useState(true);

  // Paso 1
  const [tipoEntrega, setTipoEntrega] = useState<TipoEntrega>("DOMICILIO");
  const [idSucursal, setIdSucursal] = useState<number | "">("");
  const [direccion, setDireccion] = useState("");
  const [barrio, setBarrio] = useState("");
  const [ciudad, setCiudad] = useState("");
  const [referencia, setReferencia] = useState("");
  const [notas, setNotas] = useState("");

  // Paso 2
  const [metodoPago, setMetodoPago] = useState<MetodoPago>("EFECTIVO_CONTRA_ENTREGA");

  // Final
  const [procesando, setProcesando] = useState(false);
  const [error, setError] = useState("");
  const [pedidoCreado, setPedidoCreado] = useState<number | null>(null);

  useEffect(() => {
    const cargar = async () => {
      if (!sesion) return;
      try {
        const [sucs, cli] = await Promise.all([
          obtenerSucursalesPublico(),
          obtenerClientePorId(sesion.id),
        ]);
        setSucursales(sucs);
        setCliente(cli);
        // Pre-llenar dirección del cliente
        setDireccion(cli.calle ?? "");
        setBarrio(cli.barrio ?? "");
        setCiudad(cli.ciudad ?? "");
        if (sucs.length > 0) setIdSucursal(sucs[0].idSucursal!);
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, [sesion]);

  const costoEnvio = tipoEntrega === "DOMICILIO" ? COSTO_DOMICILIO : 0;
  const totalFinal = totalCarrito + costoEnvio;

  const validarPaso1 = (): string | null => {
    if (!idSucursal) return "Selecciona una sucursal";
    if (tipoEntrega === "DOMICILIO") {
      if (!direccion.trim()) return "Ingresa la dirección de entrega";
      if (!ciudad.trim()) return "Ingresa la ciudad";
    }
    return null;
  };

  const siguiente = () => {
    if (paso === 0) {
      const err = validarPaso1();
      if (err) {
        setError(err);
        return;
      }
    }
    setError("");
    setPaso(paso + 1);
  };

  const finalizarCompra = async () => {
    if (!sesion || !idSucursal) return;
    setProcesando(true);
    setError("");
    try {
      const detalles = items.map((it) => {
        const precio = precioConDescuento(it.producto);
        return {
          idProducto: it.producto.idProducto!,
          nombreProducto: it.producto.nombre,
          cantidad: it.cantidad,
          precioUnitario: precio,
          subtotal: precio * it.cantidad,
          promocion: (it.producto.descuentoPorcentaje ?? 0) > 0,
          porcentajeDescuento: it.producto.descuentoPorcentaje ?? 0,
        };
      });

      const pedido: PedidoDTO = {
        estado: "RECIBIDO",
        tipoEntrega,
        metodoPago,
        subtotal,
        iva,
        costoEnvio,
        total: totalFinal,
        idCliente: sesion.id,
        idSucursalDespacho: idSucursal as number,
        direccionEnvio: tipoEntrega === "DOMICILIO" ? direccion : undefined,
        barrioEnvio: tipoEntrega === "DOMICILIO" ? barrio : undefined,
        ciudadEnvio: tipoEntrega === "DOMICILIO" ? ciudad : undefined,
        referenciaEnvio: tipoEntrega === "DOMICILIO" ? referencia : undefined,
        notasCliente: notas || undefined,
        detalles,
      };

      const respuesta = await crearPedido(pedido);
      setPedidoCreado(respuesta.idPedido ?? 0);
      vaciar();
    } catch (e: any) {
      setError(
        e?.response?.data?.message ??
          "Error al crear el pedido. Si el backend aún no tiene /pedido/crear implementado, este flujo está listo pero no podrá guardarse hasta que se agregue."
      );
    } finally {
      setProcesando(false);
    }
  };

  if (loading) {
    return (
      <LayoutEcommerce>
        <Box sx={{ textAlign: "center", padding: 8 }}><CircularProgress /></Box>
      </LayoutEcommerce>
    );
  }

  // Pantalla de éxito
  if (pedidoCreado !== null) {
    return (
      <LayoutEcommerce>
        <motion.div initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }}>
          <Box className="coco-card" sx={{ textAlign: "center", padding: 6, maxWidth: 560, margin: "0 auto" }}>
            <Box sx={{ fontSize: 72, color: "var(--coco-success)", marginBottom: 2 }}>
              <FontAwesomeIcon icon={faCircleCheck} />
            </Box>
            <Typography variant="h5" sx={{ fontWeight: 700, marginBottom: 1 }}>
              ¡Pedido confirmado!
            </Typography>
            <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 1 }}>
              Tu pedido <strong>#{pedidoCreado}</strong> fue recibido y será preparado pronto.
            </Typography>
            <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 3, fontSize: 13 }}>
              Te avisaremos cuando cambie de estado.
            </Typography>
            <Box sx={{ display: "flex", gap: 1.5, justifyContent: "center", flexWrap: "wrap" }}>
              <Button variant="contained" color="secondary" onClick={() => navigate("/cliente/pedidos")}>
              Ver mis pedidos
              </Button>
              <Button variant="outlined" onClick={() => navigate("/productos")}>
                Seguir comprando
              </Button>
            </Box>
          </Box>
        </motion.div>
      </LayoutEcommerce>
    );
  }

  return (
    <LayoutEcommerce>
      <Typography sx={{ fontSize: 26, fontWeight: 600, marginBottom: 3 }}>Finalizar compra</Typography>

      <Stepper activeStep={paso} sx={{ marginBottom: 4 }}>
        {pasos.map((p) => <Step key={p}><StepLabel>{p}</StepLabel></Step>)}
      </Stepper>

      {error && <Alert severity="error" sx={{ marginBottom: 2 }}>{error}</Alert>}

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 8 }}>
          <Box className="coco-card">

            {/* Paso 1: Entrega */}
            {paso === 0 && (
              <Box>
                <Typography sx={{ fontWeight: 600, marginBottom: 2 }}>¿Cómo quieres recibir tu pedido?</Typography>

                <RadioGroup value={tipoEntrega} onChange={(e) => setTipoEntrega(e.target.value as TipoEntrega)}>
                  <Box sx={{
                    border: tipoEntrega === "DOMICILIO" ? "2px solid var(--coco-primary)" : "1px solid var(--coco-border)",
                    borderRadius: 2, padding: 2, marginBottom: 1.5, cursor: "pointer",
                  }} onClick={() => setTipoEntrega("DOMICILIO")}>
                    <FormControlLabel
                      value="DOMICILIO"
                      control={<Radio />}
                      label={
                        <Box>
                          <Box sx={{ display: "flex", alignItems: "center", gap: 1, fontWeight: 600 }}>
                            <FontAwesomeIcon icon={faTruckFast} style={{ color: "var(--coco-primary)" }} />
                            Domicilio
                          </Box>
                          <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)", marginTop: 0.5 }}>
                            Llega a la dirección que indiques · ${COSTO_DOMICILIO.toLocaleString("es-CO")}
                          </Typography>
                        </Box>
                      }
                    />
                  </Box>
                  <Box sx={{
                    border: tipoEntrega === "RECOGER_EN_SUCURSAL" ? "2px solid var(--coco-primary)" : "1px solid var(--coco-border)",
                    borderRadius: 2, padding: 2, cursor: "pointer",
                  }} onClick={() => setTipoEntrega("RECOGER_EN_SUCURSAL")}>
                    <FormControlLabel
                      value="RECOGER_EN_SUCURSAL"
                      control={<Radio />}
                      label={
                        <Box>
                          <Box sx={{ display: "flex", alignItems: "center", gap: 1, fontWeight: 600 }}>
                            <FontAwesomeIcon icon={faStore} style={{ color: "var(--coco-primary)" }} />
                            Recoger en sucursal
                          </Box>
                          <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)", marginTop: 0.5 }}>
                            Sin costo de envío · Recoges cuando esté listo
                          </Typography>
                        </Box>
                      }
                    />
                  </Box>
                </RadioGroup>

                <Divider sx={{ marginY: 3 }} />

                <TextField
                  fullWidth
                  select
                  label={tipoEntrega === "DOMICILIO" ? "Sucursal que despacha" : "Sucursal donde recoges"}
                  value={idSucursal}
                  onChange={(e) => setIdSucursal(parseInt(e.target.value as string))}
                  sx={{ marginBottom: 2 }}
                >
                  {sucursales.map((s) => (
                    <MenuItem key={s.idSucursal} value={s.idSucursal}>
                      {s.nombre} — {s.ciudad}
                    </MenuItem>
                  ))}
                </TextField>

                {tipoEntrega === "DOMICILIO" && (
                  <Grid container spacing={2}>
                    <Grid size={12}>
                      <TextField fullWidth label="Dirección" value={direccion} onChange={(e) => setDireccion(e.target.value)} />
                    </Grid>
                    <Grid size={6}>
                      <TextField fullWidth label="Barrio" value={barrio} onChange={(e) => setBarrio(e.target.value)} />
                    </Grid>
                    <Grid size={6}>
                      <TextField fullWidth label="Ciudad" value={ciudad} onChange={(e) => setCiudad(e.target.value)} />
                    </Grid>
                    <Grid size={12}>
                      <TextField fullWidth label="Referencia (opcional)" placeholder="Apto, torre, indicaciones..." value={referencia} onChange={(e) => setReferencia(e.target.value)} />
                    </Grid>
                  </Grid>
                )}

                <TextField
                  fullWidth
                  multiline
                  rows={2}
                  label="Notas para el pedido (opcional)"
                  value={notas}
                  onChange={(e) => setNotas(e.target.value)}
                  sx={{ marginTop: 2 }}
                />
              </Box>
            )}

            {/* Paso 2: Pago */}
            {paso === 1 && (
              <Box>
                <Typography sx={{ fontWeight: 600, marginBottom: 1 }}>Método de pago</Typography>
                <Alert severity="info" sx={{ marginBottom: 2, fontSize: 12 }}>
                  Pagos simulados — proyecto académico. No se procesan cobros reales.
                </Alert>

                <RadioGroup value={metodoPago} onChange={(e) => setMetodoPago(e.target.value as MetodoPago)}>
                  {[
                    { v: "EFECTIVO_CONTRA_ENTREGA", t: "Efectivo contra entrega", d: "Pagas al recibir tu pedido", i: faMoneyBill },
                    { v: "TARJETA_SIMULADA", t: "Tarjeta de crédito/débito", d: "Pago simulado al confirmar", i: faCreditCard },
                    { v: "PSE_SIMULADO", t: "PSE / Transferencia bancaria", d: "Pago simulado al confirmar", i: faQrcode },
                  ].map((opt) => (
                    <Box
                      key={opt.v}
                      onClick={() => setMetodoPago(opt.v as MetodoPago)}
                      sx={{
                        border: metodoPago === opt.v ? "2px solid var(--coco-primary)" : "1px solid var(--coco-border)",
                        borderRadius: 2, padding: 2, marginBottom: 1.5, cursor: "pointer",
                      }}
                    >
                      <FormControlLabel
                        value={opt.v}
                        control={<Radio />}
                        label={
                          <Box>
                            <Box sx={{ display: "flex", alignItems: "center", gap: 1, fontWeight: 600 }}>
                              <FontAwesomeIcon icon={opt.i} style={{ color: "var(--coco-primary)" }} />
                              {opt.t}
                            </Box>
                            <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)", marginTop: 0.5 }}>
                              {opt.d}
                            </Typography>
                          </Box>
                        }
                      />
                    </Box>
                  ))}
                </RadioGroup>
              </Box>
            )}

            {/* Paso 3: Confirmación */}
            {paso === 2 && (
              <Box>
                <Typography sx={{ fontWeight: 600, marginBottom: 2 }}>Revisa tu pedido</Typography>

                <Box sx={{ marginBottom: 3 }}>
                  <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)", textTransform: "uppercase", letterSpacing: 0.5 }}>
                    Entrega
                  </Typography>
                  <Typography sx={{ fontWeight: 500, marginTop: 0.5 }}>
                    {tipoEntrega === "DOMICILIO" ? "Domicilio" : "Recoger en sucursal"}
                  </Typography>
                  {tipoEntrega === "DOMICILIO" ? (
                    <Typography sx={{ fontSize: 13, color: "var(--coco-text-secondary)" }}>
                      {direccion}, {barrio}, {ciudad}
                    </Typography>
                  ) : (
                    <Typography sx={{ fontSize: 13, color: "var(--coco-text-secondary)" }}>
                      {sucursales.find((s) => s.idSucursal === idSucursal)?.nombre}
                    </Typography>
                  )}
                </Box>

                <Box sx={{ marginBottom: 3 }}>
                  <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)", textTransform: "uppercase", letterSpacing: 0.5 }}>
                    Pago
                  </Typography>
                  <Typography sx={{ fontWeight: 500, marginTop: 0.5 }}>
                    {metodoPago === "EFECTIVO_CONTRA_ENTREGA" && "Efectivo contra entrega"}
                    {metodoPago === "TARJETA_SIMULADA" && "Tarjeta (simulado)"}
                    {metodoPago === "PSE_SIMULADO" && "PSE (simulado)"}
                  </Typography>
                </Box>

                <Divider sx={{ marginY: 2 }} />

                <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)", textTransform: "uppercase", letterSpacing: 0.5, marginBottom: 1 }}>
                  Productos ({items.length})
                </Typography>
                <Box sx={{ display: "flex", flexDirection: "column", gap: 1, marginBottom: 2 }}>
                  {items.map((it) => {
                    const p = precioConDescuento(it.producto);
                    return (
                      <Box key={it.producto.idProducto} sx={{ display: "flex", justifyContent: "space-between", fontSize: 13 }}>
                        <span>{it.cantidad}× {it.producto.nombre}</span>
                        <span style={{ fontWeight: 600 }}>${Math.round(p * it.cantidad).toLocaleString("es-CO")}</span>
                      </Box>
                    );
                  })}
                </Box>
              </Box>
            )}

            {/* Navegación */}
            <Box sx={{ display: "flex", justifyContent: "space-between", marginTop: 4 }}>
              <Button disabled={paso === 0} onClick={() => setPaso(paso - 1)}>
                Anterior
              </Button>
              {paso < pasos.length - 1 ? (
                <Button variant="contained" color="secondary" onClick={siguiente}>
                  Siguiente
                </Button>
              ) : (
                <Button
                  variant="contained"
                  color="secondary"
                  size="large"
                  onClick={finalizarCompra}
                  disabled={procesando}
                >
                  {procesando ? <CircularProgress size={20} color="inherit" /> : "Confirmar pedido"}
                </Button>
              )}
            </Box>
          </Box>
        </Grid>

        {/* Resumen lateral */}
        <Grid size={{ xs: 12, md: 4 }}>
          <Box className="coco-card" sx={{ padding: 2, position: "sticky", top: 100 }}>
            <Typography sx={{ fontWeight: 600, fontSize: 15, marginBottom: 2 }}>Resumen</Typography>
            <Box sx={{ display: "flex", justifyContent: "space-between", fontSize: 13, marginBottom: 0.5 }}>
              <span>Subtotal</span><span>${Math.round(subtotal).toLocaleString("es-CO")}</span>
            </Box>
            <Box sx={{ display: "flex", justifyContent: "space-between", fontSize: 13, marginBottom: 0.5 }}>
              <span>IVA</span><span>${Math.round(iva).toLocaleString("es-CO")}</span>
            </Box>
            <Box sx={{ display: "flex", justifyContent: "space-between", fontSize: 13, marginBottom: 1.5 }}>
              <span>Envío</span>
              <span>{costoEnvio > 0 ? `$${costoEnvio.toLocaleString("es-CO")}` : "Gratis"}</span>
            </Box>
            <Divider />
            <Box sx={{ display: "flex", justifyContent: "space-between", marginTop: 1.5, fontWeight: 700, fontSize: 18 }}>
              <span>Total</span>
              <span style={{ color: "var(--coco-primary)" }}>${Math.round(totalFinal).toLocaleString("es-CO")}</span>
            </Box>
          </Box>
        </Grid>
      </Grid>
    </LayoutEcommerce>
  );
};

export default Checkout;
