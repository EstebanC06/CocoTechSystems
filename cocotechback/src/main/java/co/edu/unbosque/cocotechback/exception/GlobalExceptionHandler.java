/**
 * Paquete que contiene las clases para el manejo de excepciones y validaciones
 * de la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

/**
 * Manejador global de excepciones para la API REST de CocoTech.
 * <p>
 * Esta clase intercepta las excepciones no capturadas en los controladores y
 * servicios, y las transforma en respuestas HTTP estructuradas con un cuerpo
 * JSON consistente, evitando que el stack trace o mensajes internos lleguen
 * al cliente.
 * <p>
 * Todas las respuestas de error siguen el mismo formato JSON:
 * <pre>
 * {
 *   "timestamp": "2026-04-21T10:30:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Descripción del error para el cliente",
 *   "path": "/ruta/del/endpoint"
 * }
 * </pre>
 * <p>
 * Las excepciones cubiertas incluyen errores de autenticación JWT, acceso
 * denegado, integridad de datos en la base de datos, argumentos inválidos y
 * errores internos no esperados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Constructor por defecto de {@code GlobalExceptionHandler}.
	 */
	public GlobalExceptionHandler() {
	}

	// ─── Errores de autenticación y autorización ──────────────────────────────

	/**
	 * Maneja excepciones de acceso denegado lanzadas por Spring Security cuando
	 * un usuario autenticado intenta acceder a un recurso para el que no tiene
	 * el rol necesario.
	 * <p>
	 * Ejemplo: un cliente ({@code ROLE_CLIENTE}) intenta acceder a
	 * {@code /empleado/mostrarTodos} que requiere {@code ROLE_ADMIN}.
	 *
	 * @param ex      La excepción {@link AccessDeniedException} lanzada.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 403 Forbidden} con un mensaje de acceso denegado.
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
			AccessDeniedException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.FORBIDDEN,
				"Acceso denegado: no tienes permisos para acceder a este recurso",
				request);
	}

	/**
	 * Maneja excepciones de credenciales incorrectas durante el proceso de
	 * autenticación en {@code /auth/login}.
	 *
	 * @param ex      La excepción {@link BadCredentialsException} lanzada.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 401 Unauthorized} con un mensaje de credenciales inválidas.
	 */
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, Object>> handleBadCredentialsException(
			BadCredentialsException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED,
				"Correo electrónico o contraseña inválidos",
				request);
	}

	/**
	 * Maneja excepciones cuando el usuario no es encontrado durante la
	 * autenticación por Spring Security.
	 *
	 * @param ex      La excepción {@link UsernameNotFoundException} lanzada.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 401 Unauthorized} con un mensaje de usuario no encontrado.
	 */
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(
			UsernameNotFoundException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED,
				"Usuario no encontrado. Verifique su correo electrónico",
				request);
	}

	// ─── Errores de JWT ───────────────────────────────────────────────────────

	/**
	 * Maneja excepciones de token JWT expirado.
	 * <p>
	 * Ocurre cuando el cliente envía un token que ya superó su tiempo de validez
	 * (1 hora por defecto en CocoTech).
	 *
	 * @param ex      La excepción {@link ExpiredJwtException} lanzada por JJWT.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 401 Unauthorized} indicando que el token ha expirado.
	 */
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<Map<String, Object>> handleExpiredJwtException(
			ExpiredJwtException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED,
				"El token JWT ha expirado. Por favor inicia sesión nuevamente",
				request);
	}

	/**
	 * Maneja excepciones de token JWT con formato inválido o corrupto.
	 *
	 * @param ex      La excepción {@link MalformedJwtException} lanzada por JJWT.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 401 Unauthorized} indicando que el token es inválido.
	 */
	@ExceptionHandler(MalformedJwtException.class)
	public ResponseEntity<Map<String, Object>> handleMalformedJwtException(
			MalformedJwtException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED,
				"Token JWT inválido o con formato incorrecto",
				request);
	}

	/**
	 * Maneja excepciones de firma JWT inválida (token manipulado o firmado con
	 * una clave diferente).
	 *
	 * @param ex      La excepción {@link SignatureException} lanzada por JJWT.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 401 Unauthorized} indicando que la firma del token es inválida.
	 */
	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<Map<String, Object>> handleSignatureException(
			SignatureException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED,
				"Firma del token JWT inválida. El token puede haber sido manipulado",
				request);
	}

	// ─── Errores de base de datos ─────────────────────────────────────────────

	/**
	 * Maneja excepciones de violación de integridad de datos en la base de datos.
	 * <p>
	 * Se lanza cuando se intenta persistir datos que violan restricciones de la
	 * base de datos, como claves únicas (por ejemplo, intentar registrar un
	 * correo que ya existe), restricciones de clave foránea o valores nulos en
	 * columnas {@code NOT NULL}.
	 *
	 * @param ex      La excepción {@link DataIntegrityViolationException} lanzada
	 *                por Spring Data / JPA.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 409 Conflict} con un mensaje de conflicto de datos.
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
			DataIntegrityViolationException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.CONFLICT,
				"Conflicto de datos: el registro ya existe o viola una restricción de la base de datos",
				request);
	}

	// ─── Errores de argumentos y rutas ────────────────────────────────────────

	/**
	 * Maneja excepciones de tipo de argumento inválido en los parámetros de
	 * los endpoints.
	 * <p>
	 * Se lanza cuando, por ejemplo, se pasa una cadena de texto donde se espera
	 * un {@code Long} en una variable de ruta o parámetro de consulta
	 * (ej. {@code /cliente/obtenerPorId/abc}).
	 *
	 * @param ex      La excepción {@link MethodArgumentTypeMismatchException}
	 *                lanzada por Spring MVC.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 400 Bad Request} con un mensaje descriptivo del error.
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(
			MethodArgumentTypeMismatchException ex, WebRequest request) {
		String message = String.format(
				"El parámetro '%s' recibió un valor inválido: '%s'. Se esperaba un valor de tipo %s",
				ex.getName(),
				ex.getValue(),
				ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido");
		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
	}

	/**
	 * Maneja excepciones de argumento ilegal pasado a un método de servicio o
	 * repositorio.
	 * <p>
	 * Se lanza cuando se pasa un valor que no cumple con las precondiciones del
	 * método (ej. un ID negativo o un enum inválido).
	 *
	 * @param ex      La excepción {@link IllegalArgumentException} lanzada.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 400 Bad Request} con el mensaje de la excepción.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
			IllegalArgumentException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST,
				"Argumento inválido: " + ex.getMessage(),
				request);
	}

	/**
	 * Maneja excepciones de recurso no encontrado en las rutas de la API.
	 * <p>
	 * Se lanza cuando un cliente hace una petición a una URL que no existe en
	 * la API (ej. un endpoint mal escrito).
	 *
	 * @param ex      La excepción {@link NoResourceFoundException} lanzada por
	 *                Spring MVC.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 404 Not Found} indicando que la ruta no existe.
	 */
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(
			NoResourceFoundException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.NOT_FOUND,
				"La ruta solicitada no existe en la API de CocoTech",
				request);
	}

	/**
	 * Maneja excepciones de estado nulo inesperado en la aplicación.
	 * <p>
	 * Se lanza cuando se intenta operar sobre un objeto {@code null} en la capa
	 * de servicio (ej. una entidad relacionada no encontrada).
	 *
	 * @param ex      La excepción {@link NullPointerException} lanzada.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 500 Internal Server Error} con un mensaje genérico.
	 */
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<Map<String, Object>> handleNullPointerException(
			NullPointerException ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
				"Error interno: se encontró un valor nulo inesperado. Contacte al administrador",
				request);
	}

	// ─── Error genérico de último recurso ─────────────────────────────────────

	/**
	 * Manejador de último recurso para cualquier excepción no capturada por los
	 * manejadores específicos anteriores.
	 * <p>
	 * Garantiza que ninguna excepción interna llegue al cliente con stack traces
	 * o mensajes de error internos que puedan exponer información sensible del
	 * sistema.
	 *
	 * @param ex      La excepción {@link Exception} genérica no manejada.
	 * @param request La solicitud web que generó el error.
	 * @return {@code 500 Internal Server Error} con un mensaje genérico y seguro.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGenericException(
			Exception ex, WebRequest request) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
				"Error interno del servidor. Por favor intente nuevamente o contacte al administrador",
				request);
	}

	// ─── Método auxiliar ──────────────────────────────────────────────────────

	/**
	 * Construye una respuesta de error con el formato estándar de CocoTech.
	 * <p>
	 * El cuerpo JSON de la respuesta incluye los campos:
	 * <ul>
	 * <li>{@code timestamp} — Fecha y hora exacta del error.</li>
	 * <li>{@code status} — Código HTTP numérico del error.</li>
	 * <li>{@code error} — Nombre del estado HTTP (ej. "Not Found").</li>
	 * <li>{@code message} — Descripción del error para el cliente.</li>
	 * <li>{@code path} — Ruta del endpoint que generó el error.</li>
	 * </ul>
	 *
	 * @param status  El {@link HttpStatus} a retornar.
	 * @param message El mensaje descriptivo del error.
	 * @param request La solicitud web para extraer la ruta del endpoint.
	 * @return Un {@link ResponseEntity} con el cuerpo de error estructurado.
	 */
	private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status,
			String message, WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		body.put("path", request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(body, status);
	}
}
