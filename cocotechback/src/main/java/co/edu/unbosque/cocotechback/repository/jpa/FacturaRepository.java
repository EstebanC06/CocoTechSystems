/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.cocotechback.model.Factura;

/**
 * Interfaz de repositorio para la entidad {@link Factura}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code factura} en la base de datos MySQL. Además, define
 * métodos de consulta personalizados para buscar facturas por venta, rango de
 * fechas y rango de valores.
 * <p>
 * Spring Data JPA genera la implementación de los métodos derivados
 * automáticamente en tiempo de ejecución a partir de la convención de nombres.
 */
public interface FacturaRepository extends JpaRepository<Factura, Long> {

	/**
	 * Busca la factura asociada a una venta específica.
	 * <p>
	 * La relación venta-factura es 1:1, por lo tanto este método retorna a lo sumo
	 * una factura por venta.
	 *
	 * @param idVenta El identificador de la venta cuya factura se desea consultar.
	 * @return Un {@link Optional} con la factura de la venta, o vacío si la venta
	 *         aún no tiene factura generada.
	 */
	public Optional<Factura> findByVenta_IdVenta(Long idVenta);

	/**
	 * Busca todas las facturas emitidas dentro de un rango de fechas.
	 *
	 * @param inicio La fecha y hora de inicio del rango (inclusive).
	 * @param fin    La fecha y hora de fin del rango (inclusive).
	 * @return Una lista de facturas emitidas dentro del rango indicado. Retorna una
	 *         lista vacía si no hay coincidencias.
	 */
	public List<Factura> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * Busca todas las facturas cuyo precio total se encuentre dentro del rango
	 * indicado (incluyendo los extremos).
	 *
	 * @param minTotal El valor mínimo del precio total.
	 * @param maxTotal El valor máximo del precio total.
	 * @return Una lista de facturas dentro del rango de precio total. Retorna una
	 *         lista vacía si no hay coincidencias.
	 */
	public List<Factura> findByPrecioTotalBetween(Double minTotal, Double maxTotal);

	/**
	 * Busca todas las facturas de un cliente específico, navegando a través de la
	 * relación Factura → Venta → Cliente.
	 *
	 * @param idCliente El identificador del cliente por el cual filtrar.
	 * @return Una lista de facturas asociadas al cliente indicado. Retorna una
	 *         lista vacía si no hay coincidencias.
	 */
	public List<Factura> findByVenta_Cliente_Id(Long idCliente);

	/**
	 * Verifica si ya existe una factura generada para la venta indicada.
	 *
	 * @param idVenta El identificador de la venta a verificar.
	 * @return {@code true} si ya existe una factura para esa venta, {@code false}
	 *         en caso contrario.
	 */
	public boolean existsByVenta_IdVenta(Long idVenta);

	/**
	 * Consulta JPQL que calcula el total de impuestos recaudados dentro de un rango
	 * de fechas.
	 * <p>
	 * Útil para reportes contables y fiscales del supermercado.
	 *
	 * @param inicio La fecha y hora de inicio del rango.
	 * @param fin    La fecha y hora de fin del rango.
	 * @return El total de impuestos recaudados en el periodo, o {@code null} si no
	 *         hay facturas en el rango.
	 */
	@Query("SELECT SUM(f.precioImpuestos) FROM Factura f WHERE f.fecha BETWEEN :inicio AND :fin")
	public Double findTotalImpuestosRecaudados(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * Consulta JPQL que calcula el ingreso bruto total del supermercado dentro de
	 * un rango de fechas (suma de todos los precios totales de facturas).
	 *
	 * @param inicio La fecha y hora de inicio del rango.
	 * @param fin    La fecha y hora de fin del rango.
	 * @return El ingreso bruto total del periodo, o {@code null} si no hay facturas
	 *         en el rango.
	 */
	/**
	 * Invoca la stored function {@code fn_ingreso_bruto_periodo} en MySQL.
	 * <p>
	 * El cálculo de la suma se ejecuta dentro del motor, evitando traer filas
	 * individuales al backend.
	 *
	 * @param inicio Fecha y hora de inicio del periodo.
	 * @param fin    Fecha y hora de fin del periodo.
	 * @return Ingreso bruto total en el rango, o 0.00 si no hay facturas.
	 */
	@Query(value = "SELECT cocotech.fn_ingreso_bruto_periodo(:inicio, :fin)", nativeQuery = true)
	public Double findIngresoBrutoPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
