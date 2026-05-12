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

import co.edu.unbosque.cocotechback.dto.ClienteDTO;
import co.edu.unbosque.cocotechback.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de clientes del supermercado.
 * <p>
 * Expone endpoints para realizar operaciones CRUD sobre la entidad
 * {@link co.edu.unbosque.cocotechback.model.Cliente}, así como operaciones
 * específicas de gestión de credenciales (contraseña, correo, código de
 * verificación).
 * <p>
 * Los endpoints de lectura y gestión de perfil propio son accesibles tanto
 * para {@code ROLE_CLIENTE} como para {@code ROLE_ADMIN}. Las operaciones
 * administrativas (eliminar, actualizar rol) están restringidas a
 * {@code ROLE_ADMIN}.
 */
@RestController
@RequestMapping("/cliente")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Clientes", description = "Endpoints para la gestión de clientes del supermercado")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

	/**
	 * Servicio para interactuar con la lógica de negocio de los clientes.
	 */
	@Autowired
	private ClienteService clienteServ;

	/**
	 * Constructor por defecto de {@code ClienteController}.
	 */
	public ClienteController() {
	}

	/**
	 * Crea un nuevo cliente en el sistema.
	 * <p>
	 * Endpoint público, accesible sin autenticación para permitir el registro
	 * de nuevos clientes.
	 *
	 * @param cliente El {@link ClienteDTO} con los datos del nuevo cliente en el
	 *                cuerpo de la solicitud.
	 * @return {@code 201 Created} si el cliente fue creado exitosamente,
	 *         {@code 409 Conflict} si el correo ya está registrado,
	 *         {@code 400 Bad Request} si los datos son inválidos.
	 */
	@PostMapping("/crear")
	@Operation(summary = "Crear cliente", description = "Registra un nuevo cliente en el sistema")
	public ResponseEntity<?> crear(@RequestBody ClienteDTO cliente) {
		int status = clienteServ.create(cliente, null);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Cliente creado exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El correo ya está registrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Datos inválidos o campos requeridos ausentes", "success", false));
		}
	}

	/**
	 * Obtiene todos los clientes registrados en el sistema.
	 * <p>
	 * Accesible solo para {@code ROLE_ADMIN}.
	 *
	 * @return {@code 202 Accepted} con la lista de clientes, o
	 *         {@code 204 No Content} si no hay clientes registrados.
	 */
	@GetMapping("/mostrarTodos")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obtener todos los clientes", description = "Retorna la lista completa de clientes (solo ADMIN)")
	public ResponseEntity<List<ClienteDTO>> mostrarTodos() {
		List<ClienteDTO> clientes = clienteServ.getAll();
		if (clientes.isEmpty()) {
			return new ResponseEntity<>(clientes, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(clientes, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene un cliente por su ID.
	 * <p>
	 * Accesible para {@code ROLE_ADMIN} y {@code ROLE_CLIENTE}.
	 *
	 * @param id El ID del cliente a buscar, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con el cliente encontrado, o
	 *         {@code 404 Not Found} si no existe.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Obtener cliente por ID", description = "Retorna los datos de un cliente por su ID")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		ClienteDTO found = clienteServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Cliente no encontrado", "success", false));
	}

	/**
	 * Verifica si existe un cliente con el ID especificado.
	 *
	 * @param id El ID del cliente, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con {@code true} si existe, o
	 *         {@code 204 No Content} con {@code false} si no existe.
	 */
	@GetMapping("/existe/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Verificar existencia de cliente", description = "Indica si existe un cliente con el ID dado")
	public ResponseEntity<Boolean> existe(@PathVariable Long id) {
		boolean found = clienteServ.exist(id);
		if (found) {
			return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
	}

	/**
	 * Retorna el total de clientes registrados en el sistema.
	 *
	 * @return {@code 202 Accepted} con el conteo, o {@code 204 No Content} si
	 *         no hay clientes.
	 */
	@GetMapping("/contar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Contar clientes", description = "Retorna el número total de clientes registrados")
	public ResponseEntity<Long> contarTodos() {
		Long count = clienteServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
	}

	/**
	 * Actualiza los datos generales de un cliente existente.
	 * <p>
	 * Solo actualiza los campos no nulos del DTO recibido.
	 *
	 * @param id      El ID del cliente a actualizar, pasado como parámetro de
	 *                consulta.
	 * @param cliente El {@link ClienteDTO} con los nuevos datos.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 409 Conflict} si el nuevo correo ya está en uso,
	 *         {@code 404 Not Found} si no existe el cliente,
	 *         {@code 400 Bad Request} si hay un error general.
	 */
	@PutMapping("/actualizar")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente")
	public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody ClienteDTO cliente) {
		int status = clienteServ.updateById(id, cliente);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Cliente actualizado exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El nuevo correo ya está en uso", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Cliente no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar el cliente", "success", false));
		}
	}

	/**
	 * Actualiza la contraseña de un cliente.
	 *
	 * @param id          El ID del cliente, pasado como parámetro de consulta.
	 * @param nuevaContrasena La nueva contraseña en texto plano.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si la contraseña es inválida.
	 */
	@PutMapping("/actualizarContrasena")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Actualizar contraseña", description = "Actualiza la contraseña de un cliente")
	public ResponseEntity<?> actualizarContrasena(@RequestParam Long id,
			@RequestParam String nuevaContrasena) {
		ClienteDTO dto = new ClienteDTO();
		dto.setContrasena(nuevaContrasena);
		int status = clienteServ.updatePassword(id, dto);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Contraseña actualizada exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Cliente no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Contraseña inválida o ausente", "success", false));
		}
	}

	/**
	 * Actualiza el correo electrónico de un cliente.
	 *
	 * @param id        El ID del cliente, pasado como parámetro de consulta.
	 * @param nuevoCorreo El nuevo correo electrónico.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 409 Conflict} si el correo ya está en uso,
	 *         {@code 404 Not Found} si no existe el cliente,
	 *         {@code 400 Bad Request} si el correo es inválido.
	 */
	@PutMapping("/actualizarCorreo")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Actualizar correo", description = "Actualiza el correo electrónico de un cliente")
	public ResponseEntity<?> actualizarCorreo(@RequestParam Long id,
			@RequestParam String nuevoCorreo) {
		ClienteDTO dto = new ClienteDTO();
		dto.setCorreo(nuevoCorreo);
		int status = clienteServ.updateCorreo(id, dto);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Correo actualizado exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El correo ya está en uso", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Cliente no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Correo inválido o ausente", "success", false));
		}
	}

	/**
	 * Actualiza el código de verificación de un cliente.
	 *
	 * @param id     El ID del cliente, pasado como parámetro de consulta.
	 * @param codigo El nuevo código de verificación.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si hay un error.
	 */
	@PutMapping("/actualizarCodigo")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	@Operation(summary = "Actualizar código de verificación", description = "Actualiza el código de verificación de un cliente")
	public ResponseEntity<?> actualizarCodigo(@RequestParam Long id, @RequestParam String codigo) {
		ClienteDTO dto = new ClienteDTO();
		dto.setCodigoVerificacion(codigo);
		int status = clienteServ.updateCode(id, dto);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Código actualizado exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Cliente no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar el código", "success", false));
		}
	}

	/**
	 * Actualiza el rol de un cliente.
	 * <p>
	 * Accesible solo para {@code ROLE_ADMIN}.
	 *
	 * @param id  El ID del cliente, pasado como parámetro de consulta.
	 * @param rol El nuevo rol a asignar.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si hay un error.
	 */
	@PutMapping("/actualizarRol")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar rol de cliente", description = "Actualiza el rol de un cliente (solo ADMIN)")
	public ResponseEntity<?> actualizarRol(@RequestParam Long id, @RequestParam String rol) {
		ClienteDTO dto = new ClienteDTO();
		try {
			dto.setRol(co.edu.unbosque.cocotechback.model.Usuario.Rol.valueOf(rol));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Rol inválido", "success", false));
		}
		int status = clienteServ.updateRol(id, dto);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Rol actualizado exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Cliente no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar el rol", "success", false));
		}
	}

	/**
	 * Elimina un cliente del sistema por su ID.
	 * <p>
	 * Accesible solo para {@code ROLE_ADMIN}.
	 *
	 * @param id El ID del cliente a eliminar, pasado como variable de ruta.
	 * @return {@code 202 Accepted} si fue eliminado exitosamente, o
	 *         {@code 404 Not Found} si no existe el cliente.
	 */
	@DeleteMapping("/eliminar/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema por su ID (solo ADMIN)")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int status = clienteServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Cliente eliminado exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Cliente no encontrado", HttpStatus.NOT_FOUND);
	}
}
