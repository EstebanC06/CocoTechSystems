/**
 * Paquete que contiene las clases relacionadas con la seguridad de la
 * aplicación CocoTech backend.
 * <p>
 * Este paquete implementa autenticación y autorización JWT (JSON Web Token)
 * sin estado (stateless), integrada con Spring Security. Las clases
 * principales son:
 * <ul>
 * <li>{@link co.edu.unbosque.cocotechback.security.JwtUtil} — Genera,
 * parsea y valida tokens JWT firmados con HMAC-SHA256. La clave secreta
 * se configura en {@code application.properties} bajo {@code jwt.secret}.</li>
 * <li>{@link co.edu.unbosque.cocotechback.security.JwtAuthenticationFilter}
 * — Filtro {@code OncePerRequestFilter} que intercepta cada petición,
 * extrae el token del encabezado {@code Authorization: Bearer <token>},
 * valida el token y configura el {@code SecurityContextHolder} con el
 * usuario autenticado.</li>
 * <li>{@link co.edu.unbosque.cocotechback.security.UserDetailsServiceImpl}
 * — Implementación de {@code UserDetailsService} que busca el usuario por
 * correo AES-encriptado primero en {@code EmpleadoRepository} y luego en
 * {@code ClienteRepository}, soportando la jerarquía de dos roles del
 * sistema.</li>
 * <li>{@link co.edu.unbosque.cocotechback.security.SecurityConfig} —
 * Configuración central de Spring Security: cadena de filtros, reglas de
 * autorización por ruta, CORS, {@code DaoAuthenticationProvider} y
 * {@code BCryptPasswordEncoder}.</li>
 * </ul>
 * <p>
 * El controlador de autenticación
 * ({@link co.edu.unbosque.cocotechback.controller.AuthController}) vive en
 * el paquete {@code controller} y expone los endpoints públicos
 * {@code /auth/login}, {@code /auth/recuperarContrasenaCliente} y
 * {@code /auth/recuperarContrasenaEmpleado}.
 */
package co.edu.unbosque.cocotechback.security;
