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

import co.edu.unbosque.cocotechback.dto.ProductoDTO;
import co.edu.unbosque.cocotechback.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión del catálogo de productos del supermercado.
 * <p>
 * La consulta de productos está disponible para todos los usuarios autenticados.
 * Las operaciones de escritura y los reportes analíticos son exclusivos de
 * {@code ROLE_ADMIN}.
 */
@RestController
@RequestMapping("/producto")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Productos", description = "Endpoints para la gestión del catálogo de productos")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

	/**
	 * Servicio para interactuar con la lógica de negocio de los productos.
	 */
	@Autowired
	private ProductoService productoServ;

	/**
	 * Constructor por defecto de {@code ProductoController}.
	 */
	public ProductoController() {
	}

	/**
	 * Crea un nuevo producto en el catálogo del supermercado.
	 *
	 * @param producto El {@link ProductoDTO} con los datos del nuevo producto.
	 * @return {@code 201 Created} si fue exitoso,
	 *         {@code 409 Conflict} si ya existe uno con ese nombre,
	 *         {@code 404 Not Found} si la categoría o el proveedor no existen,
	 *         {@code 400 Bad Request} si los datos son inválidos.
	 */
	@PostMapping("/crear")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Crear producto", description = "Agrega un nuevo producto al catálogo")
	public ResponseEntity<?> crear(@RequestBody ProductoDTO producto) {
		int status = productoServ.create(producto, null);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Producto creado exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "Ya existe un producto con ese nombre", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Categoría o proveedor no encontrados", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Datos inválidos o campos requeridos ausentes",
							"success", false));
		}
	}

	/**
	 * Obtiene todos los productos del catálogo.
	 * <p>
	 * Accesible para todos los usuarios autenticados.
	 *
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/mostrarTodos")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Obtener todos los productos", description = "Retorna el catálogo completo de productos")
	public ResponseEntity<List<ProductoDTO>> mostrarTodos() {
		List<ProductoDTO> productos = productoServ.getAll();
		if (productos.isEmpty()) {
			return new ResponseEntity<>(productos, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(productos, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene un producto por su ID.
	 *
	 * @param id El ID del producto, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con el producto, o {@code 404 Not Found}.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Obtener producto por ID", description = "Retorna los datos de un producto por su ID")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		ProductoDTO found = productoServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Producto no encontrado", "success", false));
	}

	/**
	 * Retorna el conteo total de productos en el catálogo.
	 *
	 * @return {@code 202 Accepted} con el conteo, o {@code 204 No Content}.
	 */
	@GetMapping("/contar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Contar productos", description = "Retorna el número total de productos en el catálogo")
	public ResponseEntity<Long> contarTodos() {
		Long count = productoServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
	}

	/**
	 * Actualiza los datos de un producto existente.
	 *
	 * @param id       El ID del producto, pasado como parámetro de consulta.
	 * @param producto El {@link ProductoDTO} con los nuevos datos.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 409 Conflict} si el nombre ya está en uso,
	 *         {@code 404 Not Found} si no existe el producto, categoría o proveedor,
	 *         {@code 400 Bad Request} si hay error.
	 */
	@PutMapping("/actualizar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto del catálogo")
	public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody ProductoDTO producto) {
		int status = productoServ.updateById(id, producto);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Producto actualizado exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El nombre de producto ya está en uso", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Producto, categoría o proveedor no encontrados",
							"success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar el producto", "success", false));
		}
	}

	/**
	 * Elimina un producto del catálogo por su ID.
	 *
	 * @param id El ID del producto, pasado como variable de ruta.
	 * @return {@code 202 Accepted} si fue exitoso, o {@code 404 Not Found}.
	 */
	@DeleteMapping("/eliminar/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Eliminar producto", description = "Elimina un producto del catálogo por su ID")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int status = productoServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Producto eliminado exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Producto no encontrado", HttpStatus.NOT_FOUND);
	}

	// ─── Endpoints analíticos ─────────────────────────────────────────────────

	/**
	 * Retorna el producto más vendido dentro de cada categoría.
	 * <p>
	 * Satisface el escenario analítico "Producto más vendido de cada categoría"
	 * especificado en los objetivos del proyecto.
	 *
	 * @return {@code 202 Accepted} con la lista de resultados, o
	 *         {@code 204 No Content} si no hay datos.
	 */
	@GetMapping("/reportes/masVendidoPorCategoria")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Producto más vendido por categoría",
			description = "Reporte analítico: producto más vendido dentro de cada categoría")
	public ResponseEntity<List<Object[]>> masVendidoPorCategoria() {
		List<Object[]> resultado = productoServ.getProductoMasVendidoPorCategoria();
		if (resultado.isEmpty()) {
			return new ResponseEntity<>(resultado, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(resultado, HttpStatus.ACCEPTED);
	}

	/**
	 * Retorna todos los productos ordenados de mayor a menor por cantidad total
	 * vendida.
	 *
	 * @return {@code 202 Accepted} con el ranking, o {@code 204 No Content}.
	 */
	@GetMapping("/reportes/rankingVentas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Ranking de productos por ventas",
			description = "Retorna los productos ordenados por cantidad total vendida")
	public ResponseEntity<List<Object[]>> rankingVentas() {
		List<Object[]> resultado = productoServ.getProductosOrdenadosPorVentas();
		if (resultado.isEmpty()) {
			return new ResponseEntity<>(resultado, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(resultado, HttpStatus.ACCEPTED);
	}
}
