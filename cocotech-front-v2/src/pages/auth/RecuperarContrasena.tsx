/**
 * Página de recuperación de contraseña en 3 pasos:
 *
 *  1. El cliente ingresa su correo. Si existe, el backend genera un
 *     código de 6 dígitos y lo envía por email.
 *  2. El cliente ingresa el código recibido.
 *  3. El cliente ingresa la nueva contraseña; el backend valida el
 *     código y, si es correcto, actualiza la contraseña.
 *
 * Implementado con MUI Stepper. Cada paso valida sus campos antes de
 * permitir avanzar al siguiente.
 */
import { useState } from "react";
import {
  Box,
  Button,
  TextField,
  Typography,
  Alert,
  CircularProgress,
  Stepper,
  Step,
  StepLabel,
  Stack,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import NavbarEcommerce from "../../components/layout/NavbarEcommerce";
import {
  solicitarCodigoRecuperacion,
  recuperarContrasenaConCodigo,
} from "../../services/auth.service";

const PASOS = ["Ingresa tu correo", "Ingresa el código", "Nueva contraseña"];

const RecuperarContrasena = () => {
  const navigate = useNavigate();

  const [pasoActivo, setPasoActivo] = useState(0);
  const [correo, setCorreo] = useState("");
  const [codigo, setCodigo] = useState("");
  const [nuevaContrasena, setNuevaContrasena] = useState("");
  const [confirmarContrasena, setConfirmarContrasena] = useState("");

  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  const limpiarMensajes = () => {
    setErrorMsg("");
    setSuccessMsg("");
  };

  // ─── Paso 1: solicitar código ──────────────────────────────────────────
  const solicitarCodigo = async () => {
    limpiarMensajes();
    if (!correo || !correo.includes("@")) {
      setErrorMsg("Ingresa un correo válido");
      return;
    }
    setLoading(true);
    try {
      await solicitarCodigoRecuperacion(correo);
      setSuccessMsg(`Te enviamos un código a ${correo}`);
      setPasoActivo(1);
    } catch (e: any) {
      const apiMsg = e?.response?.data?.message;
      if (e?.response?.status === 404) {
        setErrorMsg(apiMsg ?? "No encontramos una cuenta con ese correo");
      } else {
        setErrorMsg(apiMsg ?? "No pudimos enviar el código. Intenta de nuevo.");
      }
    } finally {
      setLoading(false);
    }
  };

  // ─── Paso 2: validar formato del código localmente y avanzar ──────────
  const continuarConCodigo = () => {
    limpiarMensajes();
    if (codigo.length !== 6) {
      setErrorMsg("El código debe tener 6 dígitos");
      return;
    }
    setPasoActivo(2);
  };

  // ─── Paso 3: enviar nueva contraseña con el código ────────────────────
  const guardarNuevaContrasena = async () => {
    limpiarMensajes();
    if (!nuevaContrasena || nuevaContrasena.length < 8) {
      setErrorMsg("La nueva contraseña debe tener al menos 8 caracteres");
      return;
    }
    if (nuevaContrasena !== confirmarContrasena) {
      setErrorMsg("Las contraseñas no coinciden");
      return;
    }
    setLoading(true);
    try {
      await recuperarContrasenaConCodigo(correo, codigo, nuevaContrasena);
      setSuccessMsg("Contraseña actualizada. Redirigiendo a login...");
      setTimeout(() => navigate("/login"), 1500);
    } catch (e: any) {
      const apiMsg = e?.response?.data?.message;
      if (e?.response?.status === 401) {
        setErrorMsg(apiMsg ?? "Código incorrecto. Verifica e intenta de nuevo.");
        // Devolver al paso 2 para que vuelva a ingresar el código.
        setPasoActivo(1);
      } else if (e?.response?.status === 404) {
        setErrorMsg(apiMsg ?? "No encontramos una cuenta con ese correo");
        setPasoActivo(0);
      } else {
        setErrorMsg(apiMsg ?? "No pudimos actualizar la contraseña.");
      }
    } finally {
      setLoading(false);
    }
  };

  // ─── Render de cada paso ───────────────────────────────────────────────
  const renderPaso = () => {
    if (pasoActivo === 0) {
      return (
        <Stack spacing={2}>
          <Typography
            sx={{ color: "var(--coco-text-secondary)", fontSize: 14 }}
          >
            Ingresa el correo asociado a tu cuenta. Te enviaremos un código
            de 6 dígitos para que puedas restablecer tu contraseña.
          </Typography>
          <TextField
            fullWidth
            label="Correo electrónico"
            type="email"
            value={correo}
            onChange={(e) => setCorreo(e.target.value)}
            autoFocus
          />
          <Button
            fullWidth
            variant="contained"
            color="secondary"
            size="large"
            onClick={solicitarCodigo}
            disabled={loading}
            sx={{ paddingY: 1.5 }}
          >
            {loading ? (
              <CircularProgress size={22} color="inherit" />
            ) : (
              "Enviar código"
            )}
          </Button>
        </Stack>
      );
    }

    if (pasoActivo === 1) {
      return (
        <Stack spacing={2}>
          <Typography
            sx={{ color: "var(--coco-text-secondary)", fontSize: 14 }}
          >
            Ingresa el código de 6 dígitos que enviamos a {correo}.
          </Typography>
          <TextField
            fullWidth
            label="Código"
            value={codigo}
            onChange={(e) =>
              setCodigo(e.target.value.replace(/\D/g, "").slice(0, 6))
            }
            inputProps={{
              style: {
                textAlign: "center",
                fontSize: 22,
                letterSpacing: 8,
                fontWeight: 500,
              },
              maxLength: 6,
            }}
            autoFocus
          />
          <Stack direction="row" spacing={1}>
            <Button
              fullWidth
              variant="outlined"
              size="large"
              onClick={() => {
                limpiarMensajes();
                setPasoActivo(0);
              }}
            >
              Atrás
            </Button>
            <Button
              fullWidth
              variant="contained"
              color="secondary"
              size="large"
              onClick={continuarConCodigo}
              disabled={codigo.length !== 6}
            >
              Continuar
            </Button>
          </Stack>
        </Stack>
      );
    }

    // pasoActivo === 2
    return (
      <Stack spacing={2}>
        <Typography sx={{ color: "var(--coco-text-secondary)", fontSize: 14 }}>
          Define tu nueva contraseña. Asegúrate de que tenga al menos 8
          caracteres.
        </Typography>
        <TextField
          fullWidth
          label="Nueva contraseña"
          type="password"
          value={nuevaContrasena}
          onChange={(e) => setNuevaContrasena(e.target.value)}
          autoFocus
        />
        <TextField
          fullWidth
          label="Confirmar nueva contraseña"
          type="password"
          value={confirmarContrasena}
          onChange={(e) => setConfirmarContrasena(e.target.value)}
        />
        <Stack direction="row" spacing={1}>
          <Button
            fullWidth
            variant="outlined"
            size="large"
            onClick={() => {
              limpiarMensajes();
              setPasoActivo(1);
            }}
            disabled={loading}
          >
            Atrás
          </Button>
          <Button
            fullWidth
            variant="contained"
            color="secondary"
            size="large"
            onClick={guardarNuevaContrasena}
            disabled={loading}
          >
            {loading ? (
              <CircularProgress size={22} color="inherit" />
            ) : (
              "Guardar"
            )}
          </Button>
        </Stack>
      </Stack>
    );
  };

  return (
    <>
      <NavbarEcommerce />
      <Box
        sx={{
          backgroundColor: "var(--coco-bg)",
          minHeight: "calc(100vh - 64px)",
          paddingY: 6,
        }}
      >
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <Box
            sx={{
              maxWidth: 520,
              margin: "0 auto",
              padding: { xs: 3, md: 4 },
              backgroundColor: "var(--coco-surface)",
              borderRadius: 3,
              border: "1px solid var(--coco-border)",
            }}
          >
            <Typography
              variant="h5"
              sx={{ fontWeight: 600, marginBottom: 3, textAlign: "center" }}
            >
              Recuperar contraseña
            </Typography>

            <Stepper activeStep={pasoActivo} sx={{ marginBottom: 3 }}>
              {PASOS.map((label) => (
                <Step key={label}>
                  <StepLabel>{label}</StepLabel>
                </Step>
              ))}
            </Stepper>

            {errorMsg && (
              <Alert severity="error" sx={{ marginBottom: 2 }}>
                {errorMsg}
              </Alert>
            )}
            {successMsg && (
              <Alert severity="success" sx={{ marginBottom: 2 }}>
                {successMsg}
              </Alert>
            )}

            {renderPaso()}
          </Box>
        </motion.div>
      </Box>
    </>
  );
};

export default RecuperarContrasena;
