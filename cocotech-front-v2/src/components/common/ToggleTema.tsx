/**
 * Botón flotante o inline para alternar entre modo claro y oscuro.
 */
import { IconButton, Tooltip } from "@mui/material";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSun, faMoon } from "@fortawesome/free-solid-svg-icons";
import { useTema } from "../../context/TemaContext";

interface Props {
  /** Si es true, muestra un ícono sin marco. Por defecto IconButton. */
  inline?: boolean;
}

const ToggleTema = ({ inline = false }: Props) => {
  const { modo, alternarModo } = useTema();

  if (inline) {
    return (
      <span
        onClick={alternarModo}
        style={{ cursor: "pointer", color: "var(--coco-text)" }}
      >
        <FontAwesomeIcon icon={modo === "light" ? faMoon : faSun} />
      </span>
    );
  }

  return (
    <Tooltip title={modo === "light" ? "Modo oscuro" : "Modo claro"}>
      <IconButton onClick={alternarModo} color="inherit" size="medium">
        <FontAwesomeIcon icon={modo === "light" ? faMoon : faSun} />
      </IconButton>
    </Tooltip>
  );
};

export default ToggleTema;
