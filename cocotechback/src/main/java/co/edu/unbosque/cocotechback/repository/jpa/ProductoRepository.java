/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.cocotechback.model.Producto;

/**
 * Interfaz de repositorio para la entidad {@link Producto}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code producto} en la base de datos MySQL. Además, define
 * métodos de consulta personalizados para buscar, filtrar y analizar productos
 * por nombre, categoría, proveedor, stock y fecha de vencimiento.
 * <p>
 * Incluye consultas JPQL personalizadas con {@code @Query} para escenarios
 * analíticos como el reporte de producto más vendido por categoría, requerido
 * en los objetivos del proyecto.
 * <p>
 * Spring Data JPA genera la implementación de los métodos derivados
 * automáticamente en tiempo de ejecución a partir de la convención de nombres.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {

	/**
	 * Busca un producto por su nombre exacto.
	 *
	 * @param nombre El nombre del producto a buscar.
	 * @return Un {@link Optional} con el producto encontrado, o vacío si no
	 *         existe ningún producto con ese nombre.
	 */
	public Optional<Producto> findByNombre(String nombre);

	/**
	 * Busca todos los productos cuyo nombre contenga la cadena proporcionada,
	 * sin distinguir entre mayúsculas y minúsculas.
	 *
	 * @param nombre La cadena de búsqueda parcial del nombre del producto.
	 * @return Una lista de productos cuyo nombre contenga la cadena indicada.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Producto> findByNombreContainingIgnoreCase(String nombre);

	/**
	 * Busca todos los productos pertenecientes a una categoría específica.
	 *
	 * @param idCategoria El identificador de la categoría por la cual filtrar.
	 * @return Una lista de productos de la categoría indicada. Retorna una lista
	 *         vacía si no hay coincidencias.
	 */
	public List<Producto> findByCategoria_IdCategoria(Long idCategoria);

	/**
	 * Busca todos los productos suministrados por un proveedor específico.
	 *
	 * @param idProveedor El identificador del proveedor por el cual filtrar.
	 * @return Una lista de productos del proveedor indicado. Retorna una lista
	 *         vacía si no hay coincidencias.
	 */
	public List<Producto> findByProveedor_IdProveedor(Long idProveedor);

	/**
	 * Busca todos los productos cuyo stock sea menor o igual al umbral indicado.
	 * <p>
	 * Útil para generar alertas de reabastecimiento cuando el inventario de
	 * un producto está por agotarse.
	 *
	 * @param stock El umbral máximo de stock para la búsqueda.
	 * @return Una lista de productos con stock igual o inferior al umbral.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Producto> findByStockLessThanEqual(Integer stock);

	/**
	 * Busca todos los productos cuya fecha de vencimiento sea anterior o igual
	 * a la fecha indicada.
	 * <p>
	 * Útil para identificar productos próximos a vencer o ya vencidos.
	 *
	 * @param fecha La fecha de referencia para la búsqueda.
	 * @return Una lista de productos vencidos o por vencer hasta la fecha dada.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Producto> findByFechaVencimientoLessThanEqual(LocalDate fecha);

	/**
	 * Busca todos los productos cuyo precio se encuentre dentro del rango
	 * indicado (incluyendo los extremos).
	 *
	 * @param precioMin El precio mínimo del rango.
	 * @param precioMax El precio máximo del rango.
	 * @return Una lista de productos cuyo precio esté dentro del rango.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Producto> findByPrecioBetween(Double precioMin, Double precioMax);

	/**
	 * Verifica si ya existe un producto registrado con el nombre proporcionado.
	 *
	 * @param nombre El nombre a verificar.
	 * @return {@code true} si ya existe un producto con ese nombre,
	 *         {@code false} en caso contrario.
	 */
	public boolean existsByNombre(String nombre);

	/**
	 * Consulta JPQL que retorna el producto más vendido dentro de cada categoría,
	 * basándose en la suma de cantidades vendidas en los detalles de venta.
	 * <p>
	 * Esta consulta satisface el escenario analítico "Producto más vendido de
	 * cada categoría" especificado en los objetivos del proyecto.
	 *
	 * @return Una lista de arreglos de objetos donde cada arreglo contiene:
	 *         <ul>
	 *         <li>[0] - {@link co.edu.unbosque.cocotechback.model.Categoria} nombre</li>
	 *         <li>[1] - {@link Producto} nombre</li>
	 *         <li>[2] - {@link Long} total de unidades vendidas</li>
	 *         </ul>
	 */
	@Query("SELECT p.categoria.nombre, p.nombre, SUM(d.cantidadProductos) AS totalVendido "
			+ "FROM DetalleVenta d JOIN d.producto p "
			+ "GROUP BY p.categoria.idCategoria, p.idProducto, p.categoria.nombre, p.nombre "
			+ "HAVING SUM(d.cantidadProductos) = ("
			+ "  SELECT MAX(sub.total) FROM ("
			+ "    SELECT SUM(d2.cantidadProductos) AS total "
			+ "    FROM DetalleVenta d2 JOIN d2.producto p2 "
			+ "    WHERE p2.categoria.idCategoria = p.categoria.idCategoria "
			+ "    GROUP BY p2.idProducto"
			+ "  ) sub"
			+ ") "
			+ "ORDER BY p.categoria.nombre ASC")
	public List<Object[]> findProductoMasVendidoPorCategoria();

	/**
	 * Consulta JPQL que retorna todos los productos con su cantidad total
	 * vendida, ordenados de mayor a menor.
	 * <p>
	 * Útil para generar reportes de rendimiento de productos.
	 *
	 * @return Una lista de arreglos de objetos donde cada arreglo contiene:
	 *         <ul>
	 *         <li>[0] - {@link Producto} nombre del producto</li>
	 *         <li>[1] - {@link Long} total de unidades vendidas</li>
	 *         </ul>
	 */
	@Query("SELECT p.nombre, SUM(d.cantidadProductos) AS totalVendido "
			+ "FROM DetalleVenta d JOIN d.producto p "
			+ "GROUP BY p.idProducto, p.nombre "
			+ "ORDER BY totalVendido DESC")
	public List<Object[]> findProductosOrdenadosPorVentas();

	/**
	 * Busca todos los productos de una categoría específica cuyo stock sea
	 * menor o igual al umbral indicado.
	 * <p>
	 * Útil para generar alertas de reabastecimiento segmentadas por categoría.
	 *
	 * @param idCategoria El identificador de la categoría.
	 * @param stock       El umbral máximo de stock.
	 * @return Una lista de productos de la categoría con stock bajo.
	 */
	public List<Producto> findByCategoria_IdCategoriaAndStockLessThanEqual(Long idCategoria,
			Integer stock);
}
