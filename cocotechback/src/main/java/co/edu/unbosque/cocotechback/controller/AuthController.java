/**
 * Paquete que contiene los controladores REST de la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.cocotechback.dto.ClienteDTO;
import co.edu.unbosque.cocotechback.dto.EmpleadoDTO;
import co.edu.unbosque.cocotechback.model.Cliente;
import co.edu.unbosque.cocotechback.model.Empleado;
import co.edu.unbosque.cocotechback.security.JwtUtil;
import co.edu.unbosque.cocotechback.service.ClienteService;
import co.edu.unbosque.cocotechback.service.EmpleadoService;
import co.edu.unbosque.cocotechback.util.AESUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la autenticación de usuarios en el sistema CocoTech.
 * <p>
 * Expone endpoints públicos (sin requerir token JWT) para el inicio de sesión
 * de ambos tipos de usuario del sistema:
 * <ul>
 * <li><strong>Clientes</strong> ({@code ROLE_CLIENTE}) — Pueden hacer login
 * y recuperar su contraseña.</li>
 * <li><strong>Empleados</strong> ({@code ROLE_ADMIN}) — Pueden hacer login
 * y recuperar su contraseña.</li>
 * </ul>
 * <p>
 * El endpoint {@code /auth/login} intenta autenticar primero como empleado.
 * Si no es un empleado registrado, intenta autenticar como cliente. El token
 * JWT generado incluye el rol del usuario para que el frontend pueda
 * redirigir a la interfaz correspondiente.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:4200" })
@Transactional
@Tag(name = "Autenticación", description = "Endpoints públicos para login y recuperación de contraseña")
public class AuthController {

	/**
	 * Gestor de autenticación de Spring Security. Verifica las credenciales
	 * del usuario contra la base de datos usando BCrypt.
	 */
	private final AuthenticationManager authenticationManager;

	/**
	 * Utilidad para la generación de tokens JWT tras una autenticación exitosa.
	 */
	private final JwtUtil jwtUtil;

	/**
	 * Servicio de clientes, usado para la recuperación de contraseña de
	 * clientes.
	 */
	@Autowired
	private ClienteService clienteService;

	/**
	 * Servicio de empleados, usado para la recuperación de contraseña de
	 * empleados.
	 */
	@Autowired
	private EmpleadoService empleadoService;

	/**
	 * Constructor con inyección de dependencias obligatorias.
	 *
	 * @param authenticationManager El gestor de autenticación de Spring Security.
	 * @param jwtUtil               La utilidad para generación de tokens JWT.
	 */
	public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	/**
	 * Endpoint para el inicio de sesión de clientes y empleados.
	 * <p>
	 * Recibe un JSON con {@code correo} y {@code contrasena}. Encripta el correo
	 * con AES para buscarlo en la base de datos, autentica las credenciales con
	 * Spring Security y, si es exitoso, genera un token JWT que incluye el rol
	 * del usuario ({@code ROLE_CLIENTE} o {@code ROLE_ADMIN}).
	 * <p>
	 * El frontend debe usar este token en el encabezado
	 * {@code Authorization: Bearer <token>} para todas las peticiones
	 * subsiguientes a endpoints protegidos.
	 *
	 * @param loginRequest Un {@link LoginRequest} con el correo en texto plano
	 *                     y la contraseña del usuario.
	 * @return {@code 200 OK} con el token JWT y el rol del usuario si las
	 *         credenciales son válidas, o {@code 401 Unauthorized} si no lo son.
	 */
	@PostMapping("/login")
	@Operation(summary = "Iniciar sesión",
			description = "Autentica a un cliente o empleado y retorna un token JWT")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		try {
			// Encriptamos el correo para buscarlo en BD (donde está almacenado cifrado)
			String correoEncriptado = AESUtil.encrypt(loginRequest.getCorreo());

			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							correoEncriptado, loginRequest.getContrasena()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();

			// Generamos el token incluyendo el correo en texto plano para el frontend
			String jwt = jwtUtil.generateToken(userDetails, loginRequest.getCorreo());

			// Determinamos rol e ID según el tipo de usuario autenticado
						String rol = null;
						Long id = null;
						if (userDetails instanceof Empleado) {
							Empleado emp = (Empleado) userDetails;
							rol = emp.getRol().name();
							id = emp.getId();
						} else if (userDetails instanceof Cliente) {
							Cliente cli = (Cliente) userDetails;
							rol = cli.getRol().name();
							id = cli.getId();
						}

						return ResponseEntity.ok(new AuthResponse(jwt, loginRequest.getCorreo(), rol, id));
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Correo o contraseña inválidos", "success", false));
		}
	}

	/**
	 * Endpoint para que un cliente recupere su contraseña.
	 * <p>
	 * Busca al cliente por correo electrónico y actualiza su contraseña con la
	 * nueva proporcionada (codificada con BCrypt).
	 *
	 * @param correo          El correo electrónico del cliente en texto plano.
	 * @param nuevaContrasena La nueva contraseña en texto plano.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 404 Not Found} si el cliente no existe,
	 *         {@code 400 Bad Request} si la contraseña es inválida.
	 */
	@PutMapping("/recuperarContrasenaCliente")
	@Operation(summary = "Recuperar contraseña de cliente",
			description = "Actualiza la contraseña de un cliente por su correo")
	public ResponseEntity<?> recuperarContrasenaCliente(@RequestParam String correo,
			@RequestParam String nuevaContrasena) {
		ClienteDTO dto = new ClienteDTO();
		dto.setCorreo(correo);
		dto.setContrasena(nuevaContrasena);
		int status = clienteService.rememberPassword(dto);
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
	 * Endpoint para que un empleado recupere su contraseña.
	 * <p>
	 * Busca al empleado por correo electrónico y actualiza su contraseña con la
	 * nueva proporcionada (codificada con BCrypt).
	 *
	 * @param correo          El correo electrónico del empleado en texto plano.
	 * @param nuevaContrasena La nueva contraseña en texto plano.
	 * @return {@code 202 Accepted} si fue exitoso,
	 *         {@code 404 Not Found} si el empleado no existe,
	 *         {@code 400 Bad Request} si la contraseña es inválida.
	 */
	@PutMapping("/recuperarContrasenaEmpleado")
	@Operation(summary = "Recuperar contraseña de empleado",
			description = "Actualiza la contraseña de un empleado por su correo")
	public ResponseEntity<?> recuperarContrasenaEmpleado(@RequestParam String correo,
			@RequestParam String nuevaContrasena) {
		EmpleadoDTO dto = new EmpleadoDTO();
		dto.setCorreo(correo);
		dto.setContrasena(nuevaContrasena);
		int status = empleadoService.rememberPassword(dto);
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

	// ─── Clases internas ──────────────────────────────────────────────────────

	/**
	 * Clase que representa el cuerpo de la solicitud de login.
	 * <p>
	 * El frontend debe enviar un JSON con los campos {@code correo} y
	 * {@code contrasena}.
	 */
	public static class LoginRequest {

		/** Correo electrónico del usuario en texto plano. */
		private String correo;

		/** Contraseña del usuario en texto plano. */
		private String contrasena;

		/** Constructor por defecto. */
		public LoginRequest() {
		}

		/**
		 * Obtiene el correo electrónico del usuario.
		 *
		 * @return El correo electrónico.
		 */
		public String getCorreo() {
			return correo;
		}

		/**
		 * Establece el correo electrónico del usuario.
		 *
		 * @param correo El nuevo correo electrónico.
		 */
		public void setCorreo(String correo) {
			this.correo = correo;
		}

		/**
		 * Obtiene la contraseña del usuario.
		 *
		 * @return La contraseña.
		 */
		public String getContrasena() {
			return contrasena;
		}

		/**
		 * Establece la contraseña del usuario.
		 *
		 * @param contrasena La nueva contraseña.
		 */
		public void setContrasena(String contrasena) {
			this.contrasena = contrasena;
		}
	}

	/**
	 * Clase que representa la respuesta del endpoint de login.
	 * <p>
	 * Contiene el token JWT generado, el rol del usuario autenticado
	 * ({@code ROLE_CLIENTE}, {@code ROLE_EMPLEADO} o {@code ROLE_ADMIN}),
	 * y los datos básicos del usuario que el frontend necesita para
	 * cargar su perfil sin un round-trip adicional.
	 */
	public static class AuthResponse {

		/** Token JWT generado para la sesión del usuario. */
		private final String token;

		/** Tipo de token (siempre "Bearer"). */
		private final String tipo;

		/** Correo del usuario autenticado (texto plano). */
		private final String correo;

		/** Rol del usuario autenticado. */
		private final String rol;

		/** ID del usuario autenticado (cliente o empleado). */
		private final Long id;

		/**
		 * Constructor completo de la respuesta de autenticación.
		 *
		 * @param token  El token JWT generado.
		 * @param correo El correo del usuario en texto plano.
		 * @param rol    El rol del usuario autenticado.
		 * @param id     El ID del usuario autenticado.
		 */
		public AuthResponse(String token, String correo, String rol, Long id) {
			this.token = token;
			this.tipo = "Bearer";
			this.correo = correo;
			this.rol = rol;
			this.id = id;
		}

		/**
		 * Obtiene el token JWT.
		 *
		 * @return El token JWT.
		 */
		public String getToken() {
			return token;
		}

		/**
		 * Obtiene el tipo de token.
		 *
		 * @return Siempre "Bearer".
		 */
		public String getTipo() {
			return tipo;
		}

		/**
		 * Obtiene el correo del usuario autenticado.
		 *
		 * @return El correo en texto plano.
		 */
		public String getCorreo() {
			return correo;
		}

		/**
		 * Obtiene el rol del usuario autenticado.
		 *
		 * @return El rol del usuario.
		 */
		public String getRol() {
			return rol;
		}

		/**
		 * Obtiene el ID del usuario autenticado.
		 *
		 * @return El ID (idCliente o idEmpleado).
		 */
		public Long getId() {
			return id;
		}
	}
}
