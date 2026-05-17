/**
 * Direcciones guardadas del cliente.
 * CRUD completo con marcado de "predeterminada".
 */
import { useEffect, useState } from "react";
import {
  Box, Grid, Typography, TextField, Button, IconButton, Alert,
  Dialog, DialogTitle, DialogContent, DialogActions, CircularProgress, Chip,
  FormControlLabel, Checkbox,
} from "@mui/material";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faPlus, faPenToSquare, faTrash, faMapLocationDot, faStar,
} from "@fortawesome/free-solid-svg-icons";
import LayoutEcommerce from "../../components/layout/LayoutEcommerce";
import {
  obtenerDireccionesCliente, crearDireccion, actualizarDireccion,
  eliminarDireccion, marcarPredeterminada,
} from "../../services/direccion.service";
import { useAuth } from "../../context/AuthContext";
import type { DireccionClienteDTO } from "../../types";

const direccionVacia = (idCliente: number): DireccionClienteDTO => ({
  idCliente, alias: "", calle: "", barrio: "", ciudad: "", referencia: "", predeterminada: false,
});

const MisDirecciones = () => {
  const { sesion } = useAuth();
  const [direcciones, setDirecciones] = useState<DireccionClienteDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(false);
  const [edit, setEdit] = useState<DireccionClienteDTO | null>(null);
  const [nuevo, setNuevo] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState("");

  const cargar = async () => {
    if (!sesion) return;
    setLoading(true);
    try {
      setDirecciones(await obtenerDireccionesCliente(sesion.id));
    } catch {
      setDirecciones([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { cargar(); }, [sesion]);

  const abrirNuevo = () => {
    if (!sesion) return;
    setEdit(direccionVacia(sesion.id));
    setNuevo(true);
    setModal(true);
  };

  const abrirEditar = (d: DireccionClienteDTO) => {
    setEdit({ ...d });
    setNuevo(false);
    setModal(true);
  };

  const guardar = async () => {
    if (!edit) return;
    setGuardando(true);
    setError("");
    try {
      if (nuevo) await crearDireccion(edit);
      else if (edit.idDireccion) await actualizarDireccion(edit.idDireccion, edit);
      setModal(false);
      cargar();
    } catch (e: any) {
      setError(e?.response?.data?.message ?? "Error al guardar.");
    } finally {
      setGuardando(false);
    }
  };

  const eliminar = async (id: number) => {
    if (!confirm("¿Eliminar esta dirección?")) return;
    try {
      await eliminarDireccion(id);
      cargar();
    } catch (e: any) {
      alert(e?.response?.data?.message ?? "Error al eliminar.");
    }
  };

  const marcar = async (id: number) => {
    try {
      await marcarPredeterminada(id);
      cargar();
    } catch (e: any) {
      alert(e?.response?.data?.message ?? "Error.");
    }
  };

  return (
    <LayoutEcommerce>
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 3, flexWrap: "wrap", gap: 2 }}>
        <Box>
          <Typography sx={{ fontSize: 26, fontWeight: 600 }}>Mis direcciones</Typography>
          <Typography sx={{ color: "var(--coco-text-secondary)" }}>
            Guarda direcciones para agilizar tus compras
          </Typography>
        </Box>
        <Button variant="contained" color="secondary" startIcon={<FontAwesomeIcon icon={faPlus} />} onClick={abrirNuevo}>
          Nueva dirección
        </Button>
      </Box>

      {loading ? (
        <Box sx={{ textAlign: "center", padding: 6 }}><CircularProgress /></Box>
      ) : direcciones.length === 0 ? (
        <Box className="coco-card" sx={{ textAlign: "center", padding: 6 }}>
          <FontAwesomeIcon icon={faMapLocationDot} style={{ fontSize: 48, color: "var(--coco-text-muted)" }} />
          <Typography sx={{ marginTop: 2 }}>No tienes direcciones guardadas.</Typography>
        </Box>
      ) : (
        <Grid container spacing={2}>
          {direcciones.map((d) => (
            <Grid size={{ xs: 12, sm: 6 }} key={d.idDireccion}>
              <Box className="coco-card" sx={{ position: "relative" }}>
                {d.predeterminada && (
                  <Chip
                    label="Predeterminada"
                    size="small"
                    icon={<FontAwesomeIcon icon={faStar} style={{ fontSize: 10, color: "var(--coco-secondary)" }} />}
                    sx={{
                      position: "absolute", top: 12, right: 12,
                      backgroundColor: "var(--coco-warning-fill)", color: "var(--coco-warning)",
                      fontSize: 10, fontWeight: 600,
                    }}
                  />
                )}
                <Typography sx={{ fontWeight: 600, marginBottom: 0.5 }}>{d.alias}</Typography>
                <Typography sx={{ fontSize: 13, color: "var(--coco-text-secondary)", marginBottom: 0.5 }}>
                  {d.calle}
                </Typography>
                <Typography sx={{ fontSize: 12, color: "var(--coco-text-muted)" }}>
                  {d.barrio}, {d.ciudad}
                </Typography>
                {d.referencia && (
                  <Typography sx={{ fontSize: 11, color: "var(--coco-text-muted)", marginTop: 0.5, fontStyle: "italic" }}>
                    {d.referencia}
                  </Typography>
                )}
                <Box sx={{ display: "flex", gap: 0.5, marginTop: 2 }}>
                  <IconButton size="small" onClick={() => abrirEditar(d)} sx={{ color: "var(--coco-primary)" }}>
                    <FontAwesomeIcon icon={faPenToSquare} style={{ fontSize: 13 }} />
                  </IconButton>
                  <IconButton size="small" onClick={() => eliminar(d.idDireccion!)} sx={{ color: "var(--coco-danger)" }}>
                    <FontAwesomeIcon icon={faTrash} style={{ fontSize: 13 }} />
                  </IconButton>
                  {!d.predeterminada && (
                    <Button size="small" onClick={() => marcar(d.idDireccion!)} sx={{ marginLeft: "auto", fontSize: 11 }}>
                      Marcar como principal
                    </Button>
                  )}
                </Box>
              </Box>
            </Grid>
          ))}
        </Grid>
      )}

      <Dialog open={modal} onClose={() => setModal(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{nuevo ? "Nueva dirección" : "Editar dirección"}</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ marginBottom: 2 }}>{error}</Alert>}
          {edit && (
            <Grid container spacing={2} sx={{ marginTop: 0 }}>
              <Grid size={12}>
                <TextField fullWidth label="Alias (ej: Casa, Oficina)" value={edit.alias} onChange={(e) => setEdit({ ...edit, alias: e.target.value })} />
              </Grid>
              <Grid size={12}>
                <TextField fullWidth label="Dirección" value={edit.calle} onChange={(e) => setEdit({ ...edit, calle: e.target.value })} />
              </Grid>
              <Grid size={6}>
                <TextField fullWidth label="Barrio" value={edit.barrio} onChange={(e) => setEdit({ ...edit, barrio: e.target.value })} />
              </Grid>
              <Grid size={6}>
                <TextField fullWidth label="Ciudad" value={edit.ciudad} onChange={(e) => setEdit({ ...edit, ciudad: e.target.value })} />
              </Grid>
              <Grid size={12}>
                <TextField fullWidth label="Referencia (opcional)" value={edit.referencia} onChange={(e) => setEdit({ ...edit, referencia: e.target.value })} />
              </Grid>
              <Grid size={12}>
                <FormControlLabel
                  control={<Checkbox checked={edit.predeterminada} onChange={(e) => setEdit({ ...edit, predeterminada: e.target.checked })} />}
                  label="Marcar como predeterminada"
                />
              </Grid>
            </Grid>
          )}
        </DialogContent>
        <DialogActions sx={{ padding: 2 }}>
          <Button onClick={() => setModal(false)}>Cancelar</Button>
          <Button variant="contained" color="secondary" onClick={guardar} disabled={guardando}>
            {guardando ? <CircularProgress size={20} color="inherit" /> : "Guardar"}
          </Button>
        </DialogActions>
      </Dialog>
    </LayoutEcommerce>
  );
};

export default MisDirecciones;
