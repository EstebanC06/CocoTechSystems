/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.util.Objects;

import co.edu.unbosque.cocotechback.model.Usuario.Rol;

/**
 * Clase de Transferencia de Datos (DTO) para representar la información de un
 * cliente del supermercado.
 * <p>
 * Se utiliza para transferir los datos del cliente entre las capas de la
 * aplicación y a través de la API REST, evitando exponer directamente la
 * entidad JPA {@link co.edu.unbosque.cocotechback.model.Cliente}.
 * No incluye la contraseña en operaciones de lectura por seguridad.
 */
public class ClienteDTO {

	/**
	 * Identificador único del cliente.
	 */
	private Long id;

	/**
	 * Nombres del cliente.
	 */
	private String nombres;

	/**
	 * Apellidos del cliente.
	 */
	private String apellidos;

	/**
	 * Correo electrónico del cliente. Utilizado como nombre de usuario
	 * para la autenticación.
	 */
	private String correo;

	/**
	 * Contraseña del cliente. Solo se utiliza en operaciones de creación
	 * y actualización de credenciales; no se devuelve en consultas de lectura.
	 */
	private String contrasena;

	/**
	 * Código de verificación del cliente (para recuperación de cuenta, etc.).
	 */
	private String codigoVerificacion;

	/**
	 * Rol del cliente en el sistema (siempre {@code ROLE_CLIENTE}).
	 */
	private Rol rol;

	/**
	 * Número de teléfono de contacto del cliente.
	 */
	private String telefono;

	/**
	 * Calle de residencia del cliente.
	 */
	private String calle;

	/**
	 * Barrio de residencia del cliente.
	 */
	private String barrio;

	/**
	 * Ciudad de residencia del cliente.
	 */
	private String ciudad;

	/**
	 * Constructor por defecto de {@code ClienteDTO}.
	 */
	public ClienteDTO() {
	}

	/**
	 * Constructor con parámetros para inicializar los campos del {@code ClienteDTO}.
	 *
	 * @param id                 Identificador del cliente.
	 * @param nombres            Nombres del cliente.
	 * @param apellidos          Apellidos del cliente.
	 * @param correo             Correo electrónico del cliente.
	 * @param contrasena         Contraseña del cliente.
	 * @param codigoVerificacion Código de verificación del cliente.
	 * @param rol                Rol del cliente.
	 * @param telefono           Teléfono del cliente.
	 * @param calle              Calle del cliente.
	 * @param barrio             Barrio del cliente.
	 * @param ciudad             Ciudad del cliente.
	 */
	public ClienteDTO(Long id, String nombres, String apellidos, String correo, String contrasena,
			String codigoVerificacion, Rol rol, String telefono, String calle, String barrio,
			String ciudad) {
		this.id = id;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.correo = correo;
		this.contrasena = contrasena;
		this.codigoVerificacion = codigoVerificacion;
		this.rol = rol;
		this.telefono = telefono;
		this.calle = calle;
		this.barrio = barrio;
		this.ciudad = ciudad;
	}

	/**
	 * Obtiene el identificador único del cliente.
	 *
	 * @return El ID del cliente.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador único del cliente.
	 *
	 * @param id El nuevo ID del cliente.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene los nombres del cliente.
	 *
	 * @return Los nombres del cliente.
	 */
	public String getNombres() {
		return nombres;
	}

	/**
	 * Establece los nombres del cliente.
	 *
	 * @param nombres Los nuevos nombres del cliente.
	 */
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	/**
	 * Obtiene los apellidos del cliente.
	 *
	 * @return Los apellidos del cliente.
	 */
	public String getApellidos() {
		return apellidos;
	}

	/**
	 * Establece los apellidos del cliente.
	 *
	 * @param apellidos Los nuevos apellidos del cliente.
	 */
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	/**
	 * Obtiene el correo electrónico del cliente.
	 *
	 * @return El correo del cliente.
	 */
	public String getCorreo() {
		return correo;
	}

	/**
	 * Establece el correo electrónico del cliente.
	 *
	 * @param correo El nuevo correo del cliente.
	 */
	public void setCorreo(String correo) {
		this.correo = correo;
	}

	/**
	 * Obtiene la contraseña del cliente.
	 *
	 * @return La contraseña del cliente.
	 */
	public String getContrasena() {
		return contrasena;
	}

	/**
	 * Establece la contraseña del cliente.
	 *
	 * @param contrasena La nueva contraseña del cliente.
	 */
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	/**
	 * Obtiene el código de verificación del cliente.
	 *
	 * @return El código de verificación.
	 */
	public String getCodigoVerificacion() {
		return codigoVerificacion;
	}

	/**
	 * Establece el código de verificación del cliente.
	 *
	 * @param codigoVerificacion El nuevo código de verificación.
	 */
	public void setCodigoVerificacion(String codigoVerificacion) {
		this.codigoVerificacion = codigoVerificacion;
	}

	/**
	 * Obtiene el rol del cliente en el sistema.
	 *
	 * @return El rol del cliente.
	 */
	public Rol getRol() {
		return rol;
	}

	/**
	 * Establece el rol del cliente en el sistema.
	 *
	 * @param rol El nuevo rol del cliente.
	 */
	public void setRol(Rol rol) {
		this.rol = rol;
	}

	/**
	 * Obtiene el teléfono del cliente.
	 *
	 * @return El teléfono del cliente.
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * Establece el teléfono del cliente.
	 *
	 * @param telefono El nuevo teléfono del cliente.
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/**
	 * Obtiene la calle de residencia del cliente.
	 *
	 * @return La calle del cliente.
	 */
	public String getCalle() {
		return calle;
	}

	/**
	 * Establece la calle de residencia del cliente.
	 *
	 * @param calle La nueva calle del cliente.
	 */
	public void setCalle(String calle) {
		this.calle = calle;
	}

	/**
	 * Obtiene el barrio de residencia del cliente.
	 *
	 * @return El barrio del cliente.
	 */
	public String getBarrio() {
		return barrio;
	}

	/**
	 * Establece el barrio de residencia del cliente.
	 *
	 * @param barrio El nuevo barrio del cliente.
	 */
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	/**
	 * Obtiene la ciudad de residencia del cliente.
	 *
	 * @return La ciudad del cliente.
	 */
	public String getCiudad() {
		return ciudad;
	}

	/**
	 * Establece la ciudad de residencia del cliente.
	 *
	 * @param ciudad La nueva ciudad del cliente.
	 */
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	/**
	 * Genera un código hash para el objeto {@code ClienteDTO} basado en su ID,
	 * correo y rol.
	 *
	 * @return El código hash del objeto.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, correo, rol);
	}

	/**
	 * Compara este objeto {@code ClienteDTO} con otro para determinar igualdad,
	 * basándose en el ID, correo y rol.
	 *
	 * @param obj El objeto a comparar.
	 * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClienteDTO other = (ClienteDTO) obj;
		return Objects.equals(id, other.id) && Objects.equals(correo, other.correo)
				&& rol == other.rol;
	}

	/**
	 * Devuelve una representación en cadena del objeto {@code ClienteDTO}.
	 *
	 * @return Una cadena con los atributos del DTO del cliente.
	 */
	@Override
	public String toString() {
		return "ClienteDTO [id=" + id + ", nombres=" + nombres + ", apellidos=" + apellidos
				+ ", correo=" + correo + ", codigoVerificacion=" + codigoVerificacion
				+ ", rol=" + rol + ", telefono=" + telefono + ", calle=" + calle
				+ ", barrio=" + barrio + ", ciudad=" + ciudad + "]";
	}
}
