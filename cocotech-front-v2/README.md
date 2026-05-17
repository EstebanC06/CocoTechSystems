# CocoTech Frontend v2 — E-commerce

Plataforma e-commerce de CocoTech, construida con **React 19 + TypeScript + Vite + MUI**.

> **Proyecto académico** — Universidad El Bosque · Software Engineering.

---

## Arranque rápido

```bash
npm install
npm run dev
```

Sirve en `http://localhost:5173`. Por defecto consume el backend en `http://localhost:8080`.

---

## Tres áreas, tres roles

| Área | Quién | Rutas principales |
|---|---|---|
| **Tienda pública** | Cualquier visitante | `/`, `/productos`, `/producto/:id`, `/carrito` |
| **Cliente autenticado** | `ROLE_CLIENTE` | `/checkout`, `/cliente/pedidos`, `/cliente/perfil`, `/cliente/direcciones` |
| **Empleado** | `ROLE_EMPLEADO` | `/empleado`, `/empleado/pedidos`, `/empleado/punto-venta`, `/empleado/productos` |
| **Admin** | `ROLE_ADMIN` | `/admin`, `/admin/pedidos`, `/admin/clientes`, `/admin/inventario`, + CRUDs |

El catálogo es navegable sin login; la autenticación se pide solo al hacer checkout.

---

## Stack

- **React 19 + TypeScript** (Vite)
- **MUI v7** + PrimeReact (DataTable de reportes)
- **react-router-dom** v7
- **axios** con interceptor JWT
- **recharts** para gráficas de reportes
- **framer-motion** para animaciones suaves
- **FontAwesome** para iconografía

---

## Estructura

```
src/
  App.tsx              ← rutas + guards por rol
  context/
    AuthContext        ← sesión JWT (3 roles)
    CarritoContext     ← carrito persistido en localStorage
    TemaContext        ← modo claro/oscuro
  pages/
    public/            ← Home, Catalogo, Detalle, Carrito, Checkout
    auth/              ← Login, Register, etc.
    cliente/           ← MisPedidos, DetallePedido, Perfil, Direcciones
    empleado/          ← Dashboard, Pedidos, POS, Productos, Perfil
    admin/             ← Dashboard + todos los CRUDs + Pedidos/Inventario/Clientes
  components/
    layout/            ← Navbar, Layouts y Sidebars
    producto/          ← TarjetaProducto
    common/            ← Logo, ToggleTema, ModalConfirmacion
  services/            ← clientes axios por entidad
  types/               ← DTOs sincronizados con el backend
```

---

## Backend asumido

El frontend espera estos endpoints (algunos aún por implementar en Spring Boot):

### Públicos (sin JWT)
- `GET /publico/producto/mostrarTodos`
- `GET /publico/producto/obtenerPorId/{id}`
- `GET /publico/producto/buscar?q=`
- `GET /publico/producto/destacados`
- `GET /publico/producto/porCategoria/{id}`
- `GET /publico/categoria/mostrarTodas`

> Mientras no existan, el frontend hace fallback a los endpoints autenticados normales.

### Pedidos (nuevo)
- `POST /pedido/crear`
- `GET /pedido/mostrarTodos`
- `GET /pedido/obtenerPorId/{id}`
- `GET /pedido/cliente/{idCliente}`
- `GET /pedido/sucursal/{idSucursal}`
- `GET /pedido/porEstado/{estado}`
- `PUT /pedido/cambiarEstado?id=&nuevoEstado=`
- `DELETE /pedido/cancelar/{id}`

### Direcciones (nuevo)
- `POST /direccion/crear`
- `GET /direccion/cliente/{idCliente}`
- `PUT /direccion/actualizar?id=`
- `DELETE /direccion/eliminar/{id}`
- `PUT /direccion/marcarPredeterminada/{id}`

### Cambios en entidades existentes
- `Producto`: agregar `imagenUrl`, `descripcion`, `descuentoPorcentaje`, `destacado`, `activo`
- `Categoria`: agregar `imagenUrl`, `icono`
- `Usuario`/`Rol`: agregar tercer rol `ROLE_EMPLEADO`

---

## Estado de implementación

**Entrega 1 (esta) — Estructura básica funcional:**
- ✅ Catálogo público navegable sin login
- ✅ Carrito persistente
- ✅ Checkout 3 pasos con pagos simulados
- ✅ Gestión de pedidos (cliente, empleado, admin)
- ✅ POS físico para empleado
- ✅ CRUDs admin heredados del v1 + 3 nuevas vistas

**Entrega 2 (pendiente) — Pulido:**
- 🔲 Banners y carruseles destacados en Home
- 🔲 Galería de imágenes en detalle de producto
- 🔲 Reportes con más gráficas
- 🔲 Notificaciones de cambio de estado
- 🔲 Edición de productos con imagen desde Admin
- 🔲 Más filtros avanzados en catálogo

---

## Cuentas de prueba

Crear desde el backend mediante `LoadDatabase`:

| Rol | Credenciales sugeridas |
|---|---|
| Admin | `admin@cocotech.co` / `Admin12345!` |
| Empleado | (crear desde admin después del login) |
| Cliente | Registro libre desde `/register` |
