/**
 * Página de verificación de código (tras el registro o recuperación).
 * Pide al usuario el código que recibió por correo.
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
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import NavbarEcommerce from "../../components/layout/NavbarEcommerce";

const VerificarCodigo = () => {
  const navigate = useNavigate();
  const [codigo, setCodigo] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const verificar = async () => {
    if (codigo.length !== 6) {
      setErrorMsg("El código debe tener 6 dígitos");
      return;
    }
    setLoading(true);
    try {
      // Aquí iría la llamada al endpoint /auth/verificarCodigo cuando esté disponible.
      await new Promise((r) => setTimeout(r, 1000));
      navigate("/login");
    } catch (e: any) {
      setErrorMsg("Código inválido o expirado");
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
              textAlign: "center",
            }}
          >
            <Typography variant="h5" sx={{ fontWeight: 600, marginBottom: 1 }}>
              Verificar correo electrónico
            </Typography>
            <Typography sx={{ color: "var(--coco-text-secondary)", fontSize: 14, marginBottom: 3 }}>
              Te enviamos un código de 6 dígitos a tu correo. Ingrésalo abajo para confirmar tu cuenta.
            </Typography>

            {errorMsg && <Alert severity="error" sx={{ marginBottom: 2 }}>{errorMsg}</Alert>}

            <TextField
              fullWidth
              label="Código de verificación"
              value={codigo}
              onChange={(e) => setCodigo(e.target.value.replace(/\D/g, "").slice(0, 6))}
              inputProps={{
                style: { textAlign: "center", fontSize: 22, letterSpacing: 8, fontWeight: 500 },
                maxLength: 6,
              }}
              sx={{ marginBottom: 3 }}
            />

            <Button
              fullWidth
              variant="contained"
              color="secondary"
              size="large"
              onClick={verificar}
              disabled={loading}
              sx={{ paddingY: 1.5 }}
            >
              {loading ? <CircularProgress size={22} color="inherit" /> : "Verificar"}
            </Button>

            <Typography sx={{ marginTop: 3, fontSize: 13, color: "var(--coco-text-secondary)" }}>
              ¿No recibiste el código?{" "}
              <span style={{ color: "var(--coco-primary)", cursor: "pointer", fontWeight: 500 }}>
                Reenviar
              </span>
            </Typography>
          </Box>
        </motion.div>
      </Box>
    </>
  );
};

export default VerificarCodigo;
