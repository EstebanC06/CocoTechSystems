/**
 * Perfil del empleado — sus datos personales.
 *
 * Permite editar nombres y apellidos, ver datos no editables
 * (correo, cargo, sucursal con su nombre real) y cambiar la
 * contraseña en una sección aparte.
 */
import { useEffect, useState } from "react";
import {
  Box,
  Grid,
  Typography,
  TextField,
  Button,
  Avatar,
  Alert,
  CircularProgress,
} from "@mui/material";
import LayoutEmpleado from "../../components/layout/LayoutEmpleado";
import {
  obtenerEmpleadoPorId,
  actualizarEmpleado,
  actualizarContrasenaEmpleado,
} from "../../services/empleado.service";
import { obtenerSucursalPorId } from "../../services/sucursal.service";
import { useAuth } from "../../context/AuthContext";
import type { EmpleadoDTO } from "../../types";

const PerfilEmpleado = () => {
  const { sesion } = useAuth();
  const [empleado, setEmpleado] = useState<EmpleadoDTO | null>(null);
  const [nombreSucursal, setNombreSucursal] = useState("");
  const [loading, setLoading] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [ok, setOk] = useState("");
  const [error, setError] = useState("");

  // Estado para cambio de contraseña
  const [pwdActual, setPwdActual] = useState("");
  const [pwdNueva, setPwdNueva] = useState("");
  const [pwdConfirma, setPwdConfirma] = useState("");
  const [cambiandoPwd, setCambiandoPwd] = useState(false);
  const [okPwd, setOkPwd] = useState("");
  const [errorPwd, setErrorPwd] = useState("");

  useEffect(() => {
    const cargar = async () => {
      if (!sesion) return;
      try {
        const e = await obtenerEmpleadoPorId(sesion.id);
        setEmpleado(e);
        try {
          const suc = await obtenerSucursalPorId(e.idSucursal);
          setNombreSucursal(suc.nombre ?? `Sucursal #${e.idSucursal}`);
        } catch {
          setNombreSucursal(`Sucursal #${e.idSucursal}`);
        }
      } catch {
        setError("No se pudieron cargar tus datos.");
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, [sesion]);

  const guardar = async () => {
    if (!empleado || !sesion) return;
    setGuardando(true);
    setOk("");
    setError("");
    try {
      await actualizarEmpleado(sesion.id, empleado);
      setOk("Datos actualizados correctamente.");
    } catch (e: any) {
      setError(e?.response?.data?.message ?? "Error al guardar.");
    } finally {
      setGuardando(false);
    }
  };

  const cambiarPassword = async () => {
    if (!sesion) return;
    setOkPwd("");
    setErrorPwd("");

    if (!pwdActual || !pwdNueva || !pwdConfirma) {
      setErrorPwd("Completa todos los campos.");
      return;
    }
    if (pwdNueva.length < 8) {
      setErrorPwd("La nueva contraseña debe tener al menos 8 caracteres.");
      return;
    }
    if (pwdNueva !== pwdConfirma) {
      setErrorPwd("Las contraseñas no coinciden.");
      return;
    }

    setCambiandoPwd(true);
    try {
      await actualizarContrasenaEmpleado(sesion.id, pwdNueva);
      setOkPwd("Contraseña actualizada correctamente.");
      setPwdActual("");
      setPwdNueva("");
      setPwdConfirma("");
    } catch (e: any) {
      setErrorPwd(
        e?.response?.data?.message ?? "No se pudo actualizar la contraseña."
      );
    } finally {
      setCambiandoPwd(false);
    }
  };

  if (loading) {
    return (
      <LayoutEmpleado>
        <Box sx={{ textAlign: "center", padding: 8 }}>
          <CircularProgress />
        </Box>
      </LayoutEmpleado>
    );
  }
  if (!empleado) {
    return (
      <LayoutEmpleado>
        <Alert severity="error">{error}</Alert>
      </LayoutEmpleado>
    );
  }

  const iniciales = `${empleado.nombres?.[0] ?? ""}${
    empleado.apellidos?.[0] ?? ""
  }`.toUpperCase();

  return (
    <LayoutEmpleado>
      <Typography sx={{ fontSize: 26, fontWeight: 600 }}>Mi perfil</Typography>
      <Typography sx={{ color: "var(--coco-text-secondary)", marginBottom: 3 }}>
        Tu información de empleado
      </Typography>

      {ok && (
        <Alert severity="success" sx={{ marginBottom: 2 }}>
          {ok}
        </Alert>
      )}
      {error && (
        <Alert severity="error" sx={{ marginBottom: 2 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={3}>
        {/* Tarjeta de avatar */}
        <Grid size={{ xs: 12, md: 4 }}>
          <Box className="coco-card" sx={{ textAlign: "center", padding: 3 }}>
            <Avatar
              sx={{
                bgcolor: "var(--coco-primary)",
                width: 96,
                height: 96,
                fontSize: 36,
                margin: "0 auto 16px",
              }}
            >
              {iniciales}
            </Avatar>
            <Typography sx={{ fontWeight: 600, fontSize: 17 }}>
              {empleado.nombres} {empleado.apellidos}
            </Typography>
            <Typography
              sx={{ fontSize: 13, color: "var(--coco-text-secondary)" }}
            >
              {empleado.correo}
            </Typography>
            <Typography
              sx={{
                fontSize: 12,
                color: "var(--coco-primary)",
                marginTop: 1,
                fontWeight: 600,
              }}
            >
              {empleado.cargo}
            </Typography>
            <Typography
              sx={{
                fontSize: 12,
                color: "var(--coco-text-secondary)",
                marginTop: 0.5,
              }}
            >
              {nombreSucursal}
            </Typography>
          </Box>
        </Grid>

        {/* Datos personales editables */}
        <Grid size={{ xs: 12, md: 8 }}>
          <Box className="coco-card">
            <Typography sx={{ fontWeight: 600, marginBottom: 2 }}>
              Datos personales
            </Typography>
            <Grid container spacing={2}>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField
                  fullWidth
                  label="Nombres"
                  value={empleado.nombres}
                  onChange={(e) =>
                    setEmpleado({ ...empleado, nombres: e.target.value })
                  }
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField
                  fullWidth
                  label="Apellidos"
                  value={empleado.apellidos}
                  onChange={(e) =>
                    setEmpleado({ ...empleado, apellidos: e.target.value })
                  }
                />
              </Grid>
              <Grid size={12}>
                <TextField
                  fullWidth
                  label="Correo"
                  value={empleado.correo}
                  disabled
                />
                <Typography
                  sx={{
                    fontSize: 11,
                    color: "var(--coco-text-secondary)",
                    marginTop: 0.5,
                  }}
                >
                  El correo es tu identificador de acceso y no se puede
                  modificar aquí.
                </Typography>
              </Grid>
              <Grid size={6}>
                <TextField
                  fullWidth
                  label="Cargo"
                  value={empleado.cargo}
                  disabled
                />
              </Grid>
              <Grid size={6}>
                <TextField
                  fullWidth
                  label="Sucursal"
                  value={nombreSucursal}
                  disabled
                />
              </Grid>
            </Grid>
            <Box sx={{ marginTop: 3, textAlign: "right" }}>
              <Button
                variant="contained"
                color="secondary"
                onClick={guardar}
                disabled={guardando}
              >
                {guardando ? (
                  <CircularProgress size={20} color="inherit" />
                ) : (
                  "Guardar cambios"
                )}
              </Button>
            </Box>
          </Box>

          {/* Cambio de contraseña */}
          <Box className="coco-card" sx={{ marginTop: 2 }}>
            <Typography sx={{ fontWeight: 600, marginBottom: 2 }}>
              Cambiar contraseña
            </Typography>

            {okPwd && (
              <Alert severity="success" sx={{ marginBottom: 2 }}>
                {okPwd}
              </Alert>
            )}
            {errorPwd && (
              <Alert severity="error" sx={{ marginBottom: 2 }}>
                {errorPwd}
              </Alert>
            )}

            <Grid container spacing={2}>
              <Grid size={12}>
                <TextField
                  fullWidth
                  type="password"
                  label="Contraseña actual"
                  value={pwdActual}
                  onChange={(e) => setPwdActual(e.target.value)}
                  helperText="Se pide por seguridad, pero la validación final la hace el back."
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField
                  fullWidth
                  type="password"
                  label="Nueva contraseña"
                  value={pwdNueva}
                  onChange={(e) => setPwdNueva(e.target.value)}
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField
                  fullWidth
                  type="password"
                  label="Confirmar nueva contraseña"
                  value={pwdConfirma}
                  onChange={(e) => setPwdConfirma(e.target.value)}
                />
              </Grid>
            </Grid>

            <Box sx={{ marginTop: 2, textAlign: "right" }}>
              <Button
                variant="outlined"
                color="secondary"
                onClick={cambiarPassword}
                disabled={cambiandoPwd}
              >
                {cambiandoPwd ? (
                  <CircularProgress size={20} color="inherit" />
                ) : (
                  "Actualizar contraseña"
                )}
              </Button>
            </Box>
          </Box>
        </Grid>
      </Grid>
    </LayoutEmpleado>
  );
};

export default PerfilEmpleado;
