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

import co.edu.unbosque.cocotechback.dto.ProveedorDTO;
import co.edu.unbosque.cocotechback.service.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de proveedores del supermercado.
 * <p>
 * Todos los endpoints están restringidos a {@code ROLE_ADMIN}.
 */
@RestController
@RequestMapping("/proveedor")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Proveedores", description = "Endpoints para la gestión de proveedores del supermercado")
@SecurityRequirement(name = "bearerAuth")
public class ProveedorController {

	/**
	 * Servicio para interactuar con la lógica de negocio de los proveedores.
	 */
	@Autowired
	private ProveedorService proveedorServ;

	/**
	 * Constructor por defecto de {@code ProveedorController}.
	 */
	public ProveedorController() {
	}

	/**
	 * Crea un nuevo proveedor en el sistema.
	 *
	 * @param proveedor El {@link ProveedorDTO} con los datos del nuevo proveedor.
	 * @return {@code 201 Created} si fue exitoso,
	 *         {@code 409 Conflict} si ya existe uno con ese nombre,
	 *         {@code 400 Bad Request} si los datos son inválidos.
	 */
	@PostMapping("/crear")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Crear proveedor", description = "Registra un nuevo proveedor en el sistema")
	public ResponseEntity<?> crear(@RequestBody ProveedorDTO proveedor) {
		int status = proveedorServ.create(proveedor, null);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Proveedor creado exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "Ya existe un proveedor con ese nombre", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Nombre de proveedor requerido", "success", false));
		}
	}

	/**
	 * Obtiene todos los proveedores registrados en el sistema.
	 *
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/mostrarTodos")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obtener todos los proveedores", description = "Retorna la lista de proveedores")
	public ResponseEntity<List<ProveedorDTO>> mostrarTodos() {
		List<ProveedorDTO> proveedores = proveedorServ.getAll();
		if (proveedores.isEmpty()) {
			return new ResponseEntity<>(proveedores, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(proveedores, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene un proveedor por su ID.
	 *
	 * @param id El ID del proveedor, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con el proveedor, o {@code 404 Not Found}.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obtener proveedor por ID", description = "Retorna los datos de un proveedor por su ID")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		ProveedorDTO found = proveedorServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Proveedor no encontrado", "success", false));
	}

	/**
	 * Actualiza los datos de un proveedor existente.
	 *
	 * @param id        El ID del proveedor, pasado como parámetro de consulta.
	 * @param proveedor El {@link ProveedorDTO} con los nuevos datos.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 409 Conflict} si el nombre ya está en uso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si hay error.
	 */
	@PutMapping("/actualizar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar proveedor", description = "Actualiza los datos de un proveedor")
	public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody ProveedorDTO proveedor) {
		int status = proveedorServ.updateById(id, proveedor);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Proveedor actualizado exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El nombre de proveedor ya está en uso", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Proveedor no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar el proveedor", "success", false));
		}
	}

	/**
	 * Elimina un proveedor por su ID.
	 *
	 * @param id El ID del proveedor, pasado como variable de ruta.
	 * @return {@code 202 Accepted} si fue exitoso, o {@code 404 Not Found}.
	 */
	@DeleteMapping("/eliminar/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Eliminar proveedor", description = "Elimina un proveedor por su ID")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int status = proveedorServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Proveedor eliminado exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Proveedor no encontrado", HttpStatus.NOT_FOUND);
	}
}
