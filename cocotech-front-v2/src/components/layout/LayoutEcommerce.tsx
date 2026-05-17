/**
 * Layout principal del e-commerce (público + cliente autenticado).
 * Navbar arriba, contenido en container, footer abajo.
 */
import { Box, Container, Typography } from "@mui/material";
import type { ReactNode } from "react";
import NavbarEcommerce from "./NavbarEcommerce";

interface Props {
  children: ReactNode;
  /** Si es true, no aplica container con maxWidth (para Home con secciones full-width). */
  fluid?: boolean;
}

const LayoutEcommerce = ({ children, fluid = false }: Props) => {
  return (
    <Box sx={{ minHeight: "100vh", display: "flex", flexDirection: "column", backgroundColor: "var(--coco-bg)" }}>
      <NavbarEcommerce />

      <Box component="main" sx={{ flex: 1 }}>
        {fluid ? (
          children
        ) : (
          <Container maxWidth="lg" sx={{ paddingY: 3 }}>
            {children}
          </Container>
        )}
      </Box>

      {/* Footer */}
      <Box
        sx={{
          backgroundColor: "var(--coco-primary-dark)",
          color: "#FFFFFF",
          paddingY: 3,
          paddingX: 2,
          textAlign: "center",
        }}
      >
        <Typography sx={{ fontSize: 13, opacity: 0.9, marginBottom: 0.5 }}>
          CocoTech © 2026 · Universidad El Bosque
        </Typography>
        <Typography sx={{ fontSize: 11, opacity: 0.6 }}>
          Tu supermercado online · Proyecto académico
        </Typography>
      </Box>
    </Box>
  );
};

export default LayoutEcommerce;
