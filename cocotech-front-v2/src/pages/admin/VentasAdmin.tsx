/**
 * Vista de ventas (admin).
 * Lista de todas las ventas registradas en MySQL.
 *
 * Cambios respecto a la versión anterior:
 *  - Cada venta corresponde a un pedido ENTREGADO (el back las crea
 *    automáticamente; no se editan desde aquí).
 *  - Se quitó el ícono "ver factura" porque no había endpoint cruzado
 *    estable. La factura asociada se ve en /admin/facturas.
 *  - Se muestra una nota explicativa en la cabecera para que el usuario
 *    entienda que las ventas nacen del flujo de pedidos.
 */
import { useEffect, useState } from "react";
import {
  Box,
  IconButton,
  Alert,
  Snackbar,
  CircularProgress,
  Typography,
} from "@mui/material";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTrash, faCircleInfo } from "@fortawesome/free-solid-svg-icons";
import LayoutAdmin from "../../components/layout/LayoutAdmin";
import ModalConfirmacion from "../../components/common/ModalConfirmacion";
import { obtenerVentas, eliminarVenta } from "../../services/venta.service";
import { obtenerEmpleados } from "../../services/empleado.service";
import { obtenerClientes } from "../../services/cliente.service";
import type { VentaDTO, EmpleadoDTO, ClienteDTO } from "../../types";

const VentasAdmin = () => {
  const [items, setItems] = useState<VentaDTO[]>([]);
  const [empleados, setEmpleados] = useState<EmpleadoDTO[]>([]);
  const [clientes, setClientes] = useState<ClienteDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [confirmar, setConfirmar] = useState<number | null>(null);
  const [eliminando, setEliminando] = useState(false);
  const [snack, setSnack] = useState<{ msg: string; tipo: "success" | "error" } | null>(
    null
  );

  const cargar = async () => {
    setLoading(true);
    try {
      const [vs, es, cs] = await Promise.all([
        obtenerVentas(),
        obtenerEmpleados(),
        obtenerClientes(),
      ]);
      setItems(vs);
      setEmpleados(es);
      setClientes(cs);
    } catch {
      setSnack({ msg: "Error al cargar", tipo: "error" });
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    cargar();
  }, []);

  const eliminar = async () => {
    if (confirmar === null) return;
    setEliminando(true);
    try {
      await eliminarVenta(confirmar);
      setSnack({ msg: "Venta eliminada", tipo: "success" });
      setConfirmar(null);
      cargar();
    } catch (e: any) {
      setSnack({ msg: e?.response?.data?.message ?? "Error al eliminar", tipo: "error" });
    } finally {
      setEliminando(false);
    }
  };

  const empleadoBody = (row: VentaDTO) => {
    const e = empleados.find((emp) => emp.id === row.idEmpleado);
    return e ? `${e.nombres} ${e.apellidos}` : `#${row.idEmpleado}`;
  };
  const clienteBody = (row: VentaDTO) => {
    const c = clientes.find((cl) => cl.id === row.idCliente);
    return c ? `${c.nombres} ${c.apellidos}` : `#${row.idCliente}`;
  };
  const totalBody = (row: VentaDTO) => `$${(row.total ?? 0).toLocaleString("es-CO")}`;
  const fechaBody = (row: VentaDTO) =>
    row.fecha ? new Date(row.fecha).toLocaleString("es-CO") : "—";

  const acciones = (row: VentaDTO) => (
    <Box sx={{ display: "flex", gap: 0.5 }}>
      <IconButton
        size="small"
        onClick={() => setConfirmar(row.idVenta!)}
        sx={{ color: "var(--coco-danger)" }}
      >
        <FontAwesomeIcon icon={faTrash} style={{ fontSize: 13 }} />
      </IconButton>
    </Box>
  );

  return (
    <LayoutAdmin titulo="Ventas" subtitulo={`${items.length} ventas registradas`}>
      <Alert
        severity="info"
        icon={<FontAwesomeIcon icon={faCircleInfo} />}
        sx={{ marginBottom: 2 }}
      >
        <Typography sx={{ fontSize: 13 }}>
          Las ventas se generan automáticamente cuando un pedido cambia a estado{" "}
          <strong>ENTREGADO</strong>. Cada venta tiene una factura asociada que puedes
          consultar en la sección de Facturas.
        </Typography>
      </Alert>

      <Box className="coco-card" sx={{ padding: 0, overflow: "hidden" }}>
        {loading ? (
          <Box sx={{ textAlign: "center", padding: 6 }}>
            <CircularProgress />
          </Box>
        ) : (
          <DataTable
            value={items}
            paginator
            rows={10}
            emptyMessage="No hay ventas"
            stripedRows
          >
            <Column field="idVenta" header="ID" sortable style={{ width: 80 }} />
            <Column field="fecha" header="Fecha" body={fechaBody} sortable />
            <Column field="idEmpleado" header="Empleado" body={empleadoBody} />
            <Column field="idCliente" header="Cliente" body={clienteBody} />
            <Column field="total" header="Total" body={totalBody} sortable />
            <Column header="Acciones" body={acciones} style={{ width: 110 }} />
          </DataTable>
        )}
      </Box>

      <ModalConfirmacion
        abierto={confirmar !== null}
        titulo="Eliminar venta"
        mensaje="¿Estás seguro? Esto también puede afectar la factura asociada."
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

export default VentasAdmin;
