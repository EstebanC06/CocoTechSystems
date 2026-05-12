/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 * <p>
 * Todas las interfaces de este paquete extienden
 * {@link org.springframework.data.jpa.repository.JpaRepository}, lo que les
 * proporciona automáticamente las operaciones CRUD estándar (save, findById,
 * findAll, deleteById, etc.) sobre MySQL mediante Spring Data JPA e Hibernate.
 * <p>
 * Además de los métodos derivados de convención de nombres (query methods),
 * algunos repositorios incluyen consultas JPQL personalizadas con
 * {@link org.springframework.data.jpa.repository.Query} para satisfacer los
 * escenarios analíticos requeridos en los objetivos del proyecto:
 * <ul>
 * <li>Producto más vendido por categoría ({@code ProductoRepository}).</li>
 * <li>Empleado del mes por número de ventas ({@code VentaRepository}).</li>
 * <li>Cliente con más compras realizadas ({@code VentaRepository}).</li>
 * <li>Resumen de ventas por método de pago ({@code DetalleVentaRepository}).</li>
 * <li>Ingreso bruto y total de impuestos por periodo ({@code FacturaRepository}).</li>
 * </ul>
 * <p>
 * Repositorios disponibles:
 * <ul>
 * <li>{@link co.edu.unbosque.cocotechback.repository.ClienteRepository}</li>
 * <li>{@link co.edu.unbosque.cocotechback.repository.EmpleadoRepository}</li>
 * <li>{@link co.edu.unbosque.cocotechback.repository.SucursalRepository}</li>
 * <li>{@link co.edu.unbosque.cocotechback.repository.CajaRegistradoraRepository}</li>
 * <li>{@link co.edu.unbosque.cocotechback.repository.CategoriaRepository}</li>
 * <li>{@link co.edu.unbosque.cocotechback.repository.ProveedorRepository}</li>
 * <li>{@link co.edu.unbosque.cocotechback.repository.ProductoRepository}</li>
 * <li>{@link co.edu.unbosque.cocotechback.repository.VentaRepository}</li>
 * <li>{@link co.edu.unbosque.cocotechback.repository.DetalleVentaRepository}</li>
 * <li>{@link co.edu.unbosque.cocotechback.repository.FacturaRepository}</li>
 * </ul>
 */
package co.edu.unbosque.cocotechback.repository;
