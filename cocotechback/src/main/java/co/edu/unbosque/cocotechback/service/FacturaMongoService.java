/**
 * Paquete que contiene las clases de Servicio utilizadas en la aplicación
 * CocoTech backend.
 */
package co.edu.unbosque.cocotechback.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.cocotechback.model.Cliente;
import co.edu.unbosque.cocotechback.model.DetalleVenta;
import co.edu.unbosque.cocotechback.model.Empleado;
import co.edu.unbosque.cocotechback.model.Factura;
import co.edu.unbosque.cocotechback.model.Venta;
import co.edu.unbosque.cocotechback.model.mongo.FacturaDocumento;
import co.edu.unbosque.cocotechback.model.mongo.FacturaDocumento.ClienteEmbebido;
import co.edu.unbosque.cocotechback.model.mongo.FacturaDocumento.DetalleEmbebido;
import co.edu.unbosque.cocotechback.model.mongo.FacturaDocumento.EmpleadoEmbebido;
import co.edu.unbosque.cocotechback.model.mongo.FacturaDocumento.SucursalEmbebida;
import co.edu.unbosque.cocotechback.repository.jpa.DetalleVentaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.FacturaRepository;
import co.edu.unbosque.cocotechback.repository.mongo.FacturaDocumentoRepository;

/**
 * Servicio que gestiona la proyección y consulta de facturas en MongoDB.
 * <p>
 * Implementa el Patrón de Referencia Extendida: cada vez que se emite una
 * factura en MySQL, este servicio crea (o regenera) un documento en
 * MongoDB con los datos embebidos del cliente, empleado, sucursal y
 * detalles de venta al momento de la emisión.
 * <p>
 * Las consultas que aquí se exponen reemplazan operaciones que en MySQL
 * requieren múltiples JOINs por una sola lectura del documento.
 */
@Service
public class FacturaMongoService {

	/**
	 * Logger para registrar el flujo de proyección y errores no críticos.
	 */
	private static final Logger log = LoggerFactory.getLogger(FacturaMongoService.class);

	/**
	 * Repositorio MongoDB de facturas embebidas.
	 */
	@Autowired
	private FacturaDocumentoRepository facturaDocRepo;

	/**
	 * Repositorio JPA de facturas (para sincronización inicial).
	 */
	@Autowired
	private FacturaRepository facturaRepo;

	/**
	 * Repositorio JPA de detalles de venta (para construir las líneas
	 * embebidas en el documento).
	 */
	@Autowired
	private DetalleVentaRepository detalleVentaRepo;

	/**
	 * Constructor por defecto.
	 */
	public FacturaMongoService() {
	}

	/**
	 * Proyecta una factura JPA a un documento MongoDB siguiendo el Patrón
	 * de Referencia Extendida.
	 * <p>
	 * Si ya existe un documento para esa factura (por
	 * {@code idFacturaMySQL}), lo reemplaza para mantener consistencia. Si
	 * falla la proyección, se registra un warning pero no se propaga la
	 * excepción: la factura ya existe en MySQL (fuente de verdad) y se
	 * podrá reintentar la proyección mediante el método
	 * {@link #sincronizarDesdeMySQL()}.
	 *
	 * @param factura la entidad JPA recién persistida.
	 * @return el documento generado, o {@code null} si la proyección falló.
	 */
	public FacturaDocumento proyectar(Factura factura) {
		if (factura == null || factura.getVenta() == null) {
			log.warn("No se puede proyectar una factura sin venta asociada.");
			return null;
		}
		try {
			Venta venta = factura.getVenta();
			Cliente cliente = venta.getCliente();
			Empleado empleado = venta.getEmpleado();

			FacturaDocumento doc = facturaDocRepo
					.findByIdFacturaMySQL(factura.getIdFactura())
					.orElseGet(FacturaDocumento::new);

			doc.setIdFacturaMySQL(factura.getIdFactura());
			doc.setIdVenta(venta.getIdVenta());
			doc.setFecha(factura.getFecha());
			doc.setPrecioTotal(factura.getPrecioTotal());
			doc.setPrecioImpuestos(factura.getPrecioImpuestos());

			doc.setCliente(buildClienteEmbebido(cliente));
			doc.setEmpleado(buildEmpleadoEmbebido(empleado));
			doc.setSucursal(buildSucursalEmbebida(empleado));
			doc.setDetalles(buildDetallesEmbebidos(venta));

			return facturaDocRepo.save(doc);
		} catch (RuntimeException ex) {
		    log.warn("Fallo al proyectar factura {} a MongoDB",
		            factura.getIdFactura(), ex);
		    return null;
		}
	}

