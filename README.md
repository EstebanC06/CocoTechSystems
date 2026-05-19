# CocoTechSystems

Plataforma completa para la gestión de un supermercado de cadena, que combina un **punto de venta físico (POS)** y un **e-commerce** en un solo sistema. El backend está construido con **Spring Boot 3.3.5**, **MySQL** como motor de base de datos relacional y **MongoDB** como motor de base de datos no relacional y capa de lectura intensiva. El frontend es una **SPA en React 19 + TypeScript** que cubre las tres caras del negocio: tienda pública, panel de empleado y panel de administración.

---

## Tabla de contenidos

1. [Descripción general](#descripción-general)
2. [Stack tecnológico](#stack-tecnológico)
3. [Arquitectura del proyecto](#arquitectura-del-proyecto)
4. [Módulos funcionales](#módulos-funcionales)
5. [Seguridad](#seguridad)
6. [Patrones de diseño MongoDB](#patrones-de-diseño-mongodb)
   - [Referencia Extendida — `FacturaDocumento`](#patrón-1-referencia-extendida--facturadocumento)
   - [Computado — `ReporteVentasMensual`](#patrón-2-computado--reporteventasmensual)
7. [Endpoints principales](#endpoints-principales)
8. [Frontend](#frontend)
9. [Requisitos previos](#requisitos-previos)
10. [Configuración y ejecución](#configuración-y-ejecución)
11. [Documentación de la API](#documentación-de-la-api)

---

## Descripción general

CocoTechSystems es una empresa encargada de desarrollar soluciones tecnológicas, en este caso un sistema integral para un supermercado con múltiples sucursales. La plataforma cubre dos modos de operación simultáneos:

- **POS físico:** ventas realizadas por empleados en una caja registradora dentro de una sucursal, con generación inmediata de factura.
- **E-commerce:** pedidos online realizados por clientes desde el catálogo público, con flujo completo de checkout, preparación, despacho a domicilio o recogida en sucursal, y generación automática de venta y factura al momento de la entrega.

El sistema permite:

- Gestionar el catálogo de productos por categorías y proveedores, con productos destacados, descuentos y control de stock.
- Registrar ventas con múltiples detalles (productos, cantidades, descuentos, métodos de pago).
- Emitir facturas automáticamente por cada venta realizada.
- Gestionar pedidos online de extremo a extremo: `RECIBIDO → PREPARANDO → LISTO_PARA_ENTREGA / EN_CAMINO → ENTREGADO`, con cancelación controlada y restitución de stock.
- Administrar direcciones de envío guardadas por cliente, con dirección predeterminada para agilizar el checkout.
- Administrar empleados asignados a sucursales y cajas registradoras.
- Administrar clientes y su historial de compras y pedidos.
- Generar reportes de rendimiento por sucursal y periodos de tiempo.
- Autenticar y autorizar usuarios con roles diferenciados (`ADMIN`, `EMPLEADO`, `CLIENTE`), recuperación de contraseña por código enviado al correo y registro libre para clientes.

MySQL actúa como la **fuente de verdad transaccional** (operaciones ACID: ventas, stock, pedidos, autenticación). MongoDB actúa como la **capa de lectura intensiva**, sirviendo consultas de historial, reportes y dashboards sin operaciones JOIN costosas.

---

## Stack tecnológico

### Backend

| Componente                  | Tecnología                          |
| --------------------------- | ----------------------------------- |
| Framework                   | Spring Boot 3.3.5                   |
| Lenguaje                    | Java 21                             |
| Base de datos transaccional | MySQL 8 + Spring Data JPA (Hibernate) |
| Base de datos documental    | MongoDB 7 + Spring Data MongoDB     |
| Seguridad                   | Spring Security + JWT (jjwt 0.11.5) |
| Documentación API           | SpringDoc OpenAPI (Swagger UI) 2.3.0 |
| Cifrado de datos sensibles  | AES (Apache Commons Codec)          |
| Mapeo de objetos            | ModelMapper 3.1.1                   |
| Envío de correos            | Spring Mail (SMTP)                  |
| Serialización JSON          | Gson                                |
| Empaquetado                 | WAR (Tomcat embebido)               |
| Build                       | Maven Wrapper                       |

### Frontend

| Componente            | Tecnología                          |
| --------------------- | ----------------------------------- |
| Framework             | React 19 + TypeScript               |
| Build tool            | Vite 6                              |
| UI                    | MUI v7 + PrimeReact 10 + Bootstrap 5 |
| Cliente HTTP          | axios (con interceptor JWT)         |
| Routing               | react-router-dom v7                 |
| Formularios           | react-hook-form + Zod               |
| Gráficas              | Recharts                            |
| Animaciones           | Framer Motion                       |
| Iconos                | FontAwesome + Material Icons + PrimeIcons |

---

## Arquitectura del proyecto

### Backend (`cocotechback`)

```
cocotechback/
├── configuration/       Configuración de Spring (persistencia dual, datos iniciales, OpenAPI)
├── controller/          Controladores REST (un controlador por entidad + mongo + público)
├── dto/                 Data Transfer Objects para entrada/salida de la API
├── exception/           Manejo global de excepciones
├── model/
│   ├── (entidades JPA)  Modelos relacionales MySQL
│   └── mongo/           Documentos MongoDB (FacturaDocumento, ReporteVentasMensual)
├── repository/
│   ├── jpa/             Repositorios Spring Data JPA (MySQL)
│   └── mongo/           Repositorios Spring Data MongoDB
├── security/            Filtro JWT, utilidades de token, configuración de seguridad
├── service/             Lógica de negocio (un servicio por entidad + Mongo + Email)
└── util/                Utilidades transversales (AES, ModelMapper)
```

La configuración de persistencia dual (`PersistenceConfig.java`) le indica a Spring Boot qué repositorios corresponden a cada motor, evitando conflictos entre el contexto JPA y el contexto MongoDB.

### Frontend (`cocotech-front-v2`)

```
src/
├── App.tsx              Punto de entrada con todas las rutas + guards por rol
├── context/
│   ├── AuthContext      Sesión JWT (3 roles)
│   ├── CarritoContext   Carrito persistido en localStorage
│   └── TemaContext      Modo claro/oscuro
├── pages/
│   ├── public/          Home, Catálogo, Detalle producto, Carrito, Checkout
│   ├── auth/            Login, Register, Verificar código, Recuperar contraseña
│   ├── cliente/         Mis pedidos, Detalle pedido, Perfil, Direcciones
│   ├── empleado/        Dashboard, Pedidos, Productos, Perfil
│   └── admin/           Dashboard + CRUDs completos + Pedidos + Reportes
├── components/
│   ├── layout/          Navbars, Layouts y Sidebars por rol
│   ├── producto/        Tarjeta de producto reutilizable
│   └── common/          Logo, ToggleTema, ModalConfirmacion
├── services/            Clientes axios por entidad
├── types/               DTOs sincronizados con el backend
└── utils/               Utilidades (etiquetas, formateadores)
```

---

## Módulos funcionales

### Gestión de productos y catálogo

- CRUD completo de `Producto`, `Categoria` y `Proveedor`.
- Los productos pertenecen a una categoría y pueden estar asociados a un proveedor.
- Soporte para imagen, descripción, descuento porcentual, marca de destacado y bandera de activo/inactivo.
- Endpoints públicos (`/publico/...`) que permiten navegar el catálogo, ver detalle de producto, buscar y filtrar por categoría **sin necesidad de iniciar sesión**.

### Gestión de ventas (POS físico)

- Registro de `Venta` con uno o varios `DetalleVenta` (producto, cantidad, precio unitario, descuento, método de pago, promoción).
- Cada venta está asociada a un cliente, un empleado y una caja registradora.
- Al crear o actualizar una venta se genera automáticamente su `Factura` en MySQL y se proyecta a MongoDB.

### Gestión de pedidos (e-commerce)

- Registro de `Pedido` con uno o varios `DetallePedido`, asociado a un cliente, una sucursal de despacho y opcionalmente una dirección de envío.
- Dos modalidades de entrega: **a domicilio** (con dirección desnormalizada en el pedido) o **recoger en sucursal**.
- Máquina de estados controlada: `RECIBIDO → PREPARANDO → LISTO_PARA_ENTREGA → (recoger)` o `RECIBIDO → PREPARANDO → EN_CAMINO → ENTREGADO`. La cancelación solo se permite en estado `RECIBIDO` y restituye el stock.
- Cuando un pedido pasa a `ENTREGADO`, el sistema genera automáticamente la `Venta`, los `DetalleVenta` y la `Factura` correspondientes, integrando el e-commerce con el módulo de ventas existente.

### Facturación

- `Factura` registra precio total, impuestos y fecha de emisión con relación 1:1 a la venta.
- `FacturaDocumento` en MongoDB replica la factura con todos los datos embebidos para consulta rápida sin JOINs.
- Endpoint de sincronización para migrar facturas existentes en MySQL hacia MongoDB.

### Gestión de personas

- `Cliente`: datos personales y ciudad. Correo y otros campos sensibles almacenados con cifrado AES; contraseña con bcrypt.
- `Empleado`: datos personales, cargo y sucursal de pertenencia.
- `Usuario`: credenciales de acceso con contraseña bcrypt y rol asociado.
- Recuperación de contraseña por código de verificación enviado al correo del usuario vía `EmailService` (SMTP Gmail).

### Direcciones de envío

- `DireccionCliente`: un cliente puede tener múltiples direcciones guardadas (Casa, Oficina, etc.) y marcar una como predeterminada para agilizar el checkout.
- Al momento del pedido los datos de la dirección se **desnormalizan dentro del pedido** para preservar el snapshot histórico aunque el cliente luego edite o elimine la dirección original.

### Sucursales y cajas

- `Sucursal`: nombre y ciudad. Los empleados, las cajas registradoras y los pedidos de despacho pertenecen a una sucursal.
- `CajaRegistradora`: identificada por número y asociada a una sucursal.

### Reportes (MongoDB)

- **Ingreso bruto por sucursal** en un rango de fechas (agregación nativa MongoDB).
- **Top 10 productos más vendidos** por unidades e ingresos en un periodo.
- **Top 10 clientes** por monto gastado en un periodo.
- **Reportes mensuales pre-calculados** (`ReporteVentasMensual`) con cantidad de facturas, ingreso bruto, impuestos y ticket promedio por sucursal.
- **Evolución anual por sucursal**: serie mensual de los 12 meses del año para alimentar gráficas en el dashboard administrativo.

---

## Seguridad

La API usa autenticación **stateless con JWT**. El flujo es:

1. El cliente hace `POST /auth/login` con credenciales y recibe un token Bearer.
2. Cada petición subsiguiente incluye `Authorization: Bearer <token>`.
3. El filtro `JwtAuthenticationFilter` valida el token antes de que llegue al controlador.
4. La autorización fina se aplica por anotación `@PreAuthorize` en cada endpoint y por reglas declaradas en `SecurityConfig`.

**Roles disponibles:**

- `ROLE_ADMIN`: acceso completo, incluyendo operaciones de creación, modificación, eliminación y todos los reportes.
- `ROLE_EMPLEADO`: opera el POS físico, gestiona pedidos de su sucursal y avanza estados del flujo de preparación y entrega.
- `ROLE_CLIENTE`: navega el catálogo, realiza pedidos online, consulta su historial y gestiona sus direcciones y datos personales.

**Endpoints públicos** (sin token):

- `/auth/**` — login, verificación de código, solicitud y recuperación de contraseña.
- `/publico/**` — catálogo navegable sin login (productos, categorías, sucursales).
- `/swagger-ui/**`, `/v3/api-docs/**` — documentación de la API.
- `POST /cliente/crear` — registro libre de nuevos clientes.

**Recuperación de contraseña por correo:**

1. El usuario solicita un código en `POST /auth/solicitarCodigoRecuperacion`.
2. `EmailService` envía un código numérico al correo registrado vía SMTP.
3. El usuario lo confirma en `POST /auth/verificarCodigo` y obtiene una ventana temporal para cambiar su contraseña.
4. La nueva contraseña se establece en `PUT /auth/recuperarContrasenaCliente` o `PUT /auth/recuperarContrasenaEmpleado`.

---

## Patrones de diseño MongoDB

El proyecto utiliza dos patrones de diseño de esquemas recomendados por MongoDB para complementar la base de datos relacional MySQL. La decisión de no migrar todo a MongoDB responde a que las operaciones transaccionales del negocio (descontar stock, verificar inventario, autenticación, cambios de estado de pedido) requieren garantías ACID que MySQL provee de forma nativa y directa.

### Patrón 1: Referencia Extendida — `FacturaDocumento`

#### ¿Por qué se eligió?

Una factura es el artefacto de lectura más frecuente del sistema: los clientes consultan su historial, los administradores generan reportes contables, y el sistema necesita mostrar el detalle completo de una compra en pantalla. En MySQL, responder a cualquiera de esas consultas requiere encadenar varios JOINs: `Factura → Venta → Cliente`, `Venta → Empleado → Sucursal`, `Venta → DetalleVenta → Producto → Categoria`. Ese costo se paga en cada lectura.

El patrón de Referencia Extendida resuelve esto embebiendo en un único documento MongoDB todos los datos que se necesitan para mostrar la factura completa: el snapshot del cliente, del empleado, de la sucursal y cada línea de detalle con su producto. El resultado es una lectura O(1), independiente del número de tablas relacionadas.

Adicionalmente, las facturas son **documentos legales**: deben preservar el estado en el momento de su emisión. Si un cliente cambia de correo, o un producto cambia de precio, la factura debe seguir mostrando los datos originales. El embedding garantiza esa inmutabilidad histórica por diseño, algo que en un modelo normalizado requeriría lógica adicional.

#### Función dentro del proyecto

`FacturaDocumento` vive en la colección `facturas` de MongoDB. Cada vez que se crea o actualiza una factura en MySQL — ya sea desde el POS físico o cuando un pedido online pasa a `ENTREGADO` — `FacturaMongoService.proyectar()` construye y persiste el documento embebido de forma asíncrona. Si la proyección falla (por ejemplo, si MongoDB está temporalmente caído), la factura ya está segura en MySQL y el endpoint `POST /factura/mongo/sincronizar` permite recuperar los registros pendientes en cualquier momento.

El campo `idFacturaMySQL` actúa como puente de trazabilidad: permite cruzar un documento MongoDB con su registro origen en la base de datos transaccional.

Los endpoints que se benefician directamente de este patrón son:

- `GET /factura/mongo/historialCliente/{idCliente}` — historial completo de un cliente sin JOINs.
- `GET /factura/mongo/obtenerPorIdMySQL/{id}` — factura completa en una sola lectura.
- `GET /factura/mongo/reportes/ingresoPorSucursal` — agregación nativa sobre los documentos embebidos.
- `GET /factura/mongo/reportes/topProductos` — ranking calculado con `$unwind` sobre los detalles embebidos.
- `GET /factura/mongo/reportes/topClientes` — ranking de clientes por monto gastado.

---

### Patrón 2: Computado — `ReporteVentasMensual`

#### ¿Por qué se eligió?

Los dashboards administrativos consultan métricas agregadas (ingresos totales, cantidad de facturas, ticket promedio) repetidamente a lo largo del día. Si cada consulta de dashboard recalculara esos totales recorriendo toda la colección de facturas del mes, el costo de CPU y latencia escalaría con el volumen de datos. En un supermercado con alta frecuencia de ventas, eso se vuelve un cuello de botella real.

El patrón Computado desacopla el cálculo de la consulta: las agregaciones se ejecutan una sola vez (al cierre del mes o mediante el endpoint de recálculo) y el resultado se guarda en un documento listo para leer. Las consultas posteriores al dashboard son lecturas directas O(1), sin ningún cómputo adicional.

Se eligió este patrón sobre alternativas como vistas materializadas en MySQL porque MongoDB ofrece un pipeline de agregación nativo (`$group`, `$match`, `$sort`) más expresivo para este tipo de cálculos, y el documento resultante se integra de forma natural con la capa de reportes ya construida sobre MongoDB.

#### Función dentro del proyecto

`ReporteVentasMensual` vive en la colección `reportes_ventas_mensuales`. Cada documento representa el resumen de un mes para una sucursal específica, almacenando: cantidad de facturas emitidas, ingreso bruto, total de impuestos y ticket promedio. El campo `actualizadoEn` registra la última vez que se recalculó.

`ReporteService` expone la lógica de recálculo, que lee las facturas del periodo desde MongoDB (ya embebidas por el patrón anterior), calcula los totales y sobreescribe o crea el documento de reporte correspondiente.

Los endpoints que materializan este patrón son:

- `POST /reportes/recalcular/{anio}/{mes}` — dispara el recálculo para todos los reportes del mes indicado.
- `GET /reportes/mes/{anio}/{mes}` — devuelve los reportes pre-calculados de un mes.
- `GET /reportes/evolucion/{anio}/{idSucursal}` — serie mensual de los 12 meses del año para una sucursal, lista para alimentar gráficas del dashboard.

Los reportes resultantes pueden consultarse de forma directa para alimentar cualquier dashboard o exportación contable sin impacto en el rendimiento transaccional de MySQL.

---

## Endpoints principales

> Todos los endpoints están bajo el context-path `/api`. La base por defecto es `http://localhost:9999/api`.

### Autenticación

| Método | Ruta                                  | Acceso  | Descripción                              |
| ------ | ------------------------------------- | ------- | ---------------------------------------- |
| POST   | `/auth/login`                         | Público | Obtiene token JWT                        |
| POST   | `/auth/solicitarCodigoRecuperacion`   | Público | Envía código por correo                  |
| POST   | `/auth/verificarCodigo`               | Público | Valida el código de recuperación         |
| PUT    | `/auth/recuperarContrasenaCliente`    | Público | Resetea contraseña de cliente            |
| PUT    | `/auth/recuperarContrasenaEmpleado`   | Público | Resetea contraseña de empleado           |
| POST   | `/cliente/crear`                      | Público | Registro libre de nuevos clientes        |

### Catálogo público (sin JWT)

| Método | Ruta                                       | Descripción                          |
| ------ | ------------------------------------------ | ------------------------------------ |
| GET    | `/publico/producto/mostrarTodos`           | Catálogo completo                    |
| GET    | `/publico/producto/obtenerPorId/{id}`      | Detalle de un producto               |
| GET    | `/publico/producto/buscar?q=`              | Búsqueda por nombre                  |
| GET    | `/publico/producto/destacados`             | Productos marcados como destacados   |
| GET    | `/publico/producto/porCategoria/{id}`      | Filtro por categoría                 |
| GET    | `/publico/categoria/mostrarTodas`          | Listado de categorías                |
| GET    | `/publico/sucursal/mostrarTodas`           | Listado de sucursales                |

### Pedidos (e-commerce)

| Método | Ruta                                | Rol                       | Descripción                          |
| ------ | ----------------------------------- | ------------------------- | ------------------------------------ |
| POST   | `/pedido/crear`                     | CLIENTE                   | Crear pedido desde el checkout       |
| GET    | `/pedido/mostrarTodos`              | ADMIN                     | Listar todos los pedidos             |
| GET    | `/pedido/obtenerPorId/{id}`         | CLIENTE, EMPLEADO, ADMIN  | Detalle de un pedido                 |
| GET    | `/pedido/cliente/{idCliente}`       | CLIENTE, ADMIN            | Pedidos de un cliente                |
| GET    | `/pedido/sucursal/{idSucursal}`     | EMPLEADO, ADMIN           | Pedidos por sucursal                 |
| GET    | `/pedido/porEstado/{estado}`        | EMPLEADO, ADMIN           | Pedidos filtrados por estado         |
| PUT    | `/pedido/cambiarEstado`             | EMPLEADO, ADMIN           | Avanza el estado del pedido          |
| DELETE | `/pedido/cancelar/{id}`             | CLIENTE, ADMIN            | Cancela un pedido en `RECIBIDO`      |
| GET    | `/pedido/contar`                    | ADMIN                     | Conteo total de pedidos              |

### Direcciones de envío

| Método | Ruta                                       | Rol                       | Descripción                          |
| ------ | ------------------------------------------ | ------------------------- | ------------------------------------ |
| POST   | `/direccion/crear`                         | CLIENTE, ADMIN            | Guardar nueva dirección              |
| GET    | `/direccion/cliente/{idCliente}`           | CLIENTE, EMPLEADO, ADMIN  | Direcciones de un cliente            |
| GET    | `/direccion/obtenerPorId/{id}`             | CLIENTE, ADMIN            | Detalle de una dirección             |
| PUT    | `/direccion/actualizar`                    | CLIENTE, ADMIN            | Editar una dirección                 |
| PUT    | `/direccion/marcarPredeterminada/{id}`     | CLIENTE, ADMIN            | Marcar como predeterminada           |
| DELETE | `/direccion/eliminar/{id}`                 | CLIENTE, ADMIN            | Eliminar una dirección               |

### Facturas MongoDB

| Método | Ruta                                          | Rol            | Descripción                    |
| ------ | --------------------------------------------- | -------------- | ------------------------------ |
| GET    | `/factura/mongo/historialCliente/{idCliente}` | ADMIN, CLIENTE | Historial completo sin JOINs   |
| GET    | `/factura/mongo/obtenerPorIdMySQL/{id}`       | ADMIN, CLIENTE | Factura embebida por ID        |
| GET    | `/factura/mongo/porPeriodo`                   | ADMIN          | Facturas en rango de fechas    |
| GET    | `/factura/mongo/reportes/ingresoPorSucursal`  | ADMIN          | Ingreso bruto por sucursal     |
| GET    | `/factura/mongo/reportes/topProductos`        | ADMIN          | Top 10 productos más vendidos  |
| GET    | `/factura/mongo/reportes/topClientes`         | ADMIN          | Top 10 clientes por gasto      |
| POST   | `/factura/mongo/sincronizar`                  | ADMIN          | Sincronización MySQL → MongoDB |

### Reportes computados

| Método | Ruta                                       | Rol   | Descripción                        |
| ------ | ------------------------------------------ | ----- | ---------------------------------- |
| POST   | `/reportes/recalcular/{anio}/{mes}`        | ADMIN | Recalcula reportes mensuales       |
| GET    | `/reportes/mes/{anio}/{mes}`               | ADMIN | Reportes del mes (todas sucursales)|
| GET    | `/reportes/evolucion/{anio}/{idSucursal}`  | ADMIN | Serie anual de una sucursal        |

Los demás recursos (producto, categoría, venta, detalleVenta, empleado, cliente, sucursal, proveedor, caja registradora) exponen CRUD completo bajo sus respectivas rutas, protegidos según el rol requerido.

---

## Frontend

El frontend es una **SPA en React 19 + TypeScript** (build con Vite) que cubre las tres caras del negocio:

| Área                    | Quién                  | Rutas principales                                                                       |
| ----------------------- | ---------------------- | --------------------------------------------------------------------------------------- |
| **Tienda pública**      | Cualquier visitante    | `/`, `/productos`, `/producto/:id`, `/categoria/:id`, `/carrito`                        |
| **Cliente autenticado** | `ROLE_CLIENTE`         | `/checkout`, `/cliente/pedidos`, `/cliente/pedido/:id`, `/cliente/perfil`, `/cliente/direcciones` |
| **Empleado**            | `ROLE_EMPLEADO`        | `/empleado`, `/empleado/pedidos`, `/empleado/productos`, `/empleado/perfil`             |
| **Admin**               | `ROLE_ADMIN`           | `/admin`, `/admin/pedidos`, `/admin/clientes`, `/admin/inventario`, `/admin/productos`, `/admin/categorias`, `/admin/proveedores`, `/admin/empleados`, `/admin/sucursales`, `/admin/cajas`, `/admin/ventas`, `/admin/facturas`, `/admin/reportes` |

El catálogo es navegable sin login: la autenticación se pide solo al momento del checkout. El componente `<RutaProtegida>` (en `App.tsx`) valida JWT + rol antes de renderizar cada página protegida y redirige al dashboard correspondiente si el rol no coincide.

El cliente axios (`src/services/api.ts`) inyecta el JWT automáticamente desde `localStorage` y, ante respuestas `401`/`403`, limpia la sesión y redirige al login (excepto en endpoints de auth, donde un 401 es un error legítimo de credenciales).

Por defecto el frontend consume el backend en `http://localhost:9999/api`. La constante `BASE_URL` en `src/services/api.ts` controla esto.

---

## Requisitos previos

- Java 21
- Maven (o usar el wrapper `./mvnw` incluido)
- Node.js 20+ y npm (para el frontend)
- MySQL 8+ (local o en la nube, como Google Cloud SQL)
- MongoDB 7+ en una de estas opciones:
  - **Docker**: `docker run -d --name cocotech-mongo -p 27017:27017 mongo:7`
  - **MongoDB Atlas** (gratuito): cluster M0 con URI `mongodb+srv://...` *(configuración por defecto del proyecto)*
  - **Instalación local** desde [mongodb.com/try/download/community](https://www.mongodb.com/try/download/community)
- Cuenta de correo SMTP para el envío de códigos de recuperación (por defecto Gmail con contraseña de aplicación).

---

## Configuración y ejecución

### Backend

1. Editar `cocotechback/src/main/resources/application.properties` si se quiere cambiar de las credenciales por defecto (que apuntan a Google Cloud SQL + MongoDB Atlas):

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/cocotech
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/cocotech
spring.data.mongodb.database=cocotech

# Servidor (puerto por defecto: 9999, context-path /api)
server.port=9999
server.servlet.context-path=/api

# Mail (Gmail con contraseña de aplicación)
spring.mail.username=TU_CORREO@gmail.com
spring.mail.password=TU_APP_PASSWORD
```

2. Levantar la aplicación:

```bash
cd cocotechback
./mvnw spring-boot:run
```

La API quedará disponible en `http://localhost:9999/api`. En el primer arranque, `LoadDatabase` puebla MySQL con datos de ejemplo, incluyendo facturas de prueba.

3. Sincronizar las facturas iniciales a MongoDB (una sola vez):

```bash
curl -X POST http://localhost:9999/api/factura/mongo/sincronizar \
  -H "Authorization: Bearer TU_JWT_DE_ADMIN"
```

### Frontend

```bash
cd cocotech-front-v2
npm install
npm run dev
```

El frontend queda disponible en `http://localhost:5173` y consume el backend en `http://localhost:9999/api` por defecto.

Para una build de producción:

```bash
npm run build
npm run preview
```

---

## Documentación de la API

Swagger UI disponible en: [http://localhost:9999/api/swagger-ui/index.html](http://localhost:9999/api/swagger-ui/index.html)

(El proyecto también expone Swagger en la raíz `/` del context-path por la propiedad `springdoc.swagger-ui.path=/`).

La documentación OpenAPI completa está en: `http://localhost:9999/api/v3/api-docs`
