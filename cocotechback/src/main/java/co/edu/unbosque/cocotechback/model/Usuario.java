/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;

/**
 * Clase abstracta que representa un usuario genérico del sistema CocoTech.
 * <p>
 * Esta clase actúa como superclase para {@link Cliente} y {@link Empleado},
 * centralizando los atributos y comportamientos comunes de autenticación y
 * autorización. Implementa {@link UserDetails} de Spring Security para
 * integrarse con el mecanismo de autenticación JWT.
 * <p>
 * La estrategia de herencia utilizada es {@code JOINED}, lo que genera una
 * tabla base {@code usuario} con los campos comunes y tablas separadas para
 * cada subclase con sus atributos específicos.
 */
@MappedSuperclass
public abstract class Usuario implements UserDetails {

	/**
	 * Identificador único para la serialización de objetos de esta clase.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Identificador único del usuario, generado automáticamente por la base de
	 * datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Nombres del usuario.
	 */
	private String nombres;

	/**
	 * Apellidos del usuario.
	 */
	private String apellidos;

	/**
	 * Correo electrónico único del usuario, utilizado como nombre de usuario
	 * para la autenticación.
	 */
	@Column(unique = true, nullable = false)
	private String correo;

	/**
	 * Contraseña del usuario (almacenada encriptada con BCrypt).
	 */
	@Column(nullable = false)
	private String contrasena;

	/**
	 * Código de verificación asociado al usuario (para recuperación de cuenta,
	 * confirmación de correo, etc.).
	 */
	private String codigoVerificacion;

	/**
	 * Rol del usuario en el sistema. Define los permisos de acceso a los
	 * endpoints de la API.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Rol rol;

	/**
	 * Indica si la cuenta del usuario no ha expirado.
	 */
	private boolean accountNonExpired;

	/**
	 * Indica si la cuenta del usuario no está bloqueada.
	 */
	private boolean accountNonLocked;

	/**
	 * Indica si las credenciales del usuario no han expirado.
	 */
	private boolean credentialsNonExpired;

	/**
	 * Indica si la cuenta del usuario está habilitada.
	 */
	private boolean enabled;

	/**
	 * Enumeración que define los roles disponibles en el sistema.
	 * <ul>
	 * <li>{@code ROLE_CLIENTE} - Acceso limitado: consulta de productos,
	 * historial de compras propias y gestión de su perfil.</li>
	 * <li>{@code ROLE_ADMIN} - Acceso total: gestión de inventario, empleados,
	 * proveedores, ventas y reportes.</li>
	 * </ul>
	 */
	public enum Rol {
		ROLE_CLIENTE, ROLE_ADMIN
	}

	/**
	 * Constructor por defecto. Inicializa los flags de estado de la cuenta como
	 * {@code true} y el rol como {@code null}.
	 */
	public Usuario() {
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		this.enabled = true;
		this.rol = null;
	}

	/**
	 * Constructor con parámetros para inicializar los datos comunes del usuario.
	 *
	 * @param nombres             Nombres del usuario.
	 * @param apellidos           Apellidos del usuario.
	 * @param correo              Correo electrónico del usuario.
	 * @param contrasena          Contraseña del usuario.
	 * @param codigoVerificacion  Código de verificación del usuario.
	 */
	public Usuario(String nombres, String apellidos, String correo, String contrasena,
			String codigoVerificacion) {
		this();
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.correo = correo;
		this.contrasena = contrasena;
		this.codigoVerificacion = codigoVerificacion;
	}

	/**
	 * Retorna las autoridades (roles) concedidas al usuario para Spring Security.
	 *
	 * @return Una colección con la autoridad correspondiente al rol del usuario.
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(rol.name()));
	}

	/**
	 * Retorna el correo electrónico como nombre de usuario para la autenticación.
	 *
	 * @return El correo electrónico del usuario.
	 */
	@Override
	public String getUsername() {
		return correo;
	}

	/**
	 * Retorna la contraseña del usuario.
	 *
	 * @return La contraseña encriptada del usuario.
	 */
	@Override
	public String getPassword() {
		return contrasena;
	}

