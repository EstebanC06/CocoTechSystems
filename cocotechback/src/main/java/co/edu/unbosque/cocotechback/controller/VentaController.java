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

import co.edu.unbosque.cocotechback.dto.VentaDTO;
import co.edu.unbosque.cocotechback.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de ventas del supermercado.
 * <p>
 * El registro de ventas es una operación exclusiva de {@code ROLE_ADMIN}
 * (empleados). Los clientes pueden consultar su historial de compras.
 * Los reportes analíticos están restringidos a {@code ROLE_ADMIN}.
 */
@RestController
@RequestMapping("/venta")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Ventas", description = "Endpoints para la gestión y análisis de ventas")
@SecurityRequirement(name = "bearerAuth")
public class VentaController {

	/**
	 * Servicio para interactuar con la lógica de negocio de las ventas.
	 */
	@Autowired
	private VentaService ventaServ;

	/**
	 * Constructor por defecto de {@code VentaController}.
	 */
	public VentaController() {
	}

	/**
	 * Registra una nueva venta en el sistema.
	 *
	 * @param venta El {@link VentaDTO} con los datos de la nueva venta.
	 * @return {@code 201 Created} si fue exitosa,
	 *         {@code 404 Not Found} si el empleado o el cliente no existen,
	 *         {@code 400 Bad Request} si los datos son inválidos.
	 */
	@PostMapping("/crear")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Registrar venta", description = "Registra una nueva venta en el sistema")
	public ResponseEntity<?> crear(@RequestBody VentaDTO venta) {
		int status = ventaServ.create(venta, null);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Venta registrada exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Empleado o cliente no encontrados", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Datos inválidos o campos requeridos ausentes",
							"success", false));
		}
	}

	/**
	 * Obtiene todas las ventas registradas en el sistema.
	 *
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/mostrarTodas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obtener todas las ventas", description = "Retorna la lista completa de ventas")
	public ResponseEntity<List<VentaDTO>> mostrarTodas() {
		List<VentaDTO> ventas = ventaServ.getAll();
		if (ventas.isEmpty()) {
			return new ResponseEntity<>(ventas, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(ventas, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene una venta por su ID.
	 *
	 * @param id El ID de la venta, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con la venta, o {@code 404 Not Found}.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Obtener venta por ID", description = "Retorna los datos de una venta por su ID")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		VentaDTO found = ventaServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Venta no encontrada", "success", false));
	}

	/**
	 * Actualiza el total o la fecha de una venta existente.
	 *
	 * @param id    El ID de la venta, pasado como parámetro de consulta.
	 * @param venta El {@link VentaDTO} con los nuevos datos.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si hay error.
	 */
	@PutMapping("/actualizar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar venta", description = "Actualiza el total o la fecha de una venta")
	public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody VentaDTO venta) {
		int status = ventaServ.updateById(id, venta);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Venta actualizada exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Venta no encontrada", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar la venta", "success", false));
		}
	}

	/**
	 * Elimina una venta por su ID.
	 *
	 * @param id El ID de la venta, pasado como variable de ruta.
	 * @return {@code 202 Accepted} si fue exitosa, o {@code 404 Not Found}.
	 */
	@DeleteMapping("/eliminar/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Eliminar venta", description = "Elimina una venta por su ID")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int status = ventaServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Venta eliminada exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Venta no encontrada", HttpStatus.NOT_FOUND);
	}

	// ─── Endpoints analíticos ─────────────────────────────────────────────────

	/**
	 * Retorna el empleado con más ventas registradas en un periodo dado
	 * ("Empleado del mes").
	 * <p>
	 * Satisface el escenario analítico requerido en los objetivos del proyecto.
	 *
	 * @param inicio Fecha y hora de inicio del periodo (formato ISO: yyyy-MM-dd'T'HH:mm:ss).
	 * @param fin    Fecha y hora de fin del periodo.
	 * @return {@code 202 Accepted} con el ranking de empleados, o
	 *         {@code 204 No Content} si no hay datos.
	 */
	@GetMapping("/reportes/empleadoDelMes")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Empleado del mes",
			description = "Reporte: empleado con más ventas registradas en el periodo dado")
	public ResponseEntity<List<Object[]>> empleadoDelMes(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
		List<Object[]> resultado = ventaServ.getEmpleadoDelMes(inicio, fin);
		if (resultado.isEmpty()) {
			return new ResponseEntity<>(resultado, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(resultado, HttpStatus.ACCEPTED);
	}

	/**
	 * Retorna el cliente con más compras realizadas en el sistema.
	 * <p>
	 * Satisface el escenario analítico requerido en los objetivos del proyecto.
	 *
	 * @return {@code 202 Accepted} con el ranking de clientes, o
	 *         {@code 204 No Content} si no hay datos.
	 */
	@GetMapping("/reportes/clienteConMasCompras")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Cliente con más compras",
			description = "Reporte: cliente con mayor número de compras realizadas")
	public ResponseEntity<List<Object[]>> clienteConMasCompras() {
		List<Object[]> resultado = ventaServ.getClienteConMasCompras();
		if (resultado.isEmpty()) {
			return new ResponseEntity<>(resultado, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(resultado, HttpStatus.ACCEPTED);
	}

	/**
	 * Retorna el total de ventas acumulado por empleado.
	 *
	 * @return {@code 202 Accepted} con el reporte, o {@code 204 No Content}.
	 */
	@GetMapping("/reportes/totalPorEmpleado")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Total ventas por empleado",
			description = "Retorna el monto total de ventas registradas por cada empleado")
	public ResponseEntity<List<Object[]>> totalVentasPorEmpleado() {
		List<Object[]> resultado = ventaServ.getTotalVentasPorEmpleado();
		if (resultado.isEmpty()) {
			return new ResponseEntity<>(resultado, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(resultado, HttpStatus.ACCEPTED);
	}
}
