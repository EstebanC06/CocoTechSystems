/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa el detalle de una venta en el supermercado.
 * <p>
 * Un detalle de venta describe uno de los productos incluidos dentro de una
 * venta específica: qué producto se vendió, en qué cantidad, a qué precio y
 * si se aplicó alguna promoción. Una venta se compone de uno o varios detalles.
 */
@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {

	/**
	 * Identificador único del detalle de venta, generado automáticamente por la
	 * base de datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDetalle;

	/**
	 * Cantidad de unidades del producto incluidas en este detalle.
	 */
	private Integer cantidadProductos;

	/**
	 * Precio unitario del producto en el momento de la venta.
	 */
	private Double precioUnitario;

	/**
	 * Valor subtotal de este detalle (precioUnitario × cantidadProductos,
	 * aplicando la promoción si corresponde).
	 */
	private Double subtotal;

	/**
	 * Método de pago utilizado para esta venta
	 * (ej. "Efectivo", "Tarjeta débito", "Tarjeta crédito", "Transferencia").
	 */
	private String metodoPago;

	/**
	 * Indica si se aplicó una promoción en este detalle de venta.
	 */
	private Boolean promocion;

	/**
	 * Porcentaje de descuento aplicado en caso de que haya promoción activa.
	 * Puede ser nulo si no aplica promoción.
	 */
	private Double porcentajeDescuento;

	/**
	 * Precio original del producto antes de aplicar la promoción.
	 * Puede ser nulo si no aplica promoción.
	 */
	private Double precioOriginal;

	/**
	 * Precio final del producto después de aplicar el descuento de la promoción.
	 * Puede ser nulo si no aplica promoción.
	 */
	private Double precioNuevo;

	/**
	 * Venta a la que pertenece este detalle.
	 * Muchos detalles componen una venta.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_venta", nullable = false)
	private Venta venta;

	/**
	 * Producto incluido en este detalle de venta.
	 * Muchos detalles pueden referenciar el mismo producto.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_producto", nullable = false)
	private Producto producto;

	/**
	 * Constructor por defecto de la entidad DetalleVenta.
	 */
	public DetalleVenta() {
	}

	/**
	 * Constructor con parámetros para inicializar los datos del detalle de venta.
	 *
	 * @param cantidadProductos  Cantidad de unidades vendidas.
	 * @param precioUnitario     Precio unitario del producto.
	 * @param subtotal           Valor subtotal del detalle.
	 * @param metodoPago         Método de pago utilizado.
	 * @param promocion          Indica si se aplicó promoción.
	 * @param porcentajeDescuento Porcentaje de descuento aplicado.
	 * @param precioOriginal     Precio original antes del descuento.
	 * @param precioNuevo        Precio final después del descuento.
	 * @param venta              Venta a la que pertenece el detalle.
	 * @param producto           Producto incluido en el detalle.
	 */
	public DetalleVenta(Integer cantidadProductos, Double precioUnitario, Double subtotal,
			String metodoPago, Boolean promocion, Double porcentajeDescuento,
			Double precioOriginal, Double precioNuevo, Venta venta, Producto producto) {
		this.cantidadProductos = cantidadProductos;
		this.precioUnitario = precioUnitario;
		this.subtotal = subtotal;
		this.metodoPago = metodoPago;
		this.promocion = promocion;
		this.porcentajeDescuento = porcentajeDescuento;
		this.precioOriginal = precioOriginal;
		this.precioNuevo = precioNuevo;
		this.venta = venta;
		this.producto = producto;
	}

	/**
	 * Obtiene el identificador único del detalle de venta.
	 *
	 * @return El ID del detalle.
	 */
	public Long getIdDetalle() {
		return idDetalle;
	}

	/**
	 * Establece el identificador único del detalle de venta.
	 *
	 * @param idDetalle El nuevo ID del detalle.
	 */
	public void setIdDetalle(Long idDetalle) {
		this.idDetalle = idDetalle;
	}

	/**
	 * Obtiene la cantidad de productos del detalle.
	 *
	 * @return La cantidad de productos.
	 */
	public Integer getCantidadProductos() {
		return cantidadProductos;
	}

	/**
	 * Establece la cantidad de productos del detalle.
	 *
	 * @param cantidadProductos La nueva cantidad de productos.
	 */
	public void setCantidadProductos(Integer cantidadProductos) {
		this.cantidadProductos = cantidadProductos;
	}

	/**
	 * Obtiene el precio unitario del producto en el momento de la venta.
	 *
	 * @return El precio unitario.
	 */
	public Double getPrecioUnitario() {
		return precioUnitario;
	}

	/**
	 * Establece el precio unitario del producto.
	 *
	 * @param precioUnitario El nuevo precio unitario.
	 */
	public void setPrecioUnitario(Double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	/**
	 * Obtiene el subtotal del detalle de venta.
	 *
	 * @return El subtotal.
	 */
	public Double getSubtotal() {
		return subtotal;
	}

	/**
	 * Establece el subtotal del detalle de venta.
	 *
	 * @param subtotal El nuevo subtotal.
	 */
	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	/**
	 * Obtiene el método de pago utilizado.
	 *
	 * @return El método de pago.
	 */
	public String getMetodoPago() {
		return metodoPago;
	}

	/**
	 * Establece el método de pago utilizado.
	 *
	 * @param metodoPago El nuevo método de pago.
	 */
	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}

	/**
	 * Obtiene si se aplicó una promoción en este detalle.
	 *
	 * @return {@code true} si se aplicó promoción, {@code false} en caso contrario.
	 */
	public Boolean getPromocion() {
		return promocion;
	}

	/**
	 * Establece si se aplicó una promoción en este detalle.
	 *
	 * @param promocion El nuevo valor del indicador de promoción.
	 */
	public void setPromocion(Boolean promocion) {
		this.promocion = promocion;
	}

	/**
	 * Obtiene el porcentaje de descuento aplicado.
	 *
	 * @return El porcentaje de descuento.
	 */
	public Double getPorcentajeDescuento() {
		return porcentajeDescuento;
	}

	/**
	 * Establece el porcentaje de descuento aplicado.
	 *
	 * @param porcentajeDescuento El nuevo porcentaje de descuento.
	 */
	public void setPorcentajeDescuento(Double porcentajeDescuento) {
		this.porcentajeDescuento = porcentajeDescuento;
	}

	/**
	 * Obtiene el precio original antes de la promoción.
	 *
	 * @return El precio original.
	 */
	public Double getPrecioOriginal() {
		return precioOriginal;
	}

	/**
	 * Establece el precio original antes de la promoción.
	 *
	 * @param precioOriginal El nuevo precio original.
	 */
	public void setPrecioOriginal(Double precioOriginal) {
		this.precioOriginal = precioOriginal;
	}

	/**
	 * Obtiene el precio final después de la promoción.
	 *
	 * @return El precio nuevo.
	 */
	public Double getPrecioNuevo() {
		return precioNuevo;
	}

	/**
	 * Establece el precio final después de la promoción.
	 *
	 * @param precioNuevo El nuevo precio final.
	 */
	public void setPrecioNuevo(Double precioNuevo) {
		this.precioNuevo = precioNuevo;
	}

	/**
	 * Obtiene la venta a la que pertenece este detalle.
	 *
	 * @return La venta del detalle.
	 */
	public Venta getVenta() {
		return venta;
	}

	/**
	 * Establece la venta a la que pertenece este detalle.
	 *
	 * @param venta La nueva venta del detalle.
	 */
	public void setVenta(Venta venta) {
		this.venta = venta;
	}

	/**
	 * Obtiene el producto incluido en este detalle.
	 *
	 * @return El producto del detalle.
	 */
	public Producto getProducto() {
		return producto;
	}

	/**
	 * Establece el producto incluido en este detalle.
	 *
	 * @param producto El nuevo producto del detalle.
	 */
	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	/**
	 * Devuelve una representación en cadena del objeto DetalleVenta.
	 *
	 * @return Una cadena con los atributos del detalle de venta.
	 */
	@Override
	public String toString() {
		return "DetalleVenta [idDetalle=" + idDetalle + ", cantidadProductos=" + cantidadProductos
				+ ", precioUnitario=" + precioUnitario + ", subtotal=" + subtotal
				+ ", metodoPago=" + metodoPago + ", promocion=" + promocion
				+ ", porcentajeDescuento=" + porcentajeDescuento
				+ ", venta=" + (venta != null ? venta.getIdVenta() : "null")
				+ ", producto=" + (producto != null ? producto.getIdProducto() : "null") + "]";
	}
}
