/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.util.Objects;

/**
 * Clase de Transferencia de Datos (DTO) para representar la información de un
 * proveedor del supermercado.
 * <p>
 * Se utiliza para transferir los datos del proveedor entre las capas de la
 * aplicación y a través de la API REST, evitando exponer directamente la
 * entidad JPA {@link co.edu.unbosque.cocotechback.model.Proveedor}.
 */
public class ProveedorDTO {

	/**
	 * Identificador único del proveedor.
	 */
	private Long idProveedor;

	/**
	 * Nombre o razón social del proveedor.
	 */
	private String nombre;

	/**
	 * Número de teléfono de contacto del proveedor.
	 */
	private String telefono;

	/**
	 * Calle de la dirección del proveedor.
	 */
	private String calle;

	/**
	 * Barrio de la dirección del proveedor.
	 */
	private String barrio;

	/**
	 * Ciudad donde se ubica el proveedor.
	 */
	private String ciudad;

	/**
	 * Constructor por defecto de {@code ProveedorDTO}.
	 */
	public ProveedorDTO() {
	}

	/**
	 * Constructor con parámetros para inicializar los campos del
	 * {@code ProveedorDTO}.
	 *
	 * @param idProveedor Identificador del proveedor.
	 * @param nombre      Nombre del proveedor.
	 * @param telefono    Teléfono del proveedor.
	 * @param calle       Calle del proveedor.
	 * @param barrio      Barrio del proveedor.
	 * @param ciudad      Ciudad del proveedor.
	 */
	public ProveedorDTO(Long idProveedor, String nombre, String telefono, String calle,
			String barrio, String ciudad) {
		this.idProveedor = idProveedor;
		this.nombre = nombre;
		this.telefono = telefono;
		this.calle = calle;
		this.barrio = barrio;
		this.ciudad = ciudad;
	}

	/**
	 * Obtiene el identificador único del proveedor.
	 *
	 * @return El ID del proveedor.
	 */
	public Long getIdProveedor() {
		return idProveedor;
	}

	/**
	 * Establece el identificador único del proveedor.
	 *
	 * @param idProveedor El nuevo ID del proveedor.
	 */
	public void setIdProveedor(Long idProveedor) {
		this.idProveedor = idProveedor;
	}

	/**
	 * Obtiene el nombre del proveedor.
	 *
	 * @return El nombre del proveedor.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre del proveedor.
	 *
	 * @param nombre El nuevo nombre del proveedor.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene el teléfono del proveedor.
	 *
	 * @return El teléfono del proveedor.
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * Establece el teléfono del proveedor.
	 *
	 * @param telefono El nuevo teléfono del proveedor.
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/**
	 * Obtiene la calle del proveedor.
	 *
	 * @return La calle del proveedor.
	 */
	public String getCalle() {
		return calle;
	}

	/**
	 * Establece la calle del proveedor.
	 *
	 * @param calle La nueva calle del proveedor.
	 */
	public void setCalle(String calle) {
		this.calle = calle;
	}

	/**
	 * Obtiene el barrio del proveedor.
	 *
	 * @return El barrio del proveedor.
	 */
	public String getBarrio() {
		return barrio;
	}

	/**
	 * Establece el barrio del proveedor.
	 *
	 * @param barrio El nuevo barrio del proveedor.
	 */
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	/**
	 * Obtiene la ciudad del proveedor.
	 *
	 * @return La ciudad del proveedor.
	 */
	public String getCiudad() {
		return ciudad;
	}

	/**
	 * Establece la ciudad del proveedor.
	 *
	 * @param ciudad La nueva ciudad del proveedor.
	 */
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	/**
	 * Genera un código hash para el objeto {@code ProveedorDTO} basado en su ID
	 * y nombre.
	 *
	 * @return El código hash del objeto.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(idProveedor, nombre);
	}

	/**
	 * Compara este objeto {@code ProveedorDTO} con otro para determinar igualdad,
	 * basándose en el ID y el nombre.
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
		ProveedorDTO other = (ProveedorDTO) obj;
		return Objects.equals(idProveedor, other.idProveedor)
				&& Objects.equals(nombre, other.nombre);
	}

	/**
	 * Devuelve una representación en cadena del objeto {@code ProveedorDTO}.
	 *
	 * @return Una cadena con los atributos del DTO del proveedor.
	 */
	@Override
	public String toString() {
		return "ProveedorDTO [idProveedor=" + idProveedor + ", nombre=" + nombre
				+ ", telefono=" + telefono + ", calle=" + calle + ", barrio=" + barrio
				+ ", ciudad=" + ciudad + "]";
	}
}
