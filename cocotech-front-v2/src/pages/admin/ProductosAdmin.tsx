/**
 * CRUD de Productos (admin).
 * Conecta con todos los endpoints /producto/* del backend.
 *
 * Cambios respecto a la versión anterior:
 *  - Categoría: combobox con scroll y etiquetas legibles del enum.
 *  - Proveedor: combobox con scroll y etiquetas legibles del enum.
 *  - Fecha de vencimiento: solo se muestra y persiste cuando la categoría
 *    elegida pertenece al set CATEGORIAS_CON_VENCIMIENTO. Las demás
 *    categorías guardan fechaVencimiento = null.
 *  - Campos nuevos: imagenUrl, descripcion (max 2000), descuentoPorcentaje
 *    (0-100), destacado (SI/NO). El campo activo se setea siempre en true
 *    al crear (baja lógica vendrá luego).
 *  - Validaciones: precio ≥ 0, stock ≥ 0, descuento entre 0 y 100,
 *    categoría y proveedor obligatorios.
 *  - Tabla muestra etiquetas legibles + columnas nuevas (descuento,
 *    destacado).
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
  Grid,
  IconButton,
  Chip,
  MenuItem,
  Alert,
  Snackbar,
  CircularProgress,
} from "@mui/material";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faPenToSquare, faTrash, faStar } from "@fortawesome/free-solid-svg-icons";
import LayoutAdmin from "../../components/layout/LayoutAdmin";
import ModalConfirmacion from "../../components/common/ModalConfirmacion";
import {
  obtenerProductos,
  crearProducto,
  actualizarProducto,
  eliminarProducto,
} from "../../services/producto.service";
import { obtenerCategorias } from "../../services/categoria.service";
import { obtenerProveedores } from "../../services/proveedor.service";
import {
  CATEGORIAS_CON_VENCIMIENTO,
  type ProductoDTO,
  type CategoriaDTO,
  type ProveedorDTO,
  type NombreCategoria,
} from "../../types";
import { etiquetaCategoria, etiquetaProveedor } from "../../utils/etiquetas";

const productoVacio: ProductoDTO = {
  nombre: "",
  precio: 0,
  stock: 0,
  fechaVencimiento: null,
  idCategoria: 0,
  idProveedor: 0,
  imagenUrl: "",
  descripcion: "",
  descuentoPorcentaje: 0,
  destacado: false,
  activo: true,
};

const ProductosAdmin = () => {
  const [productos, setProductos] = useState<ProductoDTO[]>([]);
  const [categorias, setCategorias] = useState<CategoriaDTO[]>([]);
  const [proveedores, setProveedores] = useState<ProveedorDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const [modalAbierto, setModalAbierto] = useState(false);
  const [editando, setEditando] = useState<ProductoDTO>(productoVacio);
  const [esNuevo, setEsNuevo] = useState(true);
  const [guardando, setGuardando] = useState(false);

  const [confirmarEliminar, setConfirmarEliminar] = useState<number | null>(null);
  const [eliminando, setEliminando] = useState(false);

  const [snack, setSnack] = useState<{ msg: string; tipo: "success" | "error" } | null>(
    null
  );

  const cargar = async () => {
    setLoading(true);
    try {
      const [prods, cats, provs] = await Promise.all([
        obtenerProductos(),
        obtenerCategorias(),
        obtenerProveedores(),
      ]);
      setProductos(prods);
      setCategorias(cats);
      setProveedores(provs);
    } catch {
      setSnack({ msg: "Error al cargar datos", tipo: "error" });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
  }, []);

  // Resuelve si la categoría actualmente elegida exige fecha de vencimiento.
  const categoriaActual = useMemo(
    () => categorias.find((c) => c.idCategoria === editando.idCategoria),
    [categorias, editando.idCategoria]
  );

  const necesitaVencimiento = useMemo(() => {
    if (!categoriaActual?.nombre) return false;
    return CATEGORIAS_CON_VENCIMIENTO.has(categoriaActual.nombre as NombreCategoria);
  }, [categoriaActual]);

  const abrirNuevo = () => {
    setEditando({ ...productoVacio });
    setEsNuevo(true);
    setModalAbierto(true);
  };

  const abrirEditar = (p: ProductoDTO) => {
    setEditando({
      ...p,
      // Asegurar defaults si el producto viejo no tenía estos campos
      imagenUrl: p.imagenUrl ?? "",
      descripcion: p.descripcion ?? "",
      descuentoPorcentaje: p.descuentoPorcentaje ?? 0,
      destacado: p.destacado ?? false,
      activo: p.activo ?? true,
    });
    setEsNuevo(false);
    setModalAbierto(true);
  };

  const guardar = async () => {
    // Validaciones
    if (!editando.nombre.trim()) {
      setSnack({ msg: "El nombre es obligatorio.", tipo: "error" });
      return;
    }
    if (editando.precio < 0) {
      setSnack({ msg: "El precio no puede ser negativo.", tipo: "error" });
      return;
    }
    if (editando.stock < 0) {
      setSnack({ msg: "El stock no puede ser negativo.", tipo: "error" });
      return;
    }
    if (!editando.idCategoria) {
      setSnack({ msg: "Selecciona una categoría.", tipo: "error" });
      return;
    }
    if (!editando.idProveedor) {
      setSnack({ msg: "Selecciona un proveedor.", tipo: "error" });
      return;
    }
    const desc = editando.descuentoPorcentaje ?? 0;
    if (desc < 0 || desc > 100) {
      setSnack({ msg: "El descuento debe estar entre 0 y 100.", tipo: "error" });
      return;
    }
    if ((editando.descripcion ?? "").length > 2000) {
      setSnack({ msg: "La descripción no puede superar 2000 caracteres.", tipo: "error" });
      return;
    }

    // Si la categoría NO necesita vencimiento, forzar null antes de mandar
    const payload: ProductoDTO = {
      ...editando,
      fechaVencimiento: necesitaVencimiento
        ? editando.fechaVencimiento || null
        : null,
    };

    setGuardando(true);
    try {
      if (esNuevo) {
        await crearProducto(payload);
        setSnack({ msg: "Producto creado correctamente", tipo: "success" });
      } else if (editando.idProducto) {
        await actualizarProducto(editando.idProducto, payload);
        setSnack({ msg: "Producto actualizado", tipo: "success" });
      }
      setModalAbierto(false);
      cargar();
    } catch (e: any) {
      setSnack({ msg: e?.response?.data?.message ?? "Error al guardar", tipo: "error" });
    } finally {
      setGuardando(false);
    }
  };

  const eliminar = async () => {
    if (confirmarEliminar === null) return;
    setEliminando(true);
    try {
      await eliminarProducto(confirmarEliminar);
      setSnack({ msg: "Producto eliminado", tipo: "success" });
      setConfirmarEliminar(null);
      cargar();
    } catch (e: any) {
      setSnack({ msg: e?.response?.data?.message ?? "Error al eliminar", tipo: "error" });
    } finally {
      setEliminando(false);
    }
  };

  // Renderers de columna
  const precioBody = (row: ProductoDTO) => `$${row.precio.toLocaleString("es-CO")}`;
  const stockBody = (row: ProductoDTO) => (
    <Chip
      label={row.stock}
      size="small"
      sx={{
        backgroundColor:
          row.stock >= 25
            ? "var(--coco-success-fill)"
            : row.stock > 0
            ? "var(--coco-warning-fill)"
            : "var(--coco-danger-fill)",
        color:
          row.stock >= 25
            ? "var(--coco-success)"
            : row.stock > 0
            ? "var(--coco-warning)"
            : "var(--coco-danger)",
        fontWeight: 600,
      }}
    />
  );
  const categoriaBody = (row: ProductoDTO) => {
    const cat = categorias.find((c) => c.idCategoria === row.idCategoria);
    return cat ? etiquetaCategoria(cat.nombre) : `#${row.idCategoria}`;
  };
  const proveedorBody = (row: ProductoDTO) => {
    const p = proveedores.find((pr) => pr.idProveedor === row.idProveedor);
    return p ? etiquetaProveedor(p.nombre) : `#${row.idProveedor}`;
  };
  const descuentoBody = (row: ProductoDTO) =>
    row.descuentoPorcentaje && row.descuentoPorcentaje > 0
      ? `${row.descuentoPorcentaje}%`
      : "—";
  const destacadoBody = (row: ProductoDTO) =>
    row.destacado ? (
      <FontAwesomeIcon icon={faStar} style={{ color: "var(--coco-warning)" }} />
    ) : (
      "—"
    );
  const descripcionBody = (row: ProductoDTO) => {
    const t = row.descripcion ?? "";
    return t.length > 60 ? t.slice(0, 60) + "…" : t || "—";
  };
  const imagenBody = (row: ProductoDTO) =>
    row.imagenUrl ? (
      <img
        src={row.imagenUrl}
        alt={row.nombre}
        style={{ width: 32, height: 32, objectFit: "cover", borderRadius: 4 }}
        onError={(e) => {
          (e.target as HTMLImageElement).style.display = "none";
        }}
      />
    ) : (
      "—"
    );
  const accionesBody = (row: ProductoDTO) => (
    <Box sx={{ display: "flex", gap: 0.5 }}>
      <IconButton
        size="small"
        onClick={() => abrirEditar(row)}
        sx={{ color: "var(--coco-primary)" }}
      >
        <FontAwesomeIcon icon={faPenToSquare} style={{ fontSize: 13 }} />
      </IconButton>
      <IconButton
        size="small"
        onClick={() => setConfirmarEliminar(row.idProducto!)}
        sx={{ color: "var(--coco-danger)" }}
      >
        <FontAwesomeIcon icon={faTrash} style={{ fontSize: 13 }} />
      </IconButton>
    </Box>
  );

  return (
    <LayoutAdmin
      titulo="Productos"
      subtitulo={`${productos.length} productos registrados`}
      acciones={
        <Button
          variant="contained"
          color="secondary"
          startIcon={<FontAwesomeIcon icon={faPlus} />}
          onClick={abrirNuevo}
        >
          Nuevo producto
        </Button>
      }
    >
      <Box className="coco-card" sx={{ padding: 0, overflow: "hidden" }}>
        {loading ? (
          <Box sx={{ textAlign: "center", padding: 6 }}>
            <CircularProgress />
          </Box>
        ) : (
          <DataTable
            value={productos}
            paginator
            rows={10}
            rowsPerPageOptions={[10, 25, 50]}
            emptyMessage="No hay productos"
            stripedRows
          >
            <Column field="idProducto" header="ID" sortable style={{ width: 70 }} />
            <Column header="Imagen" body={imagenBody} style={{ width: 80 }} />
            <Column field="nombre" header="Nombre" sortable />
            <Column field="precio" header="Precio" body={precioBody} sortable />
            <Column
              field="stock"
              header="Stock"
              body={stockBody}
              sortable
              style={{ width: 100 }}
            />
            <Column field="descuentoPorcentaje" header="Desc." body={descuentoBody} style={{ width: 80 }} />
            <Column field="destacado" header="★" body={destacadoBody} style={{ width: 60 }} />
            <Column field="idCategoria" header="Categoría" body={categoriaBody} />
            <Column field="idProveedor" header="Proveedor" body={proveedorBody} />
            <Column field="descripcion" header="Descripción" body={descripcionBody} />
            <Column header="Acciones" body={accionesBody} style={{ width: 110 }} />
          </DataTable>
        )}
      </Box>

      {/* Modal crear/editar */}
      <Dialog open={modalAbierto} onClose={() => setModalAbierto(false)} maxWidth="md" fullWidth>
        <DialogTitle>{esNuevo ? "Nuevo producto" : "Editar producto"}</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ marginTop: 0 }}>
            <Grid size={12}>
              <TextField
                fullWidth
                label="Nombre"
                value={editando.nombre}
                onChange={(e) => setEditando({ ...editando, nombre: e.target.value })}
              />
            </Grid>
            <Grid size={6}>
              <TextField
                fullWidth
                label="Precio"
                type="number"
                value={editando.precio}
                onChange={(e) => {
                  const v = parseFloat(e.target.value);
                  setEditando({ ...editando, precio: isNaN(v) ? 0 : Math.max(0, v) });
                }}
                inputProps={{ min: 0, step: 100 }}
              />
            </Grid>
            <Grid size={6}>
              <TextField
                fullWidth
                label="Stock"
                type="number"
                value={editando.stock}
                onChange={(e) => {
                  const v = parseInt(e.target.value);
                  setEditando({ ...editando, stock: isNaN(v) ? 0 : Math.max(0, v) });
                }}
                inputProps={{ min: 0, step: 1 }}
              />
            </Grid>
            <Grid size={6}>
              <TextField
                fullWidth
                select
                label="Categoría"
                value={editando.idCategoria || ""}
                onChange={(e) =>
                  setEditando({ ...editando, idCategoria: parseInt(e.target.value as string) })
                }
                SelectProps={{
                  MenuProps: { PaperProps: { style: { maxHeight: 320 } } },
                }}
              >
                {categorias.map((c) => (
                  <MenuItem key={c.idCategoria} value={c.idCategoria}>
                    {etiquetaCategoria(c.nombre)}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid size={6}>
              <TextField
                fullWidth
                select
                label="Proveedor"
                value={editando.idProveedor || ""}
                onChange={(e) =>
                  setEditando({ ...editando, idProveedor: parseInt(e.target.value as string) })
                }
                SelectProps={{
                  MenuProps: { PaperProps: { style: { maxHeight: 320 } } },
                }}
              >
                {proveedores.map((p) => (
                  <MenuItem key={p.idProveedor} value={p.idProveedor}>
                    {etiquetaProveedor(p.nombre)}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>

            {/* Fecha de vencimiento solo si la categoría lo requiere */}
            {necesitaVencimiento && (
              <Grid size={6}>
                <TextField
                  fullWidth
                  label="Fecha de vencimiento"
                  type="date"
                  InputLabelProps={{ shrink: true }}
                  value={editando.fechaVencimiento ?? ""}
                  onChange={(e) =>
                    setEditando({ ...editando, fechaVencimiento: e.target.value || null })
                  }
                  helperText="Esta categoría requiere fecha de vencimiento."
                />
              </Grid>
            )}

            <Grid size={necesitaVencimiento ? 6 : 6}>
              <TextField
                fullWidth
                label="Descuento (%)"
                type="number"
                value={editando.descuentoPorcentaje ?? 0}
                onChange={(e) => {
                  const v = parseFloat(e.target.value);
                  setEditando({
                    ...editando,
                    descuentoPorcentaje: isNaN(v) ? 0 : Math.max(0, Math.min(100, v)),
                  });
                }}
                inputProps={{ min: 0, max: 100, step: 1 }}
                helperText="Entre 0 y 100"
              />
            </Grid>

            <Grid size={necesitaVencimiento ? 12 : 6}>
              <TextField
                fullWidth
                select
                label="¿Destacado?"
                value={editando.destacado ? "SI" : "NO"}
                onChange={(e) =>
                  setEditando({ ...editando, destacado: e.target.value === "SI" })
                }
                helperText="Los destacados aparecen en la home pública."
              >
                <MenuItem value="NO">No</MenuItem>
                <MenuItem value="SI">Sí</MenuItem>
              </TextField>
            </Grid>

            <Grid size={12}>
              <TextField
                fullWidth
                label="URL de imagen (opcional)"
                value={editando.imagenUrl ?? ""}
                onChange={(e) => setEditando({ ...editando, imagenUrl: e.target.value })}
                placeholder="https://..."
              />
            </Grid>

            <Grid size={12}>
              <TextField
                fullWidth
                label="Descripción (opcional)"
                multiline
                rows={3}
                value={editando.descripcion ?? ""}
                onChange={(e) => setEditando({ ...editando, descripcion: e.target.value })}
                inputProps={{ maxLength: 2000 }}
                helperText={`${(editando.descripcion ?? "").length} / 2000`}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions sx={{ padding: 2 }}>
          <Button onClick={() => setModalAbierto(false)}>Cancelar</Button>
          <Button onClick={guardar} variant="contained" color="secondary" disabled={guardando}>
            {guardando ? <CircularProgress size={20} color="inherit" /> : "Guardar"}
          </Button>
        </DialogActions>
      </Dialog>

      <ModalConfirmacion
        abierto={confirmarEliminar !== null}
        titulo="Eliminar producto"
        mensaje="¿Estás seguro? Esta acción no se puede deshacer."
        onConfirmar={eliminar}
        onCancelar={() => setConfirmarEliminar(null)}
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

export default ProductosAdmin;
