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

import co.edu.unbosque.cocotechback.dto.EmpleadoDTO;
import co.edu.unbosque.cocotechback.service.EmpleadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión de empleados del supermercado.
 * <p>
 * Expone endpoints para realizar operaciones CRUD sobre la entidad
 * {@link co.edu.unbosque.cocotechback.model.Empleado}, así como operaciones
 * de gestión de credenciales.
 * <p>
 * Todos los endpoints de este controlador están restringidos a
 * {@code ROLE_ADMIN}, ya que la gestión de empleados es una operación
 * exclusivamente administrativa.
 */
@RestController
@RequestMapping("/empleado")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Gestión de Empleados", description = "Endpoints para la gestión de empleados del supermercado")
@SecurityRequirement(name = "bearerAuth")
public class EmpleadoController {

	/**
	 * Servicio para interactuar con la lógica de negocio de los empleados.
	 */
	@Autowired
	private EmpleadoService empleadoServ;

	/**
	 * Constructor por defecto de {@code EmpleadoController}.
	 */
	public EmpleadoController() {
	}

	/**
	 * Crea un nuevo empleado en el sistema.
	 * <p>
	 * Solo un administrador puede registrar nuevos empleados.
	 *
	 * @param empleado El {@link EmpleadoDTO} con los datos del nuevo empleado.
	 * @return {@code 201 Created} si fue creado exitosamente,
	 *         {@code 409 Conflict} si el correo ya está registrado,
	 *         {@code 404 Not Found} si la sucursal asignada no existe,
	 *         {@code 400 Bad Request} si los datos son inválidos.
	 */
	@PostMapping("/crear")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Crear empleado", description = "Registra un nuevo empleado en el sistema (solo ADMIN)")
	public ResponseEntity<?> crear(@RequestBody EmpleadoDTO empleado) {
		int status = empleadoServ.create(empleado, null);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("message", "Empleado creado exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El correo ya está registrado", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "La sucursal asignada no existe", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Datos inválidos o campos requeridos ausentes", "success", false));
		}
	}

