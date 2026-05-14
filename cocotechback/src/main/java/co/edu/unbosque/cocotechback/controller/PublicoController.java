/**
 * Paquete que contiene los controladores REST de la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.cocotechback.dto.CategoriaDTO;
import co.edu.unbosque.cocotechback.dto.ProductoDTO;
import co.edu.unbosque.cocotechback.service.CategoriaService;
import co.edu.unbosque.cocotechback.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST público que expone el catálogo del e-commerce SIN
 * autenticación.
 * <p>
 * Permite a los visitantes navegar productos y categorías antes de iniciar
 * sesión. El registro y el checkout sí requieren autenticación (se manejan
 * en {@code AuthController}, {@code ClienteController} y {@code PedidoController}).
 * <p>
 * Los endpoints filtran automáticamente productos inactivos (baja lógica) y
 * priorizan los marcados como "destacados" para la página principal.
 */
@RestController
@RequestMapping("/publico")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8080",
		"http://localhost:8081", "http://localhost:4200" })
@Transactional(readOnly = true)
@Tag(name = "Catálogo público",
		description = "Endpoints del catálogo accesibles sin autenticación")
public class PublicoController {

	/** Servicio de productos. */
	@Autowired
	private ProductoService productoServ;

	/** Servicio de categorías. */
	@Autowired
	private CategoriaService categoriaServ;

	/** Constructor por defecto. */
	public PublicoController() {
	}

	// ─── Productos ─────────────────────────────────────────────────────────

	/**
	 * Retorna todos los productos del catálogo.
	 * <p>
	 * Acceso público (sin JWT) para que visitantes anónimos puedan navegar
	 * el catálogo del e-commerce antes de iniciar sesión.
	 *
	 * @return 200 OK con la lista, o 204 si está vacía.
	 */
	@GetMapping("/producto/mostrarTodos")
	@Operation(summary = "Catálogo público de productos",
			description = "Retorna todos los productos activos del catálogo (sin requerir login)")
	public ResponseEntity<List<ProductoDTO>> mostrarTodos() {
		List<ProductoDTO> productos = productoServ.getAllActivos();
		if (productos.isEmpty()) {
			return new ResponseEntity<>(productos, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(productos, HttpStatus.OK);
	}

	/**
	 * Retorna un producto por su ID.
	 *
	 * @param id ID del producto.
	 * @return 200 OK con el DTO o 404 si no existe.
	 */
	@GetMapping("/producto/obtenerPorId/{id}")
	@Operation(summary = "Producto público por ID",
			description = "Retorna los datos de un producto específico del catálogo")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		ProductoDTO found = productoServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.OK);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Producto no encontrado", "success", false));
	}

	/**
	 * Busca productos por nombre (case-insensitive) y solo activos.
	 *
	 * @param q Texto a buscar dentro del nombre.
	 * @return 200 OK con coincidencias o 204 vacío.
	 */
	@GetMapping("/producto/buscar")
	@Operation(summary = "Búsqueda pública",
			description = "Busca productos del catálogo por término en el nombre")
	public ResponseEntity<List<ProductoDTO>> buscar(@RequestParam String q) {
		List<ProductoDTO> resultados = productoServ.buscarPorNombre(q);
		if (resultados.isEmpty()) {
			return new ResponseEntity<>(resultados, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(resultados, HttpStatus.OK);
	}

	/**
	 * Retorna los productos destacados (para la Home del e-commerce).
	 *
	 * @return 200 OK con la lista, o 204 si está vacía.
	 */
	@GetMapping("/producto/destacados")
	@Operation(summary = "Productos destacados",
			description = "Retorna los productos marcados como destacados para el Home")
	public ResponseEntity<List<ProductoDTO>> destacados() {
		List<ProductoDTO> resultados = productoServ.getDestacados();
		if (resultados.isEmpty()) {
			return new ResponseEntity<>(resultados, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(resultados, HttpStatus.OK);
	}

	/**
	 * Retorna productos activos de una categoría específica.
	 *
	 * @param idCategoria ID de la categoría.
	 * @return 200 OK con la lista, o 204 si está vacía.
	 */
	@GetMapping("/producto/porCategoria/{idCategoria}")
	@Operation(summary = "Productos por categoría",
			description = "Retorna los productos activos de una categoría")
	public ResponseEntity<List<ProductoDTO>> porCategoria(@PathVariable Long idCategoria) {
		List<ProductoDTO> resultados = productoServ.getProductosActivosPorCategoria(idCategoria);
		if (resultados.isEmpty()) {
			return new ResponseEntity<>(resultados, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(resultados, HttpStatus.OK);
	}

	// ─── Categorías ────────────────────────────────────────────────────────

	/**
	 * Retorna todas las categorías visibles públicamente.
	 *
	 * @return 200 OK con la lista, o 204 si está vacía.
	 */
	@GetMapping("/categoria/mostrarTodas")
	@Operation(summary = "Categorías públicas",
			description = "Retorna todas las categorías del catálogo")
	public ResponseEntity<List<CategoriaDTO>> mostrarTodas() {
		List<CategoriaDTO> categorias = categoriaServ.getAll();
		if (categorias.isEmpty()) {
			return new ResponseEntity<>(categorias, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(categorias, HttpStatus.OK);
	}
}