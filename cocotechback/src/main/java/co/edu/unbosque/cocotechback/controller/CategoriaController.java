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

import co.edu.unbosque.cocotechback.dto.CategoriaDTO;
import co.edu.unbosque.cocotechback.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de categorías de productos del supermercado.
 * <p>
 * La consulta de categorías está disponible para todos los usuarios
 * autenticados ({@code ROLE_CLIENTE} y {@code ROLE_ADMIN}). Las operaciones
 * de escritura son exclusivas de {@code ROLE_ADMIN}.
 */
@RestController
@RequestMapping("/categoria")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Categorías", description = "Endpoints para la gestión de categorías de productos")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

	/**
	 * Servicio para interactuar con la lógica de negocio de las categorías.
	 */
	@Autowired
	private CategoriaService categoriaServ;

	/**
	 * Constructor por defecto de {@code CategoriaController}.
	 */
	public CategoriaController() {
	}

	/**
	 * Crea una nueva categoría de productos.
	 *
	 * @param categoria El {@link CategoriaDTO} con los datos de la nueva categoría.
	 * @return {@code 201 Created} si fue exitosa,
	 *         {@code 409 Conflict} si ya existe una con ese nombre,
	 *         {@code 400 Bad Request} si los datos son inválidos.
	 */
	@PostMapping("/crear")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Crear categoría", description = "Crea una nueva categoría de productos")
	public ResponseEntity<?> crear(@RequestBody CategoriaDTO categoria) {
		int status = categoriaServ.create(categoria, null);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Categoría creada exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "Ya existe una categoría con ese nombre", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Nombre de categoría requerido", "success", false));
		}
	}

	/**
	 * Obtiene todas las categorías registradas.
	 * <p>
	 * Accesible para todos los usuarios autenticados.
	 *
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/mostrarTodas")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Obtener todas las categorías", description = "Retorna la lista de categorías de productos")
	public ResponseEntity<List<CategoriaDTO>> mostrarTodas() {
		List<CategoriaDTO> categorias = categoriaServ.getAll();
		if (categorias.isEmpty()) {
			return new ResponseEntity<>(categorias, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(categorias, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene una categoría por su ID.
	 *
	 * @param id El ID de la categoría, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con la categoría, o {@code 404 Not Found}.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Obtener categoría por ID", description = "Retorna los datos de una categoría por su ID")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		CategoriaDTO found = categoriaServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Categoría no encontrada", "success", false));
	}

	/**
	 * Actualiza los datos de una categoría existente.
	 *
	 * @param id        El ID de la categoría, pasado como parámetro de consulta.
	 * @param categoria El {@link CategoriaDTO} con los nuevos datos.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 409 Conflict} si el nombre ya está en uso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si hay error.
	 */
	@PutMapping("/actualizar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar categoría", description = "Actualiza los datos de una categoría")
	public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody CategoriaDTO categoria) {
		int status = categoriaServ.updateById(id, categoria);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Categoría actualizada exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El nombre de categoría ya está en uso", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Categoría no encontrada", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar la categoría", "success", false));
		}
	}

	/**
	 * Elimina una categoría por su ID.
	 *
	 * @param id El ID de la categoría, pasado como variable de ruta.
	 * @return {@code 202 Accepted} si fue exitosa, o {@code 404 Not Found}.
	 */
	@DeleteMapping("/eliminar/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Eliminar categoría", description = "Elimina una categoría por su ID")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int status = categoriaServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Categoría eliminada exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Categoría no encontrada", HttpStatus.NOT_FOUND);
	}
}
