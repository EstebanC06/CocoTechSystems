/**
 * Paquete que contiene las clases de configuración de la aplicación CocoTech
 * backend.
 * <p>
 * Las clases de este paquete se ejecutan al arranque de la aplicación y
 * definen beans reutilizables en el contexto de Spring:
 * <p>
 * <strong>{@link co.edu.unbosque.cocotechback.configuration.LoadDatabase}</strong>
 * — Inicializa la base de datos MySQL con datos de prueba al arrancar la
 * aplicación, cumpliendo el objetivo del proyecto que exige un mínimo de 50
 * registros por tabla principal. Utiliza un bean {@code CommandLineRunner} que
 * verifica la existencia de los datos antes de crearlos, garantizando
 * idempotencia. El orden de creación respeta las dependencias entre entidades
 * (claves foráneas):
 * <ol>
 * <li>Sucursales (5)</li>
 * <li>Categorías (10)</li>
 * <li>Proveedores (10)</li>
 * <li>Empleados (50 — 10 por sucursal)</li>
 * <li>Cajas Registradoras (10 — una por cajero)</li>
 * <li>Clientes (50)</li>
 * <li>Productos (50 — 5 por categoría)</li>
 * <li>Ventas (50)</li>
 * <li>Detalles de Venta (50 — uno por venta, con descuento cada 7)</li>
 * <li>Facturas (50 — una por venta, con IVA del 19%)</li>
 * </ol>
 * <p>
 * <strong>{@link co.edu.unbosque.cocotechback.configuration.OpenApiConfig}</strong>
 * — Configura el bean {@link io.swagger.v3.oas.models.OpenAPI} con la
 * documentación interactiva de la API usando Springdoc OpenAPI 3. Define:
 * <ul>
 * <li>Información general del sistema (título, versión, contacto, licencia).</li>
 * <li>Guía de uso para principiantes con el flujo de autenticación JWT.</li>
 * <li>Esquema de seguridad {@code bearerAuth} para probar endpoints desde
 * Swagger UI ({@code /swagger-ui/index.html}).</li>
 * <li>Respuestas globales reutilizables: {@code UnauthorizedError},
 * {@code ForbiddenError} y {@code NotFoundError}.</li>
 * </ul>
 */
package co.edu.unbosque.cocotechback.configuration;
