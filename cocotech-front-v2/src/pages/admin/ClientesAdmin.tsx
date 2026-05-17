/**
 * Gestión de clientes (vista admin).
 *
 * Solo lectura. El admin puede ver y buscar clientes, pero NO sus datos
 * personales sensibles (teléfono, dirección detallada). Eso lo edita
 * cada cliente desde su propio perfil.
 *
 * Campos mostrados al admin:
 *  - Nombre completo
 *  - Correo (identificador de cuenta)
 *  - Ciudad (referencia geográfica general)
 *
 * Campos NO mostrados (privacidad):
 *  - Contraseña (el back nunca la devuelve, igual nunca llega aquí)
 *  - Teléfono
 *  - Calle, barrio (dirección detallada)
 */
import { useEffect, useState } from "react";
import {
  Box,
  Typography,
  TextField,
  InputAdornment,
  CircularProgress,
  Avatar,
  Chip,
  Grid,
} from "@mui/material";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faMagnifyingGlass,
  faEnvelope,
  faLocationDot,
} from "@fortawesome/free-solid-svg-icons";
import LayoutAdmin from "../../components/layout/LayoutAdmin";
import { obtenerClientes } from "../../services/cliente.service";
import type { ClienteDTO } from "../../types";

const ClientesAdmin = () => {
  const [clientes, setClientes] = useState<ClienteDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [busqueda, setBusqueda] = useState("");

  useEffect(() => {
    obtenerClientes()
      .then(setClientes)
      .catch(() => setClientes([]))
      .finally(() => setLoading(false));
  }, []);

  const filtrados = clientes.filter((c) => {
    if (!busqueda) return true;
    const t = busqueda.toLowerCase();
    return (
      `${c.nombres} ${c.apellidos}`.toLowerCase().includes(t) ||
      c.correo.toLowerCase().includes(t) ||
      (c.ciudad?.toLowerCase().includes(t) ?? false)
    );
  });

  return (
    <LayoutAdmin>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: 3,
          flexWrap: "wrap",
          gap: 2,
        }}
      >
        <Box>
          <Typography sx={{ fontSize: 26, fontWeight: 600 }}>Clientes</Typography>
          <Typography sx={{ color: "var(--coco-text-secondary)" }}>
            {clientes.length} clientes registrados
          </Typography>
        </Box>
        <TextField
          size="small"
          placeholder="Buscar por nombre, correo o ciudad..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          sx={{ minWidth: 320 }}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <FontAwesomeIcon icon={faMagnifyingGlass} />
              </InputAdornment>
            ),
          }}
        />
      </Box>

      {loading ? (
        <Box sx={{ textAlign: "center", padding: 6 }}>
          <CircularProgress />
        </Box>
      ) : (
        <Grid container spacing={2}>
          {filtrados.map((c) => (
            <Grid size={{ xs: 12, sm: 6, md: 4 }} key={c.id}>
              <Box className="coco-card">
                <Box sx={{ display: "flex", alignItems: "center", gap: 2, marginBottom: 1.5 }}>
                  <Avatar
                    sx={{
                      bgcolor: "var(--coco-primary)",
                      width: 40,
                      height: 40,
                      fontSize: 13,
                    }}
                  >
                    {`${c.nombres?.[0] ?? ""}${c.apellidos?.[0] ?? ""}`.toUpperCase()}
                  </Avatar>
                  <Box sx={{ flex: 1, minWidth: 0 }}>
                    <Typography sx={{ fontWeight: 600, fontSize: 14 }}>
                      {c.nombres} {c.apellidos}
                    </Typography>
                    <Chip
                      label={`#${c.id}`}
                      size="small"
                      sx={{ fontSize: 10, height: 16 }}
                    />
                  </Box>
                </Box>

                <Box sx={{ display: "flex", flexDirection: "column", gap: 0.5, fontSize: 12 }}>
                  <Box
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      gap: 1,
                      color: "var(--coco-text-secondary)",
                    }}
                    title={c.correo}
                  >
                    <FontAwesomeIcon icon={faEnvelope} style={{ fontSize: 11, width: 14 }} />
                    <span
                      style={{
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        whiteSpace: "nowrap",
                      }}
                    >
                      {c.correo}
                    </span>
                  </Box>
                  <Box
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      gap: 1,
                      color: "var(--coco-text-secondary)",
                    }}
                  >
                    <FontAwesomeIcon icon={faLocationDot} style={{ fontSize: 11, width: 14 }} />
                    {c.ciudad || "Sin ciudad registrada"}
                  </Box>
                </Box>
              </Box>
            </Grid>
          ))}
          {!loading && filtrados.length === 0 && (
            <Grid size={12}>
              <Box className="coco-card" sx={{ textAlign: "center", padding: 6 }}>
                <Typography sx={{ color: "var(--coco-text-secondary)" }}>
                  No se encontraron clientes.
                </Typography>
              </Box>
            </Grid>
          )}
        </Grid>
      )}
    </LayoutAdmin>
  );
};

export default ClientesAdmin;
