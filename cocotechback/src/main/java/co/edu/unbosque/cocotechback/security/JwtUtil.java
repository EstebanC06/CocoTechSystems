/**
 * Paquete que contiene las clases relacionadas con la seguridad de la
 * aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Utilidad para la generación, extracción y validación de tokens JWT (JSON Web
 * Tokens) en la aplicación CocoTech.
 * <p>
 * Proporciona métodos para crear tokens JWT para usuarios autenticados
 * (tanto {@link co.edu.unbosque.cocotechback.model.Cliente} con
 * {@code ROLE_CLIENTE} como {@link co.edu.unbosque.cocotechback.model.Empleado}
 * con {@code ROLE_ADMIN}), extraer información del token (nombre de usuario,
 * fecha de expiración, autoridades) y validar si un token sigue siendo válido.
 * <p>
 * Utiliza la librería JJWT para la creación y parsing de tokens. La clave
 * secreta se configura en {@code application.properties} mediante la propiedad
 * {@code jwt.secret}.
 */
@Component
public class JwtUtil {

	/**
	 * Tiempo de validez del token JWT en milisegundos. Actualmente configurado
	 * en 1 hora (3 600 000 ms).
	 */
	private static final long JWT_TOKEN_VALIDITY = 60 * 60 * 1000; // 1 hora

	/**
	 * Clave secreta para la firma de los tokens JWT. Se obtiene de la propiedad
	 * {@code jwt.secret} en {@code application.properties}. Si no se proporciona,
	 * se utiliza una clave por defecto que <strong>debe ser reemplazada en
	 * producción</strong> por una cadena aleatoria de al menos 32 caracteres.
	 */
	@Value("${jwt.secret:cocotechSecretKeyDefaultShouldBeChangedInProduction32Chars}")
	private String secret;

	/**
	 * Construye y retorna la clave de firma HMAC-SHA256 a partir del secreto
	 * configurado.
	 *
	 * @return La {@link Key} de firma para el algoritmo HMAC-SHA256.
	 */
	private Key getSigningKey() {
		byte[] keyBytes = secret.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Extrae el nombre de usuario (subject) almacenado en el token JWT.
	 * <p>
	 * En CocoTech, el subject es el correo electrónico encriptado del usuario
	 * (tal como se persiste en la base de datos).
	 *
	 * @param token El token JWT del cual se extraerá el nombre de usuario.
	 * @return El nombre de usuario (correo encriptado) contenido en el token.
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extrae la fecha de expiración del token JWT.
	 *
	 * @param token El token JWT del cual se extraerá la fecha de expiración.
	 * @return La fecha de expiración del token.
	 */
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Extrae una claim específica del token JWT utilizando un resolvedor de claims.
	 *
	 * @param <T>            El tipo de la claim a extraer.
	 * @param token          El token JWT del cual se extraerá la claim.
	 * @param claimsResolver Función para resolver la claim a partir del objeto
	 *                       {@link Claims}.
	 * @return La claim extraída del token.
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Extrae todas las claims del token JWT.
	 *
	 * @param token El token JWT del cual se extraerán las claims.
	 * @return Un objeto {@link Claims} con todas las claims del token.
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	/**
	 * Verifica si el token JWT ha expirado.
	 *
	 * @param token El token JWT a verificar.
	 * @return {@code true} si el token ha expirado, {@code false} en caso
	 *         contrario.
	 */
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Genera un token JWT para un usuario autenticado del sistema CocoTech.
	 * <p>
	 * El token incluye como claims adicionales las autoridades del usuario
	 * (su rol: {@code ROLE_CLIENTE} o {@code ROLE_ADMIN}) y el correo
	 * desencriptado para que el frontend pueda identificar al usuario sin
	 * necesidad de hacer una consulta adicional.
	 *
	 * @param userDetails          Los detalles del usuario ({@link UserDetails})
	 *                             para el cual se generará el token.
	 * @param correoDesencriptado  El correo electrónico del usuario en texto
	 *                             plano (desencriptado), para incluirlo como
	 *                             claim legible en el token.
	 * @return El token JWT generado como cadena de texto compacta.
	 */
	public String generateToken(UserDetails userDetails, String correoDesencriptado) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("authorities", userDetails.getAuthorities());
		claims.put("correo", correoDesencriptado);
		return createToken(claims, userDetails.getUsername());
	}

	/**
	 * Crea el token JWT con las claims, el subject y los parámetros de firma
	 * y expiración.
	 *
	 * @param claims  Las claims que se incluirán en el payload del token.
	 * @param subject El subject del token (correo encriptado del usuario).
	 * @return El token JWT firmado y compactado.
	 */
	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Valida un token JWT verificando que el subject coincida con el nombre de
	 * usuario del {@code UserDetails} y que el token no haya expirado.
	 *
	 * @param token       El token JWT a validar.
	 * @param userDetails Los detalles del usuario contra los cuales se validará.
	 * @return {@code true} si el token es válido y no ha expirado,
	 *         {@code false} en caso contrario.
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
