/**
 * Contexto de tema de la aplicación.
 * Maneja el cambio entre modo claro y oscuro persistiendo la preferencia
 * en localStorage.
 */
import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import { ThemeProvider as MuiThemeProvider, createTheme } from "@mui/material";
import { CssBaseline } from "@mui/material";

type Modo = "light" | "dark";

interface TemaContextType {
  modo: Modo;
  alternarModo: () => void;
}

const TemaContext = createContext<TemaContextType | undefined>(undefined);

/**
 * Paleta de colores oficial de CocoTech (Opción 1: Verde + Naranja).
 *
 * - Primario:    Verde bosque  #2D6A4F
 * - Secundario:  Naranja       #F77F00
 * - Verde claro: Acento suave  #C0DD97
 */
export const COLORES_COCOTECH = {
  primary: "#2D6A4F",
  primaryDark: "#1B3B2E",
  primaryLight: "#52A37C",
  secondary: "#F77F00",
  secondaryDark: "#C66600",
  accent: "#C0DD97",
  greenFill: "#EAF3DE",
  amber: "#FAC775",
  red: "#E24B4A",
  blue: "#378ADD",
};

export const TemaProvider = ({ children }: { children: ReactNode }) => {
  const [modo, setModo] = useState<Modo>(() => {
    const guardado = localStorage.getItem("cocotech_tema") as Modo | null;
    return guardado ?? "light";
  });

  useEffect(() => {
    localStorage.setItem("cocotech_tema", modo);
    document.body.dataset.tema = modo;
  }, [modo]);

  const alternarModo = () =>
    setModo((prev) => (prev === "light" ? "dark" : "light"));

  /**
   * Configuración del tema MUI sincronizada con la paleta CocoTech.
   */
  const tema = useMemo(
    () =>
      createTheme({
        palette: {
          mode: modo,
          primary: {
            main: COLORES_COCOTECH.primary,
            dark: COLORES_COCOTECH.primaryDark,
            light: COLORES_COCOTECH.primaryLight,
            contrastText: "#FFFFFF",
          },
          secondary: {
            main: COLORES_COCOTECH.secondary,
            dark: COLORES_COCOTECH.secondaryDark,
            contrastText: "#FFFFFF",
          },
          background: {
            default: modo === "light" ? "#F8F9F4" : "#0F1814",
            paper: modo === "light" ? "#FFFFFF" : "#243329",
          },
          text: {
            primary: modo === "light" ? "#1A1A1A" : "#F0F0F0",
            secondary: modo === "light" ? "#6B6B6B" : "#A0B0A8",
          },
        },
        typography: {
          fontFamily: "'Inter', 'Segoe UI', system-ui, sans-serif",
          h1: { fontWeight: 600 },
          h2: { fontWeight: 600 },
          h3: { fontWeight: 600 },
        },
        shape: { borderRadius: 10 },
        components: {
          MuiButton: {
            styleOverrides: {
              root: {
                textTransform: "none",
                fontWeight: 500,
                borderRadius: 8,
              },
            },
          },
        },
      }),
    [modo]
  );

  return (
    <TemaContext.Provider value={{ modo, alternarModo }}>
      <MuiThemeProvider theme={tema}>
        <CssBaseline />
        {children}
      </MuiThemeProvider>
    </TemaContext.Provider>
  );
};

export const useTema = () => {
  const ctx = useContext(TemaContext);
  if (!ctx) throw new Error("useTema debe usarse dentro de TemaProvider");
  return ctx;
};
