/**
 * Tarjeta de producto para grilla del catálogo o carruseles del Home.
 * Muestra imagen, nombre, precio (con descuento si aplica), botón agregar.
 */
import { Box, Typography, Button, Chip } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCartPlus, faBoxOpen } from "@fortawesome/free-solid-svg-icons";
import { useState } from "react";
import { useCarrito } from "../../context/CarritoContext";
import type { ProductoDTO } from "../../types";

interface Props {
  producto: ProductoDTO;
}

const TarjetaProducto = ({ producto }: Props) => {
  const navigate = useNavigate();
  const { agregar, precioConDescuento } = useCarrito();
  const [imgError, setImgError] = useState(false);

  const tieneDescuento = (producto.descuentoPorcentaje ?? 0) > 0;
  const precioFinal = precioConDescuento(producto);
  const agotado = producto.stock <= 0;

  const handleAgregar = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (!agotado) agregar(producto, 1);
  };

  const irAlDetalle = () => navigate(`/producto/${producto.idProducto}`);

  return (
    <Box
      onClick={irAlDetalle}
      sx={{
        backgroundColor: "var(--coco-surface)",
        border: "1px solid var(--coco-border)",
        borderRadius: 2,
        overflow: "hidden",
        cursor: "pointer",
        transition: "all 0.2s ease",
        display: "flex",
        flexDirection: "column",
        height: "100%",
        position: "relative",
        "&:hover": {
          transform: "translateY(-3px)",
          boxShadow: "var(--coco-shadow-hover)",
          borderColor: "var(--coco-primary)",
        },
      }}
    >
      {/* Badge descuento */}
      {tieneDescuento && (
        <Chip
          label={`-${producto.descuentoPorcentaje}%`}
          size="small"
          sx={{
            position: "absolute",
            top: 8,
            left: 8,
            zIndex: 1,
            backgroundColor: "var(--coco-secondary)",
            color: "#FFFFFF",
            fontWeight: 700,
            fontSize: 11,
          }}
        />
      )}
      {agotado && (
        <Chip
          label="Agotado"
          size="small"
          sx={{
            position: "absolute",
            top: 8,
            right: 8,
            zIndex: 1,
            backgroundColor: "var(--coco-danger)",
            color: "#FFFFFF",
            fontWeight: 600,
            fontSize: 11,
          }}
        />
      )}

      {/* Imagen */}
      <Box
        sx={{
          height: 160,
          backgroundColor: "var(--coco-success-fill)",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          overflow: "hidden",
        }}
      >
        {producto.imagenUrl && !imgError ? (
          <img
            src={producto.imagenUrl}
            alt={producto.nombre}
            onError={() => setImgError(true)}
            style={{
              width: "100%",
              height: "100%",
              objectFit: "cover",
            }}
          />
        ) : (
          <FontAwesomeIcon
            icon={faBoxOpen}
            style={{ fontSize: 56, color: "var(--coco-primary)" }}
          />
        )}
      </Box>

      {/* Info */}
      <Box sx={{ padding: 1.5, display: "flex", flexDirection: "column", flex: 1 }}>
        <Typography
          sx={{
            fontSize: 13,
            fontWeight: 500,
            color: "var(--coco-text)",
            marginBottom: 1,
            minHeight: 36,
            display: "-webkit-box",
            WebkitLineClamp: 2,
            WebkitBoxOrient: "vertical",
            overflow: "hidden",
          }}
        >
          {producto.nombre}
        </Typography>

        <Box sx={{ marginBottom: 1.5, marginTop: "auto" }}>
          {tieneDescuento && (
            <Typography
              sx={{
                fontSize: 11,
                color: "var(--coco-text-muted)",
                textDecoration: "line-through",
              }}
            >
              ${producto.precio.toLocaleString("es-CO")}
            </Typography>
          )}
          <Typography
            sx={{
              fontSize: 17,
              fontWeight: 700,
              color: "var(--coco-primary)",
            }}
          >
            ${Math.round(precioFinal).toLocaleString("es-CO")}
          </Typography>
        </Box>

        <Button
          variant="contained"
          color="secondary"
          size="small"
          fullWidth
          disabled={agotado}
          startIcon={<FontAwesomeIcon icon={faCartPlus} style={{ fontSize: 12 }} />}
          onClick={handleAgregar}
          sx={{ fontSize: 12, paddingY: 0.7 }}
        >
          {agotado ? "Sin stock" : "Agregar"}
        </Button>
      </Box>
    </Box>
  );
};

export default TarjetaProducto;