	/**
	 * Obtiene todos los empleados registrados en el sistema.
	 *
	 * @return {@code 202 Accepted} con la lista de empleados, o
	 *         {@code 204 No Content} si no hay empleados.
	 */
	@GetMapping("/mostrarTodos")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Obtener todos los empleados", description = "Retorna la lista completa de empleados")
	public ResponseEntity<List<EmpleadoDTO>> mostrarTodos() {
		List<EmpleadoDTO> empleados = empleadoServ.getAll();
		if (empleados.isEmpty()) {
			return new ResponseEntity<>(empleados, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(empleados, HttpStatus.ACCEPTED);
	}

	/**
	 * Obtiene un empleado por su ID.
	 *
	 * @param id El ID del empleado a buscar, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con el empleado encontrado, o
	 *         {@code 404 Not Found} si no existe.
	 */
	@GetMapping("/obtenerPorId/{id}")
	@PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
	@Operation(summary = "Obtener empleado por ID", description = "Retorna los datos de un empleado por su ID")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		EmpleadoDTO found = empleadoServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "Empleado no encontrado", "success", false));
	}

	/**
	 * Verifica si existe un empleado con el ID especificado.
	 *
	 * @param id El ID del empleado, pasado como variable de ruta.
	 * @return {@code 202 Accepted} con {@code true} si existe, o
	 *         {@code 204 No Content} con {@code false} si no existe.
	 */
	@GetMapping("/existe/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Verificar existencia de empleado", description = "Indica si existe un empleado con el ID dado")
	public ResponseEntity<Boolean> existe(@PathVariable Long id) {
		boolean found = empleadoServ.exist(id);
		if (found) {
			return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
	}

	/**
	 * Retorna el total de empleados registrados en el sistema.
	 *
	 * @return {@code 202 Accepted} con el conteo, o {@code 204 No Content} si
	 *         no hay empleados.
	 */
	@GetMapping("/contar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Contar empleados", description = "Retorna el número total de empleados registrados")
	public ResponseEntity<Long> contarTodos() {
		Long count = empleadoServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
	}

	/**
	 * Actualiza los datos generales de un empleado existente.
	 *
	 * @param id       El ID del empleado, pasado como parámetro de consulta.
	 * @param empleado El {@link EmpleadoDTO} con los nuevos datos.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 409 Conflict} si el nuevo correo ya está en uso,
	 *         {@code 404 Not Found} si no existe el empleado o la sucursal,
	 *         {@code 400 Bad Request} si hay un error general.
	 */
	@PutMapping("/actualizar")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar empleado", description = "Actualiza los datos de un empleado existente")
	public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody EmpleadoDTO empleado) {
		int status = empleadoServ.updateById(id, empleado);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Empleado actualizado exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El nuevo correo ya está en uso", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Empleado o sucursal no encontrados", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar el empleado", "success", false));
		}
	}

	/**
	 * Actualiza la contraseña de un empleado.
	 *
	 * @param id              El ID del empleado, pasado como parámetro de consulta.
	 * @param nuevaContrasena La nueva contraseña en texto plano.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si la contraseña es inválida.
	 */
	@PutMapping("/actualizarContrasena")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar contraseña de empleado", description = "Actualiza la contraseña de un empleado")
	public ResponseEntity<?> actualizarContrasena(@RequestParam Long id,
			@RequestParam String nuevaContrasena) {
		EmpleadoDTO dto = new EmpleadoDTO();
		dto.setContrasena(nuevaContrasena);
		int status = empleadoServ.updatePassword(id, dto);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Contraseña actualizada exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Empleado no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Contraseña inválida o ausente", "success", false));
		}
	}

	/**
	 * Actualiza el correo electrónico de un empleado.
	 *
	 * @param id          El ID del empleado, pasado como parámetro de consulta.
	 * @param nuevoCorreo El nuevo correo electrónico.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 409 Conflict} si el correo ya está en uso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si el correo es inválido.
	 */
	@PutMapping("/actualizarCorreo")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar correo de empleado", description = "Actualiza el correo electrónico de un empleado")
	public ResponseEntity<?> actualizarCorreo(@RequestParam Long id,
			@RequestParam String nuevoCorreo) {
		EmpleadoDTO dto = new EmpleadoDTO();
		dto.setCorreo(nuevoCorreo);
		int status = empleadoServ.updateCorreo(id, dto);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Correo actualizado exitosamente", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("message", "El correo ya está en uso", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Empleado no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Correo inválido o ausente", "success", false));
		}
	}

	/**
	 * Actualiza el código de verificación de un empleado.
	 *
	 * @param id     El ID del empleado, pasado como parámetro de consulta.
	 * @param codigo El nuevo código de verificación.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si hay un error.
	 */
	@PutMapping("/actualizarCodigo")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar código de empleado", description = "Actualiza el código de verificación de un empleado")
	public ResponseEntity<?> actualizarCodigo(@RequestParam Long id, @RequestParam String codigo) {
		EmpleadoDTO dto = new EmpleadoDTO();
		dto.setCodigoVerificacion(codigo);
		int status = empleadoServ.updateCode(id, dto);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Código actualizado exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Empleado no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar el código", "success", false));
		}
	}

	/**
	 * Actualiza el rol de un empleado.
	 *
	 * @param id  El ID del empleado, pasado como parámetro de consulta.
	 * @param rol El nuevo rol a asignar.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 404 Not Found} si no existe,
	 *         {@code 400 Bad Request} si el rol es inválido.
	 */
	@PutMapping("/actualizarRol")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Actualizar rol de empleado", description = "Actualiza el rol de un empleado")
	public ResponseEntity<?> actualizarRol(@RequestParam Long id, @RequestParam String rol) {
		EmpleadoDTO dto = new EmpleadoDTO();
		try {
			dto.setRol(co.edu.unbosque.cocotechback.model.Usuario.Rol.valueOf(rol));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Rol inválido", "success", false));
		}
		int status = empleadoServ.updateRol(id, dto);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Rol actualizado exitosamente", "success", true));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Empleado no encontrado", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error al actualizar el rol", "success", false));
		}
	}

	/**
	 * Elimina un empleado del sistema por su ID.
	 *
	 * @param id El ID del empleado a eliminar, pasado como variable de ruta.
	 * @return {@code 202 Accepted} si fue eliminado exitosamente, o
	 *         {@code 404 Not Found} si no existe.
	 */
	@DeleteMapping("/eliminar/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Eliminar empleado", description = "Elimina un empleado del sistema por su ID")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int status = empleadoServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Empleado eliminado exitosamente", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Empleado no encontrado", HttpStatus.NOT_FOUND);
	}
}