/**
 * Página de verificación de código tras el registro.
 *
 * El usuario llega aquí con su correo como query param
 * (`/verificar?correo=...`) tras un registro exitoso. Recibe un código
 * de 6 dígitos en su email y debe ingresarlo para activar su cuenta.
 *
 * El código se valida contra el backend con `verificarCodigoRegistro`;
 * solo si la respuesta es exitosa se redirige a login.
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
import { useNavigate, useSearchParams } from "react-router-dom";
import { motion } from "framer-motion";
import NavbarEcommerce from "../../components/layout/NavbarEcommerce";
import { verificarCodigoRegistro } from "../../services/auth.service";

const VerificarCodigo = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const correo = searchParams.get("correo") ?? "";

  const [codigo, setCodigo] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  const verificar = async () => {
    setErrorMsg("");
    setSuccessMsg("");

    if (!correo) {
      setErrorMsg(
        "No se detectó el correo asociado. Vuelve a iniciar el registro.",
      );
      return;
    }
    if (codigo.length !== 6) {
      setErrorMsg("El código debe tener 6 dígitos");
      return;
    }
    setLoading(true);
    try {
      await verificarCodigoRegistro(correo, codigo);
      setSuccessMsg("Cuenta verificada correctamente. Redirigiendo a login...");
      setTimeout(() => navigate("/login"), 1500);
    } catch (e: any) {
      const apiMsg = e?.response?.data?.message;
      if (e?.response?.status === 401) {
        setErrorMsg(apiMsg ?? "Código incorrecto");
      } else if (e?.response?.status === 404) {
        setErrorMsg(apiMsg ?? "No encontramos una cuenta con ese correo");
      } else {
        setErrorMsg(apiMsg ?? "Error verificando el código. Intenta de nuevo.");
      }
    } finally {
      setLoading(false);
    }
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
            <Typography
              sx={{
                color: "var(--coco-text-secondary)",
                fontSize: 14,
                marginBottom: 3,
              }}
            >
              {correo
                ? `Enviamos un código de 6 dígitos a ${correo}. Ingrésalo abajo para confirmar tu cuenta.`
                : "Enviamos un código de 6 dígitos a tu correo. Ingrésalo abajo para confirmar tu cuenta."}
            </Typography>

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

            <TextField
              fullWidth
              label="Código de verificación"
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
              sx={{ marginBottom: 3 }}
            />

            <Button
              fullWidth
              variant="contained"
              color="secondary"
              size="large"
              onClick={verificar}
              disabled={loading || codigo.length !== 6}
              sx={{ paddingY: 1.5 }}
            >
              {loading ? (
                <CircularProgress size={22} color="inherit" />
              ) : (
                "Verificar"
              )}
            </Button>
          </Box>
        </motion.div>
      </Box>
    </>
  );
};

export default VerificarCodigo;
