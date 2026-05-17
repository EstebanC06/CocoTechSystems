/**
 * Layout principal del área administrativa.
 * Envuelve contenido con sidebar fijo + área principal con scroll.
 */
import { Box } from "@mui/material";
import type { ReactNode } from "react";
import SidebarAdmin from "./SidebarAdmin";

interface Props {
  children: ReactNode;
  titulo?: string;
  subtitulo?: string;
  acciones?: ReactNode;
}

const LayoutAdmin = ({ children, titulo, subtitulo, acciones }: Props) => {
  return (
    <Box sx={{ display: "flex", minHeight: "100vh", backgroundColor: "var(--coco-bg)" }}>
      <SidebarAdmin />
      <Box component="main" sx={{ flexGrow: 1, padding: "1.5rem 2rem" }}>
        {(titulo || acciones) && (
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginBottom: 3,
              flexWrap: "wrap",
              gap: 2,
            }}
          >
            <Box>
              {titulo && (
                <Box component="h1" sx={{ margin: 0, fontSize: 24, fontWeight: 600, color: "var(--coco-text)" }}>
                  {titulo}
                </Box>
              )}
              {subtitulo && (
                <Box sx={{ marginTop: 0.5, color: "var(--coco-text-secondary)", fontSize: 13 }}>
                  {subtitulo}
                </Box>
              )}
            </Box>
            {acciones && <Box>{acciones}</Box>}
          </Box>
        )}
        {children}
      </Box>
    </Box>
  );
};

export default LayoutAdmin;