	/**
	 * Construye el snapshot embebido del cliente.
	 *
	 * @param cliente el cliente de la venta.
	 * @return el subdocumento {@link ClienteEmbebido}, o {@code null} si
	 *         el cliente es nulo.
	 */
	private ClienteEmbebido buildClienteEmbebido(Cliente cliente) {
		if (cliente == null) {
			return null;
		}
		ClienteEmbebido ce = new ClienteEmbebido();
		ce.setIdCliente(cliente.getId());
		ce.setNombres(cliente.getNombres());
		ce.setApellidos(cliente.getApellidos());
		ce.setCorreo(cliente.getCorreo());
		ce.setCiudad(cliente.getCiudad());
		return ce;
	}

	/**
	 * Construye el snapshot embebido del empleado.
	 *
	 * @param empleado el empleado de la venta.
	 * @return el subdocumento {@link EmpleadoEmbebido}, o {@code null} si
	 *         el empleado es nulo.
	 */
	private EmpleadoEmbebido buildEmpleadoEmbebido(Empleado empleado) {
		if (empleado == null) {
			return null;
		}
		EmpleadoEmbebido ee = new EmpleadoEmbebido();
		ee.setIdEmpleado(empleado.getId());
		ee.setNombres(empleado.getNombres());
		ee.setApellidos(empleado.getApellidos());
		ee.setCargo(empleado.getCargo());
		return ee;
	}

	/**
	 * Construye el snapshot embebido de la sucursal del empleado.
	 *
	 * @param empleado el empleado de la venta.
	 * @return el subdocumento {@link SucursalEmbebida}, o {@code null} si
	 *         el empleado o su sucursal son nulos.
	 */
	private SucursalEmbebida buildSucursalEmbebida(Empleado empleado) {
		if (empleado == null || empleado.getSucursal() == null) {
			return null;
		}
		SucursalEmbebida se = new SucursalEmbebida();
		se.setIdSucursal(empleado.getSucursal().getIdSucursal());
		se.setNombre(empleado.getSucursal().getNombre());
		se.setCiudad(empleado.getSucursal().getCiudad());
		return se;
	}

	/**
	 * Construye la lista de detalles embebidos a partir de los detalles
	 * de venta en MySQL.
	 *
	 * @param venta la venta asociada a la factura.
	 * @return lista de subdocumentos {@link DetalleEmbebido}.
	 */
	private List<DetalleEmbebido> buildDetallesEmbebidos(Venta venta) {
		List<DetalleVenta> detalles =
				detalleVentaRepo.findByVenta_IdVenta(venta.getIdVenta());
		List<DetalleEmbebido> result = new ArrayList<>();
		for (DetalleVenta dv : detalles) {
			DetalleEmbebido d = new DetalleEmbebido();
			if (dv.getProducto() != null) {
				d.setIdProducto(dv.getProducto().getIdProducto());
				d.setNombreProducto(dv.getProducto().getNombre());
				if (dv.getProducto().getCategoria() != null) {
					d.setCategoria(dv.getProducto().getCategoria().getNombre());
				}
			}
			d.setCantidad(dv.getCantidadProductos());
			d.setPrecioUnitario(dv.getPrecioUnitario());
			d.setSubtotal(dv.getSubtotal());
			d.setMetodoPago(dv.getMetodoPago());
			d.setPromocion(dv.getPromocion());
			d.setPorcentajeDescuento(dv.getPorcentajeDescuento());
			result.add(d);
		}
		return result;
	}

