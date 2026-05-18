/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.cocotechback.model.Venta;

/**
 * Interfaz de repositorio para la entidad {@link Venta}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code venta} en la base de datos MySQL. Además, define
 * métodos de consulta personalizados para buscar ventas por empleado, cliente y
 * rango de fechas.
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
	 * @return Una lista de ventas registradas por el empleado indicado. Retorna una
	 *         lista vacía si no hay coincidencias.
	 */
	public List<Venta> findByEmpleado_Id(Long idEmpleado);

	/**
	 * Busca todas las ventas realizadas por un cliente específico.
	 *
	 * @param idCliente El identificador del cliente por el cual filtrar.
	 * @return Una lista de ventas del cliente indicado. Retorna una lista vacía si
	 *         no hay coincidencias.
	 */
	public List<Venta> findByCliente_Id(Long idCliente);

	/**
	 * Busca todas las ventas realizadas dentro de un rango de fechas.
	 *
	 * @param inicio La fecha y hora de inicio del rango (inclusive).
	 * @param fin    La fecha y hora de fin del rango (inclusive).
	 * @return Una lista de ventas realizadas dentro del rango indicado. Retorna una
	 *         lista vacía si no hay coincidencias.
	 */
	public List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * Busca todas las ventas de un empleado específico dentro de un rango de
	 * fechas.
	 *
	 * @param idEmpleado El identificador del empleado.
	 * @param inicio     La fecha y hora de inicio del rango (inclusive).
	 * @param fin        La fecha y hora de fin del rango (inclusive).
	 * @return Una lista de ventas del empleado dentro del rango de fechas. Retorna
	 *         una lista vacía si no hay coincidencias.
	 */
	public List<Venta> findByEmpleado_IdAndFechaBetween(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin);

	/**
	 * Busca todas las ventas de un cliente específico dentro de un rango de fechas.
	 *
	 * @param idCliente El identificador del cliente.
	 * @param inicio    La fecha y hora de inicio del rango (inclusive).
	 * @param fin       La fecha y hora de fin del rango (inclusive).
	 * @return Una lista de ventas del cliente dentro del rango de fechas. Retorna
	 *         una lista vacía si no hay coincidencias.
	 */
	public List<Venta> findByCliente_IdAndFechaBetween(Long idCliente, LocalDateTime inicio, LocalDateTime fin);

	/**
	 * Consulta JPQL que retorna el empleado con mayor número de ventas registradas
	 * dentro de un mes específico ("Empleado del mes").
	 * <p>
	 * Esta consulta satisface el escenario analítico "Empleado del mes (más ventas
	 * registradas)" especificado en los objetivos del proyecto.
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
	/**
	 * Invoca el stored procedure {@code sp_empleado_del_mes} en MySQL.
	 * <p>
	 * La lógica de agregación se delega al motor de base de datos para aprovechar
	 * su capacidad de ejecutar GROUP BY y ORDER BY sin transferir filas al backend.
	 * Esto sigue la filosofía del proyecto de distribuir la carga entre la BD y la
	 * aplicación.
	 *
	 * @param inicio Fecha y hora de inicio del periodo (inclusivo).
	 * @param fin    Fecha y hora de fin del periodo (inclusivo).
	 * @return Lista de filas {@code [nombres, apellidos, total_ventas]} en orden
	 *         descendente por total de ventas.
	 */
	@Procedure(procedureName = "sp_empleado_del_mes")
	public List<Object[]> findEmpleadoDelMes(@Param("p_inicio") LocalDateTime inicio,
			@Param("p_fin") LocalDateTime fin);

	/**
	 * Invoca el stored procedure {@code sp_cliente_con_mas_compras} en MySQL.
	 *
	 * @return Lista de filas {@code [nombres, apellidos, total_compras]} en orden
	 *         descendente por total de compras.
	 */
	@Procedure(procedureName = "sp_cliente_con_mas_compras")
	public List<Object[]> findClienteConMasCompras();

	/**
	 * Consulta JPQL que calcula el total de ventas por empleado, ordenado de mayor
	 * a menor monto acumulado.
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
	@Query("SELECT v.empleado.nombres, v.empleado.apellidos, SUM(v.total) AS montoTotal " + "FROM Venta v "
			+ "GROUP BY v.empleado.id, v.empleado.nombres, v.empleado.apellidos " + "ORDER BY montoTotal DESC")
	public List<Object[]> findTotalVentasPorEmpleado();
}
