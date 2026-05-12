/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import java.time.LocalDateTime;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa una venta realizada en el supermercado.
 * <p>
 * Según los supuestos del sistema:
 * <ul>
 * <li>Un empleado registra varias ventas a lo largo de su jornada laboral.</li>
 * <li>Cada cliente puede generar una o varias ventas.</li>
 * <li>Cada venta registrada genera una única factura (1:1).</li>
 * <li>Cada venta contiene varios detalles de venta que la identifican y
 * describen.</li>
 * </ul>
 */
@Entity
@Table(name = "venta")
public class Venta {

	/**
	 * Identificador único de la venta, generado automáticamente por la base de
	 * datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idVenta;

	/**
	 * Fecha y hora en la que se realizó la venta.
	 */
	private LocalDateTime fecha;

	/**
	 * Valor total de la venta (suma de todos los subtotales de los detalles).
	 */
	private Double total;

	/**
	 * Empleado que registró esta venta en el sistema.
	 * Muchas ventas pueden ser registradas por un mismo empleado.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_empleado", nullable = false)
	private Empleado empleado;

	/**
	 * Cliente que realizó esta compra.
	 * Un cliente puede aportar una o varias ventas al supermercado.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente", nullable = false)
	private Cliente cliente;

	/**
	 * Lista de detalles de venta que componen esta venta.
	 * Una venta contiene varios detalles que la describen.
	 */
	@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DetalleVenta> detallesVenta;

	/**
	 * Factura generada a partir de esta venta.
	 * Cada venta genera una única factura.
	 */
	@OneToOne(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Factura factura;

	/**
	 * Constructor por defecto de la entidad Venta.
	 */
	public Venta() {
	}

	/**
	 * Constructor con parámetros para inicializar los datos de la venta.
	 *
	 * @param fecha    Fecha y hora de la venta.
	 * @param total    Valor total de la venta.
	 * @param empleado Empleado que registró la venta.
	 * @param cliente  Cliente que realizó la compra.
	 */
	public Venta(LocalDateTime fecha, Double total, Empleado empleado, Cliente cliente) {
		this.fecha = fecha;
		this.total = total;
		this.empleado = empleado;
		this.cliente = cliente;
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
	 * @param total El nuevo valor total de la venta.
	 */
	public void setTotal(Double total) {
		this.total = total;
	}

	/**
	 * Obtiene el empleado que registró la venta.
	 *
	 * @return El empleado de la venta.
	 */
	public Empleado getEmpleado() {
		return empleado;
	}

	/**
	 * Establece el empleado que registró la venta.
	 *
	 * @param empleado El nuevo empleado de la venta.
	 */
	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Obtiene el cliente que realizó la compra.
	 *
	 * @return El cliente de la venta.
	 */
	public Cliente getCliente() {
		return cliente;
	}

	/**
	 * Establece el cliente que realizó la compra.
	 *
	 * @param cliente El nuevo cliente de la venta.
	 */
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	/**
	 * Obtiene la lista de detalles de venta de esta venta.
	 *
	 * @return La lista de detalles de venta.
	 */
	public List<DetalleVenta> getDetallesVenta() {
		return detallesVenta;
	}

	/**
	 * Establece la lista de detalles de venta de esta venta.
	 *
	 * @param detallesVenta La nueva lista de detalles de venta.
	 */
	public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
		this.detallesVenta = detallesVenta;
	}

	/**
	 * Obtiene la factura generada por esta venta.
	 *
	 * @return La factura de la venta.
	 */
	public Factura getFactura() {
		return factura;
	}

	/**
	 * Establece la factura generada por esta venta.
	 *
	 * @param factura La nueva factura de la venta.
	 */
	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	/**
	 * Devuelve una representación en cadena del objeto Venta.
	 *
	 * @return Una cadena con los atributos de la venta.
	 */
	@Override
	public String toString() {
		return "Venta [idVenta=" + idVenta + ", fecha=" + fecha + ", total=" + total
				+ ", empleado=" + (empleado != null ? empleado.getId() : "null")
				+ ", cliente=" + (cliente != null ? cliente.getId() : "null") + "]";
	}
}
