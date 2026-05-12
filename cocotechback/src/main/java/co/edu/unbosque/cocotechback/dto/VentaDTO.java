/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clase de Transferencia de Datos (DTO) para representar la información de una
 * venta del supermercado.
 * <p>
 * Se utiliza para transferir los datos de la venta entre las capas de la
 * aplicación y a través de la API REST, evitando exponer directamente la
 * entidad JPA {@link co.edu.unbosque.cocotechback.model.Venta}.
 * Las referencias al empleado y al cliente se representan mediante sus IDs
 * para evitar dependencias circulares en la serialización JSON.
 */
public class VentaDTO {

	/**
	 * Identificador único de la venta.
	 */
	private Long idVenta;

	/**
	 * Fecha y hora en la que se realizó la venta.
	 */
	private LocalDateTime fecha;

	/**
	 * Valor total de la venta.
	 */
	private Double total;

	/**
	 * Identificador del empleado que registró la venta.
	 */
	private Long idEmpleado;

	/**
	 * Identificador del cliente que realizó la compra.
	 */
	private Long idCliente;

	/**
	 * Constructor por defecto de {@code VentaDTO}.
	 */
	public VentaDTO() {
	}

	/**
	 * Constructor con parámetros para inicializar los campos del {@code VentaDTO}.
	 *
	 * @param idVenta    Identificador de la venta.
	 * @param fecha      Fecha y hora de la venta.
	 * @param total      Total de la venta.
	 * @param idEmpleado ID del empleado que registró la venta.
	 * @param idCliente  ID del cliente que realizó la compra.
	 */
	public VentaDTO(Long idVenta, LocalDateTime fecha, Double total, Long idEmpleado,
			Long idCliente) {
		this.idVenta = idVenta;
		this.fecha = fecha;
		this.total = total;
		this.idEmpleado = idEmpleado;
		this.idCliente = idCliente;
	}

	/**
	 * Obtiene el identificador único de la venta.
	 *
	 * @return El ID de la venta.
	 */
	public Long getIdVenta() {
		return idVenta;
	}

	/**
	 * Establece el identificador único de la venta.
	 *
	 * @param idVenta El nuevo ID de la venta.
	 */
	public void setIdVenta(Long idVenta) {
		this.idVenta = idVenta;
	}

	/**
	 * Obtiene la fecha y hora de la venta.
	 *
	 * @return La fecha de la venta.
	 */
	public LocalDateTime getFecha() {
		return fecha;
	}

	/**
	 * Establece la fecha y hora de la venta.
	 *
	 * @param fecha La nueva fecha de la venta.
	 */
	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	/**
	 * Obtiene el total de la venta.
	 *
	 * @return El valor total de la venta.
	 */
	public Double getTotal() {
		return total;
	}

	/**
	 * Establece el total de la venta.
	 *
	 * @param total El nuevo total de la venta.
	 */
	public void setTotal(Double total) {
		this.total = total;
	}

	/**
	 * Obtiene el ID del empleado que registró la venta.
	 *
	 * @return El ID del empleado.
	 */
	public Long getIdEmpleado() {
		return idEmpleado;
	}

	/**
	 * Establece el ID del empleado que registró la venta.
	 *
	 * @param idEmpleado El nuevo ID del empleado.
	 */
	public void setIdEmpleado(Long idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	/**
	 * Obtiene el ID del cliente que realizó la compra.
	 *
	 * @return El ID del cliente.
	 */
	public Long getIdCliente() {
		return idCliente;
	}

	/**
	 * Establece el ID del cliente que realizó la compra.
	 *
	 * @param idCliente El nuevo ID del cliente.
	 */
	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	/**
	 * Genera un código hash para el objeto {@code VentaDTO} basado en su ID
	 * y fecha.
	 *
	 * @return El código hash del objeto.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(idVenta, fecha);
	}

	/**
	 * Compara este objeto {@code VentaDTO} con otro para determinar igualdad,
	 * basándose en el ID y la fecha.
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
		VentaDTO other = (VentaDTO) obj;
		return Objects.equals(idVenta, other.idVenta) && Objects.equals(fecha, other.fecha);
	}

	/**
	 * Devuelve una representación en cadena del objeto {@code VentaDTO}.
	 *
	 * @return Una cadena con los atributos del DTO de la venta.
	 */
	@Override
	public String toString() {
		return "VentaDTO [idVenta=" + idVenta + ", fecha=" + fecha + ", total=" + total
				+ ", idEmpleado=" + idEmpleado + ", idCliente=" + idCliente + "]";
	}
}
