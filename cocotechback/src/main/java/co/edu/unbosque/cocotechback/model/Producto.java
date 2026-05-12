/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa un producto del catálogo del supermercado.
 * <p>
 * Un producto pertenece a una única categoría y es suministrado por un único
 * proveedor. Puede aparecer en múltiples detalles de venta a lo largo del
 * tiempo.
 */
@Entity
@Table(name = "producto")
public class Producto {

	/**
	 * Identificador único del producto, generado automáticamente por la base de
	 * datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	 * Fecha de vencimiento del producto. Puede ser nula para productos sin
	 * fecha de vencimiento (ej. artículos de aseo).
	 */
	private LocalDate fechaVencimiento;

	/**
	 * Categoría a la que pertenece este producto.
	 * Cada producto pertenece a una única categoría.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria", nullable = false)
	private Categoria categoria;

	/**
	 * Proveedor que suministra este producto al supermercado.
	 * Cada producto es suministrado por un único proveedor.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proveedor", nullable = false)
	private Proveedor proveedor;

	/**
	 * Lista de detalles de venta en los que aparece este producto.
	 */
	@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DetalleVenta> detallesVenta;

	/**
	 * Constructor por defecto de la entidad Producto.
	 */
	public Producto() {
	}

	/**
	 * Constructor con parámetros para inicializar los datos del producto.
	 *
	 * @param nombre           Nombre del producto.
	 * @param precio           Precio unitario del producto.
	 * @param stock            Stock disponible del producto.
	 * @param fechaVencimiento Fecha de vencimiento del producto.
	 * @param categoria        Categoría del producto.
	 * @param proveedor        Proveedor del producto.
	 */
	public Producto(String nombre, Double precio, Integer stock, LocalDate fechaVencimiento,
			Categoria categoria, Proveedor proveedor) {
		this.nombre = nombre;
		this.precio = precio;
		this.stock = stock;
		this.fechaVencimiento = fechaVencimiento;
		this.categoria = categoria;
		this.proveedor = proveedor;
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
	 * Obtiene la categoría del producto.
	 *
	 * @return La categoría del producto.
	 */
	public Categoria getCategoria() {
		return categoria;
	}

	/**
	 * Establece la categoría del producto.
	 *
	 * @param categoria La nueva categoría del producto.
	 */
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	/**
	 * Obtiene el proveedor del producto.
	 *
	 * @return El proveedor del producto.
	 */
	public Proveedor getProveedor() {
		return proveedor;
	}

	/**
	 * Establece el proveedor del producto.
	 *
	 * @param proveedor El nuevo proveedor del producto.
	 */
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	/**
	 * Obtiene la lista de detalles de venta asociados al producto.
	 *
	 * @return La lista de detalles de venta.
	 */
	public List<DetalleVenta> getDetallesVenta() {
		return detallesVenta;
	}

	/**
	 * Establece la lista de detalles de venta asociados al producto.
	 *
	 * @param detallesVenta La nueva lista de detalles de venta.
	 */
	public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
		this.detallesVenta = detallesVenta;
	}

	/**
	 * Devuelve una representación en cadena del objeto Producto.
	 *
	 * @return Una cadena con los atributos del producto.
	 */
	@Override
	public String toString() {
		return "Producto [idProducto=" + idProducto + ", nombre=" + nombre + ", precio=" + precio
				+ ", stock=" + stock + ", fechaVencimiento=" + fechaVencimiento
				+ ", categoria=" + (categoria != null ? categoria.getIdCategoria() : "null")
				+ ", proveedor=" + (proveedor != null ? proveedor.getIdProveedor() : "null") + "]";
	}
}
