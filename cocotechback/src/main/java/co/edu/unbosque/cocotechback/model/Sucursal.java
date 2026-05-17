/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa una sucursal del supermercado.
 * <p>
 * Una sucursal es una sede física del supermercado. Cada sucursal aloja a
 * varios empleados y cuenta con una o más cajas registradoras. Los empleados
 * pueden ser transferidos entre sucursales, aunque en un momento dado están
 * asignados a una sola.
 */
@Entity
@Table(name = "sucursal")
public class Sucursal {

	/**
	 * Conjunto cerrado de sucursales permitidas. El nombre de cada sucursal
	 * está restringido a uno de estos valores para garantizar consistencia
	 * y permitir filtros confiables en el front-end.
	 */
	public enum NombreSucursal {
		FONTIBON,
		USAQUEN,
		CHAPINERO,
		SUBA,
		ENGATIVA
	}

	/**
	 * Identificador único de la sucursal, generado automáticamente por la base
	 * de datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idSucursal;

	/**
	 * Nombre de la sucursal. Restringido al enum {@link NombreSucursal}.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, unique = true)
	private NombreSucursal nombre;

	/**
	 * Número de teléfono de contacto de la sucursal.
	 */
	private String telefonoContacto;

	/**
	 * Ciudad donde se encuentra ubicada la sucursal.
	 */
	private String ciudad;

	/**
	 * Barrio donde se encuentra ubicada la sucursal.
	 */
	private String barrio;

	/**
	 * Dirección completa donde se encuentra ubicada la sucursal
	 * (ej. "Calle 140 # 91-12").
	 */
	private String direccion;

	/**
	 * Lista de empleados asignados a esta sucursal.
	 * Varios empleados son asignados para alojar una sola sucursal.
	 */
	@OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Empleado> empleados;

	/**
	 * Lista de cajas registradoras ubicadas en esta sucursal.
	 */
	@OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<CajaRegistradora> cajasRegistradoras;

	/**
	 * Constructor por defecto de la entidad Sucursal.
	 */
	public Sucursal() {
	}

	/**
	 * Constructor con parámetros para inicializar los datos de la sucursal.
	 *
	 * @param nombre           Nombre de la sucursal (enum {@link NombreSucursal}).
	 * @param telefonoContacto Teléfono de contacto de la sucursal.
	 * @param ciudad           Ciudad donde se ubica la sucursal.
	 * @param barrio           Barrio donde se ubica la sucursal.
	 * @param direccion        Dirección completa donde se ubica la sucursal.
	 */
	public Sucursal(NombreSucursal nombre, String telefonoContacto, String ciudad, String barrio,
			String direccion) {
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
	 * @return El nombre de la sucursal como valor del enum.
	 */
	public NombreSucursal getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre de la sucursal.
	 *
	 * @param nombre El nuevo nombre de la sucursal.
	 */
	public void setNombre(NombreSucursal nombre) {
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
	 * Obtiene la ciudad donde se ubica la sucursal.
	 *
	 * @return La ciudad de la sucursal.
	 */
	public String getCiudad() {
		return ciudad;
	}

	/**
	 * Establece la ciudad donde se ubica la sucursal.
	 *
	 * @param ciudad La nueva ciudad de la sucursal.
	 */
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	/**
	 * Obtiene el barrio donde se ubica la sucursal.
	 *
	 * @return El barrio de la sucursal.
	 */
	public String getBarrio() {
		return barrio;
	}

	/**
	 * Establece el barrio donde se ubica la sucursal.
	 *
	 * @param barrio El nuevo barrio de la sucursal.
	 */
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	/**
	 * Obtiene la dirección completa donde se ubica la sucursal.
	 *
	 * @return La dirección de la sucursal.
	 */
	public String getDireccion() {
		return direccion;
	}

	/**
	 * Establece la dirección completa donde se ubica la sucursal.
	 *
	 * @param direccion La nueva dirección de la sucursal.
	 */
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	/**
	 * Obtiene la lista de empleados asignados a la sucursal.
	 *
	 * @return La lista de empleados.
	 */
	public List<Empleado> getEmpleados() {
		return empleados;
	}

	/**
	 * Establece la lista de empleados asignados a la sucursal.
	 *
	 * @param empleados La nueva lista de empleados.
	 */
	public void setEmpleados(List<Empleado> empleados) {
		this.empleados = empleados;
	}

	/**
	 * Obtiene la lista de cajas registradoras de la sucursal.
	 *
	 * @return La lista de cajas registradoras.
	 */
	public List<CajaRegistradora> getCajasRegistradoras() {
		return cajasRegistradoras;
	}

	/**
	 * Establece la lista de cajas registradoras de la sucursal.
	 *
	 * @param cajasRegistradoras La nueva lista de cajas registradoras.
	 */
	public void setCajasRegistradoras(List<CajaRegistradora> cajasRegistradoras) {
		this.cajasRegistradoras = cajasRegistradoras;
	}

	/**
	 * Devuelve una representación en cadena del objeto Sucursal.
	 *
	 * @return Una cadena con los atributos de la sucursal.
	 */
	@Override
	public String toString() {
		return "Sucursal [idSucursal=" + idSucursal + ", nombre=" + nombre + ", telefonoContacto="
				+ telefonoContacto + ", ciudad=" + ciudad + ", barrio=" + barrio + ", direccion=" + direccion + "]";
	}
}
