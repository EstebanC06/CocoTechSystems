/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa una caja registradora del supermercado.
 * <p>
 * Según los supuestos del sistema:
 * <ul>
 * <li>Cada caja registradora tiene asignado un solo empleado para ser
 * gestionada (relación 1:1 con {@link Empleado}).</li>
 * <li>Cada caja registradora está ubicada en una sucursal específica
 * (relación M:1 con {@link Sucursal}).</li>
 * </ul>
 */
@Entity
@Table(name = "caja_registradora")
public class CajaRegistradora {

	/**
	 * Identificador único de la caja registradora, generado automáticamente
	 * por la base de datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idCaja;

	/**
	 * Número físico de la caja registradora dentro de la sucursal.
	 */
	private Integer numeroCaja;

	/**
	 * Estado operativo de la caja registradora.
	 */
	@Enumerated(EnumType.STRING)
	private Estado estado;

	/**
	 * Empleado asignado para gestionar esta caja registradora.
	 * Un empleado gestiona una única caja.
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_empleado", nullable = false, unique = true)
	private Empleado empleado;

	/**
	 * Sucursal a la que pertenece esta caja registradora.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sucursal", nullable = false)
	private Sucursal sucursal;

	/**
	 * Enumeración que define los posibles estados de una caja registradora.
	 */
	public enum Estado {
		ACTIVA, INACTIVA, EN_MANTENIMIENTO
	}

	/**
	 * Constructor por defecto de la entidad CajaRegistradora.
	 */
	public CajaRegistradora() {
	}

	/**
	 * Constructor con parámetros para inicializar los datos de la caja.
	 *
	 * @param numeroCaja Número físico de la caja.
	 * @param estado     Estado operativo de la caja.
	 * @param empleado   Empleado asignado a la caja.
	 * @param sucursal   Sucursal a la que pertenece la caja.
	 */
	public CajaRegistradora(Integer numeroCaja, Estado estado, Empleado empleado,
			Sucursal sucursal) {
		this.numeroCaja = numeroCaja;
		this.estado = estado;
		this.empleado = empleado;
		this.sucursal = sucursal;
	}

	/**
	 * Obtiene el identificador único de la caja registradora.
	 *
	 * @return El ID de la caja registradora.
	 */
	public Long getIdCaja() {
		return idCaja;
	}

	/**
	 * Establece el identificador único de la caja registradora.
	 *
	 * @param idCaja El nuevo ID de la caja registradora.
	 */
	public void setIdCaja(Long idCaja) {
		this.idCaja = idCaja;
	}

	/**
	 * Obtiene el número de la caja registradora.
	 *
	 * @return El número de la caja.
	 */
	public Integer getNumeroCaja() {
		return numeroCaja;
	}

	/**
	 * Establece el número de la caja registradora.
	 *
	 * @param numeroCaja El nuevo número de la caja.
	 */
	public void setNumeroCaja(Integer numeroCaja) {
		this.numeroCaja = numeroCaja;
	}

	/**
	 * Obtiene el estado operativo de la caja registradora.
	 *
	 * @return El estado de la caja.
	 */
	public Estado getEstado() {
		return estado;
	}

	/**
	 * Establece el estado operativo de la caja registradora.
	 *
	 * @param estado El nuevo estado de la caja.
	 */
	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	/**
	 * Obtiene el empleado asignado a esta caja registradora.
	 *
	 * @return El empleado de la caja.
	 */
	public Empleado getEmpleado() {
		return empleado;
	}

	/**
	 * Establece el empleado asignado a esta caja registradora.
	 *
	 * @param empleado El nuevo empleado de la caja.
	 */
	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Obtiene la sucursal a la que pertenece la caja registradora.
	 *
	 * @return La sucursal de la caja.
	 */
	public Sucursal getSucursal() {
		return sucursal;
	}

	/**
	 * Establece la sucursal a la que pertenece la caja registradora.
	 *
	 * @param sucursal La nueva sucursal de la caja.
	 */
	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	/**
	 * Devuelve una representación en cadena del objeto CajaRegistradora.
	 *
	 * @return Una cadena con los atributos de la caja registradora.
	 */
	@Override
	public String toString() {
		return "CajaRegistradora [idCaja=" + idCaja + ", numeroCaja=" + numeroCaja + ", estado=" + estado
				+ ", empleado=" + (empleado != null ? empleado.getId() : "null")
				+ ", sucursal=" + (sucursal != null ? sucursal.getIdSucursal() : "null") + "]";
	}
}
