/**
 * Perfil del cliente — datos personales editables + cambio de contraseña.
 */
import { useEffect, useState } from "react";
import {
  Box, Grid, Typography, TextField, Button, Avatar, Divider,
  Alert, CircularProgress,
} from "@mui/material";
import LayoutEcommerce from "../../components/layout/LayoutEcommerce";
import {
  obtenerClientePorId, actualizarCliente, actualizarContrasenaCliente,
} from "../../services/cliente.service";
import { useAuth } from "../../context/AuthContext";
import type { ClienteDTO } from "../../types";

const PerfilCliente = () => {
  const { sesion } = useAuth();
  const [cliente, setCliente] = useState<ClienteDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [nuevaContrasena, setNuevaContrasena] = useState("");
  const [confirmar, setConfirmar] = useState("");
  const [cambiandoPass, setCambiandoPass] = useState(false);
  const [ok, setOk] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    const cargar = async () => {
      if (!sesion) return;
      try {
        setCliente(await obtenerClientePorId(sesion.id));
      } catch {
        setError("No se pudieron cargar tus datos.");
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, [sesion]);

  const guardar = async () => {
    if (!cliente || !sesion) return;
    setGuardando(true);
    setOk(""); setError("");
    try {
      await actualizarCliente(sesion.id, cliente);
      setOk("Datos actualizados correctamente.");
    } catch (e: any) {
      setError(e?.response?.data?.message ?? "Error al actualizar.");
    } finally {
      setGuardando(false);
    }
  };

  const cambiarPassword = async () => {
    if (!sesion) return;
    if (nuevaContrasena.length < 8) { setError("Mínimo 8 caracteres."); return; }
    if (nuevaContrasena !== confirmar) { setError("Las contraseñas no coinciden."); return; }
    setCambiandoPass(true);
    setOk(""); setError("");
    try {
      await actualizarContrasenaCliente(sesion.id, nuevaContrasena);
      setOk("Contraseña actualizada.");
      setNuevaContrasena(""); setConfirmar("");
    } catch (e: any) {
      setError(e?.response?.data?.message ?? "Error.");
    } finally {
      setCambiandoPass(false);
    }
  };

  if (loading) return <LayoutEcommerce><Box sx={{ textAlign: "center", padding: 8 }}><CircularProgress /></Box></LayoutEcommerce>;
  if (!cliente) return <LayoutEcommerce><Alert severity="error">{error}</Alert></LayoutEcommerce>;

  const iniciales = `${cliente.nombres?.[0] ?? ""}${cliente.apellidos?.[0] ?? ""}`.toUpperCase();

  return (
    <LayoutEcommerce>
      <Typography sx={{ fontSize: 26, fontWeight: 600, marginBottom: 0.5 }}>Mi perfil</Typography>
      <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 3 }}>Actualiza tu información personal</Typography>

      {ok && <Alert severity="success" sx={{ marginBottom: 2 }}>{ok}</Alert>}
      {error && <Alert severity="error" sx={{ marginBottom: 2 }}>{error}</Alert>}

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Box className="coco-card" sx={{ textAlign: "center", padding: 3 }}>
            <Avatar sx={{ bgcolor: "var(--coco-primary)", width: 96, height: 96, fontSize: 36, margin: "0 auto 16px" }}>
              {iniciales}
            </Avatar>
            <Typography sx={{ fontWeight: 600, fontSize: 17 }}>
              {cliente.nombres} {cliente.apellidos}
            </Typography>
            <Typography sx={{ fontSize: 13, color: "var(--coco-text-secondary)" }}>
              {cliente.correo}
            </Typography>
          </Box>
        </Grid>

        <Grid size={{ xs: 12, md: 8 }}>
          <Box className="coco-card">
            <Typography sx={{ fontWeight: 600, marginBottom: 2 }}>Datos personales</Typography>
            <Grid container spacing={2}>
              <Grid size={{ xs: 12, sm: 6 }}><TextField fullWidth label="Nombres" value={cliente.nombres} onChange={(e) => setCliente({ ...cliente, nombres: e.target.value })} /></Grid>
              <Grid size={{ xs: 12, sm: 6 }}><TextField fullWidth label="Apellidos" value={cliente.apellidos} onChange={(e) => setCliente({ ...cliente, apellidos: e.target.value })} /></Grid>
              <Grid size={12}><TextField fullWidth label="Correo" type="email" value={cliente.correo} onChange={(e) => setCliente({ ...cliente, correo: e.target.value })} /></Grid>
              <Grid size={12}><TextField fullWidth label="Teléfono" value={cliente.telefono} onChange={(e) => setCliente({ ...cliente, telefono: e.target.value })} /></Grid>
            </Grid>
            <Box sx={{ marginTop: 3, textAlign: "right" }}>
              <Button variant="contained" color="secondary" onClick={guardar} disabled={guardando}>
                {guardando ? <CircularProgress size={20} color="inherit" /> : "Guardar cambios"}
              </Button>
            </Box>
          </Box>

          <Box className="coco-card" sx={{ marginTop: 3 }}>
            <Typography sx={{ fontWeight: 600, marginBottom: 0.5 }}>Cambiar contraseña</Typography>
            <Typography sx={{ fontSize: 12, color: "var(--coco-text-secondary)", marginBottom: 2 }}>Mínimo 8 caracteres</Typography>
            <Grid container spacing={2}>
              <Grid size={{ xs: 12, sm: 6 }}><TextField fullWidth label="Nueva contraseña" type="password" value={nuevaContrasena} onChange={(e) => setNuevaContrasena(e.target.value)} /></Grid>
              <Grid size={{ xs: 12, sm: 6 }}><TextField fullWidth label="Confirmar" type="password" value={confirmar} onChange={(e) => setConfirmar(e.target.value)} /></Grid>
            </Grid>
            <Box sx={{ marginTop: 2, textAlign: "right" }}>
              <Button variant="outlined" onClick={cambiarPassword} disabled={cambiandoPass}>
                {cambiandoPass ? <CircularProgress size={20} /> : "Cambiar contraseña"}
              </Button>
            </Box>
          </Box>
        </Grid>
      </Grid>
    </LayoutEcommerce>
  );
};

export default PerfilCliente;
