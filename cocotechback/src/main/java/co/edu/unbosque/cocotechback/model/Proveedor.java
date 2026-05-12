/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa a un proveedor de productos del supermercado.
 * <p>
 * Un proveedor suministra varios productos al supermercado de manera periódica.
 * Cada producto del catálogo está asociado a un único proveedor que lo abastece.
 */
@Entity
@Table(name = "proveedor")
public class Proveedor {

	/**
	 * Identificador único del proveedor, generado automáticamente por la base
	 * de datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	 * Lista de productos que este proveedor suministra al supermercado.
	 * Un proveedor suministra varios productos.
	 */
	@OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Producto> productos;

	/**
	 * Constructor por defecto de la entidad Proveedor.
	 */
	public Proveedor() {
	}

	/**
	 * Constructor con parámetros para inicializar los datos del proveedor.
	 *
	 * @param nombre   Nombre del proveedor.
	 * @param telefono Teléfono del proveedor.
	 * @param calle    Calle del proveedor.
	 * @param barrio   Barrio del proveedor.
	 * @param ciudad   Ciudad del proveedor.
	 */
	public Proveedor(String nombre, String telefono, String calle, String barrio, String ciudad) {
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
	 * Obtiene la lista de productos suministrados por el proveedor.
	 *
	 * @return La lista de productos del proveedor.
	 */
	public List<Producto> getProductos() {
		return productos;
	}

	/**
	 * Establece la lista de productos suministrados por el proveedor.
	 *
	 * @param productos La nueva lista de productos.
	 */
	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}

	/**
	 * Devuelve una representación en cadena del objeto Proveedor.
	 *
	 * @return Una cadena con los atributos del proveedor.
	 */
	@Override
	public String toString() {
		return "Proveedor [idProveedor=" + idProveedor + ", nombre=" + nombre + ", telefono=" + telefono
				+ ", calle=" + calle + ", barrio=" + barrio + ", ciudad=" + ciudad + "]";
	}
}
