/**
 * CRUD de Sucursales (admin).
 * Conecta con /sucursal/* del backend.
 *
 * Cambios respecto a la versión anterior:
 *  - El campo "nombre" es un combobox restringido al enum NombreSucursal.
 *  - El campo "telefono" se renombró a "telefonoContacto" para alinear
 *    con el back.
 *  - El campo "calle" se renombró a "direccion" para alinear con el back.
 *  - La tabla muestra etiquetas legibles (ej. "Fontibón" en lugar de
 *    "FONTIBON").
 */
import { useEffect, useMemo, useState } from "react";
import {
  Box,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  Alert,
  Snackbar,
  CircularProgress,
  Grid,
  MenuItem,
} from "@mui/material";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faPenToSquare, faTrash } from "@fortawesome/free-solid-svg-icons";
import LayoutAdmin from "../../components/layout/LayoutAdmin";
import ModalConfirmacion from "../../components/common/ModalConfirmacion";
import {
  obtenerSucursales,
  crearSucursal,
  actualizarSucursal,
  eliminarSucursal,
} from "../../services/sucursal.service";
import {
  NOMBRES_SUCURSAL,
  ETIQUETAS_SUCURSAL,
  type SucursalDTO,
  type NombreSucursal,
} from "../../types";
import { etiquetaSucursal } from "../../utils/etiquetas";

const vacio: SucursalDTO = {
  nombre: "FONTIBON",
  telefonoContacto: "",
  ciudad: "",
  barrio: "",
  direccion: "",
};

