/**
 * Vista de facturas (admin).
 *
 * Cambios respecto a la versión anterior:
 *  - Se aclara que el flujo principal es:
 *    pedido → ENTREGADO → Venta + Factura (MySQL) → proyección a MongoDB.
 *  - Botón "Sincronizar MongoDB" se mantiene como recurso de respaldo
 *    para reproyectar facturas si alguna falló en su momento.
 *  - Se quitó el botón "ver" porque su ruta apuntaba a una página de
 *    cliente y no existía vista admin equivalente. La factura completa
 *    se ve desde Mongo si se necesita.
 *  - El monto de IVA se muestra desde el campo precioImpuestos del back
 *    (que ya viene calculado, NO recalcular como total * 0.19).
 */
import { useEffect, useState } from "react";
import {
  Box,
  Button,
  IconButton,
  Alert,
  Snackbar,
  CircularProgress,
  Typography,
} from "@mui/material";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faTrash,
  faRotate,
  faCircleInfo,
} from "@fortawesome/free-solid-svg-icons";
import LayoutAdmin from "../../components/layout/LayoutAdmin";
import ModalConfirmacion from "../../components/common/ModalConfirmacion";
import {
  obtenerFacturas,
  eliminarFactura,
  sincronizarFacturas,
} from "../../services/factura.service";
import type { FacturaDTO } from "../../types";

const FacturasAdmin = () => {
  const [items, setItems] = useState<FacturaDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [confirmar, setConfirmar] = useState<number | null>(null);
  const [eliminando, setEliminando] = useState(false);
  const [sincronizando, setSincronizando] = useState(false);
  const [snack, setSnack] = useState<{ msg: string; tipo: "success" | "error" } | null>(
    null
  );

  const cargar = async () => {
    setLoading(true);
    try {
      setItems(await obtenerFacturas());
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
      await eliminarFactura(confirmar);
      setSnack({ msg: "Factura eliminada", tipo: "success" });
      setConfirmar(null);
      cargar();
    } catch (e: any) {
      setSnack({ msg: e?.response?.data?.message ?? "Error al eliminar", tipo: "error" });
    } finally {
      setEliminando(false);
    }
  };

  const sincronizar = async () => {
    setSincronizando(true);
    try {
      await sincronizarFacturas();
      setSnack({ msg: "Facturas sincronizadas con MongoDB", tipo: "success" });
    } catch (e: any) {
      setSnack({
        msg: e?.response?.data?.message ?? "Error al sincronizar",
        tipo: "error",
      });
    } finally {
      setSincronizando(false);
    }
  };

  const totalBody = (row: FacturaDTO) =>
    `$${row.precioTotal.toLocaleString("es-CO")}`;
  const ivaBody = (row: FacturaDTO) =>
    `$${row.precioImpuestos.toLocaleString("es-CO")}`;
  const fechaBody = (row: FacturaDTO) =>
    row.fecha ? new Date(row.fecha).toLocaleString("es-CO") : "—";

  const acciones = (row: FacturaDTO) => (
    <Box sx={{ display: "flex", gap: 0.5 }}>
      <IconButton
        size="small"
        onClick={() => setConfirmar(row.idFactura!)}
        sx={{ color: "var(--coco-danger)" }}
      >
        <FontAwesomeIcon icon={faTrash} style={{ fontSize: 13 }} />
      </IconButton>
    </Box>
  );

  return (
    <LayoutAdmin
      titulo="Facturas"
      subtitulo={`${items.length} facturas emitidas`}
      acciones={
        <Button
          variant="contained"
          color="primary"
          startIcon={<FontAwesomeIcon icon={faRotate} spin={sincronizando} />}
          onClick={sincronizar}
          disabled={sincronizando}
        >
          {sincronizando ? "Sincronizando..." : "Reproyectar a MongoDB"}
        </Button>
      }
    >
      <Alert
        severity="info"
        icon={<FontAwesomeIcon icon={faCircleInfo} />}
        sx={{ marginBottom: 2 }}
      >
        <Typography sx={{ fontSize: 13 }}>
          Las facturas se generan automáticamente cuando un pedido pasa a{" "}
          <strong>ENTREGADO</strong> y se proyectan a MongoDB (patrón de Referencia
          Extendida). Usa <em>Reproyectar a MongoDB</em> solo si necesitas
          re-sincronizar la colección de lectura.
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
            emptyMessage="No hay facturas"
            stripedRows
          >
            <Column field="idFactura" header="ID" sortable style={{ width: 80 }} />
            <Column field="fecha" header="Fecha" body={fechaBody} sortable />
            <Column field="idVenta" header="Venta" sortable />
            <Column field="precioImpuestos" header="IVA" body={ivaBody} />
            <Column field="precioTotal" header="Total" body={totalBody} sortable />
            <Column header="Acciones" body={acciones} style={{ width: 110 }} />
          </DataTable>
        )}
      </Box>

      <ModalConfirmacion
        abierto={confirmar !== null}
        titulo="Eliminar factura"
        mensaje="¿Estás seguro? Esta acción no se puede deshacer."
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

export default FacturasAdmin;
