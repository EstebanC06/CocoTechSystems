/**
 * CRUD de Empleados (admin).
 * Conecta con /empleado/* del backend.
 *
 * Cambios respecto a la versión anterior:
 *  - La columna y combobox de "Sucursal" muestran etiquetas legibles
 *    del enum NombreSucursal (Fontibón, Usaquén, ...).
 *  - El campo "Salario" rechaza valores negativos.
 *  - Se eliminó el campo "Caja registradora" del formulario y de la tabla
 *    (ahora la asignación caja↔empleado se hace desde CajasAdmin, según
 *    pediste).
 */
import { useEffect, useState } from "react";
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
  obtenerEmpleados,
  crearEmpleado,
  actualizarEmpleado,
  eliminarEmpleado,
} from "../../services/empleado.service";
import { obtenerSucursales } from "../../services/sucursal.service";
import type { EmpleadoDTO, SucursalDTO } from "../../types";
import { etiquetaSucursal } from "../../utils/etiquetas";

const vacio: EmpleadoDTO = {
  nombres: "",
  apellidos: "",
  correo: "",
  contrasena: "",
  cargo: "",
  salario: 0,
  idSucursal: 0,
};

const EmpleadosAdmin = () => {
  const [items, setItems] = useState<EmpleadoDTO[]>([]);
  const [sucursales, setSucursales] = useState<SucursalDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(false);
  const [edit, setEdit] = useState<EmpleadoDTO>(vacio);
  const [nuevo, setNuevo] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [confirmar, setConfirmar] = useState<number | null>(null);
  const [eliminando, setEliminando] = useState(false);
  const [snack, setSnack] = useState<{ msg: string; tipo: "success" | "error" } | null>(null);

  const cargar = async () => {
    setLoading(true);
    try {
      const [emps, sucs] = await Promise.all([obtenerEmpleados(), obtenerSucursales()]);
      setItems(emps);
      setSucursales(sucs);
    } catch {
      setSnack({ msg: "Error al cargar", tipo: "error" });
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    cargar();
  }, []);

  const guardar = async () => {
    // Validación: salario no puede ser negativo.
    if (edit.salario < 0) {
      setSnack({ msg: "El salario no puede ser negativo.", tipo: "error" });
      return;
    }
    if (!edit.idSucursal || edit.idSucursal === 0) {
      setSnack({ msg: "Selecciona una sucursal.", tipo: "error" });
      return;
    }
    setGuardando(true);
    try {
      if (nuevo) await crearEmpleado(edit);
      else if (edit.id) await actualizarEmpleado(edit.id, edit);
      setSnack({
        msg: nuevo ? "Empleado creado" : "Empleado actualizado",
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
      await eliminarEmpleado(confirmar);
      setSnack({ msg: "Empleado eliminado", tipo: "success" });
      setConfirmar(null);
      cargar();
    } catch (e: any) {
      setSnack({ msg: e?.response?.data?.message ?? "Error al eliminar", tipo: "error" });
    } finally {
      setEliminando(false);
    }
  };

  const salarioBody = (row: EmpleadoDTO) => `$${row.salario.toLocaleString("es-CO")}`;

  const sucursalBody = (row: EmpleadoDTO) => {
    const s = sucursales.find((x) => x.idSucursal === row.idSucursal);
    return s ? etiquetaSucursal(s.nombre) : `#${row.idSucursal}`;
  };

  const acciones = (row: EmpleadoDTO) => (
    <Box sx={{ display: "flex", gap: 0.5 }}>
      <IconButton
        size="small"
        onClick={() => {
          setEdit({ ...row, contrasena: "" });
          setNuevo(false);
          setModal(true);
        }}
        sx={{ color: "var(--coco-primary)" }}
      >
        <FontAwesomeIcon icon={faPenToSquare} style={{ fontSize: 13 }} />
      </IconButton>
      <IconButton
        size="small"
        onClick={() => setConfirmar(row.id!)}
        sx={{ color: "var(--coco-danger)" }}
      >
        <FontAwesomeIcon icon={faTrash} style={{ fontSize: 13 }} />
      </IconButton>
    </Box>
  );

  return (
    <LayoutAdmin
      titulo="Empleados"
      subtitulo={`${items.length} empleados registrados`}
      acciones={
        <Button
          variant="contained"
          color="secondary"
          startIcon={<FontAwesomeIcon icon={faPlus} />}
          onClick={() => {
            setEdit(vacio);
            setNuevo(true);
            setModal(true);
          }}
        >
          Nuevo empleado
        </Button>
      }
    >
      <Box className="coco-card" sx={{ padding: 0, overflow: "hidden" }}>
        {loading ? (
          <Box sx={{ textAlign: "center", padding: 6 }}>
            <CircularProgress />
          </Box>
        ) : (
          <DataTable value={items} paginator rows={10} emptyMessage="No hay empleados" stripedRows>
            <Column field="id" header="ID" sortable style={{ width: 80 }} />
            <Column field="nombres" header="Nombres" sortable />
            <Column field="apellidos" header="Apellidos" sortable />
            <Column field="correo" header="Correo" />
            <Column field="cargo" header="Cargo" />
            <Column field="salario" header="Salario" body={salarioBody} sortable />
            <Column field="idSucursal" header="Sucursal" body={sucursalBody} />
            <Column header="Acciones" body={acciones} style={{ width: 110 }} />
          </DataTable>
        )}
      </Box>

      <Dialog open={modal} onClose={() => setModal(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{nuevo ? "Nuevo empleado" : "Editar empleado"}</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ marginTop: 0 }}>
            <Grid size={6}>
              <TextField
                fullWidth
                label="Nombres"
                value={edit.nombres}
                onChange={(e) => setEdit({ ...edit, nombres: e.target.value })}
              />
            </Grid>
            <Grid size={6}>
              <TextField
                fullWidth
                label="Apellidos"
                value={edit.apellidos}
                onChange={(e) => setEdit({ ...edit, apellidos: e.target.value })}
              />
            </Grid>
            <Grid size={12}>
              <TextField
                fullWidth
                label="Correo"
                type="email"
                value={edit.correo}
                onChange={(e) => setEdit({ ...edit, correo: e.target.value })}
              />
            </Grid>
            {nuevo && (
              <Grid size={12}>
                <TextField
                  fullWidth
                  label="Contraseña"
                  type="password"
                  value={edit.contrasena}
                  onChange={(e) => setEdit({ ...edit, contrasena: e.target.value })}
                />
              </Grid>
            )}
            <Grid size={6}>
              <TextField
                fullWidth
                label="Cargo"
                value={edit.cargo}
                onChange={(e) => setEdit({ ...edit, cargo: e.target.value })}
              />
            </Grid>
            <Grid size={6}>
              <TextField
                fullWidth
                label="Salario"
                type="number"
                value={edit.salario}
                onChange={(e) => {
                  const v = parseFloat(e.target.value);
                  setEdit({ ...edit, salario: isNaN(v) ? 0 : Math.max(0, v) });
                }}
                inputProps={{ min: 0, step: 1000 }}
                error={edit.salario < 0}
                helperText={edit.salario < 0 ? "No puede ser negativo" : ""}
              />
            </Grid>
            <Grid size={12}>
              <TextField
                fullWidth
                select
                label="Sucursal"
                value={edit.idSucursal || ""}
                onChange={(e) =>
                  setEdit({ ...edit, idSucursal: parseInt(e.target.value as string) })
                }
              >
                {sucursales.map((s) => (
                  <MenuItem key={s.idSucursal} value={s.idSucursal}>
                    {etiquetaSucursal(s.nombre)}
                  </MenuItem>
                ))}
              </TextField>
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
        titulo="Eliminar empleado"
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

export default EmpleadosAdmin;
