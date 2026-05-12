/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa a un cliente del supermercado.
 * <p>
 * Hereda los atributos comunes de autenticación y datos personales de
 * {@link Usuario}. El cliente posee el rol {@code ROLE_CLIENTE}, que le
 * otorga permisos limitados dentro del sistema: consulta de productos
 * disponibles, visualización de su historial de compras y gestión de
 * su propio perfil.
 * <p>
 * Un cliente puede realizar una o varias ventas a lo largo de su vida
 * como comprador del supermercado.
 */
@Entity
@Table(name = "cliente")
public class Cliente extends Usuario {

	/**
	 * Identificador único para la serialización de objetos de esta clase.
	 */
	private static final long serialVersionUID = 1L;

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
	 * Lista de ventas realizadas por este cliente.
	 * Un cliente puede aportar una o varias ventas al supermercado.
	 */
	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Venta> ventas;

	/**
	 * Constructor por defecto. Asigna automáticamente el rol
	 * {@code ROLE_CLIENTE} al crear un cliente.
	 */
	public Cliente() {
		super();
		this.setRol(Rol.ROLE_CLIENTE);
	}

	/**
	 * Constructor con parámetros para inicializar los datos del cliente.
	 *
	 * @param nombres            Nombres del cliente.
	 * @param apellidos          Apellidos del cliente.
	 * @param correo             Correo electrónico del cliente.
	 * @param contrasena         Contraseña del cliente.
	 * @param codigoVerificacion Código de verificación del cliente.
	 * @param telefono           Teléfono de contacto del cliente.
	 * @param calle              Calle de residencia del cliente.
	 * @param barrio             Barrio de residencia del cliente.
	 * @param ciudad             Ciudad de residencia del cliente.
	 */
	public Cliente(String nombres, String apellidos, String correo, String contrasena,
			String codigoVerificacion, String telefono, String calle, String barrio,
			String ciudad) {
		super(nombres, apellidos, correo, contrasena, codigoVerificacion);
		this.setRol(Rol.ROLE_CLIENTE);
		this.telefono = telefono;
		this.calle = calle;
		this.barrio = barrio;
		this.ciudad = ciudad;
	}

	/**
	 * Obtiene el número de teléfono del cliente.
	 *
	 * @return El teléfono del cliente.
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * Establece el número de teléfono del cliente.
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
	 * Obtiene la lista de ventas asociadas al cliente.
	 *
	 * @return La lista de ventas del cliente.
	 */
	public List<Venta> getVentas() {
		return ventas;
	}

	/**
	 * Establece la lista de ventas asociadas al cliente.
	 *
	 * @param ventas La nueva lista de ventas del cliente.
	 */
	public void setVentas(List<Venta> ventas) {
		this.ventas = ventas;
	}

	/**
	 * Devuelve una representación en cadena del objeto Cliente.
	 *
	 * @return Una cadena con los atributos del cliente.
	 */
	@Override
	public String toString() {
		return "Cliente [id=" + getId() + ", nombres=" + getNombres() + ", apellidos=" + getApellidos()
				+ ", correo=" + getCorreo() + ", telefono=" + telefono + ", calle=" + calle
				+ ", barrio=" + barrio + ", ciudad=" + ciudad + ", rol=" + getRol() + "]";
	}
}
