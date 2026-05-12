/**
 * Paquete que contiene las clases relacionadas con la seguridad de la
 * aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autenticación JWT que se ejecuta una vez por cada petición HTTP
 * entrante en la aplicación CocoTech.
 * <p>
 * Intercepta todas las solicitudes, extrae el token JWT del encabezado
 * {@code Authorization} con formato {@code Bearer <token>}, valida el token
 * y, si es válido, autentica al usuario configurando el
 * {@code SecurityContextHolder} con la información del usuario autenticado
 * (ya sea un {@link co.edu.unbosque.cocotechback.model.Cliente} o un
 * {@link co.edu.unbosque.cocotechback.model.Empleado}).
 * <p>
 * Si el token no está presente, ha expirado o es inválido, la petición
 * continúa sin autenticación y Spring Security se encargará de retornar
 * {@code 403 Forbidden} o {@code 401 Unauthorized} según corresponda.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	/**
	 * Utilidad para la manipulación y validación de tokens JWT.
	 */
	private final JwtUtil jwtUtil;

	/**
	 * Servicio para cargar los detalles del usuario a partir de su nombre de
	 * usuario (correo encriptado), buscando tanto en {@code ClienteRepository}
	 * como en {@code EmpleadoRepository}.
	 */
	private final UserDetailsService userDetailsService;

	/**
	 * Constructor con inyección de dependencias para {@link JwtUtil} y
	 * {@link UserDetailsService}.
	 *
	 * @param jwtUtil            Utilidad para la gestión de tokens JWT.
	 * @param userDetailsService Servicio para cargar los detalles del usuario.
	 */
	public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Método principal del filtro que se ejecuta para cada petición HTTP.
	 * <p>
	 * Flujo de ejecución:
	 * <ol>
	 * <li>Lee el encabezado {@code Authorization}.</li>
	 * <li>Si comienza con {@code Bearer }, extrae el token JWT.</li>
	 * <li>Extrae el nombre de usuario (correo encriptado) del token.</li>
	 * <li>Si el usuario no está ya autenticado en el contexto de seguridad,
	 * carga sus detalles desde la base de datos.</li>
	 * <li>Si el token es válido para ese usuario, configura la autenticación
	 * en el {@code SecurityContextHolder}.</li>
	 * <li>Continúa con la cadena de filtros.</li>
	 * </ol>
	 *
	 * @param request     La petición HTTP entrante.
	 * @param response    La respuesta HTTP saliente.
	 * @param filterChain La cadena de filtros para continuar el procesamiento.
	 * @throws ServletException Si ocurre un error a nivel de servlet.
	 * @throws IOException      Si ocurre un error de entrada/salida.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		final String authorizationHeader = request.getHeader("Authorization");

		String username = null;
		String jwt = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
			try {
				username = jwtUtil.extractUsername(jwt);
			} catch (Exception e) {
				logger.error("Error al extraer el usuario del token JWT: " + e.getMessage());
			}
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

			if (jwtUtil.validateToken(jwt, userDetails)) {
				UsernamePasswordAuthenticationToken authenticationToken =
						new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
				authenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}

		filterChain.doFilter(request, response);
	}
}
