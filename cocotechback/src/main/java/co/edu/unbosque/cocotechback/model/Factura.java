/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa una factura generada por una venta en el
 * supermercado.
 * <p>
 * Según los supuestos del sistema, cada venta registrada genera una única
 * factura (relación 1:1 con {@link Venta}). La factura contiene el resumen
 * financiero de la venta: el precio total, los impuestos y la fecha de
 * emisión.
 */
@Entity
@Table(name = "factura")
public class Factura {

	/**
	 * Identificador único de la factura, generado automáticamente por la base
	 * de datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	 * Venta a la que pertenece esta factura.
	 * Cada factura corresponde a una única venta.
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_venta", nullable = false, unique = true)
	private Venta venta;

	/**
	 * Constructor por defecto de la entidad Factura.
	 */
	public Factura() {
	}

	/**
	 * Constructor con parámetros para inicializar los datos de la factura.
	 *
	 * @param fecha           Fecha y hora de emisión de la factura.
	 * @param precioTotal     Precio total de la factura.
	 * @param precioImpuestos Valor de impuestos de la factura.
	 * @param venta           Venta asociada a la factura.
	 */
	public Factura(LocalDateTime fecha, Double precioTotal, Double precioImpuestos, Venta venta) {
		this.fecha = fecha;
		this.precioTotal = precioTotal;
		this.precioImpuestos = precioImpuestos;
		this.venta = venta;
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
	 * Obtiene la venta asociada a esta factura.
	 *
	 * @return La venta de la factura.
	 */
	public Venta getVenta() {
		return venta;
	}

	/**
	 * Establece la venta asociada a esta factura.
	 *
	 * @param venta La nueva venta de la factura.
	 */
	public void setVenta(Venta venta) {
		this.venta = venta;
	}

	/**
	 * Devuelve una representación en cadena del objeto Factura.
	 *
	 * @return Una cadena con los atributos de la factura.
	 */
	@Override
	public String toString() {
		return "Factura [idFactura=" + idFactura + ", fecha=" + fecha + ", precioTotal=" + precioTotal
				+ ", precioImpuestos=" + precioImpuestos
				+ ", venta=" + (venta != null ? venta.getIdVenta() : "null") + "]";
	}
}
