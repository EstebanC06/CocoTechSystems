/**
 * CRUD de Categorías (admin).
 * Conecta con /categoria/* del backend.
 *
 * Cambios respecto a la versión anterior:
 *  - El campo "nombre" es un combobox restringido al enum NombreCategoria.
 *  - La tabla y los modales muestran etiquetas legibles
 *    (ej. "Frutas y verduras" en lugar de "FRUTAS_VERDURAS").
 *  - Se agregó un campo opcional "URL de imagen" para que la categoría
 *    se proyecte luego en la capa pública del cliente.
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
  MenuItem,
  Grid,
} from "@mui/material";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faPenToSquare, faTrash } from "@fortawesome/free-solid-svg-icons";
import LayoutAdmin from "../../components/layout/LayoutAdmin";
import ModalConfirmacion from "../../components/common/ModalConfirmacion";
import {
  obtenerCategorias,
  crearCategoria,
  actualizarCategoria,
  eliminarCategoria,
} from "../../services/categoria.service";
import {
  NOMBRES_CATEGORIA,
  ETIQUETAS_CATEGORIA,
  type CategoriaDTO,
  type NombreCategoria,
} from "../../types";
import { etiquetaCategoria } from "../../utils/etiquetas";

const vacio: CategoriaDTO = {
  nombre: "ASEO",
  descripcion: "",
  imagenUrl: "",
  icono: "",
};

const CategoriasAdmin = () => {
  const [items, setItems] = useState<CategoriaDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(false);
  const [edit, setEdit] = useState<CategoriaDTO>(vacio);
  const [nuevo, setNuevo] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [confirmar, setConfirmar] = useState<number | null>(null);
  const [eliminando, setEliminando] = useState(false);
  const [snack, setSnack] = useState<{ msg: string; tipo: "success" | "error" } | null>(null);

  const cargar = async () => {
    setLoading(true);
    try {
      setItems(await obtenerCategorias());
    } catch {
      setSnack({ msg: "Error al cargar", tipo: "error" });
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    cargar();
  }, []);

  /**
   * Al crear una nueva categoría, filtra del combobox los nombres del enum
   * que ya están en uso (porque el back los rechaza por unique). Al editar,
   * permite mantener el nombre actual.
   */
  const opcionesDisponibles = useMemo(() => {
    const usados = new Set<NombreCategoria>(items.map((c) => c.nombre));
    return NOMBRES_CATEGORIA.filter((n) => !usados.has(n) || (!nuevo && n === edit.nombre));
  }, [items, nuevo, edit.nombre]);

  const guardar = async () => {
    setGuardando(true);
    try {
      if (nuevo) await crearCategoria(edit);
      else if (edit.idCategoria) await actualizarCategoria(edit.idCategoria, edit);
      setSnack({
        msg: nuevo ? "Categoría creada" : "Categoría actualizada",
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
      await eliminarCategoria(confirmar);
      setSnack({ msg: "Categoría eliminada", tipo: "success" });
      setConfirmar(null);
      cargar();
    } catch (e: any) {
      setSnack({ msg: e?.response?.data?.message ?? "Error al eliminar", tipo: "error" });
    } finally {
      setEliminando(false);
    }
  };

  // Cuerpo de columna para mostrar el nombre legible en lugar del enum crudo.
  const nombreBody = (row: CategoriaDTO) => etiquetaCategoria(row.nombre);

  const acciones = (row: CategoriaDTO) => (
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
        onClick={() => setConfirmar(row.idCategoria!)}
        sx={{ color: "var(--coco-danger)" }}
      >
        <FontAwesomeIcon icon={faTrash} style={{ fontSize: 13 }} />
      </IconButton>
    </Box>
  );

  return (
    <LayoutAdmin
      titulo="Categorías"
      subtitulo={`${items.length} categorías registradas`}
      acciones={
        <Button
          variant="contained"
          color="secondary"
          startIcon={<FontAwesomeIcon icon={faPlus} />}
          onClick={() => {
            // Toma el primer nombre disponible para que el combobox no se abra vacío.
            const usados = new Set<NombreCategoria>(items.map((c) => c.nombre));
            const primero = NOMBRES_CATEGORIA.find((n) => !usados.has(n)) ?? "ASEO";
            setEdit({ ...vacio, nombre: primero });
            setNuevo(true);
            setModal(true);
          }}
          disabled={items.length >= NOMBRES_CATEGORIA.length}
        >
          Nueva categoría
        </Button>
      }
    >
      <Box className="coco-card" sx={{ padding: 0, overflow: "hidden" }}>
        {loading ? (
          <Box sx={{ textAlign: "center", padding: 6 }}>
            <CircularProgress />
          </Box>
        ) : (
          <DataTable value={items} paginator rows={10} emptyMessage="No hay categorías" stripedRows>
            <Column field="idCategoria" header="ID" sortable style={{ width: 80 }} />
            <Column field="nombre" header="Nombre" body={nombreBody} sortable />
            <Column field="descripcion" header="Descripción" />
            <Column header="Acciones" body={acciones} style={{ width: 110 }} />
          </DataTable>
        )}
      </Box>

      <Dialog open={modal} onClose={() => setModal(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{nuevo ? "Nueva categoría" : "Editar categoría"}</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ marginTop: 0 }}>
            <Grid size={12}>
              <TextField
                fullWidth
                select
                label="Nombre"
                value={edit.nombre}
                onChange={(e) =>
                  setEdit({ ...edit, nombre: e.target.value as NombreCategoria })
                }
                SelectProps={{
                  MenuProps: {
                    PaperProps: { style: { maxHeight: 320 } },
                  },
                }}
              >
                {opcionesDisponibles.map((n) => (
                  <MenuItem key={n} value={n}>
                    {ETIQUETAS_CATEGORIA[n]}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid size={12}>
              <TextField
                fullWidth
                label="Descripción"
                multiline
                rows={3}
                value={edit.descripcion}
                onChange={(e) => setEdit({ ...edit, descripcion: e.target.value })}
              />
            </Grid>
            <Grid size={12}>
              <TextField
                fullWidth
                label="URL de imagen (opcional)"
                value={edit.imagenUrl ?? ""}
                onChange={(e) => setEdit({ ...edit, imagenUrl: e.target.value })}
                placeholder="https://..."
                helperText="Se mostrará en la capa pública del cliente."
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions sx={{ padding: 2 }}>
          <Button onClick={() => setModal(false)}>Cancelar</Button>
          <Button
            onClick={guardar}
            variant="contained"
            color="secondary"
            disabled={guardando}
          >
            {guardando ? <CircularProgress size={20} color="inherit" /> : "Guardar"}
          </Button>
        </DialogActions>
      </Dialog>

      <ModalConfirmacion
        abierto={confirmar !== null}
        titulo="Eliminar categoría"
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

export default CategoriasAdmin;
