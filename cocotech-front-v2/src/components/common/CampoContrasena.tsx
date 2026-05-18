/**
 * Campo de contraseña con toggle de visibilidad.
 *
 * Reutilizable en Login, Register y cualquier formulario que tenga un
 * campo de contraseña. Usa el patrón estándar de MUI con InputAdornment
 * y un IconButton al final del input.
 */
import { useState } from "react";
import {
  TextField,
  InputAdornment,
  IconButton,
  type TextFieldProps,
} from "@mui/material";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEye, faEyeSlash } from "@fortawesome/free-solid-svg-icons";

/**
 * Acepta todas las props de TextField excepto `type` (que controlamos
 * internamente para alternar entre "password" y "text").
 */
type CampoContrasenaProps = Omit<TextFieldProps, "type">;

const CampoContrasena = (props: CampoContrasenaProps) => {
  const [visible, setVisible] = useState(false);

  return (
    <TextField
      {...props}
      type={visible ? "text" : "password"}
      InputProps={{
        ...(props.InputProps ?? {}),
        endAdornment: (
          <InputAdornment position="end">
            <IconButton
              aria-label={
                visible ? "Ocultar contraseña" : "Mostrar contraseña"
              }
              onClick={() => setVisible((v) => !v)}
              edge="end"
              size="small"
            >
              <FontAwesomeIcon icon={visible ? faEyeSlash : faEye} />
            </IconButton>
          </InputAdornment>
        ),
      }}
    />
  );
};

export default CampoContrasena;
