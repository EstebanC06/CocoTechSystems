/**
 * Página de Login.
 * Usa react-hook-form + zod para validación y se conecta con
 * /auth/login del backend a través de auth.service.
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
} from "@mui/material";
import { useNavigate, Link as RouterLink } from "react-router-dom";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faBoxesStacked,
  faFileInvoiceDollar,
  faChartLine,
} from "@fortawesome/free-solid-svg-icons";
import Logo from "../../components/common/Logo";
import ToggleTema from "../../components/common/ToggleTema";
import { login } from "../../services/auth.service";
import { useAuth } from "../../context/AuthContext";

const loginSchema = z.object({
  username: z
    .string()
    .nonempty("El correo es obligatorio")
    .email("Correo inválido"),
  password: z.string().nonempty("La contraseña es obligatoria"),
});

type LoginForm = z.infer<typeof loginSchema>;

const Login = () => {
  const navigate = useNavigate();
  const { iniciarSesion } = useAuth();
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string>("");

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
    mode: "onSubmit",
  });

  const onSubmit = async (data: LoginForm) => {
    setLoading(true);
    setErrorMsg("");
    try {
      const respuesta = await login({
        correo: data.username,        // tu form usa "username" como nombre del campo de input, está bien
        contrasena: data.password,
      });
      iniciarSesion({
        token: respuesta.token,
        correo: respuesta.correo,
        rol: respuesta.rol,
        id: respuesta.id,
      });
      // Redirección inteligente: respeta ?redirect=... y luego rol
      const params = new URLSearchParams(window.location.search);
      const redirect = params.get("redirect");
      if (redirect) {
        navigate(redirect);
      } else if (respuesta.rol === "ROLE_ADMIN") {
        navigate("/admin");
      } else if (respuesta.rol === "ROLE_EMPLEADO") {
        navigate("/empleado");
      } else {
        navigate("/");
      }
    } catch (e: any) {
      setErrorMsg(
        e?.response?.data?.message ?? "Credenciales incorrectas o servidor inactivo."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ minHeight: "100vh", display: "flex", flexDirection: { xs: "column", md: "row" } }}>
      {/* Panel izquierdo decorativo */}
      <Box
        sx={{
          flex: 1,
          backgroundColor: "var(--coco-primary)",
          color: "#FFFFFF",
          padding: { xs: "2rem", md: "3rem" },
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          minHeight: { xs: 280, md: "100vh" },
        }}
      >
        <Box sx={{ marginBottom: 4 }}>
          <Logo size="lg" color="#FFFFFF" />
        </Box>
        <Typography variant="h4" sx={{ fontWeight: 600, marginBottom: 1.5, fontSize: { xs: 24, md: 32 } }}>
          Bienvenido de vuelta
        </Typography>
        <Typography sx={{ color: "var(--coco-accent)", marginBottom: 4, fontSize: { xs: 14, md: 16 }, lineHeight: 1.6 }}>
          Sistema integral para la gestión de tu supermercado.
        </Typography>
        <Box sx={{ display: { xs: "none", md: "flex" }, flexDirection: "column", gap: 2, fontSize: 14 }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
            <FontAwesomeIcon icon={faBoxesStacked} />
            <span>Gestión de inventario en tiempo real</span>
          </Box>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
            <FontAwesomeIcon icon={faFileInvoiceDollar} />
            <span>Facturación electrónica</span>
          </Box>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
            <FontAwesomeIcon icon={faChartLine} />
            <span>Reportes y analíticas avanzadas</span>
          </Box>
        </Box>
      </Box>

      {/* Panel derecho con formulario */}
      <Box
        sx={{
          flex: 1,
          backgroundColor: "var(--coco-bg)",
          padding: { xs: "2rem 1.5rem", md: "3rem" },
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          position: "relative",
        }}
      >
        <Box sx={{ position: "absolute", top: 16, right: 16 }}>
          <ToggleTema />
        </Box>

        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <Box sx={{ maxWidth: 420, margin: "0 auto", width: "100%" }}>
            <Typography variant="h5" sx={{ fontWeight: 600, marginBottom: 0.5 }}>
              Iniciar sesión
            </Typography>
            <Typography sx={{ color: "var(--coco-text-secondary)", fontSize: 14, marginBottom: 3 }}>
              Ingresa con tu cuenta para continuar
            </Typography>

            {errorMsg && (
              <Alert severity="error" sx={{ marginBottom: 2 }}>
                {errorMsg}
              </Alert>
            )}

            <Box component="form" onSubmit={handleSubmit(onSubmit)}>
              <TextField
                fullWidth
                label="Correo electrónico"
                placeholder="cliente@cocotech.co"
                {...register("username")}
                error={!!errors.username}
                helperText={errors.username?.message}
                sx={{ marginBottom: 2 }}
              />
              <TextField
                fullWidth
                label="Contraseña"
                type="password"
                placeholder="••••••••"
                {...register("password")}
                error={!!errors.password}
                helperText={errors.password?.message}
                sx={{ marginBottom: 1 }}
              />

              <Box sx={{ textAlign: "right", marginBottom: 3 }}>
                <RouterLink to="/recuperar" style={{ fontSize: 13 }}>
                  ¿Olvidaste tu contraseña?
                </RouterLink>
              </Box>

              <Button
                type="submit"
                fullWidth
                variant="contained"
                color="secondary"
                size="large"
                disabled={loading}
                sx={{ paddingY: 1.5, fontSize: 15 }}
              >
                {loading ? <CircularProgress size={22} color="inherit" /> : "Iniciar sesión"}
              </Button>

              <Typography sx={{ textAlign: "center", marginTop: 3, fontSize: 14, color: "var(--coco-text-secondary)" }}>
                ¿No tienes cuenta?{" "}
                <RouterLink to="/register" style={{ fontWeight: 500 }}>
                  Regístrate
                </RouterLink>
              </Typography>
            </Box>
          </Box>
        </motion.div>
      </Box>
    </Box>
  );
};

export default Login;
