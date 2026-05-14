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

import co.edu.unbosque.cocotechback.dto.PedidoDTO;
import co.edu.unbosque.cocotechback.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de pedidos del e-commerce de CocoTech.
 * <p>
 * Expone los endpoints del ciclo de vida completo de un pedido online,
 * con control de acceso diferenciado por rol:
 * <ul>
 * <li><b>Cliente</b>: crea pedidos, consulta los suyos y los cancela
 * (mientras estén en estado RECIBIDO).</li>
 * <li><b>Empleado</b>: consulta los pedidos de su sucursal y avanza su
 * estado a lo largo del flujo de preparación y entrega.</li>
 * <li><b>Admin</b>: tiene visión global de todos los pedidos y puede
 * cambiar estados de cualquiera.</li>
 * </ul>
 * El control fino de acceso se complementa con las reglas declaradas en
 * {@code SecurityConfig}.
 */
@RestController
@RequestMapping("/pedido")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8080",
		"http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Pedidos",
		description = "Endpoints para el ciclo de vida de los pedidos del e-commerce")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

	/** Servicio con la lógica de negocio de los pedidos. */
	@Autowired
	private PedidoService pedidoServ;

	/** Constructor por defecto de {@code PedidoController}. */
	public PedidoController() {
	}

	/**
	 * Crea un nuevo pedido a partir del checkout de un cliente.
	 * <p>
	 * Valida el stock disponible de cada producto y lo decrementa de forma
	 * transaccional. El total se recalcula en el servidor.
	 *
	 * @param pedido El {@link PedidoDTO} con los datos del pedido y sus
	 *               detalles.
	 * @return {@code 201 Created} si fue exitoso,
	 *         {@code 404 Not Found} si el cliente, la sucursal o algún
	 *         producto no existen,
	 *         {@code 409 Conflict} si algún producto no tiene stock suficiente,
	 *         {@code 400 Bad Request} si faltan campos requeridos.
	 */
	@PostMapping("/crear")
	@PreAuthorize("hasRole('CLIENTE')")
	@Operation(summary = "Crear pedido",
			description = "Crea un nuevo pedido del e-commerce y descuenta el stock")
	public ResponseEntity<?> crear(@RequestBody PedidoDTO pedido) {
		int status = pedidoServ.create(pedido);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Pedido creado exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message",
							"Cliente, sucursal o producto no encontrados", "success", false));
		} else if (status == 3) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message",
							"Stock insuficiente para uno o más productos", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message",
							"Datos inválidos o el pedido no tiene productos", "success", false));
		}
	}

	/**
	 * Obtiene todos los pedidos del sistema (vista de administrador).
	 *
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/mostrarTodos")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar todos los pedidos",
			description = "Retorna todos los pedidos del sistema (solo administrador)")
	public ResponseEntity<List<PedidoDTO>> mostrarTodos() {
		List<PedidoDTO> pedidos = pedidoServ.getAll();
		if (pedidos.isEmpty()) {
			return new ResponseEntity<>(pedidos, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(pedidos, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene un pedido por su ID.
	 *
	 * @param id ID del pedido.
	 * @return {@code 202 Accepted} con el DTO, o {@code 404 Not Found}.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'ADMIN')")
	@Operation(summary = "Obtener pedido por ID",
			description = "Retorna el detalle completo de un pedido")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		PedidoDTO found = pedidoServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Pedido no encontrado", "success", false));
	}

	/**
	 * Obtiene los pedidos de un cliente específico.
	 *
	 * @param idCliente ID del cliente.
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/cliente/{idCliente}")
	@PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
	@Operation(summary = "Pedidos de un cliente",
			description = "Retorna el historial de pedidos de un cliente")
	public ResponseEntity<List<PedidoDTO>> obtenerPorCliente(@PathVariable Long idCliente) {
		List<PedidoDTO> pedidos = pedidoServ.getByCliente(idCliente);
		if (pedidos.isEmpty()) {
			return new ResponseEntity<>(pedidos, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(pedidos, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene los pedidos despachados por una sucursal específica.
	 * <p>
	 * Usado por los empleados para gestionar los pedidos de su sucursal.
	 *
	 * @param idSucursal ID de la sucursal de despacho.
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/sucursal/{idSucursal}")
	@PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
	@Operation(summary = "Pedidos de una sucursal",
			description = "Retorna los pedidos despachados por una sucursal")
	public ResponseEntity<List<PedidoDTO>> obtenerPorSucursal(@PathVariable Long idSucursal) {
		List<PedidoDTO> pedidos = pedidoServ.getBySucursal(idSucursal);
		if (pedidos.isEmpty()) {
			return new ResponseEntity<>(pedidos, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(pedidos, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene los pedidos que se encuentran en un estado específico.
	 *
	 * @param estado Estado del pedido (RECIBIDO, PREPARANDO, etc.).
	 * @return {@code 202 Accepted} con la lista, o {@code 204 No Content}.
	 */
	@GetMapping("/porEstado/{estado}")
	@PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
	@Operation(summary = "Pedidos por estado",
			description = "Retorna los pedidos que están en un estado dado")
	public ResponseEntity<List<PedidoDTO>> obtenerPorEstado(@PathVariable String estado) {
		List<PedidoDTO> pedidos = pedidoServ.getByEstado(estado);
		if (pedidos.isEmpty()) {
			return new ResponseEntity<>(pedidos, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(pedidos, HttpStatus.ACCEPTED);
	}

	/**
	 * Cambia el estado de un pedido, validando que la transición sea legal.
	 * <p>
	 * Al pasar un pedido a {@code ENTREGADO}, el sistema genera
	 * automáticamente la venta y la factura asociadas.
	 *
	 * @param id          ID del pedido a actualizar.
	 * @param nuevoEstado Nuevo estado del pedido.
	 * @param idEmpleado  ID del empleado que ejecuta el cambio (opcional;
	 *                    relevante al entregar para registrar la venta).
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 409 Conflict} si la transición de estado no es válida,
	 *         {@code 404 Not Found} si el pedido no existe,
	 *         {@code 400 Bad Request} si el estado no es un valor válido.
	 */
	@PutMapping("/cambiarEstado")
	@PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
	@Operation(summary = "Cambiar estado de un pedido",
			description = "Avanza el estado de un pedido validando el flujo permitido")
	public ResponseEntity<?> cambiarEstado(@RequestParam Long id,
			@RequestParam String nuevoEstado,
			@RequestParam(required = false) Long idEmpleado) {
		int status = pedidoServ.cambiarEstado(id, nuevoEstado, idEmpleado);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Estado del pedido actualizado", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message",
							"Transición de estado no permitida", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Pedido no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Estado no válido", "success", false));
		}
	}

	/**
	 * Cancela un pedido. Solo es posible si el pedido está en estado
	 * {@code RECIBIDO}; al cancelar se restituye el stock al inventario.
	 *
	 * @param id ID del pedido a cancelar.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 409 Conflict} si el pedido no está en estado RECIBIDO,
	 *         {@code 404 Not Found} si el pedido no existe.
	 */
	@DeleteMapping("/cancelar/{id}")
	@PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
	@Operation(summary = "Cancelar pedido",
			description = "Cancela un pedido en estado RECIBIDO y restituye el stock")
	public ResponseEntity<?> cancelar(@PathVariable Long id) {
		int status = pedidoServ.cancelar(id);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Pedido cancelado exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message",
							"Solo se pueden cancelar pedidos en estado RECIBIDO",
							"success", false));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Pedido no encontrado", "success", false));
		}
	}

	/**
	 * Retorna el conteo total de pedidos del sistema.
	 *
	 * @return {@code 202 Accepted} con el conteo.
	 */
	@GetMapping("/contar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Contar pedidos",
			description = "Retorna el número total de pedidos registrados")
	public ResponseEntity<Long> contar() {
		return new ResponseEntity<>(pedidoServ.count(), HttpStatus.ACCEPTED);
	}
}