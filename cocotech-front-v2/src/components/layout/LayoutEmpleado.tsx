/**
 * Layout para el área de Empleado: Sidebar + contenido.
 */
import { Box } from "@mui/material";
import type { ReactNode } from "react";
import SidebarEmpleado from "./SidebarEmpleado";

const LayoutEmpleado = ({ children }: { children: ReactNode }) => (
  <Box sx={{ display: "flex", minHeight: "100vh", backgroundColor: "var(--coco-bg)" }}>
    <SidebarEmpleado />
    <Box component="main" sx={{ flex: 1, padding: 3, overflow: "auto" }}>
      {children}
    </Box>
  </Box>
);

export default LayoutEmpleado;
