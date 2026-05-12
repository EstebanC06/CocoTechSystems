/**
 * Paquete que contiene los controladores REST de la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.cocotechback.dto.FacturaDTO;
import co.edu.unbosque.cocotechback.service.FacturaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de facturas del supermercado.
 * <p>
 * Cada venta genera una única factura (1:1). Los endpoints de consulta de
 * factura propia son accesibles para {@code ROLE_CLIENTE}. Los reportes
 * contables y operaciones de escritura son exclusivos de {@code ROLE_ADMIN}.
 */
@RestController
@RequestMapping("/factura")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Facturas", description = "Endpoints para la gestión de facturas y reportes contables")
@SecurityRequirement(name = "bearerAuth")
public class FacturaController {

	/**
	 * Servicio para interactuar con la lógica de negocio de las facturas.
	 */
	@Autowired
	private FacturaService facturaServ;

	/**
	 * Constructor por defecto de {@code FacturaController}.
	 */
	public FacturaController() {
	}

	/**
	 * Genera una nueva factura a partir de una venta existente.
	 * <p>
	 * Valida que la venta no tenga ya una factura asociada (restricción 1:1).
	 *
	 * @param factura El {@link FacturaDTO} con los datos de la nueva factura.
	 * @return {@code 201 Created} si fue exitosa,
	 *         {@code 409 Conflict} si la venta ya tiene una factura,
	 *         {@code 404 Not Found} si la venta no existe,
	 *         {@code 400 Bad Request} si los datos son inválidos.
	 */
	@PostMapping("/crear")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Generar factura",
			description = "Genera una nueva factura para una venta registrada")
	public ResponseEntity<?> crear(@RequestBody FacturaDTO factura) {
		int status = facturaServ.create(factura, null);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Factura generada exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "La venta ya tiene una factura asociada",
							"success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Venta no encontrada", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "ID de venta requerido", "success", false));
		}
	}

	/**
	 * Obtiene todas las facturas registradas en el sistema.
	 *
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/mostrarTodas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obtener todas las facturas",
			description = "Retorna la lista completa de facturas emitidas")
	public ResponseEntity<List<FacturaDTO>> mostrarTodas() {
		List<FacturaDTO> facturas = facturaServ.getAll();
		if (facturas.isEmpty()) {
			return new ResponseEntity<>(facturas, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(facturas, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene una factura por su ID.
	 *
	 * @param id El ID de la factura, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con la factura, o {@code 404 Not Found}.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Obtener factura por ID",
			description = "Retorna los datos de una factura por su ID")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		FacturaDTO found = facturaServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Factura no encontrada", "success", false));
	}

	/**
	 * Obtiene la factura asociada a una venta específica.
	 *
	 * @param idVenta El ID de la venta, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con la factura, o {@code 404 Not Found} si
	 *         la venta no tiene factura.
	 */
	@GetMapping("/obtenerPorVenta/{idVenta}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Obtener factura por venta",
			description = "Retorna la factura asociada a una venta específica")
	public ResponseEntity<?> obtenerPorVenta(@PathVariable Long idVenta) {
		FacturaDTO found = facturaServ.getByIdVenta(idVenta);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "No existe factura para esa venta", "success", false));
	}

	/**
	 * Actualiza los datos de una factura existente.
	 *
	 * @param id      El ID de la factura, pasado como parámetro de consulta.
	 * @param factura El {@link FacturaDTO} con los nuevos datos.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si hay error.
	 */
	@PutMapping("/actualizar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar factura", description = "Actualiza los datos de una factura")
	public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody FacturaDTO factura) {
		int status = facturaServ.updateById(id, factura);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Factura actualizada exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Factura no encontrada", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar la factura", "success", false));
		}
	}

	/**
	 * Elimina una factura por su ID.
	 *
	 * @param id El ID de la factura, pasado como variable de ruta.
	 * @return {@code 202 Accepted} si fue exitosa, o {@code 404 Not Found}.
	 */
	@DeleteMapping("/eliminar/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Eliminar factura", description = "Elimina una factura por su ID")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int status = facturaServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Factura eliminada exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Factura no encontrada", HttpStatus.NOT_FOUND);
	}

	// ─── Endpoints de reportes contables ──────────────────────────────────────

	/**
	 * Calcula el ingreso bruto total del supermercado en un periodo de tiempo.
	 *
	 * @param inicio Fecha y hora de inicio del periodo (ISO: yyyy-MM-dd'T'HH:mm:ss).
	 * @param fin    Fecha y hora de fin del periodo.
	 * @return {@code 202 Accepted} con el ingreso bruto, o
	 *         {@code 204 No Content} si no hay facturas en el rango.
	 */
	@GetMapping("/reportes/ingresoBruto")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Ingreso bruto por periodo",
			description = "Calcula el ingreso bruto total del supermercado en un periodo dado")
	public ResponseEntity<?> ingresoBrutoPorPeriodo(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
		Double resultado = facturaServ.getIngresoBrutoPorPeriodo(inicio, fin);
		if (resultado == 0.0) {
			return new ResponseEntity<>(resultado, HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(Map.of("ingresoBruto", resultado, "success", true));
	}

	/**
	 * Calcula el total de impuestos recaudados en un periodo de tiempo.
	 *
	 * @param inicio Fecha y hora de inicio del periodo.
	 * @param fin    Fecha y hora de fin del periodo.
	 * @return {@code 202 Accepted} con el total de impuestos, o
	 *         {@code 204 No Content} si no hay facturas en el rango.
	 */
	@GetMapping("/reportes/impuestosRecaudados")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Impuestos recaudados por periodo",
			description = "Calcula el total de impuestos recaudados en un periodo dado")
	public ResponseEntity<?> impuestosRecaudados(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
		Double resultado = facturaServ.getTotalImpuestosRecaudados(inicio, fin);
		if (resultado == 0.0) {
			return new ResponseEntity<>(resultado, HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(Map.of("totalImpuestos", resultado, "success", true));
	}
}
