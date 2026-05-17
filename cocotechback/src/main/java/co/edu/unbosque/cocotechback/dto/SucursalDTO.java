/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.util.Objects;

/**
 * Clase de Transferencia de Datos (DTO) para representar la información de una
 * sucursal del supermercado.
 * <p>
 * Se utiliza para transferir los datos de la sucursal entre las capas de la
 * aplicación y a través de la API REST, evitando exponer directamente la
 * entidad JPA {@link co.edu.unbosque.cocotechback.model.Sucursal}.
 * <p>
 * El campo {@code nombre} viaja como String entre front y back, pero internamente
 * el back lo valida y persiste como enum
 * {@link co.edu.unbosque.cocotechback.model.Sucursal.NombreSucursal}.
 */
public class SucursalDTO {

	/**
	 * Identificador único de la sucursal.
	 */
	private Long idSucursal;

	/**
	 * Nombre de la sucursal. Debe corresponder a un valor del enum
	 * {@link co.edu.unbosque.cocotechback.model.Sucursal.NombreSucursal}.
	 */
	private String nombre;

	/**
	 * Número de teléfono de contacto de la sucursal.
	 */
	private String telefonoContacto;

	/**
	 * Ciudad donde se ubica la sucursal.
	 */
	private String ciudad;

	/**
	 * Barrio donde se ubica la sucursal.
	 */
	private String barrio;

	/**
	 * Dirección completa donde se ubica la sucursal.
	 */
	private String direccion;

	/**
	 * Constructor por defecto de {@code SucursalDTO}.
	 */
	public SucursalDTO() {
	}

	/**
	 * Constructor con parámetros para inicializar los campos del {@code SucursalDTO}.
	 *
	 * @param idSucursal       Identificador de la sucursal.
	 * @param nombre           Nombre de la sucursal.
	 * @param telefonoContacto Teléfono de contacto de la sucursal.
	 * @param ciudad           Ciudad de la sucursal.
	 * @param barrio           Barrio de la sucursal.
	 * @param direccion        Dirección completa de la sucursal.
	 */
	public SucursalDTO(Long idSucursal, String nombre, String telefonoContacto, String ciudad,
			String barrio, String direccion) {
		this.idSucursal = idSucursal;
		this.nombre = nombre;
		this.telefonoContacto = telefonoContacto;
		this.ciudad = ciudad;
		this.barrio = barrio;
		this.direccion = direccion;
	}

	/**
	 * Obtiene el identificador único de la sucursal.
	 *
	 * @return El ID de la sucursal.
	 */
	public Long getIdSucursal() {
		return idSucursal;
	}

	/**
	 * Establece el identificador único de la sucursal.
	 *
	 * @param idSucursal El nuevo ID de la sucursal.
	 */
	public void setIdSucursal(Long idSucursal) {
		this.idSucursal = idSucursal;
	}

	/**
	 * Obtiene el nombre de la sucursal.
	 *
	 * @return El nombre de la sucursal.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre de la sucursal.
	 *
	 * @param nombre El nuevo nombre de la sucursal.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene el teléfono de contacto de la sucursal.
	 *
	 * @return El teléfono de contacto.
	 */
	public String getTelefonoContacto() {
		return telefonoContacto;
	}

	/**
	 * Establece el teléfono de contacto de la sucursal.
	 *
	 * @param telefonoContacto El nuevo teléfono de contacto.
	 */
	public void setTelefonoContacto(String telefonoContacto) {
		this.telefonoContacto = telefonoContacto;
	}

	/**
	 * Obtiene la ciudad de la sucursal.
	 *
	 * @return La ciudad de la sucursal.
	 */
	public String getCiudad() {
		return ciudad;
	}

	/**
	 * Establece la ciudad de la sucursal.
	 *
	 * @param ciudad La nueva ciudad de la sucursal.
	 */
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	/**
	 * Obtiene el barrio de la sucursal.
	 *
	 * @return El barrio de la sucursal.
	 */
	public String getBarrio() {
		return barrio;
	}

	/**
	 * Establece el barrio de la sucursal.
	 *
	 * @param barrio El nuevo barrio de la sucursal.
	 */
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	/**
	 * Obtiene la dirección completa de la sucursal.
	 *
	 * @return La dirección de la sucursal.
	 */
	public String getDireccion() {
		return direccion;
	}

	/**
	 * Establece la dirección completa de la sucursal.
	 *
	 * @param direccion La nueva dirección de la sucursal.
	 */
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	/**
	 * Genera un código hash para el objeto {@code SucursalDTO} basado en su ID
	 * y nombre.
	 *
	 * @return El código hash del objeto.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(idSucursal, nombre);
	}

	/**
	 * Compara este objeto {@code SucursalDTO} con otro para determinar igualdad,
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
		SucursalDTO other = (SucursalDTO) obj;
		return Objects.equals(idSucursal, other.idSucursal) && Objects.equals(nombre, other.nombre);
	}

	/**
	 * Devuelve una representación en cadena del objeto {@code SucursalDTO}.
	 *
	 * @return Una cadena con los atributos del DTO de la sucursal.
	 */
	@Override
	public String toString() {
		return "SucursalDTO [idSucursal=" + idSucursal + ", nombre=" + nombre
				+ ", telefonoContacto=" + telefonoContacto + ", ciudad=" + ciudad
				+ ", barrio=" + barrio + ", direccion=" + direccion + "]";
	}
}