	/**
	 * Sincroniza todas las facturas existentes en MySQL hacia MongoDB.
	 * <p>
	 * Útil como job inicial al introducir MongoDB en un sistema que ya
	 * tenía facturas en MySQL, o como tarea de recuperación si la
	 * proyección en línea falló para algunos registros.
	 *
	 * @return cantidad de facturas proyectadas exitosamente.
	 */
	@Transactional
	public int sincronizarDesdeMySQL() {
		List<Factura> todas = facturaRepo.findAll();
		int exitosas = 0;
		for (Factura f : todas) {
			if (proyectar(f) != null) {
				exitosas++;
			}
		}
		log.info("Sincronización MySQL → MongoDB completada: {}/{} facturas",
				exitosas, todas.size());
		return exitosas;
	}

	/**
	 * Obtiene una factura por su ID de MySQL (puente de trazabilidad).
	 *
	 * @param idFacturaMySQL ID de la tabla {@code factura} en MySQL.
	 * @return un {@link Optional} con el documento si existe.
	 */
	public Optional<FacturaDocumento> getByIdFacturaMySQL(Long idFacturaMySQL) {
		return facturaDocRepo.findByIdFacturaMySQL(idFacturaMySQL);
	}

	/**
	 * Obtiene una factura por el ID de su venta asociada.
	 *
	 * @param idVenta ID de la venta en MySQL.
	 * @return un {@link Optional} con el documento si existe.
	 */
	public Optional<FacturaDocumento> getByIdVenta(Long idVenta) {
		return facturaDocRepo.findByIdVenta(idVenta);
	}

	/**
	 * Obtiene el historial completo de facturas de un cliente, sin JOINs.
	 *
	 * @param idCliente ID del cliente en MySQL.
	 * @return lista de facturas del cliente.
	 */
	public List<FacturaDocumento> getHistorialCliente(Long idCliente) {
		return facturaDocRepo.findByCliente_IdCliente(idCliente);
	}

	/**
	 * Obtiene todas las facturas emitidas en un rango de fechas.
	 *
	 * @param inicio fecha y hora de inicio.
	 * @param fin    fecha y hora de fin.
	 * @return lista de facturas dentro del rango.
	 */
	public List<FacturaDocumento> getFacturasPorPeriodo(
			LocalDateTime inicio, LocalDateTime fin) {
		return facturaDocRepo.findByFechaBetween(inicio, fin);
	}

	/**
	 * Reporte agregado de ingreso bruto por sucursal en un periodo.
	 * Calculado nativamente por MongoDB.
	 *
	 * @param inicio fecha y hora de inicio.
	 * @param fin    fecha y hora de fin.
	 * @return lista de documentos con sucursal, ingreso bruto, impuestos
	 *         y cantidad de facturas.
	 */
	public List<Document> getIngresoPorSucursal(
			LocalDateTime inicio, LocalDateTime fin) {
		return facturaDocRepo.ingresoBrutoPorSucursal(inicio, fin);
	}

	/**
	 * Reporte de los productos más vendidos en un periodo.
	 *
	 * @param inicio fecha y hora de inicio.
	 * @param fin    fecha y hora de fin.
	 * @return lista de documentos con producto, unidades vendidas e ingreso.
	 */
	public List<Document> getTopProductos(LocalDateTime inicio, LocalDateTime fin) {
		return facturaDocRepo.topProductosVendidos(inicio, fin);
	}

	/**
	 * Ranking de los clientes que más gastaron en un periodo.
	 *
	 * @param inicio fecha y hora de inicio.
	 * @param fin    fecha y hora de fin.
	 * @return lista de documentos con datos del cliente y total gastado.
	 */
	public List<Document> getTopClientes(LocalDateTime inicio, LocalDateTime fin) {
		return facturaDocRepo.topClientes(inicio, fin);
	}

	/**
	 * Elimina el documento Mongo correspondiente a una factura MySQL.
	 * <p>
	 * Útil cuando se elimina la factura en MySQL para mantener consistencia.
	 *
	 * @param idFacturaMySQL ID en la tabla {@code factura} de MySQL.
	 */
	public void eliminarPorIdFacturaMySQL(Long idFacturaMySQL) {
		facturaDocRepo.findByIdFacturaMySQL(idFacturaMySQL)
				.ifPresent(facturaDocRepo::delete);
	}
}
