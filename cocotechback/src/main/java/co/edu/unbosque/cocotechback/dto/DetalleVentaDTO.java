/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.util.Objects;

/**
 * Clase de Transferencia de Datos (DTO) para representar la información de un
 * detalle de venta del supermercado.
 * <p>
 * Se utiliza para transferir los datos del detalle de venta entre las capas de
 * la aplicación y a través de la API REST, evitando exponer directamente la
 * entidad JPA {@link co.edu.unbosque.cocotechback.model.DetalleVenta}.
 * Las referencias a la venta y al producto se representan mediante sus IDs
 * para evitar dependencias circulares en la serialización JSON.
 */
public class DetalleVentaDTO {

	/**
	 * Identificador único del detalle de venta.
	 */
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
	 * con descuento aplicado si corresponde).
	 */
	private Double subtotal;

	/**
	 * Método de pago utilizado (ej. "Efectivo", "Tarjeta débito").
	 */
	private String metodoPago;

	/**
	 * Indica si se aplicó una promoción en este detalle.
	 */
	private Boolean promocion;

	/**
	 * Porcentaje de descuento aplicado. Puede ser {@code null} si no aplica.
	 */
	private Double porcentajeDescuento;

	/**
	 * Precio original antes del descuento. Puede ser {@code null} si no aplica.
	 */
	private Double precioOriginal;

	/**
	 * Precio final después del descuento. Puede ser {@code null} si no aplica.
	 */
	private Double precioNuevo;

	/**
	 * Identificador de la venta a la que pertenece este detalle.
	 */
	private Long idVenta;

	/**
	 * Identificador del producto incluido en este detalle.
	 */
	private Long idProducto;

	/**
	 * Constructor por defecto de {@code DetalleVentaDTO}.
	 */
	public DetalleVentaDTO() {
	}

	/**
	 * Constructor con parámetros para inicializar los campos del
	 * {@code DetalleVentaDTO}.
	 *
	 * @param idDetalle           Identificador del detalle.
	 * @param cantidadProductos   Cantidad de productos.
	 * @param precioUnitario      Precio unitario del producto.
	 * @param subtotal            Subtotal del detalle.
	 * @param metodoPago          Método de pago utilizado.
	 * @param promocion           Indica si aplica promoción.
	 * @param porcentajeDescuento Porcentaje de descuento.
	 * @param precioOriginal      Precio original antes del descuento.
	 * @param precioNuevo         Precio final después del descuento.
	 * @param idVenta             ID de la venta asociada.
	 * @param idProducto          ID del producto incluido.
	 */
	public DetalleVentaDTO(Long idDetalle, Integer cantidadProductos, Double precioUnitario,
			Double subtotal, String metodoPago, Boolean promocion, Double porcentajeDescuento,
			Double precioOriginal, Double precioNuevo, Long idVenta, Long idProducto) {
		this.idDetalle = idDetalle;
		this.cantidadProductos = cantidadProductos;
		this.precioUnitario = precioUnitario;
		this.subtotal = subtotal;
		this.metodoPago = metodoPago;
		this.promocion = promocion;
		this.porcentajeDescuento = porcentajeDescuento;
		this.precioOriginal = precioOriginal;
		this.precioNuevo = precioNuevo;
		this.idVenta = idVenta;
		this.idProducto = idProducto;
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
	 * @return {@code true} si aplica promoción, {@code false} en caso contrario.
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
	 * Obtiene el precio final después del descuento.
	 *
	 * @return El precio nuevo.
	 */
	public Double getPrecioNuevo() {
		return precioNuevo;
	}

	/**
	 * Establece el precio final después del descuento.
	 *
	 * @param precioNuevo El nuevo precio final.
	 */
	public void setPrecioNuevo(Double precioNuevo) {
		this.precioNuevo = precioNuevo;
	}

	/**
	 * Obtiene el ID de la venta a la que pertenece el detalle.
	 *
	 * @return El ID de la venta.
	 */
	public Long getIdVenta() {
		return idVenta;
	}

	/**
	 * Establece el ID de la venta a la que pertenece el detalle.
	 *
	 * @param idVenta El nuevo ID de la venta.
	 */
	public void setIdVenta(Long idVenta) {
		this.idVenta = idVenta;
	}

	/**
	 * Obtiene el ID del producto incluido en el detalle.
	 *
	 * @return El ID del producto.
	 */
	public Long getIdProducto() {
		return idProducto;
	}

	/**
	 * Establece el ID del producto incluido en el detalle.
	 *
	 * @param idProducto El nuevo ID del producto.
	 */
	public void setIdProducto(Long idProducto) {
		this.idProducto = idProducto;
	}

	/**
	 * Genera un código hash para el objeto {@code DetalleVentaDTO} basado en su
	 * ID, ID de venta e ID de producto.
	 *
	 * @return El código hash del objeto.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(idDetalle, idVenta, idProducto);
	}

	/**
	 * Compara este objeto {@code DetalleVentaDTO} con otro para determinar
	 * igualdad, basándose en el ID, ID de venta e ID de producto.
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
		DetalleVentaDTO other = (DetalleVentaDTO) obj;
		return Objects.equals(idDetalle, other.idDetalle)
				&& Objects.equals(idVenta, other.idVenta)
				&& Objects.equals(idProducto, other.idProducto);
	}

	/**
	 * Devuelve una representación en cadena del objeto {@code DetalleVentaDTO}.
	 *
	 * @return Una cadena con los atributos del DTO del detalle de venta.
	 */
	@Override
	public String toString() {
		return "DetalleVentaDTO [idDetalle=" + idDetalle + ", cantidadProductos=" + cantidadProductos
				+ ", precioUnitario=" + precioUnitario + ", subtotal=" + subtotal
				+ ", metodoPago=" + metodoPago + ", promocion=" + promocion
				+ ", porcentajeDescuento=" + porcentajeDescuento + ", precioOriginal=" + precioOriginal
				+ ", precioNuevo=" + precioNuevo + ", idVenta=" + idVenta
				+ ", idProducto=" + idProducto + "]";
	}
}
