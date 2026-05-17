/**
 * Modal de confirmación reutilizable para acciones destructivas.
 */
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
} from "@mui/material";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTriangleExclamation } from "@fortawesome/free-solid-svg-icons";

interface Props {
  abierto: boolean;
  titulo: string;
  mensaje: string;
  onConfirmar: () => void;
  onCancelar: () => void;
  cargando?: boolean;
}

const ModalConfirmacion = ({
  abierto,
  titulo,
  mensaje,
  onConfirmar,
  onCancelar,
  cargando = false,
}: Props) => {
  return (
    <Dialog open={abierto} onClose={onCancelar} maxWidth="xs" fullWidth>
      <DialogTitle sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
        <FontAwesomeIcon icon={faTriangleExclamation} style={{ color: "var(--coco-danger)" }} />
        {titulo}
      </DialogTitle>
      <DialogContent>
        <Typography sx={{ color: "var(--coco-text-secondary)" }}>{mensaje}</Typography>
      </DialogContent>
      <DialogActions sx={{ padding: 2 }}>
        <Button onClick={onCancelar} color="inherit">
          Cancelar
        </Button>
        <Button
          onClick={onConfirmar}
          variant="contained"
          color="error"
          disabled={cargando}
        >
          {cargando ? "Eliminando..." : "Eliminar"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ModalConfirmacion;
