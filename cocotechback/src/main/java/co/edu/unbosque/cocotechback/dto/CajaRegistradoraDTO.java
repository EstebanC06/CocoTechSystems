/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.util.Objects;

import co.edu.unbosque.cocotechback.model.CajaRegistradora.Estado;

/**
 * Clase de Transferencia de Datos (DTO) para representar la información de una
 * caja registradora del supermercado.
 * <p>
 * Se utiliza para transferir los datos de la caja registradora entre las capas
 * de la aplicación y a través de la API REST, evitando exponer directamente la
 * entidad JPA {@link co.edu.unbosque.cocotechback.model.CajaRegistradora}.
 * Las referencias al empleado y la sucursal se representan mediante sus IDs
 * para evitar dependencias circulares en la serialización JSON.
 */
public class CajaRegistradoraDTO {

	/**
	 * Identificador único de la caja registradora.
	 */
	private Long idCaja;

	/**
	 * Número físico de la caja registradora dentro de la sucursal.
	 */
	private Integer numeroCaja;

	/**
	 * Estado operativo de la caja registradora.
	 */
	private Estado estado;

	/**
	 * Identificador del empleado asignado a esta caja registradora.
	 */
	private Long idEmpleado;

	/**
	 * Identificador de la sucursal a la que pertenece esta caja registradora.
	 */
	private Long idSucursal;

	/**
	 * Constructor por defecto de {@code CajaRegistradoraDTO}.
	 */
	public CajaRegistradoraDTO() {
	}

	/**
	 * Constructor con parámetros para inicializar los campos del
	 * {@code CajaRegistradoraDTO}.
	 *
	 * @param idCaja     Identificador de la caja.
	 * @param numeroCaja Número físico de la caja.
	 * @param estado     Estado operativo de la caja.
	 * @param idEmpleado ID del empleado asignado.
	 * @param idSucursal ID de la sucursal a la que pertenece.
	 */
	public CajaRegistradoraDTO(Long idCaja, Integer numeroCaja, Estado estado, Long idEmpleado,
			Long idSucursal) {
		this.idCaja = idCaja;
		this.numeroCaja = numeroCaja;
		this.estado = estado;
		this.idEmpleado = idEmpleado;
		this.idSucursal = idSucursal;
	}

	/**
	 * Obtiene el identificador único de la caja registradora.
	 *
	 * @return El ID de la caja.
	 */
	public Long getIdCaja() {
		return idCaja;
	}

	/**
	 * Establece el identificador único de la caja registradora.
	 *
	 * @param idCaja El nuevo ID de la caja.
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
	 * Obtiene el ID del empleado asignado a la caja.
	 *
	 * @return El ID del empleado.
	 */
	public Long getIdEmpleado() {
		return idEmpleado;
	}

	/**
	 * Establece el ID del empleado asignado a la caja.
	 *
	 * @param idEmpleado El nuevo ID del empleado.
	 */
	public void setIdEmpleado(Long idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	/**
	 * Obtiene el ID de la sucursal a la que pertenece la caja.
	 *
	 * @return El ID de la sucursal.
	 */
	public Long getIdSucursal() {
		return idSucursal;
	}

	/**
	 * Establece el ID de la sucursal a la que pertenece la caja.
	 *
	 * @param idSucursal El nuevo ID de la sucursal.
	 */
	public void setIdSucursal(Long idSucursal) {
		this.idSucursal = idSucursal;
	}

	/**
	 * Genera un código hash para el objeto {@code CajaRegistradoraDTO} basado en
	 * su ID y número de caja.
	 *
	 * @return El código hash del objeto.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(idCaja, numeroCaja);
	}

	/**
	 * Compara este objeto {@code CajaRegistradoraDTO} con otro para determinar
	 * igualdad, basándose en el ID y el número de caja.
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
		CajaRegistradoraDTO other = (CajaRegistradoraDTO) obj;
		return Objects.equals(idCaja, other.idCaja)
				&& Objects.equals(numeroCaja, other.numeroCaja);
	}

	/**
	 * Devuelve una representación en cadena del objeto {@code CajaRegistradoraDTO}.
	 *
	 * @return Una cadena con los atributos del DTO de la caja registradora.
	 */
	@Override
	public String toString() {
		return "CajaRegistradoraDTO [idCaja=" + idCaja + ", numeroCaja=" + numeroCaja
				+ ", estado=" + estado + ", idEmpleado=" + idEmpleado
				+ ", idSucursal=" + idSucursal + "]";
	}
}
