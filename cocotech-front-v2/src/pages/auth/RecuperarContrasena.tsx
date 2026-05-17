/**
 * Página de recuperación de contraseña.
 * Conecta con /auth/recuperarContrasenaCliente del backend.
 */
import { useState } from "react";
import {
  Box,
  Button,
  TextField,
  Typography,
  Alert,
  CircularProgress,
} from "@mui/material";
import { Link as RouterLink } from "react-router-dom";
import { motion } from "framer-motion";
import NavbarEcommerce from "../../components/layout/NavbarEcommerce";
import { recuperarContrasenaCliente } from "../../services/auth.service";

const RecuperarContrasena = () => {
  const [correo, setCorreo] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [okMsg, setOkMsg] = useState("");

  const recuperar = async () => {
    if (!correo) {
      setErrorMsg("Ingresa tu correo");
      return;
    }
    setLoading(true);
    setErrorMsg("");
    setOkMsg("");
    try {
      await recuperarContrasenaCliente(correo);
      setOkMsg("Te enviamos un código a tu correo electrónico.");
    } catch (e: any) {
      setErrorMsg(e?.response?.data?.message ?? "Error al solicitar recuperación.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <NavbarEcommerce />
      <Box sx={{ backgroundColor: "var(--coco-bg)", minHeight: "calc(100vh - 64px)", paddingY: 6 }}>
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <Box
            sx={{
              maxWidth: 480,
              margin: "0 auto",
              padding: { xs: 3, md: 4 },
              backgroundColor: "var(--coco-surface)",
              borderRadius: 3,
              border: "1px solid var(--coco-border)",
            }}
          >
            <Typography variant="h5" sx={{ fontWeight: 600, marginBottom: 1 }}>
              Recuperar contraseña
            </Typography>
            <Typography sx={{ color: "var(--coco-text-secondary)", fontSize: 14, marginBottom: 3 }}>
              Ingresa tu correo y te enviaremos un código para restablecer tu contraseña.
            </Typography>

            {errorMsg && <Alert severity="error" sx={{ marginBottom: 2 }}>{errorMsg}</Alert>}
            {okMsg && <Alert severity="success" sx={{ marginBottom: 2 }}>{okMsg}</Alert>}

            <TextField
              fullWidth
              label="Correo electrónico"
              type="email"
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
              sx={{ marginBottom: 2 }}
            />

            <Button
              fullWidth
              variant="contained"
              color="secondary"
              size="large"
              onClick={recuperar}
              disabled={loading}
              sx={{ paddingY: 1.5 }}
            >
              {loading ? <CircularProgress size={22} color="inherit" /> : "Enviar código"}
            </Button>

            <Typography sx={{ textAlign: "center", marginTop: 3, fontSize: 14 }}>
              <RouterLink to="/login">Volver al login</RouterLink>
            </Typography>
          </Box>
        </motion.div>
      </Box>
    </>
  );
};

export default RecuperarContrasena;
