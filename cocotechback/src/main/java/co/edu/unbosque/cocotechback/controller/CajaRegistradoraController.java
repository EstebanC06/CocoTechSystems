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

import co.edu.unbosque.cocotechback.dto.CajaRegistradoraDTO;
import co.edu.unbosque.cocotechback.service.CajaRegistradoraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de cajas registradoras del supermercado.
 * <p>
 * Todos los endpoints están restringidos a {@code ROLE_ADMIN}.
 */
@RestController
@RequestMapping("/caja")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Cajas Registradoras", description = "Endpoints para la gestión de cajas registradoras")
@SecurityRequirement(name = "bearerAuth")
public class CajaRegistradoraController {

	/**
	 * Servicio para interactuar con la lógica de negocio de las cajas.
	 */
	@Autowired
	private CajaRegistradoraService cajaServ;

	/**
	 * Constructor por defecto de {@code CajaRegistradoraController}.
	 */
	public CajaRegistradoraController() {
	}

	/**
	 * Crea una nueva caja registradora en el sistema.
	 * <p>
	 * Valida que el empleado no tenga ya una caja asignada (restricción 1:1).
	 *
	 * @param caja El {@link CajaRegistradoraDTO} con los datos de la nueva caja.
	 * @return {@code 201 Created} si fue exitosa,
	 *         {@code 409 Conflict} si el empleado ya tiene una caja asignada,
	 *         {@code 404 Not Found} si el empleado o la sucursal no existen,
	 *         {@code 400 Bad Request} si los datos son inválidos.
	 */
	@PostMapping("/crear")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Crear caja registradora", description = "Registra una nueva caja registradora")
	public ResponseEntity<?> crear(@RequestBody CajaRegistradoraDTO caja) {
		int status = cajaServ.create(caja, null);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Caja registradora creada exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El empleado ya tiene una caja registradora asignada",
							"success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Empleado o sucursal no encontrados", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Datos inválidos o campos requeridos ausentes",
							"success", false));
		}
	}

	/**
	 * Obtiene todas las cajas registradoras del sistema.
	 *
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/mostrarTodas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obtener todas las cajas", description = "Retorna la lista de cajas registradoras")
	public ResponseEntity<List<CajaRegistradoraDTO>> mostrarTodas() {
		List<CajaRegistradoraDTO> cajas = cajaServ.getAll();
		if (cajas.isEmpty()) {
			return new ResponseEntity<>(cajas, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(cajas, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene una caja registradora por su ID.
	 *
	 * @param id El ID de la caja, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con la caja, o {@code 404 Not Found}.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obtener caja por ID", description = "Retorna los datos de una caja por su ID")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		CajaRegistradoraDTO found = cajaServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Caja no encontrada", "success", false));
	}

	/**
	 * Actualiza los datos de una caja registradora existente.
	 *
	 * @param id   El ID de la caja, pasado como parámetro de consulta.
	 * @param caja El {@link CajaRegistradoraDTO} con los nuevos datos.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 409 Conflict} si el nuevo empleado ya tiene caja,
	 *         {@code 404 Not Found} si no existe la caja, empleado o sucursal,
	 *         {@code 400 Bad Request} si hay error.
	 */
	@PutMapping("/actualizar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar caja", description = "Actualiza los datos de una caja registradora")
	public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody CajaRegistradoraDTO caja) {
		int status = cajaServ.updateById(id, caja);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Caja actualizada exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El empleado ya tiene una caja asignada", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Caja, empleado o sucursal no encontrados", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar la caja", "success", false));
		}
	}

	/**
	 * Elimina una caja registradora por su ID.
	 *
	 * @param id El ID de la caja, pasado como variable de ruta.
	 * @return {@code 202 Accepted} si fue exitosa, o {@code 404 Not Found}.
	 */
	@DeleteMapping("/eliminar/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Eliminar caja", description = "Elimina una caja registradora por su ID")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int status = cajaServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Caja eliminada exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Caja no encontrada", HttpStatus.NOT_FOUND);
	}
}
