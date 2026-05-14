# CocoTechSystems
Backend y Frontend para el sistema de gestión de un supermercado de cadena. Construido con **Spring Boot 4**, **MySQL** como motor de base de datos relacional y **MongoDB** como motor de base de datos no relacional y capa de lectura intensiva. Expone una página web que cubre la operación completa del negocio: productos, ventas, facturación, empleados, clientes, proveedores, sucursales y reportes.

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
8. [Requisitos previos](#requisitos-previos)
9. [Configuración y ejecución](#configuración-y-ejecución)
10. [Documentación de la API](#documentación-de-la-api)

---

## Descripción general

CocoTechSystems es una empresa encargada de desarrollar soluciones tecnológicas, en este caso, un sistema POS (punto de venta) de gestión de empleados e inventario para un supermercado es un sistema con múltiples sucursales. El sistema permite:

- Gestionar el catálogo de productos por categorías y proveedores.
- Registrar ventas con múltiples detalles (productos, cantidades, descuentos, métodos de pago).
- Emitir facturas automáticamente por cada venta realizada.
- Administrar empleados asignados a sucursales y cajas registradoras.
- Administrar clientes y su historial de compras.
- Generar reportes de rendimiento por sucursal y periodos de tiempo.
- Autenticar y autorizar usuarios con roles diferenciados (`ADMIN`, `EMPLEADO` y `CLIENTE`).

MySQL actúa como la **fuente de verdad transaccional** (operaciones ACID: ventas, stock, autenticación). MongoDB actúa como la **capa de lectura intensiva**, sirviendo consultas de historial y reportes sin operaciones JOIN costosas.

---

## Stack tecnológico

| Componente                  | Tecnología                          |
| --------------------------- | ----------------------------------- |
| Framework                   | Spring Boot 4.0.6                   |
| Lenguaje                    | Java 21                             |
| Base de datos transaccional | MySQL + Spring Data JPA (Hibernate) |
| Base de datos documental    | MongoDB + Spring Data MongoDB       |
| Seguridad                   | Spring Security + JWT (jjwt 0.11.5) |
| Documentación API           | SpringDoc OpenAPI (Swagger UI)      |
| Cifrado de datos sensibles  | AES (Apache Commons Codec)          |
| Mapeo de objetos            | ModelMapper 3.1.1                   |
| Empaquetado                 | WAR (Tomcat embebido)               |
| Build                       | Maven Wrapper                       |

---

## Arquitectura del proyecto

```
cocotechback/
├── configuration/       Configuración de Spring (persistencia dual, datos iniciales, OpenAPI)
├── controller/          Controladores REST (un controlador por entidad + mongo)
├── dto/                 Data Transfer Objects para entrada/salida de la API
├── exception/           Manejo global de excepciones
├── model/
│   ├── (entidades JPA)  Modelos relacionales MySQL
│   └── mongo/           Documentos MongoDB (FacturaDocumento, ReporteVentasMensual)
├── repository/
│   ├── jpa/             Repositorios Spring Data JPA (MySQL)
│   └── mongo/           Repositorios Spring Data MongoDB
├── security/            Filtro JWT, utilidades de token, configuración de seguridad
├── service/             Lógica de negocio (un servicio por entidad + servicios Mongo)
└── util/                Utilidades transversales (AES, ModelMapper)
```

La configuración de persistencia dual (`PersistenceConfig.java`) le indica a Spring Boot qué repositorios corresponden a cada motor, evitando conflictos entre el contexto JPA y el contexto MongoDB.

---

## Módulos funcionales

### Gestión de productos y catálogo

- CRUD completo de `Producto`, `Categoria` y `Proveedor`.
- Los productos pertenecen a una categoría y pueden estar asociados a un proveedor.
- Endpoint público de lectura de categorías y productos para clientes autenticados.

### Gestión de ventas

- Registro de `Venta` con uno o varios `DetalleVenta` (producto, cantidad, precio unitario, descuento, método de pago, promoción).
- Cada venta está asociada a un cliente, un empleado y una caja registradora.
- Al crear o actualizar una venta se genera automáticamente su `Factura` en MySQL y se proyecta a MongoDB.

### Facturación

- `Factura` registra precio total, impuestos y fecha de emisión con relación 1:1 a la venta.
- `FacturaDocumento` en MongoDB replica la factura con todos los datos embebidos para consulta rápida sin JOINs.
- Endpoint de sincronización para migrar facturas existentes en MySQL hacia MongoDB.

### Gestión de personas

- `Cliente`: datos personales y ciudad. Correo almacenado con cifrado AES.
- `Empleado`: datos personales, cargo y sucursal de pertenencia.
- `Usuario`: credenciales de acceso con contraseña bcrypt y rol asociado.

### Sucursales y cajas

- `Sucursal`: nombre y ciudad. Los empleados y las cajas registradoras pertenecen a una sucursal.
- `CajaRegistradora`: identificada por número y asociada a una sucursal.

### Reportes (MongoDB)

- **Ingreso bruto por sucursal** en un rango de fechas (agregación nativa MongoDB).
- **Top 10 productos más vendidos** por unidades e ingresos en un periodo.
- **Top 10 clientes** por monto gastado en un periodo.
- **Reportes mensuales pre-calculados** (`ReporteVentasMensual`) con cantidad de facturas, ingreso bruto, impuestos y ticket promedio por sucursal.

---

## Seguridad

La API usa autenticación **stateless con JWT**. El flujo es:

1. El cliente hace `POST /auth/login` con credenciales y recibe un token Bearer.
2. Cada petición subsiguiente incluye `Authorization: Bearer <token>`.
3. El filtro `JwtAuthenticationFilter` valida el token antes de que llegue al controlador.
4. La autorización fina se aplica por anotación `@PreAuthorize` en cada endpoint.

**Roles disponibles:**

- `ROLE_ADMIN`: acceso completo, incluyendo operaciones de creación, modificación, eliminación y todos los reportes.
- `ROLE_CLIENTE`: acceso restringido a lectura de catálogo e historial propio.

**Endpoints públicos** (sin token): `/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `POST /cliente/crear`.

---

## Patrones de diseño MongoDB

El proyecto utiliza dos patrones de diseño de esquemas recomendados por MongoDB para complementar la base de datos relacional MySQL. La decisión de no migrar todo a MongoDB responde a que las operaciones transaccionales del negocio (descontar stock, verificar inventario, autenticación) requieren garantías ACID que MySQL provee de forma nativa y directa.

### Patrón 1: Referencia Extendida — `FacturaDocumento`

#### ¿Por qué se eligió?

Una factura es el artefacto de lectura más frecuente del sistema: los clientes consultan su historial, los administradores generan reportes contables, y el sistema necesita mostrar el detalle completo de una compra en pantalla. En MySQL, responder a cualquiera de esas consultas requiere encadenar varios JOINs: `Factura → Venta → Cliente`, `Venta → Empleado → Sucursal`, `Venta → DetalleVenta → Producto → Categoria`. Ese costo se paga en cada lectura.

El patrón de Referencia Extendida resuelve esto embebiendo en un único documento MongoDB todos los datos que se necesitan para mostrar la factura completa: el snapshot del cliente, del empleado, de la sucursal y cada línea de detalle con su producto. El resultado es una lectura O(1), independiente del número de tablas relacionadas.

Adicionalmente, las facturas son **documentos legales**: deben preservar el estado en el momento de su emisión. Si un cliente cambia de correo, o un producto cambia de precio, la factura debe seguir mostrando los datos originales. El embedding garantiza esa inmutabilidad histórica por diseño, algo que en un modelo normalizado requeriría lógica adicional.

#### Función dentro del proyecto

`FacturaDocumento` vive en la colección `facturas` de MongoDB. Cada vez que se crea o actualiza una factura en MySQL, `FacturaMongoService.proyectar()` construye y persiste el documento embebido de forma asíncrona. Si la proyección falla (por ejemplo, si MongoDB está temporalmente caído), la factura ya está segura en MySQL y el endpoint `POST /factura/mongo/sincronizar` permite recuperar los registros pendientes en cualquier momento.

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

El endpoint que materializa este patrón es:

- `POST /reportes/recalcular/{anio}/{mes}` — dispara el recálculo para todos los reportes del mes indicado (solo `ADMIN`).

Los reportes resultantes pueden consultarse de forma directa para alimentar cualquier dashboard o exportación contable sin impacto en el rendimiento transaccional de MySQL.

---

## Endpoints principales

### Autenticación

| Método | Ruta             | Acceso  | Descripción               |
| ------ | ---------------- | ------- | ------------------------- |
| POST   | `/auth/login`    | Público | Obtiene token JWT         |
| POST   | `/cliente/crear` | Público | Registro de nuevo cliente |

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

| Método | Ruta                                | Rol   | Descripción                  |
| ------ | ----------------------------------- | ----- | ---------------------------- |
| POST   | `/reportes/recalcular/{anio}/{mes}` | ADMIN | Recalcula reportes mensuales |

Los demás recursos (producto, categoría, venta, empleado, cliente, sucursal, proveedor, caja registradora) exponen CRUD completo bajo sus respectivas rutas, protegidos según el rol requerido.

---

## Requisitos previos

- Java 21
- Maven (o usar el wrapper `./mvnw` incluido)
- MySQL 8+ corriendo en `localhost:3306`
- MongoDB 7+ en una de estas opciones:
  - **Docker**: `docker run -d --name cocotech-mongo -p 27017:27017 mongo:7`
  - **MongoDB Atlas** (gratuito): cluster M0 con URI `mongodb+srv://...`
  - **Instalación local** desde [mongodb.com/try/download/community](https://www.mongodb.com/try/download/community)

---

## Configuración y ejecución

1. Editar `src/main/resources/application.properties`:

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/cocotechdb
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/cocotech
```

2. Levantar la aplicación:

```bash
./mvnw spring-boot:run
```

En el primer arranque, `LoadDatabase` puebla MySQL con datos de ejemplo, incluyendo 50 facturas de prueba.

3. Sincronizar las facturas iniciales a MongoDB (una sola vez):

```bash
curl -X POST http://localhost:8080/api/factura/mongo/sincronizar \
  -H "Authorization: Bearer TU_JWT_DE_ADMIN"
```

---

## Documentación de la API

Swagger UI disponible en: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

La documentación OpenAPI completa está en: `http://localhost:8080/v3/api-docs`
