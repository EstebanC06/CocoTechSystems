/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.util.Objects;

/**
 * Clase de Transferencia de Datos (DTO) para representar la información de una
 * categoría de productos del supermercado.
 * <p>
 * Se utiliza para transferir los datos de la categoría entre las capas de la
 * aplicación y a través de la API REST, evitando exponer directamente la
 * entidad JPA {@link co.edu.unbosque.cocotechback.model.Categoria}.
 */
public class CategoriaDTO {

	/**
	 * Identificador único de la categoría.
	 */
	private Long idCategoria;

	/**
	 * Nombre de la categoría (ej. "Lácteos", "Bebidas", "Aseo").
	 */
	private String nombre;

	/**
	 * Descripción de la categoría y los productos que incluye.
	 */
	private String descripcion;

	/** URL de la imagen representativa de la categoría. */
	private String imagenUrl;

	/** Nombre del ícono FontAwesome para la categoría. */
	private String icono;

	/**
	 * Constructor por defecto de {@code CategoriaDTO}.
	 */
	public CategoriaDTO() {
	}

	/**
	 * Constructor con parámetros para inicializar los campos del
	 * {@code CategoriaDTO}.
	 *
	 * @param idCategoria Identificador de la categoría.
	 * @param nombre      Nombre de la categoría.
	 * @param descripcion Descripción de la categoría.
	 */
	public CategoriaDTO(Long idCategoria, String nombre, String descripcion) {
		this.idCategoria = idCategoria;
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

	/** @return URL de la imagen de la categoría. */
	public String getImagenUrl() {
		return imagenUrl;
	}

	/** @param imagenUrl Nueva URL de la imagen de la categoría. */
	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	/** @return Nombre del ícono FontAwesome. */
	public String getIcono() {
		return icono;
	}

	/** @param icono Nuevo nombre del ícono. */
	public void setIcono(String icono) {
		this.icono = icono;
	}

	/**
	 * Genera un código hash para el objeto {@code CategoriaDTO} basado en su ID
	 * y nombre.
	 *
	 * @return El código hash del objeto.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(idCategoria, nombre);
	}

	/**
	 * Compara este objeto {@code CategoriaDTO} con otro para determinar igualdad,
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
		CategoriaDTO other = (CategoriaDTO) obj;
		return Objects.equals(idCategoria, other.idCategoria)
				&& Objects.equals(nombre, other.nombre);
	}

	/**
	 * Devuelve una representación en cadena del objeto {@code CategoriaDTO}.
	 *
	 * @return Una cadena con los atributos del DTO de la categoría.
	 */
	@Override
	public String toString() {
		return "CategoriaDTO [idCategoria=" + idCategoria + ", nombre=" + nombre
				+ ", descripcion=" + descripcion + "]";
	}
}