	/**
	 * Indica si la cuenta del usuario no ha expirado.
	 *
	 * @return {@code true} si la cuenta no ha expirado.
	 */
	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	/**
	 * Indica si la cuenta del usuario no está bloqueada.
	 *
	 * @return {@code true} si la cuenta no está bloqueada.
	 */
	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	/**
	 * Indica si las credenciales del usuario no han expirado.
	 *
	 * @return {@code true} si las credenciales no han expirado.
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	/**
	 * Indica si la cuenta del usuario está habilitada.
	 *
	 * @return {@code true} si la cuenta está habilitada.
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Obtiene el identificador único del usuario.
	 *
	 * @return El ID del usuario.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador único del usuario.
	 *
	 * @param id El nuevo ID del usuario.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene los nombres del usuario.
	 *
	 * @return Los nombres del usuario.
	 */
	public String getNombres() {
		return nombres;
	}

	/**
	 * Establece los nombres del usuario.
	 *
	 * @param nombres Los nuevos nombres del usuario.
	 */
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	/**
	 * Obtiene los apellidos del usuario.
	 *
	 * @return Los apellidos del usuario.
	 */
	public String getApellidos() {
		return apellidos;
	}

	/**
	 * Establece los apellidos del usuario.
	 *
	 * @param apellidos Los nuevos apellidos del usuario.
	 */
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	/**
	 * Obtiene el correo electrónico del usuario.
	 *
	 * @return El correo electrónico del usuario.
	 */
	public String getCorreo() {
		return correo;
	}

	/**
	 * Establece el correo electrónico del usuario.
	 *
	 * @param correo El nuevo correo electrónico del usuario.
	 */
	public void setCorreo(String correo) {
		this.correo = correo;
	}

	/**
	 * Obtiene la contraseña del usuario.
	 *
	 * @return La contraseña del usuario.
	 */
	public String getContrasena() {
		return contrasena;
	}

	/**
	 * Establece la contraseña del usuario.
	 *
	 * @param contrasena La nueva contraseña del usuario.
	 */
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	/**
	 * Obtiene el código de verificación del usuario.
	 *
	 * @return El código de verificación.
	 */
	public String getCodigoVerificacion() {
		return codigoVerificacion;
	}

	/**
	 * Establece el código de verificación del usuario.
	 *
	 * @param codigoVerificacion El nuevo código de verificación.
	 */
	public void setCodigoVerificacion(String codigoVerificacion) {
		this.codigoVerificacion = codigoVerificacion;
	}

	/**
	 * Obtiene el rol del usuario en el sistema.
	 *
	 * @return El rol del usuario.
	 */
	public Rol getRol() {
		return rol;
	}

	/**
	 * Establece el rol del usuario en el sistema.
	 *
	 * @param rol El nuevo rol del usuario.
	 */
	public void setRol(Rol rol) {
		this.rol = rol;
	}

	/**
	 * Establece si la cuenta del usuario no ha expirado.
	 *
	 * @param accountNonExpired El nuevo valor para el flag.
	 */
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	/**
	 * Establece si la cuenta del usuario no está bloqueada.
	 *
	 * @param accountNonLocked El nuevo valor para el flag.
	 */
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	/**
	 * Establece si las credenciales del usuario no han expirado.
	 *
	 * @param credentialsNonExpired El nuevo valor para el flag.
	 */
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	/**
	 * Establece si la cuenta del usuario está habilitada.
	 *
	 * @param enabled El nuevo valor para el flag.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Genera un código hash para el objeto basado en ID, contraseña y correo.
	 *
	 * @return El código hash del objeto.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, contrasena, correo);
	}

	/**
	 * Compara este objeto con otro para determinar igualdad basada en ID,
	 * contraseña y correo.
	 *
	 * @param obj El objeto a comparar.
	 * @return {@code true} si los objetos son iguales, {@code false} en caso
	 *         contrario.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id) && Objects.equals(contrasena, other.contrasena)
				&& Objects.equals(correo, other.correo);
	}

	/**
	 * Devuelve una representación en cadena del objeto Usuario.
	 *
	 * @return Una cadena con los atributos principales del usuario.
	 */
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombres=" + nombres + ", apellidos=" + apellidos
				+ ", correo=" + correo + ", codigoVerificacion=" + codigoVerificacion
				+ ", rol=" + rol + ", enabled=" + enabled + "]";
	}
}
