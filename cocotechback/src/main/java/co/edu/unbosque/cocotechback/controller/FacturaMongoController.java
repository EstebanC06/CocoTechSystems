/**
 * Paquete que contiene los controladores REST de la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.cocotechback.model.mongo.FacturaDocumento;
import co.edu.unbosque.cocotechback.service.FacturaMongoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para los endpoints de lectura intensiva sobre las
 * facturas embebidas en MongoDB.
 * <p>
 * Estos endpoints aprovechan el Patrón de Referencia Extendida: las
 * consultas devuelven el documento completo (cliente, empleado, sucursal y
 * detalles) sin necesidad de operaciones JOIN.
 */
@RestController
@RequestMapping("/factura/mongo")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8080",
		"http://localhost:8081", "http://localhost:4200" })
@Tag(name = "Facturas (MongoDB)",
		description = "Lecturas de alto rendimiento sobre la vista embebida de facturas")
@SecurityRequirement(name = "bearerAuth")
public class FacturaMongoController {

	/**
	 * Servicio que expone consultas y proyecciones sobre MongoDB.
	 */
	@Autowired
	private FacturaMongoService facturaMongoServ;

	/**
	 * Constructor por defecto.
	 */
	public FacturaMongoController() {
	}

	/**
	 * Obtiene una factura completa (cliente, empleado, sucursal y detalles
	 * embebidos) por su ID de MySQL.
	 *
	 * @param idFacturaMySQL ID en la tabla {@code factura} de MySQL.
	 * @return {@code 200 OK} con el documento, o {@code 404 Not Found}.
	 */
	@GetMapping("/obtenerPorIdMySQL/{idFacturaMySQL}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Factura embebida por ID MySQL",
			description = "Lectura O(1) sin JOINs desde MongoDB")
	public ResponseEntity<?> obtenerPorIdMySQL(@PathVariable Long idFacturaMySQL) {
		Optional<FacturaDocumento> doc =
				facturaMongoServ.getByIdFacturaMySQL(idFacturaMySQL);
		if (doc.isPresent()) {
			return ResponseEntity.ok(doc.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Factura no encontrada en MongoDB",
						"success", false));
	}

	/**
	 * Obtiene una factura completa por el ID de su venta asociada.
	 *
	 * @param idVenta ID de la venta en MySQL.
	 * @return {@code 200 OK} con el documento, o {@code 404 Not Found}.
	 */
	@GetMapping("/obtenerPorVenta/{idVenta}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Factura embebida por venta",
			description = "Lectura O(1) de la factura asociada a una venta")
	public ResponseEntity<?> obtenerPorVenta(@PathVariable Long idVenta) {
		Optional<FacturaDocumento> doc = facturaMongoServ.getByIdVenta(idVenta);
		if (doc.isPresent()) {
			return ResponseEntity.ok(doc.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Sin factura para esa venta", "success", false));
	}

	/**
	 * Historial completo de facturas de un cliente.
	 * <p>
	 * Reemplaza el JOIN Factura → Venta → Cliente por una lectura indexada
	 * en MongoDB.
	 *
	 * @param idCliente ID del cliente.
	 * @return {@code 200 OK} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/historialCliente/{idCliente}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Historial de facturas del cliente",
			description = "Lista todas las facturas de un cliente sin operaciones JOIN")
	public ResponseEntity<?> historialCliente(@PathVariable Long idCliente) {
		List<FacturaDocumento> historial =
				facturaMongoServ.getHistorialCliente(idCliente);
		if (historial.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(historial);
	}

	/**
	 * Lista de facturas emitidas dentro de un rango de fechas.
	 *
	 * @param inicio fecha y hora de inicio.
	 * @param fin    fecha y hora de fin.
	 * @return {@code 200 OK} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/porPeriodo")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Facturas por periodo",
			description = "Lista de facturas emitidas en un rango de fechas")
	public ResponseEntity<?> porPeriodo(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
		List<FacturaDocumento> lista =
				facturaMongoServ.getFacturasPorPeriodo(inicio, fin);
		if (lista.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(lista);
	}

	// ─── Reportes calculados nativamente en MongoDB ────────────────────

	/**
	 * Reporte de ingreso bruto agrupado por sucursal.
	 *
	 * @param inicio fecha y hora de inicio.
	 * @param fin    fecha y hora de fin.
	 * @return {@code 200 OK} con la lista agregada.
	 */
	@GetMapping("/reportes/ingresoPorSucursal")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Ingreso bruto por sucursal",
			description = "Agregación nativa sobre las facturas embebidas")
	public ResponseEntity<List<Document>> ingresoPorSucursal(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
		return ResponseEntity.ok(facturaMongoServ.getIngresoPorSucursal(inicio, fin));
	}

	/**
	 * Top 10 de productos más vendidos en un periodo.
	 *
	 * @param inicio fecha y hora de inicio.
	 * @param fin    fecha y hora de fin.
	 * @return {@code 200 OK} con la lista del top.
	 */
	@GetMapping("/reportes/topProductos")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Top productos más vendidos",
			description = "Ranking calculado nativamente con $unwind en MongoDB")
	public ResponseEntity<List<Document>> topProductos(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
		return ResponseEntity.ok(facturaMongoServ.getTopProductos(inicio, fin));
	}

	/**
	 * Top 10 de clientes que más gastaron en un periodo.
	 *
	 * @param inicio fecha y hora de inicio.
	 * @param fin    fecha y hora de fin.
	 * @return {@code 200 OK} con la lista del top.
	 */
	@GetMapping("/reportes/topClientes")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Top clientes",
			description = "Ranking de clientes por monto gastado en el periodo")
	public ResponseEntity<List<Document>> topClientes(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
		return ResponseEntity.ok(facturaMongoServ.getTopClientes(inicio, fin));
	}

	// ─── Operaciones administrativas ───────────────────────────────────

	/**
	 * Sincroniza todas las facturas de MySQL hacia MongoDB.
	 * <p>
	 * Endpoint útil para la primera carga al introducir MongoDB en un
	 * sistema con facturas pre-existentes en MySQL.
	 *
	 * @return {@code 200 OK} con la cantidad de facturas proyectadas.
	 */
	@PostMapping("/sincronizar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Sincronización inicial MySQL → MongoDB",
			description = "Proyecta todas las facturas existentes en MySQL hacia MongoDB")
	public ResponseEntity<?> sincronizar() {
		int cantidad = facturaMongoServ.sincronizarDesdeMySQL();
		return ResponseEntity.ok(Map.of(
				"message", "Sincronización completada",
				"facturasProyectadas", cantidad,
				"success", true));
	}
}