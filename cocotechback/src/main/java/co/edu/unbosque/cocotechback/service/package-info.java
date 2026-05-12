/**
 * Paquete que contiene las clases de Servicio utilizadas
 * en la aplicación CocoTech backend.
 * <p>
 * Todos los servicios de este paquete implementan la interfaz genérica
 * {@link co.edu.unbosque.cocotechback.service.CRUDOperation}, que define el
 * contrato común de operaciones CRUD para las entidades del sistema.
 * <p>
 * Convención de códigos de retorno en los métodos que retornan {@code int}:
 * <ul>
 * <li>{@code 0} - Operación exitosa.</li>
 * <li>{@code 1} - Conflicto de datos (registro duplicado).</li>
 * <li>{@code 2} - Entidad no encontrada.</li>
 * <li>{@code 3} - Error genérico.</li>
 * <li>{@code 4} - Validación fallida (campo requerido ausente o formato
 * inválido).</li>
 * <li>{@code -1} - Operación no aplicable para esta entidad.</li>
 * </ul>
 * <p>
 * Servicios de usuario (con encriptación AES y BCrypt):
 * <ul>
 * <li>{@link co.edu.unbosque.cocotechback.service.ClienteService}</li>
 * <li>{@link co.edu.unbosque.cocotechback.service.EmpleadoService}</li>
 * </ul>
 * <p>
 * Servicios de dominio del supermercado:
 * <ul>
 * <li>{@link co.edu.unbosque.cocotechback.service.SucursalService}</li>
 * <li>{@link co.edu.unbosque.cocotechback.service.CajaRegistradoraService}</li>
 * <li>{@link co.edu.unbosque.cocotechback.service.CategoriaService}</li>
 * <li>{@link co.edu.unbosque.cocotechback.service.ProveedorService}</li>
 * <li>{@link co.edu.unbosque.cocotechback.service.ProductoService}</li>
 * <li>{@link co.edu.unbosque.cocotechback.service.VentaService}</li>
 * <li>{@link co.edu.unbosque.cocotechback.service.DetalleVentaService}</li>
 * <li>{@link co.edu.unbosque.cocotechback.service.FacturaService}</li>
 * </ul>
 */
package co.edu.unbosque.cocotechback.service;
