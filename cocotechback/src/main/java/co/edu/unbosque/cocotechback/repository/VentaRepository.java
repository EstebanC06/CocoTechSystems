/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.edu.unbosque.cocotechback.model.Venta;

/**
 * Interfaz de repositorio para la entidad {@link Venta}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code venta} en la base de datos MySQL. Además, define
 * métodos de consulta personalizados para buscar ventas por empleado, cliente
 * y rango de fechas.
 * <p>
 * Incluye consultas JPQL personalizadas con {@code @Query} para los escenarios
 * analíticos de "empleado del mes" y "cliente con más compras", requeridos en
 * los objetivos del proyecto.
 * <p>
 * Spring Data JPA genera la implementación de los métodos derivados
 * automáticamente en tiempo de ejecución a partir de la convención de nombres.
 */
public interface VentaRepository extends JpaRepository<Venta, Long> {

	/**
	 * Busca todas las ventas registradas por un empleado específico.
	 *
	 * @param idEmpleado El identificador del empleado por el cual filtrar.
	 * @return Una lista de ventas registradas por el empleado indicado. Retorna
	 *         una lista vacía si no hay coincidencias.
	 */
	public List<Venta> findByEmpleado_Id(Long idEmpleado);

	/**
	 * Busca todas las ventas realizadas por un cliente específico.
	 *
	 * @param idCliente El identificador del cliente por el cual filtrar.
	 * @return Una lista de ventas del cliente indicado. Retorna una lista vacía
	 *         si no hay coincidencias.
	 */
	public List<Venta> findByCliente_Id(Long idCliente);

	/**
	 * Busca todas las ventas realizadas dentro de un rango de fechas.
	 *
	 * @param inicio La fecha y hora de inicio del rango (inclusive).
	 * @param fin    La fecha y hora de fin del rango (inclusive).
	 * @return Una lista de ventas realizadas dentro del rango indicado. Retorna
	 *         una lista vacía si no hay coincidencias.
	 */
	public List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * Busca todas las ventas de un empleado específico dentro de un rango de
	 * fechas.
	 *
	 * @param idEmpleado El identificador del empleado.
	 * @param inicio     La fecha y hora de inicio del rango (inclusive).
	 * @param fin        La fecha y hora de fin del rango (inclusive).
	 * @return Una lista de ventas del empleado dentro del rango de fechas.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Venta> findByEmpleado_IdAndFechaBetween(Long idEmpleado, LocalDateTime inicio,
			LocalDateTime fin);

	/**
	 * Busca todas las ventas de un cliente específico dentro de un rango de
	 * fechas.
	 *
	 * @param idCliente El identificador del cliente.
	 * @param inicio    La fecha y hora de inicio del rango (inclusive).
	 * @param fin       La fecha y hora de fin del rango (inclusive).
	 * @return Una lista de ventas del cliente dentro del rango de fechas.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Venta> findByCliente_IdAndFechaBetween(Long idCliente, LocalDateTime inicio,
			LocalDateTime fin);

	/**
	 * Consulta JPQL que retorna el empleado con mayor número de ventas
	 * registradas dentro de un mes específico ("Empleado del mes").
	 * <p>
	 * Esta consulta satisface el escenario analítico "Empleado del mes (más
	 * ventas registradas)" especificado en los objetivos del proyecto.
	 *
	 * @param inicio La fecha y hora de inicio del mes.
	 * @param fin    La fecha y hora de fin del mes.
	 * @return Una lista de arreglos de objetos donde cada arreglo contiene:
	 *         <ul>
	 *         <li>[0] - {@link String} nombres del empleado</li>
	 *         <li>[1] - {@link String} apellidos del empleado</li>
	 *         <li>[2] - {@link Long} total de ventas registradas</li>
	 *         </ul>
	 *         Ordenados de mayor a menor número de ventas.
	 */
	@Query("SELECT v.empleado.nombres, v.empleado.apellidos, COUNT(v) AS totalVentas "
			+ "FROM Venta v "
			+ "WHERE v.fecha BETWEEN :inicio AND :fin "
			+ "GROUP BY v.empleado.id, v.empleado.nombres, v.empleado.apellidos "
			+ "ORDER BY totalVentas DESC")
	public List<Object[]> findEmpleadoDelMes(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * Consulta JPQL que retorna el cliente con más compras realizadas en el
	 * sistema (por número de ventas).
	 * <p>
	 * Esta consulta satisface el escenario analítico "Cliente con más compras
	 * realizadas" especificado en los objetivos del proyecto.
	 *
	 * @return Una lista de arreglos de objetos donde cada arreglo contiene:
	 *         <ul>
	 *         <li>[0] - {@link String} nombres del cliente</li>
	 *         <li>[1] - {@link String} apellidos del cliente</li>
	 *         <li>[2] - {@link Long} total de compras realizadas</li>
	 *         </ul>
	 *         Ordenados de mayor a menor número de compras.
	 */
	@Query("SELECT v.cliente.nombres, v.cliente.apellidos, COUNT(v) AS totalCompras "
			+ "FROM Venta v "
			+ "GROUP BY v.cliente.id, v.cliente.nombres, v.cliente.apellidos "
			+ "ORDER BY totalCompras DESC")
	public List<Object[]> findClienteConMasCompras();

	/**
	 * Consulta JPQL que calcula el total de ventas por empleado, ordenado de
	 * mayor a menor monto acumulado.
	 * <p>
	 * Útil para generar reportes de rendimiento financiero por empleado.
	 *
	 * @return Una lista de arreglos de objetos donde cada arreglo contiene:
	 *         <ul>
	 *         <li>[0] - {@link String} nombres del empleado</li>
	 *         <li>[1] - {@link String} apellidos del empleado</li>
	 *         <li>[2] - {@link Double} suma total de ventas en pesos</li>
	 *         </ul>
	 */
	@Query("SELECT v.empleado.nombres, v.empleado.apellidos, SUM(v.total) AS montoTotal "
			+ "FROM Venta v "
			+ "GROUP BY v.empleado.id, v.empleado.nombres, v.empleado.apellidos "
			+ "ORDER BY montoTotal DESC")
	public List<Object[]> findTotalVentasPorEmpleado();
}
