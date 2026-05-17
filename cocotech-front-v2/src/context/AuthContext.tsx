/**
 * Contexto de autenticación de CocoTech.
 *
 * Provee el estado global de la sesión del usuario y los métodos para
 * iniciar y cerrar sesión. Se sincroniza con localStorage a través del
 * servicio de autenticación.
 */
import {
  createContext,
  useContext,
  useState,
  useEffect,
  type ReactNode,
} from "react";
import {
  obtenerSesion,
  guardarSesion as guardarSesionSvc,
  cerrarSesion as cerrarSesionSvc,
} from "../services/auth.service";
import type { SesionUsuario } from "../types";

interface AuthContextType {
  sesion: SesionUsuario | null;
  iniciarSesion: (s: SesionUsuario) => void;
  cerrarSesion: () => void;
  esAdmin: boolean;
  esEmpleado: boolean;
  esCliente: boolean;
  estaAutenticado: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [sesion, setSesion] = useState<SesionUsuario | null>(null);

  useEffect(() => {
    setSesion(obtenerSesion());
  }, []);

  const iniciarSesion = (nuevaSesion: SesionUsuario) => {
    guardarSesionSvc(nuevaSesion);
    setSesion(nuevaSesion);
  };

  const cerrarSesion = () => {
    cerrarSesionSvc();
    setSesion(null);
  };

  return (
    <AuthContext.Provider
      value={{
        sesion,
        iniciarSesion,
        cerrarSesion,
        esAdmin: sesion?.rol === "ROLE_ADMIN",
        esEmpleado: sesion?.rol === "ROLE_EMPLEADO",
        esCliente: sesion?.rol === "ROLE_CLIENTE",
        estaAutenticado: sesion !== null,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth debe usarse dentro de AuthProvider");
  return ctx;
};
