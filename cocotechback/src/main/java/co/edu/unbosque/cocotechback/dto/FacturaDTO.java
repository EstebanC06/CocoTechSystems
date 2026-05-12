/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clase de Transferencia de Datos (DTO) para representar la información de una
 * factura del supermercado.
 * <p>
 * Se utiliza para transferir los datos de la factura entre las capas de la
 * aplicación y a través de la API REST, evitando exponer directamente la
 * entidad JPA {@link co.edu.unbosque.cocotechback.model.Factura}.
 * La referencia a la venta se representa mediante su ID para evitar
 * dependencias circulares en la serialización JSON.
 */
public class FacturaDTO {

	/**
	 * Identificador único de la factura.
	 */
	private Long idFactura;

	/**
	 * Fecha y hora de emisión de la factura.
	 */
	private LocalDateTime fecha;

	/**
	 * Precio total de la factura (incluye impuestos).
	 */
	private Double precioTotal;

	/**
	 * Valor de los impuestos aplicados en la factura.
	 */
	private Double precioImpuestos;

	/**
	 * Identificador de la venta asociada a esta factura.
	 */
	private Long idVenta;

	/**
	 * Constructor por defecto de {@code FacturaDTO}.
	 */
	public FacturaDTO() {
	}

	/**
	 * Constructor con parámetros para inicializar los campos del
	 * {@code FacturaDTO}.
	 *
	 * @param idFactura       Identificador de la factura.
	 * @param fecha           Fecha y hora de emisión.
	 * @param precioTotal     Precio total de la factura.
	 * @param precioImpuestos Valor de los impuestos.
	 * @param idVenta         ID de la venta asociada.
	 */
	public FacturaDTO(Long idFactura, LocalDateTime fecha, Double precioTotal,
			Double precioImpuestos, Long idVenta) {
		this.idFactura = idFactura;
		this.fecha = fecha;
		this.precioTotal = precioTotal;
		this.precioImpuestos = precioImpuestos;
		this.idVenta = idVenta;
	}

	/**
	 * Obtiene el identificador único de la factura.
	 *
	 * @return El ID de la factura.
	 */
	public Long getIdFactura() {
		return idFactura;
	}

	/**
	 * Establece el identificador único de la factura.
	 *
	 * @param idFactura El nuevo ID de la factura.
	 */
	public void setIdFactura(Long idFactura) {
		this.idFactura = idFactura;
	}

	/**
	 * Obtiene la fecha y hora de emisión de la factura.
	 *
	 * @return La fecha de la factura.
	 */
	public LocalDateTime getFecha() {
		return fecha;
	}

	/**
	 * Establece la fecha y hora de emisión de la factura.
	 *
	 * @param fecha La nueva fecha de la factura.
	 */
	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	/**
	 * Obtiene el precio total de la factura.
	 *
	 * @return El precio total.
	 */
	public Double getPrecioTotal() {
		return precioTotal;
	}

	/**
	 * Establece el precio total de la factura.
	 *
	 * @param precioTotal El nuevo precio total.
	 */
	public void setPrecioTotal(Double precioTotal) {
		this.precioTotal = precioTotal;
	}

	/**
	 * Obtiene el valor de los impuestos de la factura.
	 *
	 * @return El valor de los impuestos.
	 */
	public Double getPrecioImpuestos() {
		return precioImpuestos;
	}

	/**
	 * Establece el valor de los impuestos de la factura.
	 *
	 * @param precioImpuestos El nuevo valor de impuestos.
	 */
	public void setPrecioImpuestos(Double precioImpuestos) {
		this.precioImpuestos = precioImpuestos;
	}

	/**
	 * Obtiene el ID de la venta asociada a esta factura.
	 *
	 * @return El ID de la venta.
	 */
	public Long getIdVenta() {
		return idVenta;
	}

	/**
	 * Establece el ID de la venta asociada a esta factura.
	 *
	 * @param idVenta El nuevo ID de la venta.
	 */
	public void setIdVenta(Long idVenta) {
		this.idVenta = idVenta;
	}

	/**
	 * Genera un código hash para el objeto {@code FacturaDTO} basado en su ID
	 * e ID de venta.
	 *
	 * @return El código hash del objeto.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(idFactura, idVenta);
	}

	/**
	 * Compara este objeto {@code FacturaDTO} con otro para determinar igualdad,
	 * basándose en el ID y el ID de venta.
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
		FacturaDTO other = (FacturaDTO) obj;
		return Objects.equals(idFactura, other.idFactura)
				&& Objects.equals(idVenta, other.idVenta);
	}

	/**
	 * Devuelve una representación en cadena del objeto {@code FacturaDTO}.
	 *
	 * @return Una cadena con los atributos del DTO de la factura.
	 */
	@Override
	public String toString() {
		return "FacturaDTO [idFactura=" + idFactura + ", fecha=" + fecha + ", precioTotal=" + precioTotal
				+ ", precioImpuestos=" + precioImpuestos + ", idVenta=" + idVenta + "]";
	}
}
