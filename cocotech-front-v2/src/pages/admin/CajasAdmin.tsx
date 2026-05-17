/**
 * CRUD de Cajas Registradoras (admin).
 * Conecta con /caja/* del backend.
 *
 * Cambios respecto a la versión anterior:
 *  - El enum de estado usa "EN_MANTENIMIENTO" (coincide con el back),
 *    no "MANTENIMIENTO".
 *  - El combobox de sucursal muestra etiquetas legibles del enum.
 *  - El campo "número de caja" rechaza valores negativos o cero.
 *  - El combobox de "Empleado asignado" muestra "Nombres Apellidos".
 *  - Al asignar empleado a la caja, el back se encargará de actualizar
 *    el campo cajaRegistradora del Empleado (lógica que ya existía).
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
  Chip,
} from "@mui/material";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faPenToSquare, faTrash } from "@fortawesome/free-solid-svg-icons";
import LayoutAdmin from "../../components/layout/LayoutAdmin";
import ModalConfirmacion from "../../components/common/ModalConfirmacion";
import {
  obtenerCajas,
  crearCaja,
  actualizarCaja,
  eliminarCaja,
} from "../../services/caja.service";
import { obtenerSucursales } from "../../services/sucursal.service";
import { obtenerEmpleados } from "../../services/empleado.service";
import {
  ESTADOS_CAJA,
  ETIQUETAS_ESTADO_CAJA,
  type CajaRegistradoraDTO,
  type EstadoCaja,
  type SucursalDTO,
  type EmpleadoDTO,
} from "../../types";
import { etiquetaSucursal, etiquetaEstadoCaja } from "../../utils/etiquetas";

const vacio: CajaRegistradoraDTO = {
  numero: 1,
  estado: "ACTIVA",
  idEmpleado: 0,
  idSucursal: 0,
};

const colorEstado: Record<EstadoCaja, { bg: string; fg: string }> = {
  ACTIVA: { bg: "var(--coco-success-fill)", fg: "var(--coco-success)" },
  INACTIVA: { bg: "var(--coco-danger-fill)", fg: "var(--coco-danger)" },
  EN_MANTENIMIENTO: { bg: "var(--coco-warning-fill)", fg: "var(--coco-warning)" },
};

const CajasAdmin = () => {
  const [items, setItems] = useState<CajaRegistradoraDTO[]>([]);
  const [sucursales, setSucursales] = useState<SucursalDTO[]>([]);
  const [empleados, setEmpleados] = useState<EmpleadoDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(false);
  const [edit, setEdit] = useState<CajaRegistradoraDTO>(vacio);
  const [nuevo, setNuevo] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [confirmar, setConfirmar] = useState<number | null>(null);
  const [eliminando, setEliminando] = useState(false);
  const [snack, setSnack] = useState<{ msg: string; tipo: "success" | "error" } | null>(null);

  const cargar = async () => {
    setLoading(true);
    try {
      const [cs, ss, es] = await Promise.all([
        obtenerCajas(),
        obtenerSucursales(),
        obtenerEmpleados(),
      ]);
      setItems(cs);
      setSucursales(ss);
      setEmpleados(es);
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
    // Validaciones
    if (edit.numero <= 0) {
      setSnack({ msg: "El número de caja debe ser mayor que 0.", tipo: "error" });
      return;
    }
    if (!edit.idSucursal || edit.idSucursal === 0) {
      setSnack({ msg: "Selecciona una sucursal.", tipo: "error" });
      return;
    }
    if (!edit.idEmpleado || edit.idEmpleado === 0) {
      setSnack({ msg: "Selecciona un empleado asignado.", tipo: "error" });
      return;
    }
    setGuardando(true);
    try {
      if (nuevo) await crearCaja(edit);
      else if (edit.idCaja) await actualizarCaja(edit.idCaja, edit);
      setSnack({
        msg: nuevo ? "Caja creada" : "Caja actualizada",
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
      await eliminarCaja(confirmar);
      setSnack({ msg: "Caja eliminada", tipo: "success" });
      setConfirmar(null);
      cargar();
    } catch (e: any) {
      setSnack({ msg: e?.response?.data?.message ?? "Error al eliminar", tipo: "error" });
    } finally {
      setEliminando(false);
    }
  };

  const estadoBody = (row: CajaRegistradoraDTO) => {
    const c = colorEstado[row.estado];
    return (
      <Chip
        label={etiquetaEstadoCaja(row.estado)}
        size="small"
        sx={{ backgroundColor: c.bg, color: c.fg, fontWeight: 600 }}
      />
    );
  };

  const sucursalBody = (row: CajaRegistradoraDTO) => {
    const s = sucursales.find((x) => x.idSucursal === row.idSucursal);
    return s ? etiquetaSucursal(s.nombre) : `#${row.idSucursal}`;
  };

  const empleadoBody = (row: CajaRegistradoraDTO) => {
    const e = empleados.find((emp) => emp.id === row.idEmpleado);
    return e ? `${e.nombres} ${e.apellidos}` : `#${row.idEmpleado}`;
  };

  const acciones = (row: CajaRegistradoraDTO) => (
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
        onClick={() => setConfirmar(row.idCaja!)}
        sx={{ color: "var(--coco-danger)" }}
      >
        <FontAwesomeIcon icon={faTrash} style={{ fontSize: 13 }} />
      </IconButton>
    </Box>
  );

  return (
    <LayoutAdmin
      titulo="Cajas registradoras"
      subtitulo={`${items.length} cajas registradas`}
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
          Nueva caja
        </Button>
      }
    >
      <Box className="coco-card" sx={{ padding: 0, overflow: "hidden" }}>
        {loading ? (
          <Box sx={{ textAlign: "center", padding: 6 }}>
            <CircularProgress />
          </Box>
        ) : (
          <DataTable value={items} paginator rows={10} emptyMessage="No hay cajas" stripedRows>
            <Column field="idCaja" header="ID" sortable style={{ width: 80 }} />
            <Column field="numero" header="Número" sortable />
            <Column field="estado" header="Estado" body={estadoBody} />
            <Column field="idSucursal" header="Sucursal" body={sucursalBody} />
            <Column field="idEmpleado" header="Empleado" body={empleadoBody} />
            <Column header="Acciones" body={acciones} style={{ width: 110 }} />
          </DataTable>
        )}
      </Box>

      <Dialog open={modal} onClose={() => setModal(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{nuevo ? "Nueva caja" : "Editar caja"}</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ marginTop: 0 }}>
            <Grid size={6}>
              <TextField
                fullWidth
                label="Número"
                type="number"
                value={edit.numero}
                onChange={(e) => {
                  const v = parseInt(e.target.value);
                  setEdit({ ...edit, numero: isNaN(v) ? 0 : Math.max(0, v) });
                }}
                inputProps={{ min: 1, step: 1 }}
                error={edit.numero <= 0}
                helperText={edit.numero <= 0 ? "Debe ser mayor que 0" : ""}
              />
            </Grid>
            <Grid size={6}>
              <TextField
                fullWidth
                select
                label="Estado"
                value={edit.estado}
                onChange={(e) =>
                  setEdit({ ...edit, estado: e.target.value as EstadoCaja })
                }
              >
                {ESTADOS_CAJA.map((s) => (
                  <MenuItem key={s} value={s}>
                    {ETIQUETAS_ESTADO_CAJA[s]}
                  </MenuItem>
                ))}
              </TextField>
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
            <Grid size={12}>
              <TextField
                fullWidth
                select
                label="Empleado asignado"
                value={edit.idEmpleado || ""}
                onChange={(e) =>
                  setEdit({ ...edit, idEmpleado: parseInt(e.target.value as string) })
                }
                SelectProps={{
                  MenuProps: { PaperProps: { style: { maxHeight: 320 } } },
                }}
              >
                {empleados.map((e) => (
                  <MenuItem key={e.id} value={e.id}>
                    {e.nombres} {e.apellidos}
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
        titulo="Eliminar caja"
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

export default CajasAdmin;
