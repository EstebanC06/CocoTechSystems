/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase de Transferencia de Datos (DTO) para representar la información de un
 * producto del catálogo del supermercado.
 * <p>
 * Se utiliza para transferir los datos del producto entre las capas de la
 * aplicación y a través de la API REST, evitando exponer directamente la
 * entidad JPA {@link co.edu.unbosque.cocotechback.model.Producto}.
 * Las referencias a la categoría y al proveedor se representan mediante sus
 * IDs para evitar dependencias circulares en la serialización JSON.
 */
public class ProductoDTO {

	/**
	 * Identificador único del producto.
	 */
	private Long idProducto;

	/**
	 * Nombre del producto.
	 */
	private String nombre;

	/**
	 * Precio unitario de venta del producto.
	 */
	private Double precio;

	/**
	 * Cantidad de unidades disponibles en inventario (stock actual).
	 */
	private Integer stock;

	/**
	 * Fecha de vencimiento del producto. Puede ser {@code null} para productos
	 * sin fecha de vencimiento (ej. artículos de aseo).
	 */
	private LocalDate fechaVencimiento;

	/**
	 * Identificador de la categoría a la que pertenece el producto.
	 */
	private Long idCategoria;

	/**
	 * Identificador del proveedor que suministra el producto.
	 */
	private Long idProveedor;

	/**
	 * Constructor por defecto de {@code ProductoDTO}.
	 */
	public ProductoDTO() {
	}

	/**
	 * Constructor con parámetros para inicializar los campos del
	 * {@code ProductoDTO}.
	 *
	 * @param idProducto       Identificador del producto.
	 * @param nombre           Nombre del producto.
	 * @param precio           Precio unitario del producto.
	 * @param stock            Stock disponible del producto.
	 * @param fechaVencimiento Fecha de vencimiento del producto.
	 * @param idCategoria      ID de la categoría del producto.
	 * @param idProveedor      ID del proveedor del producto.
	 */
	public ProductoDTO(Long idProducto, String nombre, Double precio, Integer stock,
			LocalDate fechaVencimiento, Long idCategoria, Long idProveedor) {
		this.idProducto = idProducto;
		this.nombre = nombre;
		this.precio = precio;
		this.stock = stock;
		this.fechaVencimiento = fechaVencimiento;
		this.idCategoria = idCategoria;
		this.idProveedor = idProveedor;
	}

	/**
	 * Obtiene el identificador único del producto.
	 *
	 * @return El ID del producto.
	 */
	public Long getIdProducto() {
		return idProducto;
	}

	/**
	 * Establece el identificador único del producto.
	 *
	 * @param idProducto El nuevo ID del producto.
	 */
	public void setIdProducto(Long idProducto) {
		this.idProducto = idProducto;
	}

	/**
	 * Obtiene el nombre del producto.
	 *
	 * @return El nombre del producto.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre del producto.
	 *
	 * @param nombre El nuevo nombre del producto.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene el precio unitario del producto.
	 *
	 * @return El precio del producto.
	 */
	public Double getPrecio() {
		return precio;
	}

	/**
	 * Establece el precio unitario del producto.
	 *
	 * @param precio El nuevo precio del producto.
	 */
	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	/**
	 * Obtiene el stock actual del producto.
	 *
	 * @return El stock disponible del producto.
	 */
	public Integer getStock() {
		return stock;
	}

	/**
	 * Establece el stock del producto.
	 *
	 * @param stock El nuevo stock del producto.
	 */
	public void setStock(Integer stock) {
		this.stock = stock;
	}

	/**
	 * Obtiene la fecha de vencimiento del producto.
	 *
	 * @return La fecha de vencimiento del producto.
	 */
	public LocalDate getFechaVencimiento() {
		return fechaVencimiento;
	}

	/**
	 * Establece la fecha de vencimiento del producto.
	 *
	 * @param fechaVencimiento La nueva fecha de vencimiento.
	 */
	public void setFechaVencimiento(LocalDate fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	/**
	 * Obtiene el ID de la categoría del producto.
	 *
	 * @return El ID de la categoría.
	 */
	public Long getIdCategoria() {
		return idCategoria;
	}

	/**
	 * Establece el ID de la categoría del producto.
	 *
	 * @param idCategoria El nuevo ID de la categoría.
	 */
	public void setIdCategoria(Long idCategoria) {
		this.idCategoria = idCategoria;
	}

	/**
	 * Obtiene el ID del proveedor del producto.
	 *
	 * @return El ID del proveedor.
	 */
	public Long getIdProveedor() {
		return idProveedor;
	}

	/**
	 * Establece el ID del proveedor del producto.
	 *
	 * @param idProveedor El nuevo ID del proveedor.
	 */
	public void setIdProveedor(Long idProveedor) {
		this.idProveedor = idProveedor;
	}

	/**
	 * Genera un código hash para el objeto {@code ProductoDTO} basado en su ID
	 * y nombre.
	 *
	 * @return El código hash del objeto.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(idProducto, nombre);
	}

	/**
	 * Compara este objeto {@code ProductoDTO} con otro para determinar igualdad,
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
		ProductoDTO other = (ProductoDTO) obj;
		return Objects.equals(idProducto, other.idProducto)
				&& Objects.equals(nombre, other.nombre);
	}

	/**
	 * Devuelve una representación en cadena del objeto {@code ProductoDTO}.
	 *
	 * @return Una cadena con los atributos del DTO del producto.
	 */
	@Override
	public String toString() {
		return "ProductoDTO [idProducto=" + idProducto + ", nombre=" + nombre + ", precio=" + precio
				+ ", stock=" + stock + ", fechaVencimiento=" + fechaVencimiento
				+ ", idCategoria=" + idCategoria + ", idProveedor=" + idProveedor + "]";
	}
}
