/**
 * Paquete que contiene los controladores REST de la aplicación CocoTech backend.
 * <p>
 * Todos los controladores de este paquete son clases anotadas con
 * {@link org.springframework.web.bind.annotation.RestController} que exponen
 * la API REST del sistema de gestión del supermercado CocoTech. Utilizan
 * {@link org.springframework.security.access.prepost.PreAuthorize} para
 * aplicar el control de acceso basado en roles:
 * <ul>
 * <li>{@code ROLE_ADMIN} - Empleados: acceso total a todas las operaciones.</li>
 * <li>{@code ROLE_CLIENTE} - Clientes: acceso restringido a consultas de
 * productos, categorías y su propio historial de compras y facturas.</li>
 * </ul>
 * <p>
 * Convención de códigos HTTP de respuesta:
 * <ul>
 * <li>{@code 201 Created} - Recurso creado exitosamente.</li>
 * <li>{@code 202 Accepted} - Operación exitosa con datos retornados.</li>
 * <li>{@code 204 No Content} - Operación exitosa sin datos (lista vacía).</li>
 * <li>{@code 400 Bad Request} - Datos inválidos o error general.</li>
 * <li>{@code 404 Not Found} - Recurso no encontrado.</li>
 * <li>{@code 409 Conflict} - Conflicto de datos (duplicado o restricción).</li>
 * </ul>
 * <p>
 * Controladores disponibles:
 * <ul>
 * <li>{@link co.edu.unbosque.cocotechback.controller.ClienteController} -
 * {@code /cliente}</li>
 * <li>{@link co.edu.unbosque.cocotechback.controller.EmpleadoController} -
 * {@code /empleado}</li>
 * <li>{@link co.edu.unbosque.cocotechback.controller.SucursalController} -
 * {@code /sucursal}</li>
 * <li>{@link co.edu.unbosque.cocotechback.controller.CajaRegistradoraController} -
 * {@code /caja}</li>
 * <li>{@link co.edu.unbosque.cocotechback.controller.CategoriaController} -
 * {@code /categoria}</li>
 * <li>{@link co.edu.unbosque.cocotechback.controller.ProveedorController} -
 * {@code /proveedor}</li>
 * <li>{@link co.edu.unbosque.cocotechback.controller.ProductoController} -
 * {@code /producto}</li>
 * <li>{@link co.edu.unbosque.cocotechback.controller.VentaController} -
 * {@code /venta}</li>
 * <li>{@link co.edu.unbosque.cocotechback.controller.DetalleVentaController} -
 * {@code /detalleVenta}</li>
 * <li>{@link co.edu.unbosque.cocotechback.controller.FacturaController} -
 * {@code /factura}</li>
 * </ul>
 */
package co.edu.unbosque.cocotechback.controller;
