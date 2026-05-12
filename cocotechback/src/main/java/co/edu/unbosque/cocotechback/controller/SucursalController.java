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

import co.edu.unbosque.cocotechback.dto.SucursalDTO;
import co.edu.unbosque.cocotechback.service.SucursalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de sucursales del supermercado.
 * <p>
 * Todos los endpoints están restringidos a {@code ROLE_ADMIN}, ya que la
 * gestión de sucursales es una operación administrativa.
 */
@RestController
@RequestMapping("/sucursal")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Sucursales", description = "Endpoints para la gestión de sucursales del supermercado")
@SecurityRequirement(name = "bearerAuth")
public class SucursalController {

	/**
	 * Servicio para interactuar con la lógica de negocio de las sucursales.
	 */
	@Autowired
	private SucursalService sucursalServ;

	/**
	 * Constructor por defecto de {@code SucursalController}.
	 */
	public SucursalController() {
	}

	/**
	 * Crea una nueva sucursal en el sistema.
	 *
	 * @param sucursal El {@link SucursalDTO} con los datos de la nueva sucursal.
	 * @return {@code 201 Created} si fue exitosa,
	 *         {@code 409 Conflict} si ya existe una con ese nombre,
	 *         {@code 400 Bad Request} si los datos son inválidos.
	 */
	@PostMapping("/crear")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Crear sucursal", description = "Registra una nueva sucursal en el sistema")
	public ResponseEntity<?> crear(@RequestBody SucursalDTO sucursal) {
		int status = sucursalServ.create(sucursal, null);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Sucursal creada exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "Ya existe una sucursal con ese nombre", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Datos inválidos o campos requeridos ausentes", "success", false));
		}
	}

	/**
	 * Obtiene todas las sucursales registradas en el sistema.
	 *
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content} si
	 *         no hay sucursales.
	 */
	@GetMapping("/mostrarTodas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obtener todas las sucursales", description = "Retorna la lista completa de sucursales")
	public ResponseEntity<List<SucursalDTO>> mostrarTodas() {
		List<SucursalDTO> sucursales = sucursalServ.getAll();
		if (sucursales.isEmpty()) {
			return new ResponseEntity<>(sucursales, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(sucursales, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene una sucursal por su ID.
	 *
	 * @param id El ID de la sucursal, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con la sucursal, o {@code 404 Not Found}.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obtener sucursal por ID", description = "Retorna los datos de una sucursal por su ID")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		SucursalDTO found = sucursalServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Sucursal no encontrada", "success", false));
	}

	/**
	 * Retorna el total de sucursales registradas.
	 *
	 * @return {@code 202 Accepted} con el conteo, o {@code 204 No Content}.
	 */
	@GetMapping("/contar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Contar sucursales", description = "Retorna el número total de sucursales")
	public ResponseEntity<Long> contarTodas() {
		Long count = sucursalServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
	}

	/**
	 * Actualiza los datos de una sucursal existente.
	 *
	 * @param id       El ID de la sucursal, pasado como parámetro de consulta.
	 * @param sucursal El {@link SucursalDTO} con los nuevos datos.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 409 Conflict} si el nombre ya está en uso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si hay error.
	 */
	@PutMapping("/actualizar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar sucursal", description = "Actualiza los datos de una sucursal existente")
	public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody SucursalDTO sucursal) {
		int status = sucursalServ.updateById(id, sucursal);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Sucursal actualizada exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El nombre de sucursal ya está en uso", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Sucursal no encontrada", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar la sucursal", "success", false));
		}
	}

	/**
	 * Elimina una sucursal del sistema por su ID.
	 *
	 * @param id El ID de la sucursal, pasado como variable de ruta.
	 * @return {@code 202 Accepted} si fue exitosa, o {@code 404 Not Found}.
	 */
	@DeleteMapping("/eliminar/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Eliminar sucursal", description = "Elimina una sucursal del sistema por su ID")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int status = sucursalServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Sucursal eliminada exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Sucursal no encontrada", HttpStatus.NOT_FOUND);
	}
}