const SucursalesAdmin = () => {
  const [items, setItems] = useState<SucursalDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(false);
  const [edit, setEdit] = useState<SucursalDTO>(vacio);
  const [nuevo, setNuevo] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [confirmar, setConfirmar] = useState<number | null>(null);
  const [eliminando, setEliminando] = useState(false);
  const [snack, setSnack] = useState<{ msg: string; tipo: "success" | "error" } | null>(null);

  const cargar = async () => {
    setLoading(true);
    try {
      setItems(await obtenerSucursales());
    } catch {
      setSnack({ msg: "Error al cargar", tipo: "error" });
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    cargar();
  }, []);

  const opcionesDisponibles = useMemo(() => {
    const usados = new Set<NombreSucursal>(items.map((s) => s.nombre));
    return NOMBRES_SUCURSAL.filter(
      (n) => !usados.has(n) || (!nuevo && n === edit.nombre)
    );
  }, [items, nuevo, edit.nombre]);

  const guardar = async () => {
    setGuardando(true);
    try {
      if (nuevo) await crearSucursal(edit);
      else if (edit.idSucursal) await actualizarSucursal(edit.idSucursal, edit);
      setSnack({
        msg: nuevo ? "Sucursal creada" : "Sucursal actualizada",
        tipo: "success",
      });
      setModal(false);
      cargar();
    } catch (e: any) {
      setSnack({ msg: e?.response?.data?.message ?? "Error al guardar", tipo: "error" });
    } finally {
      setGuardando(false);
    }
  };

  const eliminar = async () => {
    if (confirmar === null) return;
    setEliminando(true);
    try {
      await eliminarSucursal(confirmar);
      setSnack({ msg: "Sucursal eliminada", tipo: "success" });
      setConfirmar(null);
      cargar();
    } catch (e: any) {
      setSnack({ msg: e?.response?.data?.message ?? "Error al eliminar", tipo: "error" });
    } finally {
      setEliminando(false);
    }
  };

  const nombreBody = (row: SucursalDTO) => etiquetaSucursal(row.nombre);

  const acciones = (row: SucursalDTO) => (
    <Box sx={{ display: "flex", gap: 0.5 }}>
      <IconButton
        size="small"
        onClick={() => {
          setEdit({ ...row });
          setNuevo(false);
          setModal(true);
        }}
        sx={{ color: "var(--coco-primary)" }}
      >
        <FontAwesomeIcon icon={faPenToSquare} style={{ fontSize: 13 }} />
      </IconButton>
      <IconButton
        size="small"
        onClick={() => setConfirmar(row.idSucursal!)}
        sx={{ color: "var(--coco-danger)" }}
      >
        <FontAwesomeIcon icon={faTrash} style={{ fontSize: 13 }} />
      </IconButton>
    </Box>
  );

  return (
    <LayoutAdmin
      titulo="Sucursales"
      subtitulo={`${items.length} sucursales registradas`}
      acciones={
        <Button
          variant="contained"
          color="secondary"
          startIcon={<FontAwesomeIcon icon={faPlus} />}
          onClick={() => {
            const usados = new Set<NombreSucursal>(items.map((s) => s.nombre));
            const primero = NOMBRES_SUCURSAL.find((n) => !usados.has(n)) ?? "FONTIBON";
            setEdit({ ...vacio, nombre: primero });
            setNuevo(true);
            setModal(true);
          }}
          disabled={items.length >= NOMBRES_SUCURSAL.length}
        >
          Nueva sucursal
        </Button>
      }
    >
      <Box className="coco-card" sx={{ padding: 0, overflow: "hidden" }}>
        {loading ? (
          <Box sx={{ textAlign: "center", padding: 6 }}>
            <CircularProgress />
          </Box>
        ) : (
          <DataTable value={items} paginator rows={10} emptyMessage="No hay sucursales" stripedRows>
            <Column field="idSucursal" header="ID" sortable style={{ width: 80 }} />
            <Column field="nombre" header="Nombre" body={nombreBody} sortable />
            <Column field="telefonoContacto" header="Teléfono" />
            <Column field="direccion" header="Dirección" />
            <Column field="barrio" header="Barrio" />
            <Column field="ciudad" header="Ciudad" sortable />
            <Column header="Acciones" body={acciones} style={{ width: 110 }} />
          </DataTable>
        )}
      </Box>

      <Dialog open={modal} onClose={() => setModal(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{nuevo ? "Nueva sucursal" : "Editar sucursal"}</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ marginTop: 0 }}>
            <Grid size={12}>
              <TextField
                fullWidth
                select
                label="Nombre"
                value={edit.nombre}
                onChange={(e) =>
                  setEdit({ ...edit, nombre: e.target.value as NombreSucursal })
                }
              >
                {opcionesDisponibles.map((n) => (
                  <MenuItem key={n} value={n}>
                    {ETIQUETAS_SUCURSAL[n]}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid size={6}>
              <TextField
                fullWidth
                label="Teléfono"
                value={edit.telefonoContacto}
                onChange={(e) => setEdit({ ...edit, telefonoContacto: e.target.value })}
              />
            </Grid>
            <Grid size={6}>
              <TextField
                fullWidth
                label="Ciudad"
                value={edit.ciudad}
                onChange={(e) => setEdit({ ...edit, ciudad: e.target.value })}
              />
            </Grid>
            <Grid size={12}>
              <TextField
                fullWidth
                label="Dirección"
                value={edit.direccion}
                onChange={(e) => setEdit({ ...edit, direccion: e.target.value })}
              />
            </Grid>
            <Grid size={12}>
              <TextField
                fullWidth
                label="Barrio"
                value={edit.barrio}
                onChange={(e) => setEdit({ ...edit, barrio: e.target.value })}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions sx={{ padding: 2 }}>
          <Button onClick={() => setModal(false)}>Cancelar</Button>
          <Button onClick={guardar} variant="contained" color="secondary" disabled={guardando}>
            {guardando ? <CircularProgress size={20} color="inherit" /> : "Guardar"}
          </Button>
        </DialogActions>
      </Dialog>

      <ModalConfirmacion
        abierto={confirmar !== null}
        titulo="Eliminar sucursal"
        mensaje="¿Estás seguro?"
        onConfirmar={eliminar}
        onCancelar={() => setConfirmar(null)}
        cargando={eliminando}
      />

      <Snackbar
        open={snack !== null}
        autoHideDuration={3000}
        onClose={() => setSnack(null)}
        anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
      >
        {snack ? <Alert severity={snack.tipo}>{snack.msg}</Alert> : undefined}
      </Snackbar>
    </LayoutAdmin>
  );
};

export default SucursalesAdmin;
