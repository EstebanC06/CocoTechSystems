/**
 * Paquete que contiene las clases de configuración de la aplicación CocoTech
 * backend.
 */
package co.edu.unbosque.cocotechback.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Clase de configuración para la generación de la documentación interactiva
 * de la API REST de CocoTech mediante OpenAPI 3 / Swagger UI.
 * <p>
 * Define el bean {@link OpenAPI} que personaliza la información general del
 * sistema, el esquema de seguridad JWT ({@code bearerAuth}) y las respuestas
 * HTTP globales más comunes, facilitando la exploración y prueba de los
 * endpoints desde la interfaz Swagger UI disponible en
 * {@code /swagger-ui/index.html}.
 */
@Configuration
public class OpenApiConfig {

	/**
	 * Constructor por defecto de {@code OpenApiConfig}.
	 */
	public OpenApiConfig() {
	}

	/**
	 * Define y configura el objeto {@link OpenAPI} con la documentación completa
	 * de la API de CocoTech.
	 * <p>
	 * La configuración incluye:
	 * <ul>
	 * <li>Información general del proyecto: título, versión, descripción,
	 * contacto del equipo y licencia.</li>
	 * <li>Guía de uso para principiantes: explica el flujo de autenticación JWT,
	 * los roles disponibles y los códigos HTTP más comunes.</li>
	 * <li>Esquema de seguridad {@code bearerAuth} para que Swagger UI permita
	 * ingresar el token JWT y autenticar las peticiones directamente desde la
	 * interfaz de documentación.</li>
	 * <li>Respuestas globales reutilizables: {@code UnauthorizedError},
	 * {@code ForbiddenError} y {@code NotFoundError}.</li>
	 * </ul>
	 *
	 * @return Un objeto {@link OpenAPI} configurado con toda la información y
	 *         definiciones de seguridad de la API de CocoTech.
	 */
	@Bean
	public OpenAPI customOpenAPI() {

		String mainDescription = "<h2>Guía de uso de la API REST de CocoTech</h2>"
				+ "<p>Esta API gestiona el sistema de base de datos de un supermercado: "
				+ "productos, clientes, empleados, ventas, facturas, sucursales y más.</p>"
				+ "<h3>Conceptos básicos:</h3><ul>"
				+ "<li><strong>JWT (JSON Web Token)</strong>: Cuando inicias sesión en "
				+ "<code>/auth/login</code>, recibirás un token que debes incluir en todas "
				+ "las peticiones posteriores.</li>"
				+ "<li><strong>Autenticación</strong>: Verificación de identidad mediante "
				+ "correo electrónico y contraseña.</li>"
				+ "<li><strong>Autorización</strong>: Determina qué operaciones puede realizar "
				+ "cada usuario según su rol.</li></ul>"
				+ "<h3>Flujo básico de uso:</h3><ol>"
				+ "<li>Regístrate como cliente usando <code>/cliente/crear</code> (público)</li>"
				+ "<li>Inicia sesión con <code>/auth/login</code> para obtener un token JWT</li>"
				+ "<li>Haz clic en el botón <strong>Authorize</strong> en esta página e ingresa: "
				+ "<code>Bearer tu_token_jwt</code></li>"
				+ "<li>Ahora puedes usar todos los endpoints disponibles para tu rol</li></ol>"
				+ "<h3>Roles del sistema:</h3><ul>"
				+ "<li><strong>ROLE_CLIENTE</strong>: Consulta el catálogo, gestiona su "
				+ "carrito, crea pedidos y consulta su historial de compras y facturas "
				+ "propias.</li>"
				+ "<li><strong>ROLE_EMPLEADO</strong>: Gestiona los pedidos del e-commerce "
				+ "de su sucursal, opera el punto de venta físico y consulta el inventario "
				+ "en modo lectura.</li>"
				+ "<li><strong>ROLE_ADMIN</strong>: Acceso total — gestión de inventario, "
				+ "empleados, proveedores, sucursales, ventas y reportes analíticos "
				+ "(incluyendo los reportes MongoDB).</li></ul>"
				+ "<h3>Códigos de estado HTTP más comunes:</h3><ul>"
				+ "<li><strong>201</strong>: Recurso creado exitosamente</li>"
				+ "<li><strong>202</strong>: Operación exitosa con datos retornados</li>"
				+ "<li><strong>204</strong>: Operación exitosa sin datos (lista vacía)</li>"
				+ "<li><strong>400</strong>: Datos inválidos o campos requeridos ausentes</li>"
				+ "<li><strong>401</strong>: No autenticado (token inválido o expirado)</li>"
				+ "<li><strong>403</strong>: Acceso denegado (rol insuficiente)</li>"
				+ "<li><strong>404</strong>: Recurso no encontrado</li>"
				+ "<li><strong>409</strong>: Conflicto de datos (registro duplicado o "
				+ "restricción violada)</li></ul>";

		String securityDescription = "Autenticación mediante JWT (JSON Web Token)."
				+ "<p>Para autenticarte en Swagger UI, sigue estos pasos:</p>"
				+ "<ol>"
				+ "<li>Usa el endpoint <code>/auth/login</code> con tu correo y contraseña</li>"
				+ "<li>Copia el valor del campo <code>token</code> de la respuesta</li>"
				+ "<li>Haz clic en el botón <strong>Authorize</strong> en la parte superior</li>"
				+ "<li>En el campo <strong>Value</strong>, escribe: "
				+ "<code>Bearer tu_token_jwt</code></li>"
				+ "<li>Haz clic en <strong>Authorize</strong> y luego en <strong>Close</strong></li>"
				+ "</ol>"
				+ "<p>A partir de ese momento todas las peticiones incluirán el token "
				+ "automáticamente.</p>";

		Info info = new Info()
				.title("CocoTech Systems — API de Gestión de Supermercado")
				.version("1.0.0")
				.description(mainDescription)
				.contact(new Contact()
						.name("CocoTech Systems — Equipo de Desarrollo")
						.email("soporte@cocotech.com")
						.url("https://github.com/cocotech-systems"))
				.license(new License()
						.name("Licencia MIT")
						.url("https://opensource.org/licenses/MIT"));

		SecurityScheme securityScheme = new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT")
				.description(securityDescription);

		return new OpenAPI()
				.info(info)
				.components(new Components()
						.addSecuritySchemes("bearerAuth", securityScheme)
						.addResponses("UnauthorizedError",
								new ApiResponse()
										.description("No autenticado — Token JWT inválido, "
												+ "expirado o ausente")
										.content(new Content().addMediaType("application/json",
												new MediaType().addExamples("error",
														new Example().value(
																"{\"timestamp\": \"2026-04-21T10:30:00\","
																+ " \"status\": 401,"
																+ " \"error\": \"Unauthorized\","
																+ " \"message\": \"El token JWT ha expirado\","
																+ " \"path\": \"/producto/mostrarTodos\"}")))))
						.addResponses("ForbiddenError",
								new ApiResponse()
										.description("Acceso denegado — Rol insuficiente para "
												+ "este recurso")
										.content(new Content().addMediaType("application/json",
												new MediaType().addExamples("error",
														new Example().value(
																"{\"timestamp\": \"2026-04-21T10:30:00\","
																+ " \"status\": 403,"
																+ " \"error\": \"Forbidden\","
																+ " \"message\": \"Acceso denegado: no tienes permisos\","
																+ " \"path\": \"/empleado/mostrarTodos\"}")))))
						.addResponses("NotFoundError",
								new ApiResponse()
										.description("Recurso no encontrado")
										.content(new Content().addMediaType("application/json",
												new MediaType().addExamples("error",
														new Example().value(
																"{\"timestamp\": \"2026-04-21T10:30:00\","
																+ " \"status\": 404,"
																+ " \"error\": \"Not Found\","
																+ " \"message\": \"Producto no encontrado\","
																+ " \"path\": \"/producto/obtenerPorId/99\"}"))))));
	}
}