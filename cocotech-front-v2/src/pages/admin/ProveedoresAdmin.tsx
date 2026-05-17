/**
 * CRUD de Proveedores (admin).
 * Conecta con /proveedor/* del backend.
 *
 * Cambios respecto a la versión anterior:
 *  - El campo "nombre" es un combobox restringido al enum NombreProveedor.
 *  - El campo "calle" se renombró a "direccion" para que coincida con el back.
 *  - La tabla muestra etiquetas legibles (ej. "Procter & Gamble" en lugar
 *    de "P_AND_G").
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
  obtenerProveedores,
  crearProveedor,
  actualizarProveedor,
  eliminarProveedor,
} from "../../services/proveedor.service";
import {
  NOMBRES_PROVEEDOR,
  ETIQUETAS_PROVEEDOR,
  type ProveedorDTO,
  type NombreProveedor,
} from "../../types";
import { etiquetaProveedor } from "../../utils/etiquetas";

const vacio: ProveedorDTO = {
  nombre: "P_AND_G",
  telefono: "",
  direccion: "",
  barrio: "",
  ciudad: "",
};

const ProveedoresAdmin = () => {
  const [items, setItems] = useState<ProveedorDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(false);
  const [edit, setEdit] = useState<ProveedorDTO>(vacio);
  const [nuevo, setNuevo] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [confirmar, setConfirmar] = useState<number | null>(null);
  const [eliminando, setEliminando] = useState(false);
  const [snack, setSnack] = useState<{ msg: string; tipo: "success" | "error" } | null>(null);

  const cargar = async () => {
    setLoading(true);
    try {
      setItems(await obtenerProveedores());
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
    const usados = new Set<NombreProveedor>(items.map((p) => p.nombre));
    return NOMBRES_PROVEEDOR.filter(
      (n) => !usados.has(n) || (!nuevo && n === edit.nombre)
    );
  }, [items, nuevo, edit.nombre]);

  const guardar = async () => {
    setGuardando(true);
    try {
      if (nuevo) await crearProveedor(edit);
      else if (edit.idProveedor) await actualizarProveedor(edit.idProveedor, edit);
      setSnack({
        msg: nuevo ? "Proveedor creado" : "Proveedor actualizado",
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
      await eliminarProveedor(confirmar);
      setSnack({ msg: "Proveedor eliminado", tipo: "success" });
      setConfirmar(null);
      cargar();
    } catch (e: any) {
      setSnack({ msg: e?.response?.data?.message ?? "Error al eliminar", tipo: "error" });
    } finally {
      setEliminando(false);
    }
  };

  const nombreBody = (row: ProveedorDTO) => etiquetaProveedor(row.nombre);

  const acciones = (row: ProveedorDTO) => (
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
        onClick={() => setConfirmar(row.idProveedor!)}
        sx={{ color: "var(--coco-danger)" }}
      >
        <FontAwesomeIcon icon={faTrash} style={{ fontSize: 13 }} />
      </IconButton>
    </Box>
  );

  return (
    <LayoutAdmin
      titulo="Proveedores"
      subtitulo={`${items.length} proveedores registrados`}
      acciones={
        <Button
          variant="contained"
          color="secondary"
          startIcon={<FontAwesomeIcon icon={faPlus} />}
          onClick={() => {
            const usados = new Set<NombreProveedor>(items.map((p) => p.nombre));
            const primero = NOMBRES_PROVEEDOR.find((n) => !usados.has(n)) ?? "P_AND_G";
            setEdit({ ...vacio, nombre: primero });
            setNuevo(true);
            setModal(true);
          }}
          disabled={items.length >= NOMBRES_PROVEEDOR.length}
        >
          Nuevo proveedor
        </Button>
      }
    >
      <Box className="coco-card" sx={{ padding: 0, overflow: "hidden" }}>
        {loading ? (
          <Box sx={{ textAlign: "center", padding: 6 }}>
            <CircularProgress />
          </Box>
        ) : (
          <DataTable value={items} paginator rows={10} emptyMessage="No hay proveedores" stripedRows>
            <Column field="idProveedor" header="ID" sortable style={{ width: 80 }} />
            <Column field="nombre" header="Nombre" body={nombreBody} sortable />
            <Column field="telefono" header="Teléfono" />
            <Column field="direccion" header="Dirección" />
            <Column field="barrio" header="Barrio" />
            <Column field="ciudad" header="Ciudad" sortable />
            <Column header="Acciones" body={acciones} style={{ width: 110 }} />
          </DataTable>
        )}
      </Box>

      <Dialog open={modal} onClose={() => setModal(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{nuevo ? "Nuevo proveedor" : "Editar proveedor"}</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ marginTop: 0 }}>
            <Grid size={12}>
              <TextField
                fullWidth
                select
                label="Nombre"
                value={edit.nombre}
                onChange={(e) =>
                  setEdit({ ...edit, nombre: e.target.value as NombreProveedor })
                }
                SelectProps={{
                  MenuProps: { PaperProps: { style: { maxHeight: 320 } } },
                }}
              >
                {opcionesDisponibles.map((n) => (
                  <MenuItem key={n} value={n}>
                    {ETIQUETAS_PROVEEDOR[n]}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid size={6}>
              <TextField
                fullWidth
                label="Teléfono"
                value={edit.telefono}
                onChange={(e) => setEdit({ ...edit, telefono: e.target.value })}
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
        titulo="Eliminar proveedor"
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

export default ProveedoresAdmin;
