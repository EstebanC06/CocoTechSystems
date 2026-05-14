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
 * Entidad JPA que representa una línea de detalle dentro de un {@link Pedido}.
 * <p>
 * Cada detalle describe uno de los productos incluidos en el pedido: qué
 * producto se compró, en qué cantidad, a qué precio unitario y si tenía
 * promoción al momento de la compra. Un pedido se compone de uno o varios
 * detalles.
 * <p>
 * El {@code precioUnitario} y el {@code subtotal} se guardan como snapshot
 * para preservar el precio histórico, ya que el precio del {@link Producto}
 * puede cambiar después de realizado el pedido.
 */
@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

	/**
	 * Identificador único del detalle de pedido, generado automáticamente.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDetallePedido;

	/**
	 * Pedido al que pertenece este detalle.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pedido", nullable = false)
	private Pedido pedido;

	/**
	 * Producto incluido en este detalle.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_producto", nullable = false)
	private Producto producto;

	/**
	 * Cantidad de unidades del producto incluidas en el pedido.
	 */
	private Integer cantidad;

	/**
	 * Precio unitario del producto al momento de la compra (con descuento ya
	 * aplicado si había promoción).
	 */
	private Double precioUnitario;

	/**
	 * Valor subtotal de la línea: {@code precioUnitario × cantidad}.
	 */
	private Double subtotal;

	/**
	 * Indica si el producto tenía una promoción activa al momento de la compra.
	 */
	private Boolean promocion;

	/**
	 * Porcentaje de descuento aplicado, si había promoción. {@code 0} si no.
	 */
	private Integer porcentajeDescuento;

	/**
	 * Constructor por defecto de {@code DetallePedido}.
	 */
	public DetallePedido() {
	}

	/**
	 * Constructor con parámetros para inicializar el detalle de pedido.
	 *
	 * @param pedido              Pedido al que pertenece.
	 * @param producto            Producto comprado.
	 * @param cantidad            Cantidad de unidades.
	 * @param precioUnitario      Precio unitario en el momento de la compra.
	 * @param subtotal            Subtotal de la línea.
	 * @param promocion           Si tenía promoción.
	 * @param porcentajeDescuento Porcentaje de descuento aplicado.
	 */
	public DetallePedido(Pedido pedido, Producto producto, Integer cantidad,
			Double precioUnitario, Double subtotal, Boolean promocion,
			Integer porcentajeDescuento) {
		this.pedido = pedido;
		this.producto = producto;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.subtotal = subtotal;
		this.promocion = promocion;
		this.porcentajeDescuento = porcentajeDescuento;
	}

	/** @return ID del detalle de pedido. */
	public Long getIdDetallePedido() {
		return idDetallePedido;
	}

	/** @param idDetallePedido Nuevo ID del detalle. */
	public void setIdDetallePedido(Long idDetallePedido) {
		this.idDetallePedido = idDetallePedido;
	}

	/** @return Pedido al que pertenece. */
	public Pedido getPedido() {
		return pedido;
	}

	/** @param pedido Nuevo pedido. */
	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	/** @return Producto del detalle. */
	public Producto getProducto() {
		return producto;
	}

	/** @param producto Nuevo producto. */
	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	/** @return Cantidad de unidades. */
	public Integer getCantidad() {
		return cantidad;
	}

	/** @param cantidad Nueva cantidad. */
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	/** @return Precio unitario (snapshot). */
	public Double getPrecioUnitario() {
		return precioUnitario;
	}

	/** @param precioUnitario Nuevo precio unitario. */
	public void setPrecioUnitario(Double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	/** @return Subtotal de la línea. */
	public Double getSubtotal() {
		return subtotal;
	}

	/** @param subtotal Nuevo subtotal. */
	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	/** @return {@code true} si tenía promoción. */
	public Boolean getPromocion() {
		return promocion;
	}

	/** @param promocion Nuevo valor de promoción. */
	public void setPromocion(Boolean promocion) {
		this.promocion = promocion;
	}

	/** @return Porcentaje de descuento aplicado. */
	public Integer getPorcentajeDescuento() {
		return porcentajeDescuento;
	}

	/** @param porcentajeDescuento Nuevo porcentaje de descuento. */
	public void setPorcentajeDescuento(Integer porcentajeDescuento) {
		this.porcentajeDescuento = porcentajeDescuento;
	}

	@Override
	public String toString() {
		return "DetallePedido [idDetallePedido=" + idDetallePedido
				+ ", producto=" + (producto != null ? producto.getIdProducto() : "null")
				+ ", cantidad=" + cantidad + ", precioUnitario=" + precioUnitario
				+ ", subtotal=" + subtotal + ", promocion=" + promocion + "]";
	}
}