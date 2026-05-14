/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.util.Objects;

/**
 * DTO para representar una línea de detalle dentro de un pedido del e-commerce.
 * <p>
 * Incluye campos hidratados ({@code nombreProducto}, {@code imagenUrl}) que el
 * backend rellena al enviar la respuesta, para que el frontend pueda mostrar
 * la información del producto sin hacer peticiones adicionales.
 */
public class DetallePedidoDTO {

	/** Identificador único del detalle de pedido. */
	private Long idDetallePedido;

	/** Identificador del pedido al que pertenece. */
	private Long idPedido;

	/** Identificador del producto comprado. */
	private Long idProducto;

	/** Nombre del producto (hidratado por el backend para la respuesta). */
	private String nombreProducto;

	/** URL de la imagen del producto (hidratada por el backend). */
	private String imagenUrl;

	/** Cantidad de unidades compradas. */
	private Integer cantidad;

	/** Precio unitario en el momento de la compra (snapshot). */
	private Double precioUnitario;

	/** Subtotal de la línea: precioUnitario × cantidad. */
	private Double subtotal;

	/** Si el producto tenía promoción al momento de la compra. */
	private Boolean promocion;

	/** Porcentaje de descuento aplicado (0 si no había promoción). */
	private Integer porcentajeDescuento;

	/** Constructor por defecto. */
	public DetallePedidoDTO() {
	}

	/**
	 * Constructor con los campos esenciales para crear un detalle.
	 *
	 * @param idProducto          ID del producto.
	 * @param cantidad            Cantidad de unidades.
	 * @param precioUnitario      Precio unitario.
	 * @param subtotal            Subtotal de la línea.
	 * @param promocion           Si tenía promoción.
	 * @param porcentajeDescuento Porcentaje de descuento.
	 */
	public DetallePedidoDTO(Long idProducto, Integer cantidad, Double precioUnitario,
			Double subtotal, Boolean promocion, Integer porcentajeDescuento) {
		this.idProducto = idProducto;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.subtotal = subtotal;
		this.promocion = promocion;
		this.porcentajeDescuento = porcentajeDescuento;
	}

	/** @return ID del detalle. */
	public Long getIdDetallePedido() { return idDetallePedido; }
	/** @param idDetallePedido Nuevo ID del detalle. */
	public void setIdDetallePedido(Long idDetallePedido) { this.idDetallePedido = idDetallePedido; }

	/** @return ID del pedido. */
	public Long getIdPedido() { return idPedido; }
	/** @param idPedido Nuevo ID del pedido. */
	public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

	/** @return ID del producto. */
	public Long getIdProducto() { return idProducto; }
	/** @param idProducto Nuevo ID del producto. */
	public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

	/** @return Nombre del producto. */
	public String getNombreProducto() { return nombreProducto; }
	/** @param nombreProducto Nuevo nombre del producto. */
	public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

	/** @return URL de la imagen del producto. */
	public String getImagenUrl() { return imagenUrl; }
	/** @param imagenUrl Nueva URL de la imagen. */
	public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

	/** @return Cantidad de unidades. */
	public Integer getCantidad() { return cantidad; }
	/** @param cantidad Nueva cantidad. */
	public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

	/** @return Precio unitario. */
	public Double getPrecioUnitario() { return precioUnitario; }
	/** @param precioUnitario Nuevo precio unitario. */
	public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

	/** @return Subtotal de la línea. */
	public Double getSubtotal() { return subtotal; }
	/** @param subtotal Nuevo subtotal. */
	public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

	/** @return {@code true} si tenía promoción. */
	public Boolean getPromocion() { return promocion; }
	/** @param promocion Nuevo valor de promoción. */
	public void setPromocion(Boolean promocion) { this.promocion = promocion; }

	/** @return Porcentaje de descuento. */
	public Integer getPorcentajeDescuento() { return porcentajeDescuento; }
	/** @param porcentajeDescuento Nuevo porcentaje de descuento. */
	public void setPorcentajeDescuento(Integer porcentajeDescuento) {
		this.porcentajeDescuento = porcentajeDescuento;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idDetallePedido, idProducto);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		DetallePedidoDTO other = (DetallePedidoDTO) obj;
		return Objects.equals(idDetallePedido, other.idDetallePedido)
				&& Objects.equals(idProducto, other.idProducto);
	}

	@Override
	public String toString() {
		return "DetallePedidoDTO [idDetallePedido=" + idDetallePedido + ", idProducto="
				+ idProducto + ", cantidad=" + cantidad + ", subtotal=" + subtotal + "]";
	}
}