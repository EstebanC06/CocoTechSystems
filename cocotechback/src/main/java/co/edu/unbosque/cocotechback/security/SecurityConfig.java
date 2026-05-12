/**
 * Paquete que contiene las clases relacionadas con la seguridad de la
 * aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración central de seguridad para la aplicación CocoTech backend.
 * <p>
 * Esta clase configura la autenticación JWT sin estado (stateless), la
 * autorización por roles, CORS y el codificador de contraseñas BCrypt.
 * <p>
 * La anotación {@code @EnableMethodSecurity} activa el uso de
 * {@code @PreAuthorize} en los controladores, que es el mecanismo primario
 * de control de acceso por rol en este proyecto (más específico y explícito
 * que el {@code authorizeHttpRequests} global).
 * <p>
 * Estrategia de autorización:
 * <ul>
 * <li>{@code /auth/**} — Público: login y registro de clientes.</li>
 * <li>{@code /swagger-ui/**} y {@code /v3/api-docs/**} — Público: documentación
 * de la API.</li>
 * <li>{@code /cliente/crear} — Público: registro de nuevos clientes.</li>
 * <li>{@code /categoria/mostrarTodas} y {@code /producto/mostrarTodos} —
 * Accesibles para clientes autenticados.</li>
 * <li>Todo lo demás — Requiere autenticación; el control fino por rol se
 * delega a {@code @PreAuthorize} en cada controlador.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	/**
	 * Filtro JWT que intercepta cada petición para validar el token Bearer.
	 */
	private final JwtAuthenticationFilter jwtAuthFilter;

	/**
	 * Servicio que carga los detalles del usuario (Empleado o Cliente) desde
	 * la base de datos durante la autenticación.
	 */
	private final UserDetailsService userDetailsService;

	/**
	 * Constructor con inyección de dependencias.
	 *
	 * @param jwtAuthFilter      El filtro de autenticación JWT.
	 * @param userDetailsService El servicio de detalles de usuario.
	 */
	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
			UserDetailsService userDetailsService) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Configura la cadena de filtros de seguridad para las peticiones HTTP.
	 * <p>
	 * Deshabilita CSRF (innecesario en APIs REST stateless con JWT), configura
	 * CORS, define reglas de autorización por ruta y establece la política de
	 * sesión como {@code STATELESS}. Registra el filtro JWT antes del filtro
	 * estándar de autenticación de Spring Security.
	 *
	 * @param http El objeto {@link HttpSecurity} para construir la configuración.
	 * @return El {@link SecurityFilterChain} construido.
	 * @throws Exception Si ocurre algún error durante la configuración.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(auth -> {
					// ── Endpoints públicos ────────────────────────────────────
					// Autenticación (login de clientes y empleados)
					auth.requestMatchers("/auth/**").permitAll();
					// Swagger / OpenAPI
					auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
							"/swagger-ui.html").permitAll();
					// Registro de clientes (no requiere token)
					auth.requestMatchers("/cliente/crear").permitAll();

					// ── Endpoints para ROLE_CLIENTE y ROLE_ADMIN ─────────────
					// Catálogo de productos y categorías (solo lectura)
					auth.requestMatchers("/producto/mostrarTodos").hasAnyRole("CLIENTE", "ADMIN");
					auth.requestMatchers("/producto/obtenerPorId/**").hasAnyRole("CLIENTE", "ADMIN");
					auth.requestMatchers("/categoria/mostrarTodas").hasAnyRole("CLIENTE", "ADMIN");
					auth.requestMatchers("/categoria/obtenerPorId/**").hasAnyRole("CLIENTE", "ADMIN");
					// Historial propio (venta y factura)
					auth.requestMatchers("/venta/obtenerPorId/**").hasAnyRole("CLIENTE", "ADMIN");
					auth.requestMatchers("/factura/obtenerPorId/**").hasAnyRole("CLIENTE", "ADMIN");
					auth.requestMatchers("/factura/obtenerPorVenta/**")
							.hasAnyRole("CLIENTE", "ADMIN");
					auth.requestMatchers("/detalleVenta/obtenerPorId/**")
							.hasAnyRole("CLIENTE", "ADMIN");
					// Gestión del propio perfil de cliente
					auth.requestMatchers("/cliente/obtenerPorId/**")
							.hasAnyRole("CLIENTE", "ADMIN");
					auth.requestMatchers("/cliente/actualizar").hasAnyRole("CLIENTE", "ADMIN");
					auth.requestMatchers("/cliente/actualizarContrasena")
							.hasAnyRole("CLIENTE", "ADMIN");
					auth.requestMatchers("/cliente/actualizarCorreo")
							.hasAnyRole("CLIENTE", "ADMIN");
					auth.requestMatchers("/cliente/actualizarCodigo")
							.hasAnyRole("CLIENTE", "ADMIN");

					// ── Todo lo demás requiere ROLE_ADMIN ─────────────────────
					auth.requestMatchers("/empleado/**").hasRole("ADMIN");
					auth.requestMatchers("/sucursal/**").hasRole("ADMIN");
					auth.requestMatchers("/caja/**").hasRole("ADMIN");
					auth.requestMatchers("/proveedor/**").hasRole("ADMIN");
					auth.requestMatchers("/producto/**").hasRole("ADMIN");
					auth.requestMatchers("/categoria/**").hasRole("ADMIN");
					auth.requestMatchers("/venta/**").hasRole("ADMIN");
					auth.requestMatchers("/detalleVenta/**").hasRole("ADMIN");
					auth.requestMatchers("/factura/**").hasRole("ADMIN");
					auth.requestMatchers("/cliente/**").hasRole("ADMIN");

					// Cualquier otra petición requiere autenticación
					auth.anyRequest().authenticated();
				})
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * Configura las reglas CORS para permitir peticiones desde los orígenes
	 * del frontend de CocoTech (Angular en desarrollo y producción).
	 *
	 * @return Un {@link CorsConfigurationSource} con los orígenes, métodos y
	 *         encabezados permitidos.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(
				"http://localhost:4200",  // Angular dev server
				"http://localhost:8080",
				"http://localhost:8081"));
		configuration.setAllowedMethods(
				Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	/**
	 * Define el proveedor de autenticación basado en DAO (base de datos).
	 * <p>
	 * Usa {@link UserDetailsServiceImpl} para cargar el usuario y
	 * {@link BCryptPasswordEncoder} para verificar la contraseña.
	 *
	 * @return El {@link AuthenticationProvider} configurado.
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider authProvider =
	            new DaoAuthenticationProvider(userDetailsService);
	    authProvider.setPasswordEncoder(passwordEncoder());
	    return authProvider;
	}

	/**
	 * Expone el {@link AuthenticationManager} como bean de Spring para que
	 * pueda ser inyectado en el controlador de autenticación
	 * ({@code AuthController}).
	 *
	 * @param config La configuración de autenticación proporcionada por Spring.
	 * @return El {@link AuthenticationManager} del contexto de aplicación.
	 * @throws Exception Si ocurre un error al obtener el manager.
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
			throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * Define el codificador de contraseñas BCrypt utilizado para el hashing
	 * de contraseñas de clientes y empleados.
	 *
	 * @return Una instancia de {@link BCryptPasswordEncoder}.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
