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
 * Entidad JPA que representa una categoría de productos del supermercado.
 * <p>
 * Las categorías permiten clasificar y organizar los productos disponibles
 * en el supermercado (ej. Lácteos, Bebidas, Carnes, Aseo). Cada categoría
 * cuenta con varios productos que pertenecen a ella.
 */
@Entity
@Table(name = "categoria")
public class Categoria {

	/**
	 * Identificador único de la categoría, generado automáticamente por la base
	 * de datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idCategoria;

	/**
	 * Nombre de la categoría (ej. "Lácteos", "Bebidas", "Carnes").
	 */
	private String nombre;

	/**
	 * Descripción detallada de la categoría y los productos que incluye.
	 */
	private String descripcion;

	/**
	 * URL de la imagen representativa de la categoría para mostrar en el
	 * e-commerce.
	 */
	@jakarta.persistence.Column(length = 500)
	private String imagenUrl;

	/**
	 * Nombre del ícono FontAwesome a usar como representación visual de la
	 * categoría cuando no hay imagen disponible (ej. "faAppleAlt", "faBreadSlice").
	 */
	private String icono;

	/**
	 * Lista de productos que pertenecen a esta categoría.
	 * Una categoría contiene varios productos.
	 */
	@OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Producto> productos;

	/**
	 * Constructor por defecto de la entidad Categoria.
	 */
	public Categoria() {
	}

	/**
	 * Constructor con parámetros para inicializar los datos de la categoría.
	 *
	 * @param nombre      Nombre de la categoría.
	 * @param descripcion Descripción de la categoría.
	 */
	public Categoria(String nombre, String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene el identificador único de la categoría.
	 *
	 * @return El ID de la categoría.
	 */
	public Long getIdCategoria() {
		return idCategoria;
	}

	/**
	 * Establece el identificador único de la categoría.
	 *
	 * @param idCategoria El nuevo ID de la categoría.
	 */
	public void setIdCategoria(Long idCategoria) {
		this.idCategoria = idCategoria;
	}

	/**
	 * Obtiene el nombre de la categoría.
	 *
	 * @return El nombre de la categoría.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre de la categoría.
	 *
	 * @param nombre El nuevo nombre de la categoría.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene la descripción de la categoría.
	 *
	 * @return La descripción de la categoría.
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece la descripción de la categoría.
	 *
	 * @param descripcion La nueva descripción de la categoría.
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Obtiene la lista de productos pertenecientes a esta categoría.
	 *
	 * @return La lista de productos.
	 */
	public List<Producto> getProductos() {
		return productos;
	}

	/**
	 * Establece la lista de productos pertenecientes a esta categoría.
	 *
	 * @param productos La nueva lista de productos.
	 */
	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}

	/**
	 * Obtiene la URL de la imagen de la categoría.
	 *
	 * @return La URL de la imagen, o {@code null} si no se ha cargado.
	 */
	public String getImagenUrl() {
		return imagenUrl;
	}

	/**
	 * Establece la URL de la imagen de la categoría.
	 *
	 * @param imagenUrl La nueva URL de la imagen.
	 */
	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	/**
	 * Obtiene el nombre del ícono FontAwesome de la categoría.
	 *
	 * @return El nombre del ícono.
	 */
	public String getIcono() {
		return icono;
	}

	/**
	 * Establece el nombre del ícono FontAwesome de la categoría.
	 *
	 * @param icono El nuevo nombre del ícono.
	 */
	public void setIcono(String icono) {
		this.icono = icono;
	}

	/**
	 * Devuelve una representación en cadena del objeto Categoria.
	 *
	 * @return Una cadena con los atributos de la categoría.
	 */
	@Override
	public String toString() {
		return "Categoria [idCategoria=" + idCategoria + ", nombre=" + nombre
				+ ", descripcion=" + descripcion + "]";
	}
}