/**
 * Paquete que contiene los controladores REST de la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import co.edu.unbosque.cocotechback.dto.DetalleVentaDTO;
import co.edu.unbosque.cocotechback.service.DetalleVentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de detalles de venta del supermercado.
 * <p>
 * Al registrar un detalle, se descuenta automáticamente el stock del producto
 * correspondiente. Todos los endpoints son exclusivos de {@code ROLE_ADMIN},
 * ya que el registro de detalles de venta es una operación de caja.
 */
@RestController
@RequestMapping("/detalleVenta")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Detalles de Venta", description = "Endpoints para la gestión de detalles de venta")
@SecurityRequirement(name = "bearerAuth")
public class DetalleVentaController {

	/**
	 * Servicio para interactuar con la lógica de negocio de los detalles de venta.
	 */
	@Autowired
	private DetalleVentaService detalleVentaServ;

	/**
	 * Constructor por defecto de {@code DetalleVentaController}.
	 */
	public DetalleVentaController() {
	}

	/**
	 * Registra un nuevo detalle de venta.
	 * <p>
	 * Descuenta automáticamente la cantidad del stock del producto vendido.
	 *
	 * @param detalle El {@link DetalleVentaDTO} con los datos del detalle.
	 * @return {@code 201 Created} si fue exitoso,
	 *         {@code 409 Conflict} si no hay stock suficiente,
	 *         {@code 404 Not Found} si la venta o el producto no existen,
	 *         {@code 400 Bad Request} si los datos son inválidos.
	 */
	@PostMapping("/crear")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Registrar detalle de venta",
			description = "Agrega un nuevo detalle a una venta y descuenta el stock del producto")
	public ResponseEntity<?> crear(@RequestBody DetalleVentaDTO detalle) {
		int status = detalleVentaServ.create(detalle, null);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Detalle de venta registrado exitosamente",
							"success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "Stock insuficiente para el producto solicitado",
							"success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Venta o producto no encontrados", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Datos inválidos o campos requeridos ausentes",
							"success", false));
		}
	}

	/**
	 * Obtiene todos los detalles de venta registrados en el sistema.
	 *
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/mostrarTodos")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obtener todos los detalles",
			description = "Retorna la lista completa de detalles de venta")
	public ResponseEntity<List<DetalleVentaDTO>> mostrarTodos() {
		List<DetalleVentaDTO> detalles = detalleVentaServ.getAll();
		if (detalles.isEmpty()) {
			return new ResponseEntity<>(detalles, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(detalles, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene un detalle de venta por su ID.
	 *
	 * @param id El ID del detalle, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con el detalle, o {@code 404 Not Found}.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Obtener detalle por ID", description = "Retorna los datos de un detalle por su ID")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		DetalleVentaDTO found = detalleVentaServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Detalle de venta no encontrado", "success", false));
	}

	/**
	 * Actualiza los datos de un detalle de venta existente.
	 *
	 * @param id      El ID del detalle, pasado como parámetro de consulta.
	 * @param detalle El {@link DetalleVentaDTO} con los nuevos datos.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si hay error.
	 */
	@PutMapping("/actualizar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar detalle", description = "Actualiza los datos de un detalle de venta")
	public ResponseEntity<?> actualizar(@RequestParam Long id,
			@RequestBody DetalleVentaDTO detalle) {
		int status = detalleVentaServ.updateById(id, detalle);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Detalle actualizado exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Detalle no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar el detalle", "success", false));
		}
	}

	/**
	 * Elimina un detalle de venta por su ID.
	 *
	 * @param id El ID del detalle, pasado como variable de ruta.
	 * @return {@code 202 Accepted} si fue exitoso, o {@code 404 Not Found}.
	 */
	@DeleteMapping("/eliminar/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Eliminar detalle", description = "Elimina un detalle de venta por su ID")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int status = detalleVentaServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Detalle eliminado exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Detalle no encontrado", HttpStatus.NOT_FOUND);
	}

	// ─── Endpoints analíticos ─────────────────────────────────────────────────

	/**
	 * Retorna el resumen de ventas agrupado por método de pago.
	 *
	 * @return {@code 202 Accepted} con el resumen, o {@code 204 No Content}.
	 */
	@GetMapping("/reportes/resumenPorMetodoPago")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Resumen por método de pago",
			description = "Reporte: total de transacciones e ingresos por método de pago")
	public ResponseEntity<List<Object[]>> resumenPorMetodoPago() {
		List<Object[]> resultado = detalleVentaServ.getResumenPorMetodoPago();
		if (resultado.isEmpty()) {
			return new ResponseEntity<>(resultado, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(resultado, HttpStatus.ACCEPTED);
	}

	/**
	 * Retorna todos los detalles de venta que tuvieron promoción activa.
	 *
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/reportes/conPromocion")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Detalles con promoción",
			description = "Retorna todos los detalles de venta donde se aplicó un descuento")
	public ResponseEntity<List<Object[]>> detallesConPromocion() {
		List<Object[]> resultado = detalleVentaServ.getDetallesConPromocion();
		if (resultado.isEmpty()) {
			return new ResponseEntity<>(resultado, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(resultado, HttpStatus.ACCEPTED);
	}
}
