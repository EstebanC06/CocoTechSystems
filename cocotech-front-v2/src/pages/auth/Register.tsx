/**
 * Página de registro de nuevo cliente.
 * Envía datos a /cliente/crear del backend.
 */
import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Box,
  Button,
  TextField,
  Typography,
  Alert,
  CircularProgress,
  Grid,
} from "@mui/material";
import { useNavigate, Link as RouterLink } from "react-router-dom";
import { motion } from "framer-motion";
import NavbarEcommerce from "../../components/layout/NavbarEcommerce";
import { registrarCliente } from "../../services/auth.service";
import CampoContrasena from "../../components/common/CampoContrasena";

const registroSchema = z
  .object({
    nombres: z.string().nonempty("Nombre requerido").min(2, "Mínimo 2 caracteres"),
    apellidos: z.string().nonempty("Apellidos requeridos").min(2, "Mínimo 2 caracteres"),
    correo: z.string().nonempty("Correo requerido").email("Correo inválido"),
    contrasena: z
      .string()
      .min(8, "Mínimo 8 caracteres")
      .regex(/[A-Z]/, "Debe contener mayúscula")
      .regex(/[0-9]/, "Debe contener número"),
    confirmar: z.string(),
    telefono: z.string().regex(/^3\d{9}$/, "Teléfono colombiano (10 dígitos, inicia en 3)"),
    calle: z.string().nonempty("Dirección requerida"),
    barrio: z.string().nonempty("Barrio requerido"),
    ciudad: z.string().nonempty("Ciudad requerida"),
  })
  .refine((data) => data.contrasena === data.confirmar, {
    message: "Las contraseñas no coinciden",
    path: ["confirmar"],
  });

type RegistroForm = z.infer<typeof registroSchema>;

const Register = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [okMsg, setOkMsg] = useState("");

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegistroForm>({
    resolver: zodResolver(registroSchema),
    mode: "onSubmit",
  });

  const onSubmit = async (data: RegistroForm) => {
    setLoading(true);
    setErrorMsg("");
    setOkMsg("");
    try {
      await registrarCliente({
        nombres: data.nombres,
        apellidos: data.apellidos,
        correo: data.correo,
        contrasena: data.contrasena,
        telefono: data.telefono,
        calle: data.calle,
        barrio: data.barrio,
        ciudad: data.ciudad,
      });
      setOkMsg("¡Cuenta creada! Revisa tu correo para verificar.");
      setTimeout(
        () =>
          navigate(`/verificar?correo=${encodeURIComponent(data.correo)}`, {
            state: { contrasena: data.contrasena },
          }),
        1500,
      );
    } catch (e: any) {
      setErrorMsg(e?.response?.data?.message ?? "Error al crear la cuenta.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <NavbarEcommerce />
      <Box sx={{ backgroundColor: "var(--coco-bg)", minHeight: "calc(100vh - 64px)", paddingY: 4 }}>
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <Box
            sx={{
              maxWidth: 640,
              margin: "0 auto",
              padding: { xs: 3, md: 4 },
              backgroundColor: "var(--coco-surface)",
              borderRadius: 3,
              border: "1px solid var(--coco-border)",
            }}
          >
            <Typography variant="h5" sx={{ fontWeight: 600, marginBottom: 0.5 }}>
              Crear cuenta
            </Typography>
            <Typography sx={{ color: "var(--coco-text-secondary)", fontSize: 14, marginBottom: 3 }}>
              Únete a CocoTech y empieza a aprovechar todos los beneficios.
            </Typography>

            {errorMsg && <Alert severity="error" sx={{ marginBottom: 2 }}>{errorMsg}</Alert>}
            {okMsg && <Alert severity="success" sx={{ marginBottom: 2 }}>{okMsg}</Alert>}

            <Box component="form" onSubmit={handleSubmit(onSubmit)}>
              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField fullWidth label="Nombres" {...register("nombres")} error={!!errors.nombres} helperText={errors.nombres?.message} />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField fullWidth label="Apellidos" {...register("apellidos")} error={!!errors.apellidos} helperText={errors.apellidos?.message} />
                </Grid>
                <Grid size={12}>
                  <TextField fullWidth label="Correo electrónico" type="email" {...register("correo")} error={!!errors.correo} helperText={errors.correo?.message} />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField fullWidth label="Contraseña" type="password" {...register("contrasena")} error={!!errors.contrasena} helperText={errors.contrasena?.message} />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField fullWidth label="Confirmar contraseña" type="password" {...register("confirmar")} error={!!errors.confirmar} helperText={errors.confirmar?.message} />
                </Grid>
                <Grid size={12}>
                  <TextField fullWidth label="Teléfono" placeholder="3001234567" {...register("telefono")} error={!!errors.telefono} helperText={errors.telefono?.message} />
                </Grid>
                <Grid size={12}>
                  <TextField fullWidth label="Calle / dirección" {...register("calle")} error={!!errors.calle} helperText={errors.calle?.message} />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField fullWidth label="Barrio" {...register("barrio")} error={!!errors.barrio} helperText={errors.barrio?.message} />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <TextField fullWidth label="Ciudad" {...register("ciudad")} error={!!errors.ciudad} helperText={errors.ciudad?.message} />
                </Grid>
              </Grid>

              <Button
                type="submit"
                fullWidth
                variant="contained"
                color="secondary"
                size="large"
                disabled={loading}
                sx={{ marginTop: 3, paddingY: 1.5 }}
              >
                {loading ? <CircularProgress size={22} color="inherit" /> : "Crear cuenta"}
              </Button>

              <Typography sx={{ textAlign: "center", marginTop: 3, fontSize: 14, color: "var(--coco-text-secondary)" }}>
                ¿Ya tienes cuenta? <RouterLink to="/login" style={{ fontWeight: 500 }}>Inicia sesión</RouterLink>
              </Typography>
            </Box>
          </Box>
        </motion.div>
      </Box>
    </>
  );
};

export default Register;
