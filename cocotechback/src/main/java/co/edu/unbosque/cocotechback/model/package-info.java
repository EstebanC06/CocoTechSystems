/**
 * Paquete que contiene las clases de Entidad (modelo de dominio) utilizadas
 * en la aplicación CocoTech backend.
 * <p>
 * Las entidades de este paquete representan las tablas de la base de datos
 * MySQL y están anotadas con Jakarta Persistence (JPA) para su mapeo
 * objeto-relacional mediante Hibernate.
 * <p>
 * Jerarquía de herencia para usuarios del sistema:
 * <ul>
 * <li>{@link co.edu.unbosque.cocotechback.model.Usuario} - Superclase abstracta
 * con atributos comunes y lógica de autenticación Spring Security.</li>
 * <li>{@link co.edu.unbosque.cocotechback.model.Cliente} - Rol ROLE_CLIENTE,
 * permisos limitados de consulta.</li>
 * <li>{@link co.edu.unbosque.cocotechback.model.Empleado} - Rol ROLE_ADMIN,
 * acceso total al sistema de gestión.</li>
 * </ul>
 * <p>
 * Demás entidades del dominio del supermercado:
 * {@link co.edu.unbosque.cocotechback.model.Sucursal},
 * {@link co.edu.unbosque.cocotechback.model.CajaRegistradora},
 * {@link co.edu.unbosque.cocotechback.model.Categoria},
 * {@link co.edu.unbosque.cocotechback.model.Proveedor},
 * {@link co.edu.unbosque.cocotechback.model.Producto},
 * {@link co.edu.unbosque.cocotechback.model.Venta},
 * {@link co.edu.unbosque.cocotechback.model.DetalleVenta},
 * {@link co.edu.unbosque.cocotechback.model.Factura}.
 */
package co.edu.unbosque.cocotechback.model;
