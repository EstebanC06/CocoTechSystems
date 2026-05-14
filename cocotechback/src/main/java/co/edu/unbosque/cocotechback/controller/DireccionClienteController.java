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

import co.edu.unbosque.cocotechback.dto.DireccionClienteDTO;
import co.edu.unbosque.cocotechback.service.DireccionClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de direcciones guardadas de clientes.
 * <p>
 * Cada cliente puede tener múltiples direcciones y marcar una como
 * predeterminada para acelerar el checkout. Las direcciones son privadas;
 * cada cliente solo gestiona las suyas. Los administradores tienen acceso
 * de lectura para fines de soporte.
 */
@RestController
@RequestMapping("/direccion")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8080",
		"http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Direcciones de Cliente",
		description = "Endpoints para administrar las direcciones guardadas de los clientes")
@SecurityRequirement(name = "bearerAuth")
public class DireccionClienteController {

	/** Servicio de direcciones. */
	@Autowired
	private DireccionClienteService direccionServ;

	/** Constructor por defecto. */
	public DireccionClienteController() {
	}

	/**
	 * Crea una nueva dirección guardada para un cliente.
	 *
	 * @param dir DTO con los datos de la dirección.
	 * @return 201 Created si fue exitoso, 404 si el cliente no existe,
	 *         400 si faltan datos.
	 */
	@PostMapping("/crear")
	@PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
	@Operation(summary = "Crear dirección",
			description = "Guarda una nueva dirección de envío para un cliente")
	public ResponseEntity<?> crear(@RequestBody DireccionClienteDTO dir) {
		int status = direccionServ.create(dir, null);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Dirección creada exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Cliente no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Datos inválidos o campos requeridos ausentes",
							"success", false));
		}
	}

	/**
	 * Lista todas las direcciones de un cliente.
	 *
	 * @param idCliente ID del cliente.
	 * @return Lista de direcciones o 204 si está vacía.
	 */
	@GetMapping("/cliente/{idCliente}")
	@PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'ADMIN')")
	@Operation(summary = "Direcciones de un cliente",
			description = "Retorna las direcciones guardadas de un cliente específico")
	public ResponseEntity<List<DireccionClienteDTO>> obtenerPorCliente(
			@PathVariable Long idCliente) {
		List<DireccionClienteDTO> direcciones = direccionServ.getByCliente(idCliente);
		if (direcciones.isEmpty()) {
			return new ResponseEntity<>(direcciones, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(direcciones, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene una dirección por su ID.
	 *
	 * @param id ID de la dirección.
	 * @return DTO o 404.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
	@Operation(summary = "Obtener dirección por ID",
			description = "Retorna los datos de una dirección específica")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		DireccionClienteDTO found = direccionServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Dirección no encontrada", "success", false));
	}

	/**
	 * Actualiza una dirección existente.
	 *
	 * @param id  ID de la dirección.
	 * @param dir DTO con los nuevos datos.
	 * @return 202 si fue exitoso, 404 si no existe.
	 */
	@PutMapping("/actualizar")
	@PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
	@Operation(summary = "Actualizar dirección",
			description = "Actualiza los campos de una dirección guardada")
	public ResponseEntity<?> actualizar(@RequestParam Long id,
			@RequestBody DireccionClienteDTO dir) {
		int status = direccionServ.updateById(id, dir);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Dirección actualizada exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Dirección no encontrada", "success", false));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("message", "Error al actualizar la dirección", "success", false));
	}

	/**
	 * Marca una dirección como predeterminada (y desmarca las demás).
	 *
	 * @param id ID de la dirección a marcar.
	 * @return 202 si fue exitoso, 404 si no existe.
	 */
	@PutMapping("/marcarPredeterminada/{id}")
	@PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
	@Operation(summary = "Marcar como predeterminada",
			description = "Establece esta dirección como la predeterminada del cliente")
	public ResponseEntity<?> marcarPredeterminada(@PathVariable Long id) {
		int status = direccionServ.marcarPredeterminada(id);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Dirección marcada como predeterminada",
							"success", true));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Dirección no encontrada", "success", false));
	}

	/**
	 * Elimina una dirección guardada.
	 *
	 * @param id ID de la dirección.
	 * @return 202 si fue exitoso, 404 si no existe.
	 */
	@DeleteMapping("/eliminar/{id}")
	@PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
	@Operation(summary = "Eliminar dirección",
			description = "Elimina una dirección guardada del cliente")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int status = direccionServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Dirección eliminada exitosamente",
					HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Dirección no encontrada", HttpStatus.NOT_FOUND);
	}
}