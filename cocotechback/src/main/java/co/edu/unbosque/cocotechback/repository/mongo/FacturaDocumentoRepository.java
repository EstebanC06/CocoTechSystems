/**
 * Paquete que contiene los repositorios MongoDB de la aplicación CocoTech
 * backend.
 */
package co.edu.unbosque.cocotechback.repository.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import co.edu.unbosque.cocotechback.model.mongo.FacturaDocumento;

/**
 * Repositorio MongoDB para {@link FacturaDocumento}.
 * <p>
 * Las consultas aquí no requieren JOINs porque los datos de cliente,
 * empleado, sucursal y detalles están embebidos directamente en cada
 * documento (Patrón de Referencia Extendida).
 * <p>
 * Spring Data MongoDB genera la implementación automáticamente a partir de
 * los nombres de los métodos y las anotaciones {@code @Aggregation}.
 */
public interface FacturaDocumentoRepository
		extends MongoRepository<FacturaDocumento, String> {

	/**
	 * Busca un documento de factura por su ID de MySQL.
	 *
	 * @param idFacturaMySQL ID en la tabla {@code factura} de MySQL.
	 * @return un {@link Optional} con la factura, o vacío si no existe.
	 */
	Optional<FacturaDocumento> findByIdFacturaMySQL(Long idFacturaMySQL);

	/**
	 * Busca un documento de factura por el ID de la venta asociada.
	 *
	 * @param idVenta ID de la venta en MySQL.
	 * @return un {@link Optional} con la factura, o vacío si no existe.
	 */
	Optional<FacturaDocumento> findByIdVenta(Long idVenta);

	/**
	 * Busca todas las facturas de un cliente específico.
	 * <p>
	 * Aprovecha la denormalización: el ID del cliente está embebido en cada
	 * documento, evitando un JOIN.
	 *
	 * @param idCliente ID del cliente en MySQL.
	 * @return lista de facturas del cliente.
	 */
	List<FacturaDocumento> findByCliente_IdCliente(Long idCliente);

	/**
	 * Busca todas las facturas emitidas dentro de un rango de fechas.
	 *
	 * @param inicio fecha y hora de inicio (inclusive).
	 * @param fin    fecha y hora de fin (inclusive).
	 * @return lista de facturas dentro del rango.
	 */
	List<FacturaDocumento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * Busca facturas por método de pago dentro de un rango de fechas.
	 * <p>
	 * Útil para reportes de "más vendido por tarjeta", "más vendido por
	 * efectivo", etc.
	 *
	 * @param metodoPago el método de pago.
	 * @param inicio     fecha y hora de inicio.
	 * @param fin        fecha y hora de fin.
	 * @return lista de facturas que coinciden.
	 */
	List<FacturaDocumento> findByDetalles_MetodoPagoAndFechaBetween(
			String metodoPago, LocalDateTime inicio, LocalDateTime fin);

	/**
	 * Pipeline de agregación que retorna el ingreso bruto agrupado por
	 * sucursal en un periodo dado.
	 * <p>
	 * Aprovecha el aggregation framework de MongoDB para evitar trasladar
	 * los datos al servicio: la agregación ocurre del lado del motor.
	 *
	 * @param inicio fecha y hora de inicio.
	 * @param fin    fecha y hora de fin.
	 * @return lista de documentos donde {@code _id} es un subdocumento con
	 *         {@code idSucursal} y {@code nombre}, más los campos
	 *         {@code ingresoBruto}, {@code impuestos} y
	 *         {@code cantidadFacturas}.
	 */
	@Aggregation(pipeline = {
			"{ $match: { fecha: { $gte: ?0, $lte: ?1 } } }",
			"{ $group: { _id: { idSucursal: '$sucursal.idSucursal', " +
					"                   nombre: '$sucursal.nombre' }, " +
					"            ingresoBruto: { $sum: '$precioTotal' }, " +
					"            impuestos: { $sum: '$precioImpuestos' }, " +
					"            cantidadFacturas: { $sum: 1 } } }",
			"{ $sort: { ingresoBruto: -1 } }"
	})
	List<Document> ingresoBrutoPorSucursal(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * Pipeline de agregación que retorna los productos más vendidos en un
	 * periodo (por cantidad), descendiendo a la lista de detalles embebidos.
	 *
	 * @param inicio fecha y hora de inicio.
	 * @param fin    fecha y hora de fin.
	 * @return lista de documentos con {@code _id} (nombre del producto) y
	 *         {@code unidadesVendidas}.
	 */
	@Aggregation(pipeline = {
			"{ $match: { fecha: { $gte: ?0, $lte: ?1 } } }",
			"{ $unwind: '$detalles' }",
			"{ $group: { _id: '$detalles.nombreProducto', " +
					"            unidadesVendidas: { $sum: '$detalles.cantidad' }, " +
					"            ingresoProducto: { $sum: '$detalles.subtotal' } } }",
			"{ $sort: { unidadesVendidas: -1 } }",
			"{ $limit: 10 }"
	})
	List<Document> topProductosVendidos(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * Pipeline de agregación que retorna el ranking de clientes por
	 * monto gastado en un periodo.
	 *
	 * @param inicio fecha y hora de inicio.
	 * @param fin    fecha y hora de fin.
	 * @return lista de documentos con datos del cliente y total gastado.
	 */
	@Aggregation(pipeline = {
			"{ $match: { fecha: { $gte: ?0, $lte: ?1 } } }",
			"{ $group: { _id: '$cliente.idCliente', " +
					"            nombres: { $first: '$cliente.nombres' }, " +
					"            apellidos: { $first: '$cliente.apellidos' }, " +
					"            totalGastado: { $sum: '$precioTotal' }, " +
					"            cantidadCompras: { $sum: 1 } } }",
			"{ $sort: { totalGastado: -1 } }",
			"{ $limit: 10 }"
	})
	List<Document> topClientes(LocalDateTime inicio, LocalDateTime fin);
}