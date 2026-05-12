/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.edu.unbosque.cocotechback.model.DetalleVenta;

/**
 * Interfaz de repositorio para la entidad {@link DetalleVenta}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code detalle_venta} en la base de datos MySQL. Además,
 * define métodos de consulta personalizados para buscar detalles por venta,
 * producto y método de pago.
 * <p>
 * Incluye consultas JPQL para análisis de ventas por método de pago y
 * detalles con promociones activas.
 * <p>
 * Spring Data JPA genera la implementación de los métodos derivados
 * automáticamente en tiempo de ejecución a partir de la convención de nombres.
 */
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

	/**
	 * Busca todos los detalles de venta pertenecientes a una venta específica.
	 *
	 * @param idVenta El identificador de la venta por la cual filtrar.
	 * @return Una lista de detalles de venta de la venta indicada. Retorna una
	 *         lista vacía si no hay coincidencias.
	 */
	public List<DetalleVenta> findByVenta_IdVenta(Long idVenta);

	/**
	 * Busca todos los detalles de venta que incluyan un producto específico.
	 *
	 * @param idProducto El identificador del producto por el cual filtrar.
	 * @return Una lista de detalles de venta que incluyen el producto indicado.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<DetalleVenta> findByProducto_IdProducto(Long idProducto);

	/**
	 * Busca todos los detalles de venta que hayan utilizado un método de pago
	 * específico.
	 *
	 * @param metodoPago El método de pago por el cual filtrar
	 *                   (ej. "Efectivo", "Tarjeta débito", "Tarjeta crédito").
	 * @return Una lista de detalles de venta con el método de pago indicado.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<DetalleVenta> findByMetodoPago(String metodoPago);

	/**
	 * Busca todos los detalles de venta que tengan o no una promoción activa.
	 *
	 * @param promocion {@code true} para buscar detalles con promoción,
	 *                  {@code false} para buscar detalles sin promoción.
	 * @return Una lista de detalles de venta según el criterio de promoción.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<DetalleVenta> findByPromocion(Boolean promocion);

	/**
	 * Busca todos los detalles de venta de una venta específica que tengan un
	 * método de pago determinado.
	 *
	 * @param idVenta    El identificador de la venta.
	 * @param metodoPago El método de pago a filtrar.
	 * @return Una lista de detalles de la venta indicada con el método de pago
	 *         especificado. Retorna una lista vacía si no hay coincidencias.
	 */
	public List<DetalleVenta> findByVenta_IdVentaAndMetodoPago(Long idVenta, String metodoPago);

	/**
	 * Consulta JPQL que calcula el total de unidades vendidas y el ingreso
	 * generado por cada método de pago.
	 * <p>
	 * Útil para análisis financiero de preferencias de pago de los clientes.
	 *
	 * @return Una lista de arreglos de objetos donde cada arreglo contiene:
	 *         <ul>
	 *         <li>[0] - {@link String} método de pago</li>
	 *         <li>[1] - {@link Long} total de transacciones</li>
	 *         <li>[2] - {@link Double} ingreso total generado</li>
	 *         </ul>
	 *         Ordenados de mayor a menor ingreso.
	 */
	@Query("SELECT d.metodoPago, COUNT(d), SUM(d.subtotal) AS ingresoTotal "
			+ "FROM DetalleVenta d "
			+ "GROUP BY d.metodoPago "
			+ "ORDER BY ingresoTotal DESC")
	public List<Object[]> findResumenPorMetodoPago();

	/**
	 * Consulta JPQL que retorna todos los detalles de venta con promoción
	 * activa, junto con el ahorro generado por descuento.
	 * <p>
	 * Útil para medir el impacto de las promociones en las ventas.
	 *
	 * @return Una lista de arreglos de objetos donde cada arreglo contiene:
	 *         <ul>
	 *         <li>[0] - {@link String} nombre del producto</li>
	 *         <li>[1] - {@link Double} precio original</li>
	 *         <li>[2] - {@link Double} precio con descuento</li>
	 *         <li>[3] - {@link Double} porcentaje de descuento</li>
	 *         </ul>
	 */
	@Query("SELECT d.producto.nombre, d.precioOriginal, d.precioNuevo, d.porcentajeDescuento "
			+ "FROM DetalleVenta d "
			+ "WHERE d.promocion = true "
			+ "ORDER BY d.porcentajeDescuento DESC")
	public List<Object[]> findDetallesConPromocion();
}
